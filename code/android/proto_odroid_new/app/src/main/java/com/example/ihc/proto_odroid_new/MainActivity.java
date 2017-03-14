package com.example.ihc.proto_odroid_new;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback {
    private LatLng startingPoint = new LatLng(37.5, 127);
    private MapFragment gMap;
    private int DEFAULT_ZOOM_LEVEL = 18;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseMessaging.getInstance().subscribeToTopic("alert");

        gMap = (MapFragment)getFragmentManager().findFragmentById(R.id.map);
        gMap.getMapAsync(this);

        Alert aaa = new Alert();


    }

    /**
     * OnMapReady 는 map이 사용가능하면 호출되는 콜백 메소드
     * 여기서 marker 나 line, listener, camera 이동 등을 설정해두면 됩니다.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(startingPoint,DEFAULT_ZOOM_LEVEL));
        Log.d("onMapReady","called");
        if(getIntent().getExtras() != null) {
            Log.d("인텐트 존재","called");
            //경보발생구역 위도 경도.
            float latitude = getIntent().getExtras().getFloat("latitude");
            float longitude = getIntent().getExtras().getFloat("longitude");

            MarkerOptions opt = new MarkerOptions();
            opt.position(new LatLng(latitude,longitude));
            opt.title("보행자주의!");
            googleMap.addMarker(opt).showInfoWindow();

            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude,longitude),DEFAULT_ZOOM_LEVEL));
        }
    }
}
