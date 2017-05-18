package com.example.ihc.proto_odroid_new;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.ListView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;

public class AlarmDetailActivity extends FragmentActivity implements OnMapReadyCallback {
    private LatLng startingPoint = new LatLng(37.3402850, 126.7335080);
    private GoogleMap gMap;
    private MapFragment gmapFragment;
    private int DEFAULT_ZOOM_LEVEL = 18;
    private ListView historyListView;

    //데이터베이스
//    AlertHistoryDBHelper database = new ;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_detail);
        new GpsInfo().requestPermission(this);
        //fcm푸시메세지 topic설정. 플래그개념. 서버에서 전체 전송할 때, 내부적으로 이 설정값에 따라 받을지 말지 결정(추측)
        FirebaseMessaging.getInstance().subscribeToTopic("alert");
//        historyListView = (ListView)findViewById(R.id.history_listview);

        //알림을 눌러서 액티비티에 접근한 경우
        if(getIntent().hasCategory("noti")){
            Log.d("noti메인액티비티","온크리에이트");
            AlertInfo a = new AlertInfo();
            a.setTarg_latitude(getIntent().getExtras().getDouble("targ_latitude"));
            a.setTarg_longitude(getIntent().getExtras().getDouble("targ_longitude"));
            a.setAddress(getIntent().getExtras().getString("address"));
            a.setTime(getIntent().getExtras().getString("time"));
            a.setMessage(getIntent().getExtras().getString("alert"));
            ArrayList<AlertInfo> list = new ArrayList<>();
/*            list.add(a);
            historyListView.setAdapter(new myAdapter(list));*/

        }else{
            Log.d("normal메인액티비티","온크리에이트");
            AlertInfo a = new AlertInfo();
            a.setTarg_latitude(37.3397711);
            a.setTarg_longitude(126.7347297);
            a.setAddress("경기도 시흥시 정왕동 한국산업기술대학교 tip기숙사 1201호");
            a.setTime("2017/04/12 15:30");
            a.setMessage("default");
            ArrayList<AlertInfo> list = new ArrayList<>();
            list.add(a);
            list.add(a);
            list.add(a);
            list.add(a);
            list.add(a);
            list.add(a);
            list.add(a);
 /*           historyListView.setAdapter(new myAdapter(list));*/
        }

        //구글맵준비
        gmapFragment = (MapFragment)getFragmentManager().findFragmentById(R.id.map);
        gmapFragment.getMapAsync(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("메인액티비티","스탑");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("메인액티비티","디스트로이");
    }



    /**
     * OnMapReady 는 map이 사용가능하면 호출되는 콜백 메소드
     * 여기서 marker 나 line, listener, camera 이동 등을 설정해두면 됩니다.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;
        //지도셋팅값( 기본값 )
        CameraPosition.Builder builder = new CameraPosition.Builder();
        builder.zoom(DEFAULT_ZOOM_LEVEL); //줌 설정
        builder.bearing(300); //회전 각도 설정
        builder.tilt(50);  //바라보는 기울기


        //지도프래그먼트의 가로세로크기구하기(모든 디바이스에 비례하여 마커크기를 지정하기 위함)
        double width = gmapFragment.getActivity().getWindowManager().getDefaultDisplay().getWidth();
        double height = gmapFragment.getActivity().getWindowManager().getDefaultDisplay().getHeight();


        if(getIntent().hasCategory("noti")){
            Log.d("noti온맵레디","실행");
            Log.d("targ_latitude의값: ",String.valueOf(getIntent().getExtras().getDouble("targ_latitude")));
            //경보발생구역 위도 경도.
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





            //경보발생구역으로 지도 포커스 이동
//            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(targ_latitude,targ_longitude),DEFAULT_ZOOM_LEVEL));
            //카메라 중심 좌표 설정( 경보구역으로)
            builder.target(calcMid(new LatLng(dev_latitude,dev_longitude),new LatLng(targ_latitude,targ_longitude)));
            //해당 설정값을 지도에 적용
            CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(builder.build());
            googleMap.moveCamera(cameraUpdate);

            return;
        }

//        Location mLocation = new GpsInfo(this).getLocationInService();
//        if(mLocation != null){
//            Log.d("normal온맵레디","로케이션 있을때");
//            //마커옵션에 경보발생구역, 현재위치 설정
//            MarkerOptions curOpt = new MarkerOptions();
//            curOpt.position(new LatLng(mLocation.getLatitude(), mLocation.getLongitude()));
//            //마커에 표시할 타이틀 입력
//            curOpt.title("내 위치");
////            curOpt.icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_car));
//            curOpt.icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("icon_car",(int)width/8,(int)height/24)));
//            //지도에 마커 추가 및 표시
//            googleMap.addMarker(curOpt).showInfoWindow();
////            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLocation.getLatitude(), mLocation.getLongitude()), DEFAULT_ZOOM_LEVEL));
//            //카메라 중심 좌표 설정( 경보구역으로)
//            builder.target(new LatLng(mLocation.getLatitude(), mLocation.getLongitude()));
//            //해당 설정값을 지도에 적용
//            CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(builder.build());
//            googleMap.moveCamera(cameraUpdate);
//        }
//        else {
//            Log.d("normal온맵레디","로케이션 없을때");
//            //마커옵션에 경보발생구역, 현재위치 설정
//            MarkerOptions curOpt = new MarkerOptions();
//            curOpt.position(startingPoint);
//            //마커에 표시할 타이틀 입력
//            curOpt.title("기준위치!");
//            //지도에 마커 추가 및 표시
//            googleMap.addMarker(curOpt).showInfoWindow();
//            //카메라 중심 좌표 설정( 경보구역으로)
//            builder.target(startingPoint);
//            //해당 설정값을 지도에 적용
//            CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(builder.build());
//            googleMap.moveCamera(cameraUpdate);
////            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(startingPoint, DEFAULT_ZOOM_LEVEL));
//        }


    }
    public Bitmap resizeMapIcons(String iconName, int width, int height){
        Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(),getResources().getIdentifier(iconName, "drawable", getPackageName()));
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, width, height, false);
        return resizedBitmap;
    }

    private LatLng calcMid(LatLng a,LatLng b){
        return new LatLng((Math.max(a.latitude,b.latitude) - Math.min(a.latitude,b.latitude)) / 2 + Math.min(a.latitude,b.latitude),
                (Math.max(a.longitude,b.longitude) - Math.min(a.longitude,b.longitude)) / 2 + Math.min(a.longitude,b.longitude));
    }


//    //권한체크 후 호출되는 콜백 메소드
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        //권한 코드 확인
//        if(requestCode == pmManager.GPS_PERMISSION_REQUESTCODE){
//            //첫번째 권한(ACCESS_FINE_LOCATION)이 허용되었다면
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                Log.d("권한요청성공","권한요청성공");
//            }else {
//                Log.d("권한요청실패","권한요청실패");
//                //권한이 거절되었을 때.
//                //우리 어플에서 ACCESS_FIND_LOCATION권한이 거절되면 서비스를 이용할 수 없으므로 꼭 받아야한다.
//                //무조건 권한 설정을 해야 이용할 수 있게, 여기서는 어플을 종료시킨다.
//                AlertDialog.Builder resultDialog = new AlertDialog.Builder(this);
//                resultDialog.setMessage("서비스를 이용하려면 gps권한을 꼭 허용해야합니다.\n앱을 종료합니다.");
//                resultDialog.setCancelable(false);
//                resultDialog.setPositiveButton("확인",
//                        new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                finish();
//                            }
//                        });
//                final AlertDialog alert =resultDialog.create();
//                // Icon for AlertDialog
//                alert.show();
//            }
//        }
//    }

  /*  private class myAdapter extends BaseAdapter {
        ArrayList<AlertInfo> items = new ArrayList<>();

        // 리스트 뷰가 자동으로 호출할 함수들
        public myAdapter(ArrayList<AlertInfo> datas){
            this.items = datas;
        }

        @Override
        // 리스트 뷰가 어댑터에게 데이터 몇 개 가지고 있니? 물어보는 함수
        public int getCount() {
            return items.size();
        }


        @Override
        // 넘겨 받은 index의 값을 리턴
        public Object getItem(int position) {
            return items.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        // 각각의 아이템을 위한 뷰를 생성한다
        // convertView 리스트 뷰 용량 너무 많아지면 메모리 폭발 -> 아이템 재사용
        // 5. 뷰그룹을 만들기 위해 singer_item.xml 생성
        public View getView(int position, View convertView, ViewGroup parent) {
            final int pos = position;
            final Context context = parent.getContext();

            // "listview_item" Layout을 inflate하여 convertView 참조 획득.
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.history_listview_item_design, parent, false);
            }

            // 화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
            ImageView iconImageView = (ImageView) convertView.findViewById(R.id.kindImage) ;
            TextView time = (TextView) convertView.findViewById(R.id.time) ;
            TextView address = (TextView) convertView.findViewById(R.id.address) ;

            // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
            AlertInfo item = items.get(position);

            Log.d("아이템개수:",String.valueOf(items.size()));
            // 아이템 내 각 위젯에 데이터 반영
            if(item != null && (item.getMessage().equals("default") || item.getMessage().equals("dangerous")))
                iconImageView.setImageResource(R.drawable.warning);
            if(item != null && item.getMessage().equals("icon_caution"))
                iconImageView.setImageResource(R.drawable.icon_caution);

            time.setText(item.getTime());
            address.setText(item.getAddress());

            return convertView;
        }
    }*/
}
