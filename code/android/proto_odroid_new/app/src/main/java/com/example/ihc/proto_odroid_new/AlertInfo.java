package com.example.ihc.proto_odroid_new;

/**
 * Created by ihc on 2017-01-31.
 */
public class AlertInfo {

    private double dev_latitude;
    private double dev_longitude;
    //위도,경도,위험종류
    private double targ_latitude;
    private double targ_longitude;
    private String message;
    private String time;
    private String address;
    private String content;

    public AlertInfo(){}
    public AlertInfo(double latitude,double longitude,String message){
        this.targ_latitude = latitude;
        this.targ_longitude = longitude;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public double getTarg_longitude() {
        return targ_longitude;
    }

    public double getTarg_latitude() {
        return targ_latitude;
    }

    public double getDev_latitude() {
        return dev_latitude;
    }

    public double getDev_longitude() {
        return dev_longitude;
    }

    public String getTime() {
        return time;
    }

    public String getAddress() {
        return address;
    }

    public String getContent() {
        return content;
    }

    public void setTarg_latitude(double latitude) {
        this.targ_latitude = latitude;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setTarg_longitude(double longitude) {
        this.targ_longitude = longitude;
    }

    public void setDev_latitude(double targ_latitude) {
        this.dev_latitude = targ_latitude;
    }

    public void setDev_longitude(double targ_longitude) {
        this.dev_longitude = targ_longitude;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
