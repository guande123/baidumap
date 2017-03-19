package com.example.administrator.a20170313adbaidumap;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.TextureMapView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private TextureMapView mMapView;
    private BaiduMap mBaiduMap;
    private PoiSearch mPoiSearch;
    private   LocationClient locationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //在使用SDK各组件之前初始化context信息，传入ApplicationContext
        //注意该方法要再setContentView方法之前实现
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_main);

        //getPermission();
        initBDMap();
       initLocClient();
        locationClient.start();
        searchPoi();
    }

    private void initBDMap() {
        mMapView = (TextureMapView) findViewById(R.id.bmapView);
        mBaiduMap= mMapView.getMap();
       mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
    }

    private void searchPoi() {
        mPoiSearch = PoiSearch.newInstance();
        OnGetPoiSearchResultListener poiListener = new OnGetPoiSearchResultListener(){
            public void onGetPoiResult(PoiResult result){
                //获取POI检索结果
                Log.i( TAG, "result.getAllPoi().size(): "+result.getAllPoi().size());
                Log.i( TAG, "result.getAllPoi().get(0).location: "+result.getAllPoi().get(0).location);
                Log.i( TAG, "result.getAllPoi().get(0).location: "+result.getAllPoi().get(0).type);
                Log.i( TAG, "onGetPoiResult: "+result.getAllPoi().get(0).name);


            }
            public void onGetPoiDetailResult(PoiDetailResult result){
                //获取Place详情页检索结果
                Log.i( TAG, "onGetPoiDetailResult: "+result.toString());
            }

            @Override
            public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {
                Log.i( TAG, "onGetPoiIndoorResult: "+poiIndoorResult.toString());
            }
        };
        mPoiSearch.setOnGetPoiSearchResultListener(poiListener);
        mPoiSearch.searchInCity((new PoiCitySearchOption() .city("北京")
                .keyword("美食")
                .pageNum(10)));
    }

    private void initLocClient() {
        locationClient = new LocationClient(getApplicationContext());
        locationClient.registerLocationListener(new Location());
        LocationClientOption lop = new LocationClientOption();
       // lop.setScanSpan(5000);
         //设置定位模式，设备传感 ： GPS
        //lop.setLocationMode(LocationClientOption.LocationMode.Device_Sensors);
        locationClient.setLocOption(lop);
    }

    private  void  getPermission() {
        List<String> permissionList = new ArrayList<String>();
              /* if(ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this, new String []{Manifest.permission.ACCESS_COARSE_LOCATION},1001);
        }*/
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
            // ActivityCompat.requestPermissions(MainActivity.this, new String []{Manifest.permission.ACCESS_FINE_LOCATION},1002);
        }
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            // ActivityCompat.requestPermissions(MainActivity.this, new String []{Manifest.permission.WRITE_EXTERNAL_STORAGE},1003);
        }
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.READ_PHONE_STATE)!= PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
            //  ActivityCompat.requestPermissions(MainActivity.this, new String []{Manifest.permission.READ_PHONE_STATE},1004);
        }

        if(!permissionList.isEmpty()){
            String[] permissions =  permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(this,permissions,1001);
        }
    }

    class Location implements BDLocationListener {

      @Override
      public void onReceiveLocation(BDLocation bdLocation) {

          double  lat =  bdLocation.getLatitude();
          double  lng =  bdLocation.getLongitude();
          Log.i(TAG, "bdLocation.getLatitude(): "+ lat);
          Log.i(TAG, "bdLocation.getLatitude():"+lng);
          Log.i( TAG, "bdLocation.getLocType(): "+bdLocation.getLocType());


         LatLng ll = new LatLng(lat,lng);
          MapStatusUpdate msu= MapStatusUpdateFactory.newLatLng(ll);
          mBaiduMap.animateMapStatus(  msu );
        //刷新百度Views显示定位图层
         msu= MapStatusUpdateFactory.zoomTo(16f);
          mBaiduMap.animateMapStatus(  msu);
        // 构造定位数据
          MyLocationData locData = new MyLocationData.Builder()
                  .accuracy(bdLocation.getRadius())
                  // 此处设置开发者获取到的方向信息，顺时针0-360
                  //  .direction(100)
                  .latitude(lat)
                  .longitude(lng).build();
       /*   // 设置定位图层的配置（定位模式，是否允许方向信息，用户自定义定位图标）
          BitmapDescriptor mCurrentMarker = BitmapDescriptorFactory
                  .fromResource(R.drawable.l_icon);
          MyLocationConfiguration config = new MyLocationConfiguration(MyLocationConfiguration.LocationMode.NORMAL, true, mCurrentMarker);
          mBaiduMap.setMyLocationConfigeration(config);
// 设置定位数据*/
          mBaiduMap.setMyLocationData(locData);
      }

      @Override
      public void onConnectHotSpotMessage(String s, int i) {
          Log.i(TAG, "onReceiveLocation: "+s);
      }
  }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理  
        mMapView.onDestroy();
        mPoiSearch.destroy();
        locationClient.stop();
        // 当不需要定位图层时关闭定位图层
        mBaiduMap.setMyLocationEnabled(false);
    }
    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理  
        mMapView.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理  
        mMapView.onPause();

    }

}
