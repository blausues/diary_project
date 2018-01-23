package com.example.student.diary_project.vo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by student on 2018-01-23.
 */

public class NormalVO {
    private String normalWriteDate;
    private String normalWriteContent;
    private ArrayList<String> normalWriteImagePath;
    private int theme;

    public NormalVO(String normalWriteDate, String normalWriteContent, ArrayList<String> normalWriteImagePath, int theme) {
        this.normalWriteDate = normalWriteDate;
        this.normalWriteContent = normalWriteContent;
        this.normalWriteImagePath = normalWriteImagePath;
        this.theme = theme;
    }
    public  NormalVO(){

    }

    public String getNormalWriteDate() {
        return normalWriteDate;
    }

    public void setNormalWriteDate(String normalWriteDate) {
        this.normalWriteDate = normalWriteDate;
    }

    public String getNormalWriteContent() {
        return normalWriteContent;
    }

    public void setNormalWriteContent(String normalWriteContent) {
        this.normalWriteContent = normalWriteContent;
    }

    public ArrayList<String> getNormalWriteImagePath() {
        return normalWriteImagePath;
    }

    public void setNormalWriteImagePath(ArrayList<String> normalWriteImagePath) {
        this.normalWriteImagePath = normalWriteImagePath;
    }

    public int getTheme() {
        return theme;
    }

    public void setTheme(int theme) {
        this.theme = theme;
    }

    @Override
    public String toString() {
        return "NormalVO{" +
                "normalWriteDate='" + normalWriteDate + '\'' +
                ", normalWriteContent='" + normalWriteContent + '\'' +
                ", normalWriteImagePath=" + normalWriteImagePath +
                ", theme=" + theme +
                '}';
    }
}
