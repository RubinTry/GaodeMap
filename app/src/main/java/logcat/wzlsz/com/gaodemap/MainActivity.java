package logcat.wzlsz.com.gaodemap;


import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import logcat.wzlsz.com.gaodemap.Map.DrawMarker;
import logcat.wzlsz.com.gaodemap.Map.Location;

public class MainActivity extends AppCompatActivity implements  View.OnClickListener, PoiSearch.OnPoiSearchListener, GeocodeSearch.OnGeocodeSearchListener {

    public AMapLocationListener aMapLocationListener;

    private MapView mapView;
    public static AMap aMap = null;

    private Button navMap,nightMap,dayMap,starMap;

    private LatLng latLng;
    private List<LatLng> latLngs;
    private Polyline polyline;



    public static MainActivity activity;
    public static EditText input_search;

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

        Location.initClient();

        Location.initLocationStyle();
        drawMarker();
//        drawLine();

    }
    public void drawMarker(){

        DrawMarker.draw();
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
        input_search.addTextChangedListener(Location.changeListener);

    }


    public void setOnclick(){
        navMap.setOnClickListener(this);
        nightMap.setOnClickListener(this);
        dayMap.setOnClickListener(this);
        starMap.setOnClickListener(this);
    }






    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        if(null != Location.mlocationClient){
            Location.mlocationClient.onDestroy();
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
        Log.d("tag", "result.getPois: "+poiResult.getPois().get(0).getTitle());
        Toast.makeText(activity, poiResult.getPois()+"", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onPoiItemSearched(PoiItem poiItem, int i) {

    }

    @Override
    public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {

    }

    @Override
    public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {
       if(i == 1000){

           //坐标  geocodeResult.getGeocodeAddressList().get(t).getLatLonPoint()   t为第几条item

           Log.d("tag", "onGeocodeSearched: "+geocodeResult.getGeocodeAddressList().get(0).getLatLonPoint());
       }
    }
}
