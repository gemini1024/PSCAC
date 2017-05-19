package com.example.ihc.proto_odroid_new;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class AlarmDetailActivity extends FragmentActivity implements OnMapReadyCallback {
    private LatLng startingPoint = new LatLng(37.3402850, 126.7335080);
    private GoogleMap gMap;
    private MapFragment gmapFragment;
    private int DEFAULT_ZOOM_LEVEL = 18;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_detail);

        //구글맵준비
        gmapFragment = (MapFragment)getFragmentManager().findFragmentById(R.id.map);
        gmapFragment.getMapAsync(this);
    }

    /**
     * OnMapReady 는 map이 사용가능하면 호출되는 콜백 메소드
     * 여기서 marker 나 line, listener, camera 이동 등을 설정해두면 됩니다.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d("onMapReady ","call");

        gMap = googleMap;
        //지도셋팅값( 기본값 )
        CameraPosition.Builder builder = new CameraPosition.Builder();
        builder.zoom(DEFAULT_ZOOM_LEVEL); //줌 설정
        builder.bearing(300); //회전 각도 설정
        builder.tilt(50);  //바라보는 기울기


        //지도프래그먼트의 가로세로크기구하기(모든 디바이스에 비례하여 마커크기를 지정하기 위함)
        double width = gmapFragment.getActivity().getWindowManager().getDefaultDisplay().getWidth();
        double height = gmapFragment.getActivity().getWindowManager().getDefaultDisplay().getHeight();


        //알림을 통해서 메인액티비티에 접근할 때,
        if(getIntent().hasCategory("noti")){
            //경보발생구역 위도 경도 intent로부터 가져오기
            double targ_latitude = getIntent().getExtras().getDouble("targ_latitude");
            double targ_longitude = getIntent().getExtras().getDouble("targ_longitude");
            double dev_latitude = getIntent().getExtras().getDouble("dev_latitude");
            double dev_longitude = getIntent().getExtras().getDouble("dev_longitude");

            //마커옵션에 경보발생구역, 현재위치 설정
            MarkerOptions targOpt = new MarkerOptions();
            MarkerOptions devOpt = new MarkerOptions();

            //경보 종류에 따른 경보발생구역 이미지 설정
            if(getIntent().getExtras().getString("alert").equals("default") || getIntent().getExtras().getString("alert").equals("dangerous"))
                targOpt.icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("icon_caution",(int)width/10,(int)height/18)));
            else if(getIntent().getExtras().getString("alert").equals("caution"))
                targOpt.icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("icon_warning",(int)width/10,(int)height/18)));
            targOpt.position(new LatLng(targ_latitude,targ_longitude));

            devOpt.icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("icon_car",(int)width/8,(int)height/24)));
            devOpt.position(new LatLng(dev_latitude,dev_longitude));

            //마커에 표시할 타이틀 입력
            targOpt.title("위험발생구역");
            targOpt.snippet("어린이 차도 횡단 중");
            devOpt.title("내 위치");
            devOpt.snippet("위험발생지역 200m근처");

            //지도에 마커 추가 및 표시
            googleMap.addMarker(devOpt).showInfoWindow();
            googleMap.addMarker(targOpt).showInfoWindow();


            //카메라 중심 좌표 설정( 경보구역으로)
            builder.target(calcMid(new LatLng(dev_latitude,dev_longitude),new LatLng(targ_latitude,targ_longitude)));
            //해당 설정값을 지도에 적용
            CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(builder.build());
            googleMap.moveCamera(cameraUpdate);
            return;
        }


//        //알림을 통하지 않고 어플을 켰을때,
//        Location mLocation = new GpsInfo(this).getLocationInService();
//        if(mLocation != null){
//            //현재 디바이스 위치가 제대로 받아와졌을때 호출됨
//
//            //마커옵션에 경보발생구역, 현재위치 설정
//            MarkerOptions curOpt = new MarkerOptions();
//            curOpt.position(new LatLng(mLocation.getLatitude(), mLocation.getLongitude()));
//
//            //현재위치 마커에 표시할 타이틀 입력
//            curOpt.title("내 위치");
//            //현재위치 아이콘 설정
//            curOpt.icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("icon_car",(int)width/8,(int)height/24)));
//            //지도에 현재위치 마커 추가 및 표시
//            googleMap.addMarker(curOpt).showInfoWindow();
//            //카메라 중심 좌표 설정( 현재위치로)
//            builder.target(new LatLng(mLocation.getLatitude(), mLocation.getLongitude()));
//            //해당 설정값을 지도에 적용
//            CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(builder.build());
//            googleMap.moveCamera(cameraUpdate);
//        }
//        else {
//            //현재 디바이스 위치가 제대로 받아지지 않았을때, 디폴트 설정으로 지도에 마커를 표시한다.
//            Log.d("normal온맵레디","로케이션 없을때");
//            //마커옵션에 경보발생구역, 현재위치 설정
//            MarkerOptions curOpt = new MarkerOptions();
//            curOpt.position(startingPoint);
//            //현재위치 마커에 표시할 타이틀 입력
//            curOpt.title("기준위치!");
//            //지도에 현재위치 마커 추가 및 표시
//            googleMap.addMarker(curOpt).showInfoWindow();
//            //카메라 중심 좌표 설정( 현재위치로)
//            builder.target(startingPoint);
//            //해당 설정값을 지도에 적용
//            CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(builder.build());
//            googleMap.moveCamera(cameraUpdate);
//        }


    }

    /**
     * @param iconName 사진이름
     * @param width 리사이즈 가로 사이즈
     * @param height 리사이즈 세로 사이즈
     * @return
     */
    public Bitmap resizeMapIcons(String iconName, int width, int height){
        Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(),getResources().getIdentifier(iconName, "drawable", getPackageName()));
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, width, height, false);
        return resizedBitmap;
    }

    /**
     * 두 좌표의 중심좌표 구하기.
     * 지도에서 현재위치와 경보구역 마커 사이에 포커스 두기 위해 이용.
     */
    private LatLng calcMid(LatLng a,LatLng b){
        return new LatLng((Math.max(a.latitude,b.latitude) - Math.min(a.latitude,b.latitude)) / 2 + Math.min(a.latitude,b.latitude),
                (Math.max(a.longitude,b.longitude) - Math.min(a.longitude,b.longitude)) / 2 + Math.min(a.longitude,b.longitude));
    }

}
