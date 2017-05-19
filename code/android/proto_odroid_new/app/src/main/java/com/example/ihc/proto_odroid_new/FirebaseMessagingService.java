package com.example.ihc.proto_odroid_new;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.messaging.RemoteMessage;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.example.ihc.proto_odroid_new.R.drawable.icon_warning;

/**
 * Created by ihc on 2017-01-19.
 * 서버에서 보내는 푸시 메세지의 형태는
 * ".-w-값-.-g-값-.-alt-값-."
 * 으로 한다.
 */
public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {
    private static final String TAG = "FirebaseMsgService";
    //최소 알람거리. 경보가 발생한 위치과 현재 디바이스의 위치사이의 거리가 '최소 알람거리' 이내라면 경보한다.
    private static final double ALERT_DISTANCE = 200;
    private AlertInfo warning = new AlertInfo();
    /**
     * fcm서버로부터 메세지가 도착하면 호출되는 메소드
     * onMessageReceived 메소드는 앱이 포그라운드에 있을때만 작동한다.
     * 백그라운드 상태에서는 디폴트 푸쉬가 뜸.(원하는 동작이 안된다.)
     * 백그라운드 상태일 때, fcm api를 직접 호출해줘야한다.
     * @param remoteMessage fcm서버로부터 받은 메세지 저장되어있음
     */
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d("onMessageReceived","call");

        //받은 메세지에서 위도,경도,위험경보 데이터를 가져온다.
        Map<String,String> msgData = remoteMessage.getData();

        //서버로 받은 데이터 중 누락된게 있는지 확인.
        //누락되었다면 뒷 작업 들어가지 않고 종료
        if(checkMissedData(msgData))  return;

        //AlertInfo객체(warning)에 데이터 set
        insertTargInfo(msgData);

        //정보로 조건을 체크하고 경보하기
        checkAndAlert(warning);

    }
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("onCreate","call");
        //디바이스 현재위치 불러오기
        getGps();
    }

    //fcm서버로받은 메세지에서 보드의 위도,경도 등의 정보를 warning객체에 채워준다.
    private void insertTargInfo(Map<String,String> msgData){
        Log.d("insertTargInfo","call");
        //위도,경도,위험메세지를 담는 AlertInfo객체를 만들고 데이터를 넣는다.
        warning.setTarg_latitude(Double.parseDouble(msgData.get("latitude")));
        warning.setTarg_longitude(Double.parseDouble(msgData.get("longitude")));
        warning.setMessage(msgData.get("alarm"));

        //경고발생시간설정
        warning.setTime(getCurTime());
        //경고발생주소입력
        warning.setAddress(getAddress(getApplicationContext(),warning.getTarg_latitude(),warning.getTarg_longitude()));
    }
    //현재 디바이스의 위치를 가져와서 warning객체에 채워준다.
    private void getGps(){
        Log.d("getGps","call");

        Location devLocation = new GpsInfo(getApplicationContext()).getLocationInService();
        warning.setDev_latitude(devLocation.getLatitude());
        warning.setDev_longitude(devLocation.getLongitude());
        Log.d("현재 latitude", String.valueOf(devLocation.getLatitude()));
        Log.d("현재 longitude", String.valueOf(devLocation.getLongitude()) );
    }


    /**
     * 서버로 부터 받은 데이터들이 제대로 들어왔는지 일일히 체크
     * 하나라도 누락되면 false반환
     * @param msgData 서버로부터 받은 경보에 대한 정보들이 담겨있는 해시맵
     * @return 누락이 되었는지 여부(누락-true, 미누락-false)
     */
    boolean checkMissedData(Map<String,String> msgData){
        if(msgData.get("latitude") == null)
            return true;
        if( msgData.get("longitude") == null)
            return true;
        if(msgData.get("alarm") == null)
            return true;
        return false;
    }

    /**현재시간 가져오기
     *
     * @return 현재시간
     */
    private String getCurTime(){
        // 현재시간을 msec 으로 구한다.
        long now = System.currentTimeMillis();
        // 현재시간을 date 변수에 저장한다.
        Date date = new Date(now);
        // 시간을 나타냇 포맷을 정한다 ( yyyy/MM/dd 같은 형태로 변형 가능 )
        SimpleDateFormat sdfNow = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        // nowDate 변수에 값을 저장한다.
        String formatDate = sdfNow.format(date);


        return formatDate;
    }


    /**
     * 위도,경도로 주소구하기
     * @param lat 위도
     * @param lng 경도
     * @return 위도,경도에 대한 주소
     */
    public static String getAddress(Context mContext,double lat, double lng) {
        String nowAddress ="현재 위치를 확인 할 수 없습니다.";
        Geocoder geocoder = new Geocoder(mContext, Locale.KOREA);
        List<Address> address;
        try {
            if (geocoder != null) {
                //세번째 파라미터는 좌표에 대해 주소를 리턴 받는 갯수로
                //한좌표에 대해 두개이상의 이름이 존재할수있기에 주소배열을 리턴받기 위해 최대갯수 설정
                address = geocoder.getFromLocation(lat, lng, 1);

                if (address != null && address.size() > 0) {
                    // 주소 받아오기
                    String currentLocationAddress = address.get(0).getAddressLine(0).toString();
                    nowAddress  = currentLocationAddress;

                }
            }

        } catch (IOException e) {
            Toast.makeText(mContext, "주소를 가져 올 수 없습니다.", Toast.LENGTH_LONG).show();

            e.printStackTrace();
        }
        return nowAddress;
    }

    /**
     * 위도,경도,위험종류값 체크해서 경보(푸시알림)해버리기
     * 성공적으로 완료되면 위도, 경도, 위험경보를 담은 AlertInfo 객체를 반환.
     * 실패하면 NULL반환
     * @param warning 경보정보가 담긴 AlertInfo 객체
     * @return
     */
    private AlertInfo checkAndAlert(AlertInfo warning) {
        Log.d("checkAndAlert","call");

        //객체가 null이면 null 반환
        if (warning == null)
            return null;

        //위치(위도,경도) 정보가 없을때 null 반환
        if (String.valueOf(warning.getTarg_latitude()).equals("") || String.valueOf(warning.getTarg_longitude()).equals(""))
            return null;
        Log.d("checkAndAlert","dataCheck");

        //거리계산
        //두 디바이스 간 거리가 설정범위보다 멀리 떨어져 있으면 알람하지 않는다.
        double distance = getDistance(warning.getTarg_latitude(), warning.getTarg_longitude(), warning.getDev_latitude(), warning.getDev_longitude(), "meter");
        Log.d("checkAndAlert ","보드위치: " + String.valueOf(warning.getTarg_latitude())+ String.valueOf(warning.getTarg_longitude()));
        Log.d("checkAndAlert ","현재위치: " + String.valueOf(warning.getDev_latitude())+String.valueOf(warning.getDev_longitude()));
        Log.d("checkAndAlert ","사이거리: " + String.valueOf(distance));
        if (distance > ALERT_DISTANCE || distance == -1.0)  return null;



        //알림을 눌렀을 때 MainActivity로 전달될 데이터 intent에 넣어주기!
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addCategory("noti");
        intent.putExtra("alert", warning.getMessage());
        intent.putExtra("targ_latitude", warning.getTarg_latitude());
        intent.putExtra("targ_longitude", warning.getTarg_longitude());
        intent.putExtra("dev_latitude",warning.getDev_latitude());
        intent.putExtra("dev_longitude",warning.getDev_longitude());
        //----------------------------------------임시코드---------------------
        intent.putExtra("time",getCurTime());
        intent.putExtra("address",getAddress(getApplicationContext(),warning.getTarg_latitude(),warning.getTarg_longitude()));
        //----------------------------------------임시코드-------------------- 나중에 listview에 표시할 주소,시간정보 표시하기위해 이용됨

        //pendingIntent에 Intent할당
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        //상태바에 표시될 알림설정하고 위에서 생성한 pendingIntent를 notificationBuilder에 넣어준다! 이후 상태바의 알림을 눌렀을때 여기에 등록된
        //pendingIntent의 정보를 참고하여 MainActivity를 실행하고 intent의 정보를 넘긴다
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(icon_warning).setLargeIcon(BitmapFactory.decodeResource(getResources(), icon_warning))
                .setContentTitle("보행자주의!")
                .setAutoCancel(true)
                .setLights(000000255, 500, 2000)
                .setContentIntent(pendingIntent);

        //경보 종류에 따른 상태바에 표시될 알림설정(푸시알림 및 소리)
        setMsgNSound(warning,notificationBuilder);

        //상태바에 알림 표시하기
        ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).notify(0 /* ID of notification */, notificationBuilder.build());

        return warning;
    }

    /**
     * 상태바에 표시될 알림표시에 대한 설정.
     * 경보 상태에 따른 음성 및 텍스트를 설정한다.
     * @param warning 경보정보가 담긴 AlertInfo객체
     * @param notificationBuilder 알림설정객체
     * */
    void setMsgNSound(AlertInfo warning, NotificationCompat.Builder notificationBuilder){
        Log.d("setMsgNSound ","call");

        //경보가 디폴트일때, 디폴트경보발생(푸시알림 및 소리)
        //경보종류 다양해 질 경우 if문으로 추가
        if (warning.getMessage().equals("default") || warning.getMessage().equals("dangerous")) {
            notificationBuilder.setContentText("주변 차도에 보행자가 있습니다!");
            notificationBuilder.setSound(Uri.parse("android.resource://com.example.ihc.proto_odroid_new/" + R.raw.warinngmp3));
        }
        if(warning.getMessage().equals("caution")){
            notificationBuilder.setContentText("주변 보행자가 차도로 접근 중입니다!");
            notificationBuilder.setSound(Uri.parse("android.resource://com.example.ihc.proto_odroid_new/" + R.raw.cautionmp3));
        }
    }



    /**
     * 두 지점간의 거리 계산
     *
     * @param lat1 지점1 위도
     * @param lon1 지점1 경도
     * @param lat2 지점2 위도
     * @param lon2 지점2 경도
     * @param unit 거리 표출단위
     * @return
     */
    private static double getDistance(double lat1, double lon1, double lat2, double lon2, String unit) {
        Log.d("getDistance ","call");
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));

        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;

        if (unit == "kilometer")
            dist = dist * 1.609344;
        else if(unit == "meter")
            dist = dist * 1609.344;

        return dist;
    }
    // This function converts decimal degrees to radians
    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }
    // This function converts radians to decimal degrees
    private static double rad2deg(double rad) {
        return (rad * 180 / Math.PI);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("FirebaseMessagingSvc","onDestroty call");
    }


}
