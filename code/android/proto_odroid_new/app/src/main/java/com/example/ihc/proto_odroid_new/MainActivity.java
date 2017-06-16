package com.example.ihc.proto_odroid_new;

import android.app.ProgressDialog;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;
import com.skp.Tmap.TMapData;
import com.skp.Tmap.TMapGpsManager;
import com.skp.Tmap.TMapMarkerItem;
import com.skp.Tmap.TMapPoint;
import com.skp.Tmap.TMapPolyLine;
import com.skp.Tmap.TMapView;

import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnEditorAction;

public class MainActivity extends AppCompatActivity implements TMapGpsManager.onLocationChangedCallback {
    // DEFAULT DATA
    private static final String LOG_TAG = "MainActivity";
    private double mLatitude = 37.339898, mLongitude = 126.734769;

    // map & LatLng
    private TMapPoint start;
    private TMapPoint end;
    private TMapMarkerItem curMarker = new TMapMarkerItem();

    @BindView(R.id.tmap)
    TMapView mMapView;
    @BindView(R.id.destination)
    AutoCompleteTextView destination;
    @BindView(R.id.send)
    ImageView send;

    // draw direction
    private ProgressDialog progressDialog;
    private TMapPolyLine polyline;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        //퍼미션체크
        new GpsInfo().requestPermission(this);
        //fcm푸시메세지 topic설정. 서버에서 전체 어플사용자로 전송할 때, 내부적으로 이 설정값에 따라 받을지 말지 결정(추측)
        FirebaseMessaging.getInstance().subscribeToTopic("alert");

        // Tmap
        mMapView.setSKPMapApiKey(getResources().getString(R.string.tmap_appkey));
//        mMapView.setCompassMode(true); //지도를 디바이스의 방향에 따라 움직이는 나침반 모드로 변경
        mMapView.setIconVisibility(true);
        mMapView.MapZoomIn(); //맵 한단계 확대
        mMapView.setMapType(TMapView.MAPTYPE_STANDARD);
        mMapView.setLanguage(TMapView.LANGUAGE_KOREAN);
//        mMapView.setTrackingMode(true);
        mMapView.setSightVisible(true);
        mMapView.setTrafficInfo(true);

        if(new GpsInfo(getApplicationContext()).checkPermission() &&  new GpsInfo(getApplicationContext()).isGetLocation(MainActivity.this)) {
            Location location = new GpsInfo(getApplicationContext()).getLocationInService();
            Log.d("현재 latitude", String.valueOf(location.getLatitude()));
            Log.d("현재 longitude", String.valueOf(location.getLongitude()) );
            mLatitude = location.getLatitude();
            mLongitude = location.getLongitude();
            Log.d(LOG_TAG, "현재위치 불러오기 완료");
        }

        start = new TMapPoint(mLatitude, mLongitude);
        curMarker.setTMapPoint(start);
        curMarker.setIcon(BitmapFactory.decodeResource(getResources(),R.drawable.icon_car));
        mMapView.addMarkerItem("현재위치", curMarker);
        mMapView.setCenterPoint(mLongitude, mLatitude);

    }


    @OnEditorAction(R.id.destination)
    protected boolean commitEditText(int actionId) {
        if( actionId == EditorInfo.IME_ACTION_GO ) {
            sendRequest();
            return true;
        }
        return false;
    }


    @OnClick(R.id.send)
    protected void sendRequest()
    {
        if(CheckOnline.isOnline(this)) {
            end = getLocationFromAddress(destination.getText().toString());

            if(end == null) destination.setError("목적지를 찾을 수 없습니다");
            else searchRoute();
        }
        else {
            Toast.makeText(this,"인터넷 연결 상태를 확인해주세요.",Toast.LENGTH_SHORT).show();
        }
    }



    private TMapPoint getLocationFromAddress(String strAddress){
        Geocoder geocoder = new Geocoder(this);
        List<Address> addresses;
        TMapPoint tMapPoint = null;
        try {
            addresses = geocoder.getFromLocationName(strAddress, 1);
            if(addresses.size() > 0) {
                tMapPoint = new TMapPoint(addresses.get(0).getLatitude(), addresses.get(0).getLongitude());
                Log.d("길찾기-위도", String.valueOf(addresses.get(0).getLatitude()));
                Log.d("길찾기-경도", String.valueOf(addresses.get(0).getLongitude()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tMapPoint;
    }


    public void searchRoute() {
        progressDialog = ProgressDialog.show(this, "Please wait.",
                    "Fetching route information.", true);
        new TMapData().findPathDataWithType(TMapData.TMapPathType.CAR_PATH, start, end, new TMapData.FindPathDataListenerCallback() {
            @Override
            public void onFindPathData(TMapPolyLine tMapPolyLine) {
                polyline = tMapPolyLine;
                mMapView.addTMapPath(polyline);
            }
        });
        progressDialog.dismiss();
    }


    @Override
    public void onLocationChange(Location location) {
        start.setLatitude(location.getLatitude());
        start.setLongitude(location.getLongitude());
        mMapView.setCenterPoint(mLongitude, mLatitude);
        curMarker.setTMapPoint(start);
    }
}