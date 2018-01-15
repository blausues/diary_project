package com.example.student.diary_project.vo;

import java.util.Date;

/**
 * Created by student on 2018-01-15.
 */

public class NoSmokingVO {
    private Date writeDate;
    private Date startDate;
    private int giveUp;
    private String promise;

    public NoSmokingVO() {}

    public NoSmokingVO(Date writeDate, Date startDate, int giveUp, String promise) {
        this.writeDate = writeDate;
        this.startDate = startDate;
        this.giveUp = giveUp;
        this.promise = promise;
    }

    public Date getWriteDate() {
        return writeDate;
    }

    public void setWriteDate(Date writeDate) {
        this.writeDate = writeDate;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
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
}
