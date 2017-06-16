package com.example.ihc.proto_odroid_new;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.skp.Tmap.TMapGpsManager;
import com.skp.Tmap.TMapMarkerItem;
import com.skp.Tmap.TMapPoint;
import com.skp.Tmap.TMapView;

public class AlarmDetailActivity extends Activity implements TMapGpsManager.onLocationChangedCallback {
    private LatLng startingPoint = new LatLng(37.3402850, 126.7335080);
    private MapFragment gmapFragment;
    private int DEFAULT_ZOOM_LEVEL = 18;
    private TMapView mMapView = null;
    private double targ_latitude;
    private double targ_longitude;
    private double dev_latitude;
    private double dev_longitude;
    private TMapPoint dev_point = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_detail);
        mMapView = (TMapView)findViewById(R.id.tmap);
        mMapView.setSKPMapApiKey(getResources().getString(R.string.tmap_appkey));

        //경보발생구역 위도 경도 intent로부터 가져오기
        targ_latitude = getIntent().getExtras().getDouble("targ_latitude");
        targ_longitude = getIntent().getExtras().getDouble("targ_longitude");
        dev_latitude = getIntent().getExtras().getDouble("dev_latitude");
        dev_longitude = getIntent().getExtras().getDouble("dev_longitude");

        //지도를 디바이스의 방향에 따라 움직이는 나침반 모드로 변경
        mMapView.setCompassMode(true);
        //맵 한단계 확대
        mMapView.MapZoomIn();

        //보드,디바이스 마커 객체 생성
        TMapPoint targ_point = new TMapPoint(targ_latitude,targ_longitude);
        dev_point = new TMapPoint(dev_latitude,dev_longitude);
        TMapMarkerItem targ_marker = new TMapMarkerItem();
        TMapMarkerItem dev_marker = new TMapMarkerItem();

        //보드,디바이스 마커에 좌표설정
        targ_marker.setTMapPoint(targ_point);
        dev_marker.setTMapPoint(dev_point);

        //보드,디바이스 마커에 이미지설정
        targ_marker.setIcon(BitmapFactory.decodeResource(getResources(),R.drawable.icon_caution));
        dev_marker.setIcon(BitmapFactory.decodeResource(getResources(),R.drawable.icon_car));

        mMapView.addMarkerItem("위험발생",targ_marker);
        mMapView.addMarkerItem("현재위치",dev_marker);

        //디바이스의 위치를 맵 중앙으로 설정
        mMapView.setCenterPoint(dev_point.getLongitude(),dev_point.getLatitude());

//        //경보 종류에 따른 경보발생구역 이미지 설정
//        if(getIntent().getExtras().getString("alert").equals("default") || getIntent().getExtras().getString("alert").equals("dangerous"))
//            targOpt.icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("icon_caution",(int)width/10,(int)height/18)));
//        else if(getIntent().getExtras().getString("alert").equals("caution"))
//            targOpt.icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("icon_warning",(int)width/10,(int)height/18)));
//        targOpt.position(new LatLng(targ_latitude,targ_longitude));
//
//        devOpt.icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("icon_car",(int)width/8,(int)height/24)));
//        devOpt.position(new LatLng(dev_latitude,dev_longitude));
//
//        //마커에 표시할 타이틀 입력
//        targOpt.title("위험발생구역");
//        targOpt.snippet("어린이 차도 횡단 중");
//        devOpt.title("내 위치");
//        devOpt.snippet("위험발생지역 200m근처");
//
//        //지도에 마커 추가 및 표시
//
//
//
//        //카메라 중심 좌표 설정( 경보구역으로)
//        builder.target(calcMid(new LatLng(dev_latitude,dev_longitude),new LatLng(targ_latitude,targ_longitude)));
//        //해당 설정값을 지도에 적용
//        CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(builder.build());
//        googleMap.moveCamera(cameraUpdate);
    }

    @Override
    public void onLocationChange(Location location) {
        //마커에 연동된 tmappoint 객체에 바뀐 경도와 위도를 설정한다.
        dev_point.setLatitude(location.getLatitude());
        dev_point.setLongitude(location.getLongitude());
        //현재위치로 맵의 중앙을 설정해줌
        mMapView.setCenterPoint(dev_point.getLongitude(),dev_point.getLatitude());
    }

}
