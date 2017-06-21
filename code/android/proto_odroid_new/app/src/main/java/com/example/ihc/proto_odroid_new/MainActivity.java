package com.example.ihc.proto_odroid_new;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.messaging.FirebaseMessaging;
import com.skp.Tmap.TMapData;
import com.skp.Tmap.TMapGpsManager;
import com.skp.Tmap.TMapMarkerItem;
import com.skp.Tmap.TMapPoint;
import com.skp.Tmap.TMapPolyLine;
import com.skp.Tmap.TMapView;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnEditorAction;

import static com.example.ihc.proto_odroid_new.FirebaseMessagingService.ALERT_DISTANCE;
import static com.example.ihc.proto_odroid_new.FirebaseMessagingService.distance;
import static com.example.ihc.proto_odroid_new.FirebaseMessagingService.warning;

public class MainActivity extends AppCompatActivity implements TMapGpsManager.onLocationChangedCallback, LocationListener {
    // DEFAULT DATA
    private static final String LOG_TAG = "MainActivity";
    private final double DEFAULT_LATITUDE = 37.339898;
    private final double DEFAULT_LONGITUDE = 126.734769;

    // map & LatLng
    private TMapPoint curPoint = new TMapPoint(DEFAULT_LATITUDE, DEFAULT_LONGITUDE);
    private TMapPoint destPoint;
    private TMapMarkerItem curMarker = new TMapMarkerItem();
    private TMapMarkerItem alertMarker = new TMapMarkerItem();
    private BroadcastReceiver alertReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            showAlertMarker();
        }
    };

    // Medias
    private MediaPlayer cautionMedia;
    private MediaPlayer dangerousMedia;

    // Bitmaps
    private Bitmap markerDangerousImage;
    private Bitmap markerCautionImage;
    private AlertSituation curSituation = AlertSituation.SAFETY;

    // Views
    @BindView(R.id.loading_indicator) LinearLayout loading_screen;
    @BindView(R.id.destination) AutoCompleteTextView destination;
    @BindView(R.id.send) ImageView send;
    @BindView(R.id.tmap) TMapView mMapView;
    @BindView(R.id.sign_img) ImageView signImageView;
    @BindView(R.id.sign_text) TextView signTextView;

    // draw direction
    private Geocoder geocoder;
    private ProgressDialog progressDialog;
    private TMapPolyLine polyline;

    @Override
    protected void onStart() {
        super.onStart();
        // 로드 화면
        new Thread() {
            @Override
            public void run() {
                try {
                    super.run();
                    sleep(3000);
                } catch (Exception e) {

                } finally {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            loading_screen.setVisibility(View.GONE);
                        }
                    });
                }
            }
        }.start();

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        // 음성파일 준비
        cautionMedia = MediaPlayer.create(this, R.raw.cautionmp3);
        dangerousMedia = MediaPlayer.create(this, R.raw.warinngmp3);

        //퍼미션체크
        new GpsInfo().requestPermission(this);
        //fcm푸시메세지 topic설정. 서버에서 전체 어플사용자로 전송할 때, 내부적으로 이 설정값에 따라 받을지 말지 결정(추측)
        FirebaseMessaging.getInstance().subscribeToTopic("alert");

        // 이미지 준비
        Glide.with(this).load(R.raw.sign_safe).into(signImageView);
        markerDangerousImage = BitmapFactory.decodeResource(getResources(), R.drawable.icon_warning);
        markerCautionImage = BitmapFactory.decodeResource(getResources(), R.drawable.icon_caution);

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
        mMapView.setMarkerRotate(true);

        if(new GpsInfo(getApplicationContext()).checkPermission() &&  new GpsInfo(getApplicationContext()).isGetLocation(MainActivity.this)) {
            Location location = new GpsInfo(getApplicationContext()).getLocationInService();
            Log.d("현재 latitude", String.valueOf(location.getLatitude()));
            Log.d("현재 longitude", String.valueOf(location.getLongitude()) );
            curPoint.setLatitude(location.getLatitude());
            curPoint.setLongitude(location.getLongitude());
        }

        // 마커
        curMarker.setTMapPoint(curPoint);
        curMarker.setIcon(BitmapFactory.decodeResource(getResources(),R.drawable.icon_car));
        mMapView.addMarkerItem("현재위치", curMarker);
        mMapView.addMarkerItem("위험위치", alertMarker);
        mMapView.setCenterPoint(curPoint.getLongitude(), curPoint.getLatitude());

        // 경로
        geocoder = new Geocoder(this);
        registerReceiver(alertReceiver, new IntentFilter(FirebaseMessagingService.SHOW_ALERT_SIGN));
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if(new GpsInfo(getApplicationContext()).checkPermission()) {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 0, this);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 0, this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(alertReceiver);
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
            destPoint = getLocationFromAddress(destination.getText().toString());

            if(destPoint == null) destination.setError("목적지를 찾을 수 없습니다");
            else searchRoute();
        }
        else {
            Toast.makeText(this,"인터넷 연결 상태를 확인해주세요.",Toast.LENGTH_SHORT).show();
        }
    }



    private TMapPoint getLocationFromAddress(String strAddress){
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

    private String getAddressFromLocation(double latitude, double longitude) {
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());
        String address = "위치정보를 찾을 수 없음";

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
            address = addresses.get(0).getAddressLine(0);
//            String city = addresses.get(0).getLocality();
//            String state = addresses.get(0).getAdminArea();
//            String country = addresses.get(0).getCountryName();
//            String postalCode = addresses.get(0).getPostalCode();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return address;
    }


    private void searchRoute() {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(destination.getWindowToken(), 0);
        progressDialog = ProgressDialog.show(this, "Please wait.",
                "Fetching route information.", true);
        new TMapData().findPathDataWithType(TMapData.TMapPathType.CAR_PATH, curPoint, destPoint, new TMapData.FindPathDataListenerCallback() {
            @Override
            public void onFindPathData(TMapPolyLine tMapPolyLine) {
                polyline = tMapPolyLine;
                mMapView.addTMapPath(polyline);
            }
        });
        progressDialog.dismiss();
    }


    private void showAlertMarker() {
        Log.d(LOG_TAG, "showAlertMarker");

        if (warning.getSituation() == AlertSituation.DANGEROUS) {
            Glide.with(this).load(R.raw.sign_dangerous).into(signImageView);
            signTextView.setText("주변도로상황 : 위험");
            mMapView.getMarkerItemFromID("위험위치").setIcon(markerDangerousImage);
            dangerousMedia.start();
            curSituation = AlertSituation.DANGEROUS;
        } else if(warning.getSituation() == AlertSituation.CAUTION) {
            Glide.with(this).load(R.raw.sign_caution).into(signImageView);
            signTextView.setText("주변도로상황 : 주의");
            mMapView.getMarkerItemFromID("위험위치").setIcon(markerCautionImage);
            cautionMedia.start();
            curSituation = AlertSituation.CAUTION;
        }
        mMapView.getMarkerItemFromID("위험위치").setTMapPoint(new TMapPoint(warning.getTarg_latitude(), warning.getTarg_longitude()));
        mMapView.setCenterPoint(curPoint.getLongitude(), curPoint.getLatitude());
    }


    @Override
    public void onLocationChange(Location location) {
        OnLocChange(location);
        mMapView.setCenterPoint(curPoint.getLongitude(), curPoint.getLatitude());
    }

    @Override
    public void onLocationChanged(Location location) {
        OnLocChange(location);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    private void OnLocChange(Location location) {
        Log.d(LOG_TAG, "위치변경됨");
        curPoint.setLatitude(location.getLatitude());
        curPoint.setLongitude(location.getLongitude());
        mMapView.getMarkerItemFromID("현재위치").setTMapPoint(curPoint);

        if ( curSituation != AlertSituation.SAFETY && (distance > ALERT_DISTANCE || distance == -1.0)) {
            Glide.with(this).load(R.raw.sign_safe).into(signImageView);
            signTextView.setText("주변도로상황 : 안전");
            curSituation = AlertSituation.SAFETY;
        }
    }
}