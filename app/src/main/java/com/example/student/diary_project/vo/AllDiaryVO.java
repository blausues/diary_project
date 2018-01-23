package com.example.student.diary_project.vo;

/**
 * Created by student on 2018-01-23.
 */

public class AllDiaryVO {
    private String writeDate;
    private String content;
    private int theme;

    public AllDiaryVO(String writeDate, String content, int theme) {
        this.writeDate = writeDate;
        this.content = content;
        this.theme = theme;
    }

    public AllDiaryVO() {
    }

    public String getWriteDate() {
        return writeDate;
    }

    public void setWriteDate(String writeDate) {
        this.writeDate = writeDate;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getTheme() {
        return theme;
    }

    public void setTheme(int theme) {
        this.theme = theme;
    }

    @Override
    public String toString() {
        return "AllDiaryVO{" +
                "writeDate='" + writeDate + '\'' +
                ", content='" + content + '\'' +
                ", theme=" + theme +
                '}';
    }
}
