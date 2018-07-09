package logcat.wzlsz.com.gaodemap.Map;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;

import logcat.wzlsz.com.gaodemap.MainActivity;
import logcat.wzlsz.com.gaodemap.R;

/**
 * Created by Administrator on 2018/7/9.
 */

public class DrawMarker {
    private static LatLng latLng;
    private static Marker marker;
    private static TextView infoTitle,infoSnippet;
    public static void draw(){
        latLng = new LatLng(29.980385,120.616301);
        final MarkerOptions markerOptions = new MarkerOptions();

        markerOptions.position(latLng);
        markerOptions.title("华航信");
        markerOptions.snippet("我所在的公司");
        marker = MainActivity.aMap.addMarker(markerOptions);
        Log.d("tag", "Marker: "+marker);
        AMap.OnMarkerClickListener markerClickListener = new AMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
//                marker.showInfoWindow();
                Log.d("tag", "title: "+markerOptions.getTitle()+"  snippet:"+markerOptions.getSnippet());
                return false;
            }
        };
        MainActivity.aMap.setOnMarkerClickListener(markerClickListener);
        MainActivity.aMap.setInfoWindowAdapter(new AMap.InfoWindowAdapter() {
            View infoWindow = null;
            @Override
            public View getInfoWindow(Marker marker) {
                if( infoWindow ==   null ){
                    infoWindow = LayoutInflater.from(MainActivity.activity).inflate(R.layout.custom_info_window,null);
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
        MainActivity.aMap.setOnInfoWindowClickListener(new AMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                marker.hideInfoWindow();
            }
        });
    }
}
