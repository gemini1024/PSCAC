package com.example.ihc.proto_odroid_new;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.RemoteMessage;

import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * Created by ihc on 2017-01-19.
 * 서버에서 보내는 푸시 메세지의 형태는
 * ".-w-값-.-g-값-.-alt-값-."
 * 으로 한다.
 */
public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {
    private static final String TAG = "FirebaseMsgService";
    private static final double ALERT_DISTANCE = 200;
    //경보를 저장할 객체
    private AlertInfo warning;
    private PowerManager.WakeLock wakelock = null;


    // [START receive_message]
    //onMessageReceived 메소드는 앱이 포그라운드에 있을때만 작동한다.
    //백그라운드 상태에서는 디폴트 푸쉬가 뜸.(원하는 동작이 안된다.)
    //백그라운드 상태일 때, fcm api를 직접 호출해줘야한다.
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        String message = "";
        Log.d(TAG, "first_data: " + remoteMessage.getData());
        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            //큰따옴표를 토큰으로 문자열 추출
            StringTokenizer token = new StringTokenizer(remoteMessage.getData().get("message"), "\"");
            //추출한 문자열 리스트에 저장
            ArrayList<String> tempArr = new ArrayList<>();
            while (token.hasMoreTokens())
                tempArr.add(token.nextToken());

            //content 다음의 콜론 뒤에 메세지내용이 옴. 따라서 이 메세지 내용을 message String 객체에 저장.
            for (int i = 0; i < tempArr.size(); i++) {
                if (tempArr.get(i).equals("content")) message = tempArr.get(i + 2);
            }

            //푸쉬알림 보내기
            sendPushNotification(message);
        }
    }

    //푸쉬알림보내기
    //받은 메시지를 파싱하여 원하는 데이터를 추출한 후
    //푸쉬알림을 띄워주는 기능실행한다.
    private void sendPushNotification(String message) {
        System.out.println("received message : " + message);

        //푸시알림 메세지로부터 위도, 경도, 경보알림값을 받아온다.
        warning = parsingMessage(message);
        checkAndAlert(warning);
    }

    //메세지에서 위도,경도,위험종류 뽑아내버리기(파싱)
    private AlertInfo parsingMessage(String message) {
        AlertInfo result = null;
        ArrayList<String> first_parsing = new ArrayList<>();
        ArrayList<String> temp = new ArrayList<>();
        ArrayList<String> final_parsing = new ArrayList<>();


        /*
          온전한 메세지일 경우(위도, 경도 존재할경우! 위험메세지가 온전치 안아도 default 값으로 경고띄움)
         */

        //결과값 저장할 객체 생성
        result = new AlertInfo();
        //하나의 메세지 안에서 위도,경도,위험종류 구분하여 first_parsing에 저장
        //일단 콤마 토큰을 기준으로 분리하여 first_parsing에 저장
        StringTokenizer met = new StringTokenizer(message, ".");
        while (met.hasMoreTokens())
            first_parsing.add(met.nextToken());


        //위도,경도,위험종류를 담고있는 first_parsing list에서 값들을 뽑아내어 final_parsing list에 저장.
        //최종적으로 final_parsing list의 0,1,2 index에 위도,경도,위험경보값이 들어가있게 된다.
        for (int i = 0; i < first_parsing.size(); i++) {
            met = new StringTokenizer(first_parsing.get(i), "-");
            while (met.hasMoreTokens()) {
                temp.add(met.nextToken());
            }
            final_parsing.add(temp.get(1));
            temp.clear();
        }

        //위도 경도가 숫자로 들어왔는지 판단
        if (!isStringFloat(final_parsing.get(0)) || !isStringFloat(final_parsing.get(1)))
            return null;

        //위도, 경도, 위험경보값 결과객체에 저장.
        result.setW(Float.parseFloat(final_parsing.get(0)));
        result.setG(Float.parseFloat(final_parsing.get(1)));
        result.setWarning(final_parsing.get(2));

        Log.d("~~~~~파싱완료~~~~", "파싱완료");
        Log.d("위도: ", String.valueOf(result.getW()));
        Log.d("경도: ", String.valueOf(result.getG()));
        Log.d("위험경보: ", String.valueOf(result.getWarning()));

        return result;
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
        if (String.valueOf(info.getW()).equals("") || String.valueOf(info.getG()).equals(""))
            return null;

        //거리계산
        //두 디바이스 간 거리가 설정범위보다 멀리 떨어져 있으면 알람하지 않는다.
//        double distance = calcDistance(info);
//        if (distance < ALERT_DISTANCE) return null;


        //푸시알림을 클릭했을 때 나오는 액티비티 설정
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("alert", info.getWarning());
        intent.putExtra("latitude", info.getW());
        intent.putExtra("longitude", info.getG());
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.push_image_billy).setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.push_image_billy))
                .setContentTitle("Push Title ")
                .setAutoCancel(true)
                .setSound(Uri.parse("android.resource://com.example.ihc.proto_odroid_new/" + R.raw.alert_default))
                .setLights(000000255, 500, 2000)
                .setContentIntent(pendingIntent);


        //푸시알림 설정
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);


        //경보가 디폴트일때, 디폴트경보발생(푸시알림 및 소리)
        if (info.getWarning().equals("default")) {
            notificationBuilder.setContentText("주변에 차도횡단보행자 감지!\n주의해주세요");
            notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
            result = info;
        }

        return result;
    }

    //디바이스 위치와 경보발생 보드 간 거리계산
    private double calcDistance(AlertInfo info) {
        long minTime = 60000;
        float minDistance = 1;
        Log.d("clacDistance", "called");

        //위치매니저
        //위치받아올 때 쓰인다.
        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        //위치매니저로 위치를 받아올 때 이 클래스 안에서 해당 위치정보 접근이 가능.
        //실제로 여기서 좌표비교를 진행한다.
        CompareGPS compareGPS = new CompareGPS();

        //경보발생 보드의 위치정보를 넘긴다.
        compareGPS.setTargetPosition(info);

//        //권한체크 후 위치 가져온다.
//        //권한이 제대로 설정되어있지 않다면 -1반환
//        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            Log.d("permission check","called");
//            return -1;
//        }

//        manager.requestLocationUpdates(
//                LocationManager.GPS_PROVIDER,
//                minTime,
//                minDistance,
//                compareGPS
//        );

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return -1;
        }
        manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                minTime, minDistance, compareGPS);
        manager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                minTime, minDistance, compareGPS);

        Log.d("gps?: ",String.valueOf(compareGPS.getDistance()));
        manager.removeUpdates(compareGPS);

        return compareGPS.getDistance();
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
    private class CompareGPS implements LocationListener {
        //현재 디바이스 위도, 경도
        private double dev_Latitude, dev_Longitude;
        private double distance;

        //경보발생 보드 위도, 경도
        private double targ_Latitude, targ_Longitude;

        public void onLocationChanged(Location location) {
            //capture location data sent by current provider
            Log.d("call LocationChanged","OK");

            dev_Latitude = location.getLatitude();
            dev_Longitude = location.getLongitude();
            Log.d("Latitude: ",String.valueOf(dev_Latitude));
            Log.d("Longitude: ",String.valueOf(dev_Longitude));

            Location targ_location = new Location("target");
            targ_location.setLatitude(targ_Latitude);
            targ_location.setLongitude(targ_Longitude);

            Location dev_location = new Location("device");
            dev_location.setLatitude(dev_Latitude);
            dev_location.setLongitude(dev_Longitude);

            distance = targ_location.distanceTo(dev_location);
            Log.d("distance: ",String.valueOf(distance));
        }
        public void onProviderDisabled(String provider) { }
        public void onProviderEnabled(String provider) { }
        public void onStatusChanged(String provider, int status, Bundle extras) { }

        public void setTargetPosition(AlertInfo info){
            this.targ_Latitude = info.getW();
            this.targ_Longitude = info.getG();
        }

        public double getDistance() {
            return distance;
        }
    }
}
