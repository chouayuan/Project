package com.help.demo;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.TextureSupportMapFragment;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;

/**
 * Created by yuan on 2017/2/28.
 */

public class BaseTextureSupportMapFragmentActivity extends FragmentActivity implements LocationSource, AMapLocationListener, PoiSearch.OnPoiSearchListener {
    private AMap mMap;

    private AMapLocationClient mlocationClient;
    private AMapLocationClientOption mlocationOption;
    private OnLocationChangedListener mListener;

    private UiSettings mUiSettings;

    private LatLng mlatLng;

    private String mCity;
    private PoiSearch poiSearch;
    private int currentPage = 0;
    private PoiSearch.Query query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.basemap_texture_support_fragment_activity);
        setUpMapIfNeeded();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    private void setUpMapIfNeeded() {
        if (mMap == null) {
            mMap = ((TextureSupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map)).getMap();
            mUiSettings = mMap.getUiSettings();
            setUpMap();
        }
        mUiSettings.setRotateGesturesEnabled(false);//旋转手势

    }


    private void setUpMap() {
        mMap.setLocationSource(this);// 设置定位监听
        mMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
        mMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        // 设置定位的类型为定位模式 ，可以由定位(LOCATION_TYPE_LOCATE)、跟随(LOCATION_TYPE_MAP_FOLLOW)或地图根据面向方向旋转(LOCATION_TYPE_MAP_ROTATE)几种
        mMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);
    }

    /**
     * 激活定位
     */
    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        mListener = onLocationChangedListener;
        if (mlocationClient == null) {
            mlocationClient = new AMapLocationClient(this);
            mlocationOption = new AMapLocationClientOption();
            //设置定位监听
            mlocationClient.setLocationListener(this);
            //设置为高精度定位模式
            mlocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            //设置定位参数
            mlocationClient.setLocationOption(mlocationOption);
            // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
            // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
            // 在定位结束后，在合适的生命周期调用onDestroy()方法
            // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
            mlocationClient.startLocation();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        deactivate();
    }

    @Override
    public void deactivate() {
        mListener = null;
        if (mlocationClient != null) {
            mlocationClient.stopLocation();
            mlocationClient.onDestroy();
        }
        mlocationClient = null;
    }

    /*
    * location---latitude=40.035912#longitude=116.372423#province=北京市#city=北京市#district=海淀区#cityCode=010#adCode=110108#address=北京市海淀区黑泉路靠近康健宝盛广场D座#country=中国#road=黑泉路#poiName=康健宝盛广场D座#street=黑泉路#streetNum=10号#aoiName=康健宝盛广场#poiid=#floor=#errorCode=0#errorInfo=success#locationDetail=-1 #csid:a574a0ca21e4420fb2a0fb2db8cce3f8#locationType=2
02-28 19:57:53.199 19144-19144/com.help.demo I/location: location---latitude=40.035912#longitude=116.372423#province=北京市#city=北京市#district=海淀区#cityCode=010#adCode=110108#address=北京市海淀区黑泉路靠近康健宝盛广场D座#country=中国#road=黑泉路#poiName=康健宝盛广场D座#street=黑泉路#streetNum=10号#aoiName=康健宝盛广场#poiid=#floor=#errorCode=0#errorInfo=success#locationDetail=-1 #csid:a574a0ca21e4420fb2a0fb2db8cce3f8#locationType=2
    * */
    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (mListener != null && amapLocation != null) {
            if (amapLocation != null
                    && amapLocation.getErrorCode() == 0) {
//                mLocationErrText.setVisibility(View.GONE);
//                mListener.onLocationChanged(amapLocation);// 显示系统小蓝点
                Log.i("location", "location---" + amapLocation.toString());
                mCity = amapLocation.getCity();
                mlatLng = new LatLng(amapLocation.getLatitude(), amapLocation.getLongitude());

                changeCamera(
                        CameraUpdateFactory.newCameraPosition(new CameraPosition(
                                mlatLng, 18, 30, 30)));
                mMap.clear();
                mMap.addMarker(new MarkerOptions().position(mlatLng)
                        .icon(BitmapDescriptorFactory
                                .defaultMarker(BitmapDescriptorFactory.HUE_RED)));


            } else {
                String errText = "定位失败," + amapLocation.getErrorCode() + ": " + amapLocation.getErrorInfo();
                Log.e("AmapErr", errText);
//                mLocationErrText.setVisibility(View.VISIBLE);
//                mLocationErrText.setText(errText);
            }
        }
    }

    /**
     * 调用函数animateCamera或moveCamera来改变可视区域
     */
    private void changeCamera(CameraUpdate update) {

        mMap.moveCamera(update);

    }

    /**
     * 开始进行poi搜索
     */
    protected void doSearchQuery() {
//        showProgressDialog();// 显示进度框
        currentPage = 0;
        query = new PoiSearch.Query("北新桥", "", mCity);// 第一个参数表示搜索字符串，第二个参数表示poi搜索类型，第三个参数表示poi搜索区域（空字符串代表全国）
        query.setPageSize(10);// 设置每页最多返回多少条poiitem
        query.setPageNum(currentPage);// 设置查第一页

        poiSearch = new PoiSearch(this, query);
        poiSearch.setOnPoiSearchListener(this);
        poiSearch.searchPOIAsyn();
    }

    @Override
    public void onPoiSearched(PoiResult poiResult, int i) {

    }

    @Override
    public void onPoiItemSearched(PoiItem poiItem, int i) {

    }
}
