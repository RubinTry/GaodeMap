package logcat.wzlsz.com.gaodemap.Map;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMapOptions;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.services.geocoder.GeocodeQuery;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.poisearch.PoiSearch;

import java.text.SimpleDateFormat;
import java.util.Date;

import logcat.wzlsz.com.gaodemap.MainActivity;

/**
 * Created by Administrator on 2018/7/9.
 */

public class Location {
    //声明mlocationClient对象
    public static AMapLocationClient mlocationClient;

    //声明mLocationOption对象
    public static AMapLocationClientOption mLocationOption = null;

    private static UiSettings mUisettings;

    private static PoiSearch poiSearch;

    private static GeocodeSearch geocodeSearch;
    private static GeocodeQuery geocodeQuery;


    //文本框监听
    public static TextWatcher changeListener = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {



            Log.d("tag", "afterTextChanged: ");
            PoiSearch.Query query = new PoiSearch.Query(MainActivity.input_search.getText().toString(),"");
            query.setPageSize(10);
            query.setPageNum(1);
            poiSearch = new PoiSearch(MainActivity.activity,query);
            poiSearch.setOnPoiSearchListener(MainActivity.activity);
            poiSearch.searchPOIAsyn();

        }
    };


    //初始化定位客户端
    public static void initClient(){
        mlocationClient = new AMapLocationClient(MainActivity.activity);
        //初始化定位参数
        mLocationOption = new AMapLocationClientOption();

        //设置定位监听
        mlocationClient.setLocationListener(new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation amapLocation) {

                if (amapLocation.getErrorCode() == 0) {
                    //定位成功回调信息，设置相关消息
                    amapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见定位类型表
                    amapLocation.getLatitude();//获取纬度
                    amapLocation.getLongitude();//获取经度
                    amapLocation.getAccuracy();//获取精度信息
                    Log.d("tag", "维度: "+amapLocation.getLatitude()+"  经度："+amapLocation.getLongitude());
                    Log.d("tag", "精度: "+amapLocation.getAccuracy());
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date date = new Date(amapLocation.getTime());
                    df.format(date);//定位时间
                    Log.d("tag", "定位时间: "+df.format(date));
                } else {
                    //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                    Log.e("AmapError","location Error, ErrCode:"
                            + amapLocation.getErrorCode() + ", errInfo:"
                            + amapLocation.getErrorInfo());
                }


            }
        });


        //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
//        //设置定位间隔,单位毫秒,默认为2000ms
//        mLocationOption.setInterval(2000);

        mLocationOption.setLocationPurpose(AMapLocationClientOption.AMapLocationPurpose.SignIn);
        if(null != mlocationClient){
            mlocationClient.setLocationOption(mLocationOption);
            //设置场景模式后最好调用一次stop，再调用start以保证场景模式生效
            mlocationClient.stopLocation();
            mlocationClient.startLocation();
        }

        mLocationOption.setNeedAddress(true);

        //设置定位参数
        mlocationClient.setLocationOption(mLocationOption);
        // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
        // 注意设置合适的定位时间的间隔（最小间隔支持为1000ms），并且在合适时间调用stopLocation()方法来取消定位请求
        // 在定位结束后，在合适的生命周期调用onDestroy()方法
        // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
        //启动定位
        mlocationClient.startLocation();
    }


    //初始化地图控制器对象
    public static void initLocationStyle(){
        MyLocationStyle myLocationStyle;
        myLocationStyle = new MyLocationStyle();
        myLocationStyle.interval(5000);
        MainActivity.aMap.setMyLocationStyle(myLocationStyle);
        MainActivity.aMap.setMyLocationEnabled(true);

        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_FOLLOW_NO_CENTER) ;
        myLocationStyle.showMyLocation(true);//设置是否显示定位小蓝点，用于满足只想使用定位，不想使用定位小蓝点的场景，设置false以后图面上不再有定位蓝点的概念，但是会持续回调位置信息。

        mUisettings = MainActivity.aMap.getUiSettings();
        mUisettings.setZoomControlsEnabled(false);
        mUisettings.setCompassEnabled(true);
        mUisettings.setMyLocationButtonEnabled(true);
        mUisettings.setScaleControlsEnabled(true);
        mUisettings.setRotateGesturesEnabled(false);

        Log.d("tag", "logoPosition: "+ AMapOptions.LOGO_POSITION_BOTTOM_CENTER);
        mUisettings.setLogoPosition(AMapOptions.LOGO_POSITION_BOTTOM_CENTER);
        Log.d("tag", "initLocationStyle: ");
    }


    //获得地址描述
    public static void getDescribe(String adCode){
        geocodeSearch = new GeocodeSearch(MainActivity.activity);
        geocodeSearch.setOnGeocodeSearchListener(MainActivity.activity);
        Log.d("tag", "getDescribe: ");
        geocodeQuery = new GeocodeQuery(MainActivity.input_search.getText().toString(),adCode);
        geocodeSearch.getFromLocationNameAsyn(geocodeQuery);
    }

}
