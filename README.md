# baidumap
一、 申请ak
1.appkey.store获取SHA1 密钥
2.项目包名
二、环境配置
1.百度官方下载LBS.jar
2.集成到项目lib中放android_baiduLBS.jar、jniLibs放.so文件
3.配置Application  <meta>，输入ak
4. SDKInitializer.initialize(getApplicationContext());初始化SDK

三 定位（动态权限获取）
1.配置service：
  <service android:name="com.baidu.location.f"
            android:enabled="true"
            android:process=":remote"/>
2.创建locationClient实例，并register  DBLocationListener监听定位详情
3.locationclientoption 设置定位方式 
4.跳转位置：
  // 开启定位图层   mBaiduMap.setMyLocationEnabled(true);
  // 构造定位数据  new MyLocationData.Builder()
  // 设置定位数据    mBaiduMap.setMyLocationData(locData);
  // 设置定位图层的配置（定位模式，是否允许方向信息，用户自定义定位图标）
 MyLocationConfiguration config = new MyLocationConfiguration(MyLocationConfiguration.LocationMode.NORMAL, true, mCurrentMarker);
          mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(MyLocationConfiguration.LocationMode.NORMAL,true,mCurrentMarker));
  //刷新百度Views显示定位图层
          MapStatusUpdate msu= MapStatusUpdateFactory.zoomBy(8.0f);
          mBaiduMap.animateMapStatus(  msu );
