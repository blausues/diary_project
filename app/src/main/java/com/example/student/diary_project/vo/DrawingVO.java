package com.example.student.diary_project.vo;

/**
 * Created by student on 2018-01-19.
 */

public class DrawingVO {
    private String drawDate;
    private String drawContent;
    private String drawFileName;
    private int theme;

    public DrawingVO(String drawDate, String drawContent, String drawFileName, int theme) {
        this.drawDate = drawDate;
        this.drawContent = drawContent;
        this.drawFileName = drawFileName;
        this.theme = theme;
    }

    public DrawingVO(){}

    public String getDrawDate() {
        return drawDate;
    }

    public void setDrawDate(String drawDate) {
        this.drawDate = drawDate;
    }

    public String getDrawContent() {
        return drawContent;
    }

    public void setDrawContent(String drawContent) {
        this.drawContent = drawContent;
    }

    public String getDrawFileName() {
        return drawFileName;
    }

    public void setDrawFileName(String drawFileName) {
        this.drawFileName = drawFileName;
    }

    public int getTheme() {
        return theme;
    }

    public void setTheme(int theme) {
        this.theme = theme;
    }

    @Override
    public String toString() {
        return "DrawingVO{" +
                "drawDate='" + drawDate + '\'' +
                ", drawContent='" + drawContent + '\'' +
                ", drawFileName='" + drawFileName + '\'' +
                ", theme=" + theme +
                '}';
    }
}
