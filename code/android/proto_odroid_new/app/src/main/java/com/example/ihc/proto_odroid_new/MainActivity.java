package com.example.ihc.proto_odroid_new;

import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.Toast;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.RequestResult;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks, LocationListener {
    protected GoogleMap map;
    protected LatLng start;
    protected LatLng end;
    @BindView(R.id.start)
    AutoCompleteTextView starting;
    @BindView(R.id.destination)
    AutoCompleteTextView destination;
    @BindView(R.id.send)
    ImageView send;
    private static final String LOG_TAG = "MainActivity";
    protected GoogleApiClient mGoogleApiClient;
    private ProgressDialog progressDialog;
    private List<Polyline> polylines;
    private static final int[] COLORS = new int[]{R.color.primary_dark, R.color.primary, R.color.primary_light, R.color.accent, R.color.primary_dark_material_light};

    /**
     * This activity loads a map and then displays the route and pushpins on it.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);

        polylines = new ArrayList<>();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        MapsInitializer.initialize(this);
        mGoogleApiClient.connect();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        if (mapFragment == null) {
            mapFragment = SupportMapFragment.newInstance();
            getSupportFragmentManager().beginTransaction().replace(R.id.map, mapFragment).commit();
        }
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                Log.d(LOG_TAG, "맵 불러오기");
//                googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                googleMap.setTrafficEnabled(true);
                googleMap.setIndoorEnabled(true);
                googleMap.setBuildingsEnabled(true);
                googleMap.getUiSettings().setZoomControlsEnabled(true);
                map = googleMap;


                Log.d(LOG_TAG, "맵 이동");
                double latitude = 37.339898;
                double longitude = 126.734769;

                LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                if(ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        || ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    Log.d(LOG_TAG, "현재위치 불러오기");
                    List<String> providers = locationManager.getProviders(true);
                    Location bestLocation = null;
                    for (String provider : providers) {
                        Location loc = locationManager.getLastKnownLocation(provider);
                        if( loc == null ) continue;
                        if( bestLocation == null || loc.getAccuracy() < bestLocation.getAccuracy() ) {
                            bestLocation = loc;
                        }
                    }
                    Log.d("현재 latitude", String.valueOf(bestLocation.getLatitude()));
                    Log.d("현재 longitude", String.valueOf(bestLocation.getLongitude()) );
                    latitude = bestLocation.getLatitude();
                    longitude = bestLocation.getLongitude();
                    Log.d(LOG_TAG, "현재위치 불러오기 완료");
                }

                CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(latitude, longitude));
                CameraUpdate zoom = CameraUpdateFactory.zoomTo(16);

                map.moveCamera(center);
                map.animateCamera(zoom);
                Log.d(LOG_TAG, "맵 준비 완료");
            }
        });



        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 0, this);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 0, this);
    }

    @OnClick(R.id.send)
    public void sendRequest()
    {
        if(CheckOnline.isOnline(this)) {
            start = getLocationFromAddress(starting.getText().toString());
            end = getLocationFromAddress(destination.getText().toString());
            route();
        }
        else {
            Toast.makeText(this,"No internet connectivity",Toast.LENGTH_SHORT).show();
        }
    }


    private LatLng getLocationFromAddress(String strAddress){
        Geocoder geocoder = new Geocoder(this);
        List<Address> addresses;
        LatLng latLng = null;
        try {
            addresses = geocoder.getFromLocationName(strAddress, 1);
            if(addresses.size() > 0)
                latLng = new LatLng(addresses.get(0).getLatitude(), addresses.get(0).getLongitude());
            Log.d("길찾기-위도", String.valueOf(addresses.get(0).getLatitude()));
            Log.d("길찾기-경도", String.valueOf(addresses.get(0).getLongitude()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return latLng;
    }


    public void route() {
        if(start==null)
            starting.setError("출발지를 찾을 수 없습니다");
        else if(end==null)
            destination.setError("목적지를 찾을 수 없습니다");
        else {
            progressDialog = ProgressDialog.show(this, "Please wait.",
                    "Fetching route information.", true);
            GoogleDirection.withServerKey("AIzaSyBybudInt6teU6vHcNzXUwcQTvfjnFudlQ")
                    .from(start)
                    .to(end)
                    .execute(new DirectionCallback() {
                        @Override
                        public void onDirectionSuccess(Direction direction, String rawBody) {
                            progressDialog.dismiss();
                            CameraUpdate center = CameraUpdateFactory.newLatLng(start);
                            map.moveCamera(center);

                            if(direction.isOK()) {
                                onRoutingSuccess(direction.getRouteList());
                            } else {
                                String resultStatus = direction.getStatus();
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
                        }

                        @Override
                        public void onDirectionFailure(Throwable t) {
                            Toast.makeText(getApplicationContext(),"Direction Failure",Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }


    public void onRoutingSuccess(List<Route> route) {

        if(polylines.size()>0) {
            for (Polyline poly : polylines) {
                poly.remove();
            }
        }

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

            Toast.makeText(getApplicationContext(),route.get(i).getSummary(),Toast.LENGTH_SHORT).show();
        }

        // Start marker
        MarkerOptions options = new MarkerOptions();
        options.position(start);
        options.icon(BitmapDescriptorFactory.fromResource(R.drawable.start_blue));
        map.addMarker(options);

        // End marker
        options = new MarkerOptions();
        options.position(end);
        options.icon(BitmapDescriptorFactory.fromResource(R.drawable.end_green));
        map.addMarker(options);

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude()));
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(16);

        map.moveCamera(center);
        map.animateCamera(zoom);

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