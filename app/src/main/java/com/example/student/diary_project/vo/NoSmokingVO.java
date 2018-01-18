package com.example.student.diary_project.vo;

import java.util.Date;

/**
 * Created by student on 2018-01-15.
 */

public class NoSmokingVO {
    private String writeDate;
    private String startDate;
    private int giveUp;
    private String promise;

    public NoSmokingVO() {}

    public NoSmokingVO(String writeDate, String startDate, int giveUp, String promise) {
        this.writeDate = writeDate;
        this.startDate = startDate;
        this.giveUp = giveUp;
        this.promise = promise;
    }

    public String getWriteDate() {
        return writeDate;
    }

    public void setWriteDate(String writeDate) {
        this.writeDate = writeDate;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public int getGiveUp() {
        return giveUp;
    }

    public void setGiveUp(int giveUp) {
        this.giveUp = giveUp;
    }

    public String getPromise() {
        return promise;
    }

    public void setPromise(String promise) {
        this.promise = promise;
    }

    @Override
    public String toString() {
        return "NoSmokingVO{" +
                "writeDate='" + writeDate + '\'' +
                ", startDate='" + startDate + '\'' +
                ", giveUp=" + giveUp +
                ", promise='" + promise + '\'' +
                '}';
    }
}
