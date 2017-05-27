package com.example.ihc.proto_odroid_new;

import android.app.ProgressDialog;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.Toast;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.Language;
import com.akexorcist.googledirection.constant.RequestResult;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Leg;
import com.akexorcist.googledirection.model.Route;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnEditorAction;

public class MainActivity extends AppCompatActivity implements LocationListener {
    // DEFAULT DATA
    private static final String LOG_TAG = "MainActivity";
    private final int DEFAULT_ZOOM_LEVEL = 18, DEFAULT_TILT = 50;
    private double mLatitude = 37.339898, mLongitude = 126.734769;

    // map & LatLng
    private GoogleMap map;
    private Marker curMarker;
    private LatLng start;
    private LatLng end;

    @BindView(R.id.destination)
    AutoCompleteTextView destination;
    @BindView(R.id.send)
    ImageView send;

    // draw direction
    private GoogleApiClient mGoogleApiClient;
    private ProgressDialog progressDialog;
    private List<Polyline> polylines;
    private static final int[] COLORS = new int[]{R.color.primary_dark, R.color.primary, R.color.primary_light, R.color.accent, R.color.primary_dark_material_light};



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        //퍼미션체크
        new GpsInfo().requestPermission(this);
        //fcm푸시메세지 topic설정. 서버에서 전체 어플사용자로 전송할 때, 내부적으로 이 설정값에 따라 받을지 말지 결정(추측)
        FirebaseMessaging.getInstance().subscribeToTopic("alert");

        // 경로 그리기위한 설정
        polylines = new ArrayList<>();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {

                    }

                    @Override
                    public void onConnectionSuspended(int i) {

                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                    }
                })
                .build();
        MapsInitializer.initialize(this);
        mGoogleApiClient.connect();

        // map fragment
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment == null) {
            mapFragment = SupportMapFragment.newInstance();
            getSupportFragmentManager().beginTransaction().replace(R.id.map, mapFragment).commit();
        }
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                Log.d(LOG_TAG, "Start Map Async");
//                googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                googleMap.setTrafficEnabled(true);
                googleMap.setIndoorEnabled(true);
                googleMap.setBuildingsEnabled(true);
                googleMap.getUiSettings().setZoomControlsEnabled(true);
                map = googleMap;

                if(new GpsInfo(getApplicationContext()).checkPermission()) {
                    Location location = new GpsInfo(getApplicationContext()).getLocationInService();
                    Log.d("현재 latitude", String.valueOf(location.getLatitude()));
                    Log.d("현재 longitude", String.valueOf(location.getLongitude()) );
                    mLatitude = location.getLatitude();
                    mLongitude = location.getLongitude();
                    Log.d(LOG_TAG, "현재위치 불러오기 완료");
                }

                start = new LatLng(mLatitude, mLongitude);

                //지도셋팅값( 기본값 )
                CameraPosition.Builder builder = new CameraPosition.Builder()
                        .zoom(DEFAULT_ZOOM_LEVEL)
                        .tilt(DEFAULT_TILT)
                        .target(start);
                //해당 설정값을 지도에 적용
                CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(builder.build());
                map.moveCamera(cameraUpdate);

                //현재위치 설정
                MarkerOptions curOpt = new MarkerOptions()
                        .position(start)
                        .title("현재 위치")
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_car));
                //지도에 현재위치 마커 추가 및 표시
                curMarker = map.addMarker(curOpt);
                curMarker.showInfoWindow();
                Log.d(LOG_TAG, "End Map Async");
            }
        });



        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if(new GpsInfo(getApplicationContext()).checkPermission()) {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 0, this);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 0, this);
        }
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



    private LatLng getLocationFromAddress(String strAddress){
        Geocoder geocoder = new Geocoder(this);
        List<Address> addresses;
        LatLng latLng = null;
        try {
            addresses = geocoder.getFromLocationName(strAddress, 1);
            if(addresses.size() > 0) {
                latLng = new LatLng(addresses.get(0).getLatitude(), addresses.get(0).getLongitude());
                Log.d("길찾기-위도", String.valueOf(addresses.get(0).getLatitude()));
                Log.d("길찾기-경도", String.valueOf(addresses.get(0).getLongitude()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return latLng;
    }


    public void searchRoute() {
        progressDialog = ProgressDialog.show(this, "Please wait.",
                    "Fetching route information.", true);
        GoogleDirection.withServerKey("AIzaSyADCnhnBxpgCRP3nqWZh_1XwjPyJ37ByBo")
                .from(start)
                .to(end)
//                    .transportMode(TransportMode.DRIVING) // 운전용은 구글에서 지원이 잘 안됨.
                .transportMode(TransportMode.TRANSIT)
                .language(Language.KOREAN)
                .execute(new DirectionCallback() {
                    @Override
                    public void onDirectionSuccess(Direction direction, String rawBody) {
                        progressDialog.dismiss();
                        CameraUpdate center = CameraUpdateFactory.newLatLng(start);
                        map.moveCamera(center);

                        if(direction.isOK()) {
                            onRoutingSuccess(direction.getRouteList());
                        } else {
                            onRoutingFailure(direction.getStatus());
                        }
                    }

                    @Override
                    public void onDirectionFailure(Throwable t) {
                        Toast.makeText(getApplicationContext(),"경로찾기 실패",Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void onRoutingSuccess(List<Route> route) {
        if(polylines.size()>0)
            for (Polyline poly : polylines)
                poly.remove();

        polylines = new ArrayList<>();
        //add route(s) to the map.
        for (int i = 0; i <route.size(); i++) {
            //In case of more than 5 alternative routes
            int colorIndex = i % COLORS.length;
            Leg leg = route.get(i).getLegList().get(0);

            PolylineOptions polyOptions = new PolylineOptions();
            polyOptions.color(getResources().getColor(COLORS[colorIndex]));
            polyOptions.width(10 + i * 3);
            polyOptions.addAll(leg.getDirectionPoint());
            Polyline polyline = map.addPolyline(polyOptions);
            polylines.add(polyline);
        }

        // End marker
        MarkerOptions options = new MarkerOptions();
        options.position(end);
        options.icon(BitmapDescriptorFactory.fromResource(R.drawable.end_green));
        map.addMarker(options);
    }


    private void onRoutingFailure(String resultStatus) {
        // 에러 종류 출력
        if( resultStatus.equals(RequestResult.NOT_FOUND) )
            Toast.makeText(getApplicationContext(),"NOT_FOUND",Toast.LENGTH_SHORT).show();
        else if( resultStatus.equals(RequestResult.ZERO_RESULTS) )
            Toast.makeText(getApplicationContext(),"ZERO_RESULTS",Toast.LENGTH_SHORT).show();
        else if( resultStatus.equals(RequestResult.MAX_WAYPOINTS_EXCEEDED) )
            Toast.makeText(getApplicationContext(),"MAX_WAYPOINTS_EXCEEDED",Toast.LENGTH_SHORT).show();
        else if( resultStatus.equals(RequestResult.INVALID_REQUEST) )
            Toast.makeText(getApplicationContext(),"INVALID_REQUEST",Toast.LENGTH_SHORT).show();
        else if( resultStatus.equals(RequestResult.OVER_QUERY_LIMIT) )
            Toast.makeText(getApplicationContext(),"OVER_QUERY_LIMIT",Toast.LENGTH_SHORT).show();
        else if( resultStatus.equals(RequestResult.REQUEST_DENIED) )
            Toast.makeText(getApplicationContext(),"REQUEST_DENIED",Toast.LENGTH_SHORT).show();
        else if( resultStatus.equals(RequestResult.UNKNOWN_ERROR) )
            Toast.makeText(getApplicationContext(),"UNKNOWN_ERROR",Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(getApplicationContext(),"Not OK",Toast.LENGTH_SHORT).show();

    }


    @Override
    public void onLocationChanged(Location location) {
        start = new LatLng(location.getLatitude(), location.getLongitude());
        map.moveCamera(CameraUpdateFactory.newLatLng(start));
        curMarker.setPosition(start);
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
}