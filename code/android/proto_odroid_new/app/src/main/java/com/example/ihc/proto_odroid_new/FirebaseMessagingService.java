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
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
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

/**
 * Created by ihc on 2017-01-19.
 * 서버에서 보내는 푸시 메세지의 형태는
 * ".-w-값-.-g-값-.-alt-값-."
 * 으로 한다.
 */
public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {
    private static final String TAG = "FirebaseMsgService";
    private static final double ALERT_DISTANCE = 200;
    Location location;
    //경보를 저장할 객체
    private AlertInfo warning;



    // [START receive_message]
    //onMessageReceived 메소드는 앱이 포그라운드에 있을때만 작동한다.
    //백그라운드 상태에서는 디폴트 푸쉬가 뜸.(원하는 동작이 안된다.)
    //백그라운드 상태일 때, fcm api를 직접 호출해줘야한다.
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d("메세지호출","콜");

        //받은 메세지에서 위도,경도,위험경보 데이터를 가져온다.
        Map<String,String> msgData = remoteMessage.getData();
        //모든 정보가 제대로 들어온게 확인되면 작업진행
        if(msgData.get("latitude") != null && msgData.get("longitude") != null && msgData.get("alarm") != null){
            Log.d("타이틀",msgData.get("title"));
            Log.d("받은위도:",msgData.get("latitude"));
            Log.d("받은경도:",msgData.get("longitude"));
            Log.d("알람",msgData.get("alarm"));

            //위도,경도,위험메세지를 담는 AlertInfo객체를 만들고 데이터를 넣는다.
            warning = new AlertInfo(Double.parseDouble(msgData.get("latitude")),
                    Double.parseDouble(msgData.get("longitude")),msgData.get("alarm"));

            //경고발생시간설정
            warning.setTime(getCurTime());
            //경고발생주소입력
            warning.setAddress(getAddress(getApplicationContext(),warning.getTarg_latitude(),warning.getTarg_longitude()));

            //정보로 조건을 체크하고 경보하기
            checkAndAlert(warning);
            Log.d("체크앤알럴트 종료","마지막로직");
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("서비스종료!!!!","서비스종료!!!");
        Log.d("서비스종료!!!!","서비스종료!!!");
        Log.d("서비스종료!!!!","서비스종료!!!");
        Log.d("서비스종료!!!!","서비스종료!!!");
    }

    //현재시간 가져오기
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
     * @param lat
     * @param lng
     * @return 주소
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


    //위도,경도,위험종류값 체크해서 경보(푸시알림)해버리기
    //성공적으로 완료되면 위도, 경도, 위험경보를 담은 AlertInfo 객체를 반환.
    //실패하면 NULL반환
    private AlertInfo checkAndAlert(AlertInfo info) {
        AlertInfo result = null;

        //객체가 null이면 null 반환
        if (info == null)
            return null;
        //위치(위도,경도) 정보가 없을때 null 반환
        if (String.valueOf(info.getTarg_latitude()).equals("") || String.valueOf(info.getTarg_longitude()).equals(""))
            return null;

        //거리계산
        //두 디바이스 간 거리가 설정범위보다 멀리 떨어져 있으면 알람하지 않는다.
        double distance = calcDistance();
        Log.d("사이거리2: ",String.valueOf(distance));
        Log.d("설정거리: ",String.valueOf(ALERT_DISTANCE));
        if (distance > ALERT_DISTANCE || distance == -1.0)  return null;


        //푸시알림을 클릭했을 때 나오는 액티비티 설정
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addCategory("noti");
        intent.putExtra("alert", info.getMessage());
        intent.putExtra("targ_latitude", info.getTarg_latitude());
        intent.putExtra("targ_longitude", info.getTarg_longitude());
        intent.putExtra("dev_latitude",info.getDev_latitude());
        intent.putExtra("dev_longitude",info.getDev_longitude());
        //----------------------------------------임시코드
        intent.putExtra("time",getCurTime());
        intent.putExtra("address",getAddress(getApplicationContext(),info.getTarg_latitude(),info.getTarg_longitude()));
        //----------------------------------------임시코드
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.warning).setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.warning))
                .setContentTitle("보행자주의!")
                .setAutoCancel(true)
                .setLights(000000255, 500, 2000)
                .setContentIntent(pendingIntent);


        //푸시알림 설정
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);


        //경보가 디폴트일때, 디폴트경보발생(푸시알림 및 소리)
        if (info.getMessage().equals("default") || info.getMessage().equals("dangerous")) {
            notificationBuilder.setContentText("주변에 차도횡단보행자 감지!\n주의해주세요");
            notificationBuilder.setSound(Uri.parse("android.resource://com.example.ihc.proto_odroid_new/" + R.raw.alert_default));
            notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
            result = info;
        }

        if(info.getMessage().equals("caution")){
            notificationBuilder.setContentText("주변에 차도횡단보행자 감지!\n주의해주세요");
            notificationBuilder.setSound(Uri.parse("android.resource://com.example.ihc.proto_odroid_new/" + R.raw.alert_default));
            notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
            result = info;
        }

        return result;
    }

    //디바이스 위치와 경보발생 보드 간 거리계산
    static double beet_distance;
    static boolean isEnd;
    Thread thread;
    private double calcDistance() {

        Log.d("거리계산","호출");
//        final Context mCtx = this;
//        final Location location;
        isEnd = false;

        new Thread(new Runnable() {
            @Override
            public void run() {
                Handler cHandler = new Handler(Looper.getMainLooper()) {
                    @Override public void handleMessage(Message msg) {
                        // 이제 오류 안 뜸.
                        GpsInfo gpsinfo = new GpsInfo(getApplicationContext());
                        Location location = gpsinfo.getLocationInService();

                        if (location != null) {
                            warning.setDev_latitude(location.getLatitude());
                            warning.setDev_longitude(location.getLongitude());

                            beet_distance = getDistance(warning.getTarg_latitude(), warning.getTarg_longitude(), warning.getDev_latitude(), warning.getDev_longitude(), "meter");
                            Log.d("1위도,경도: ", String.valueOf(warning.getTarg_latitude()) + "," + String.valueOf(warning.getTarg_longitude()));
                            Log.d("2위도,경도: ", String.valueOf(warning.getDev_latitude()) + "," + String.valueOf(warning.getDev_longitude()));
                            Log.d("사이거리:", String.valueOf(beet_distance));
                            gpsinfo.stopUsingGpsInService();
                        } else
                            beet_distance = -1.0;
                        Log.d("칼크디스턴스", "핸들러탈출직전");
                        isEnd = true;
                    }
                };
                cHandler.sendEmptyMessage(0);
            }
        }).start();
        while(!isEnd){Log.d("isEnd value",String.valueOf(isEnd));}


        Log.d("칼크디스턴스","루프탈출");
//        Thread t = new Thread(new Runnable() {
//            @Override
//            public void run() {
////여기서 UI 작업을 수행하면 Exception 발생 함.
//                // 내용
//                GpsInfo gpsinfo = new GpsInfo(mCtx);
//                Location location = gpsinfo.getLocationInService();
//
//                if(location != null){
//                    warning.setDev_latitude(location.getLatitude());
//                    warning.setDev_longitude(location.getLongitude());
//
//                    beet_distance =getDistance(warning.getTarg_latitude(),warning.getTarg_longitude(),warning.getDev_latitude(),warning.getDev_longitude(),"meter");
//                    Log.d("1위도,경도: ",String.valueOf(warning.getTarg_latitude())+","+String.valueOf(warning.getTarg_longitude()));
//                    Log.d("2위도,경도: ",String.valueOf(warning.getDev_latitude())+","+String.valueOf(warning.getDev_longitude()));
//                    Log.d("사이거리:",String.valueOf(beet_distance));
//                    gpsinfo.stopUsingGpsInService();
//                }else
//                    beet_distance = -1.0;
//                Log.d("칼크디스턴스","핸들러탈출직전");
//                isEnd = true;
//            }
//        });
//        t.start();
//        while(!isEnd){Log.d("isEnd value",String.valueOf(isEnd));}
//
//
//        Log.d("칼크디스턴스","루프탈출");

//        final Handler mHandler = new Handler(Looper.getMainLooper());
//        mHandler.postDelayed(new Runnable() {
//
//            @Override
//            public void run() {
//                // 내용
//                GpsInfo gpsinfo = new GpsInfo(mCtx);
//                Location location = gpsinfo.getLocationInService();
//
//                if(location != null){
//                    warning.setDev_latitude(location.getLatitude());
//                    warning.setDev_longitude(location.getLongitude());
//
//                    beet_distance =getDistance(warning.getTarg_latitude(),warning.getTarg_longitude(),warning.getDev_latitude(),warning.getDev_longitude(),"meter");
//                    Log.d("1위도,경도: ",String.valueOf(warning.getTarg_latitude())+","+String.valueOf(warning.getTarg_longitude()));
//                    Log.d("2위도,경도: ",String.valueOf(warning.getDev_latitude())+","+String.valueOf(warning.getDev_longitude()));
//                    Log.d("사이거리:",String.valueOf(beet_distance));
//                    isEnd = true;
//                    gpsinfo.stopUsingGpsInService();
//                }else
//                    beet_distance = -1.0;
//                Log.d("칼크디스턴스","핸들러탈출직전");
//            }}, 0);
//            Log.d("칼크디스턴스","핸들러탈출");
//        while(!isEnd){Log.d("isEnd value",String.valueOf(isEnd));}
//        Log.d("칼크디스턴스","루프탈출");
        return beet_distance;
    }

    /**
     * 두 지점간의 거리 계산
     *
     * @param lat1 지점 1 위도
     * @param lon1 지점 1 경도
     * @param lat2 지점 2 위도
     * @param lon2 지점 2 경도
     * @param unit 거리 표출단위
     * @return
     */
    private static double getDistance(double lat1, double lon1, double lat2, double lon2, String unit) {

        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));

        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;

        if (unit == "kilometer") {
            dist = dist * 1.609344;
        } else if(unit == "meter"){
            dist = dist * 1609.344;
        }

        return (dist);
    }
    // This function converts decimal degrees to radians
    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }
    // This function converts radians to decimal degrees
    private static double rad2deg(double rad) {
        return (rad * 180 / Math.PI);
    }



    private String getContents(){
        return new String();
    }
    //float 판단함수
    public static boolean isStringFloat(String s) {
        try {
            Float.parseFloat(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }


    class myServiceHandler extends Handler {
        @Override
        public void handleMessage(android.os.Message msg) {

            try {
                GpsInfo gpsinfo = new GpsInfo(getApplicationContext());
                location = gpsinfo.getLocationInService();
                gpsinfo.stopUsingGpsInService();

            } catch (SecurityException ex) {
            }

        }

    }


}
