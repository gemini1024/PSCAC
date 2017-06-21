package com.example.ihc.proto_odroid_new;

/**
 * Created by jh on 17. 6. 21.
 */

public enum AlertSituation {
    SAFETY("safe"), CAUTION("caution"), DANGEROUS("dangerous");

    private String message;

    AlertSituation(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return message;
    }
}
