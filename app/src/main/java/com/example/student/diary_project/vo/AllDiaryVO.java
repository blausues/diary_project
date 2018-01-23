package com.example.student.diary_project.vo;

/**
 * Created by student on 2018-01-23.
 */

public class AllDiaryVO {
    private int type;
    private String writeDate;
    private String content;
    private int theme;

    public AllDiaryVO(int type, String writeDate, String content, int theme) {
        this.type = type;
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

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

}
