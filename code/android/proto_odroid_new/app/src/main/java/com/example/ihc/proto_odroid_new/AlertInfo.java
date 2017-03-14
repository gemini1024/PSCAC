package com.example.ihc.proto_odroid_new;

/**
 * Created by ihc on 2017-01-31.
 */
public class AlertInfo {
    //위도,경도,위험종류
    private float w;
    private float g;
    private String warning;

    public void setW(float w) {
        this.w = w;
    }
    public void setG(float g) {
        this.g = g;
    }
    public void setWarning(String warning) {
        this.warning = warning;
    }
    public float getW() {
        return w;
    }
    public float getG() {
        return g;
    }
    public String getWarning() {
        return warning;
    }

}
