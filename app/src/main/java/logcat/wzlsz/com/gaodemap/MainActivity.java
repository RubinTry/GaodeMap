package logcat.wzlsz.com.gaodemap;


import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapOptions;
import com.amap.api.maps.MapView;

import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import logcat.wzlsz.com.gaodemap.Listener.MyPoiSearchListener;
import logcat.wzlsz.com.gaodemap.Util.SHA1Util;

public class MainActivity extends AppCompatActivity implements  View.OnClickListener, PoiSearch.OnPoiSearchListener {
    //声明mlocationClient对象
    public AMapLocationClient mlocationClient;
    public AMapLocationListener aMapLocationListener;
    //声明mLocationOption对象
    public AMapLocationClientOption mLocationOption = null;
    private MapView mapView;
    private AMap aMap = null;

    private Button navMap,nightMap,dayMap,starMap;
    private UiSettings mUisettings;
    private LatLng latLng;
    private List<LatLng> latLngs;
    private Polyline polyline;

    private PoiSearch poiSearch;


    private Marker marker;
    private MainActivity activity;
    private TextView infoTitle,infoSnippet;
    private EditText input_search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_main);
        activity=this;

        mapView=findViewById(R.id.mapview);
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，创建地图
        mapView .onCreate(savedInstanceState);
        if (aMap == null) {
            aMap = mapView.getMap();
            aMap.showIndoorMap(true);
        }

        initView();
        setOnclick();

        initLocationClient();

        initLocationStyle();
        drawMarker();
//        drawLine();


    }
    public void drawMarker(){
        //以公司所在地址为例
        latLng = new LatLng(29.980385,120.616301);
        final MarkerOptions markerOptions = new MarkerOptions();

        markerOptions.position(latLng);
        markerOptions.title("华航信");
        markerOptions.snippet("我所在的公司");
        marker = aMap.addMarker(markerOptions);
        Log.d("tag", "Marker: "+marker);
        AMap.OnMarkerClickListener markerClickListener = new AMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
//                marker.showInfoWindow();
                Log.d("tag", "title: "+markerOptions.getTitle()+"  snippet:"+markerOptions.getSnippet());
                return false;
            }
        };
        aMap.setOnMarkerClickListener(markerClickListener);
        aMap.setInfoWindowAdapter(new AMap.InfoWindowAdapter() {
            View infoWindow = null;
            @Override
            public View getInfoWindow(Marker marker) {
                if( infoWindow ==   null ){
                    infoWindow = LayoutInflater.from(activity).inflate(R.layout.custom_info_window,null);
                    infoTitle=infoWindow.findViewById(R.id.infoTitle);
                    infoSnippet=infoWindow.findViewById(R.id.infoSnippet);
                    infoTitle.setText(markerOptions.getTitle());
                    infoSnippet.setText(markerOptions.getSnippet());
                }
                render(marker , infoWindow);
                return infoWindow;
            }

            @Override
            public View getInfoContents(Marker marker) {
                return null;
            }

            public void render(Marker marker , View view){

            }
        });
        aMap.setOnInfoWindowClickListener(new AMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                marker.hideInfoWindow();
            }
        });
    }

    public void drawLine(){
        latLngs = new ArrayList<LatLng>();
        latLngs.add(new LatLng(29.980373,120.616277));
        latLngs.add(new LatLng(30.000000,120.513250));
        latLngs.add(new LatLng(30.123200,121.002533));
        latLngs.add(new LatLng(30.302501,121.325600));
        polyline = aMap.addPolyline(new PolylineOptions().addAll(latLngs).width(10).color(Color.RED));
        Log.d("tag", "Polyline: "+polyline);
        Log.d("tag", "drawLine: ");

    }


    public void initView(){

        navMap=findViewById(R.id.navMap);
        nightMap=findViewById(R.id.nightMap);
        dayMap=findViewById(R.id.dayMap);
        starMap=findViewById(R.id.starMap);
        input_search=findViewById(R.id.input_search);

        //监听文字内容变化
        input_search.addTextChangedListener(changeListener);

    }TextWatcher changeListener = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            Log.d("tag", "afterTextChanged: ");
            PoiSearch.Query query = new PoiSearch.Query(input_search.getText().toString(),"");
            query.setPageSize(10);
            query.setPageNum(1);
            poiSearch = new PoiSearch(activity,query);
            poiSearch.setOnPoiSearchListener(activity);
            poiSearch.searchPOIAsyn();
        }
    };

    public void setOnclick(){
        navMap.setOnClickListener(this);
        nightMap.setOnClickListener(this);
        dayMap.setOnClickListener(this);
        starMap.setOnClickListener(this);
    }

    public void initLocationClient(){
        mlocationClient = new AMapLocationClient(getApplicationContext());
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
    public void initLocationStyle(){
        MyLocationStyle myLocationStyle;
        myLocationStyle = new MyLocationStyle();
        myLocationStyle.interval(5000);
        aMap.setMyLocationStyle(myLocationStyle);
        aMap.setMyLocationEnabled(true);

        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_FOLLOW_NO_CENTER) ;
        myLocationStyle.showMyLocation(true);//设置是否显示定位小蓝点，用于满足只想使用定位，不想使用定位小蓝点的场景，设置false以后图面上不再有定位蓝点的概念，但是会持续回调位置信息。

        mUisettings = aMap.getUiSettings();
        mUisettings.setZoomControlsEnabled(false);
        mUisettings.setCompassEnabled(true);
        mUisettings.setMyLocationButtonEnabled(true);
        mUisettings.setScaleControlsEnabled(true);
        mUisettings.setRotateGesturesEnabled(false);

        Log.d("tag", "logoPosition: "+AMapOptions.LOGO_POSITION_BOTTOM_CENTER);
        mUisettings.setLogoPosition(AMapOptions.LOGO_POSITION_BOTTOM_CENTER);
        Log.d("tag", "initLocationStyle: ");
    }




    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        if(null != mlocationClient){
            mlocationClient.onDestroy();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }



    @Override
    public void onClick(View view) {
         switch (view.getId()){
             case R.id.navMap:
                 aMap.setMapType(AMap.MAP_TYPE_NAVI);
                 break;
             case R.id.nightMap:
                 aMap.setMapType(AMap.MAP_TYPE_NIGHT);
                 break;
             case R.id.dayMap:
                 aMap.setMapType(AMap.MAP_TYPE_NORMAL);
                 break;
             case R.id.starMap:
                 aMap.setMapType(AMap.MAP_TYPE_SATELLITE);
                 break;
         }
    }

    @Override
    public void onPoiSearched(PoiResult poiResult, int i) {
        Log.d("tag", "result.getPois: "+poiResult.getPois().get(1).getTitle());

    }

    @Override
    public void onPoiItemSearched(PoiItem poiItem, int i) {

    }
}
