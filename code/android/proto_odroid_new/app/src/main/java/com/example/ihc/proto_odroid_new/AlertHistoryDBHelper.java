package com.example.ihc.proto_odroid_new;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by ihc on 2017-04-26.
 */

public class AlertHistoryDBHelper extends SQLiteOpenHelper {

    public static AlertHistoryDBHelper alertHistoryDbHelper = null;
    static final String DB_NAME = "PSCAC";
    static final int DB_VERSION = 1;
    public static final String TABLE_NAME = "alert_hitory";
    //컬럼
    public static final String ID = "id";
    public static final String TIME = "time";
    public static final String ALERT = "alert";
    public static final String TARG_LATITUDE = "targ_latitude";
    public static final String TARG_LONGITUDE = "targ_longitude";
    public static final String DEV_LATITUDE = "dev_latitude";
    public static final String DEV_LONGITUDE = "dev_longitude";

    private SQLiteDatabase db;

    //싱글톤패턴
    //한번에 하나의 디비만 열 수 있다.
    public static AlertHistoryDBHelper getInstance(Context context){ // 싱글턴 패턴으로 구현하였다.
        if(alertHistoryDbHelper == null){
            alertHistoryDbHelper = new AlertHistoryDBHelper(context);
        }
        return alertHistoryDbHelper;
    }


    public AlertHistoryDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    public void openDB(){
        if(db == null)
            db = this.getWritableDatabase();
    }

    public void closeDB(){
        if(db != null)
            db.close();
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        db.execSQL("create table " + TABLE_NAME + " ("
                + ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
                + TIME + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
                + ALERT + " TEXT, "
                + TARG_LATITUDE + " REAL, "
                + TARG_LONGITUDE + " REAL, "
                + DEV_LATITUDE + " REAL, "
                + DEV_LONGITUDE + " REAL, "
                + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean insertData(AlertInfo data){
        ContentValues contentValues = new ContentValues();

        contentValues.put(ALERT, data.getSituation().toString());
        contentValues.put(TARG_LATITUDE,data.getTarg_latitude());
        contentValues.put(TARG_LONGITUDE,data.getTarg_longitude());
        contentValues.put(DEV_LATITUDE,data.getDev_latitude());
        contentValues.put(DEV_LONGITUDE,data.getDev_longitude());

        long result = db.insert(TABLE_NAME, null, contentValues);
        if(result == -1)
            return false;
        else
            return true;

    }

}
