package com.example.ihc.proto_odroid_new;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by ihc on 2017-02-04.
 */
public class UpdateReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(Intent.ACTION_PACKAGE_ADDED)){
           //앱이 설치되었을 때
        }else if(intent.getAction().equals(Intent.ACTION_PACKAGE_REMOVED)){
            //앱이 삭제되었을 때
        }else if(intent.getAction().equals(Intent.ACTION_PACKAGE_REPLACED)){
            //앱이 업데이트 되었을 때
        }
    }
}
