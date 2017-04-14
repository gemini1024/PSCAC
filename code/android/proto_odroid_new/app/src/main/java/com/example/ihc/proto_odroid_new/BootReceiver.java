package com.example.ihc.proto_odroid_new;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by ihc on 2017-02-04.
 * 디바이스 켜질때,
 * 메세지 받는 서비스, 토큰 refresh서비스 시작해주는 브로드캐스트 리시버
 * 안드로이드 시스템으로부터 BOOTING 방송을 받고 서비스를 시작해준다.
 */
public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //디바이스 부팅이 완료되면 호출됨
        if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)){
            //ACCESS_FIND_LOCATION 권한이 설정되어 있다면 진입
            GpsInfo gpsInfo = new GpsInfo(context);
            if(gpsInfo.checkPermission()){
                //서비스를 실행
                Intent messageService =  new Intent(context, FirebaseMessagingService.class);
                Intent tokenService = new Intent(context, FirebaseInstanceIDService.class);
                context.startService(messageService);
                context.startService(tokenService);
            }
        }
    }

}
