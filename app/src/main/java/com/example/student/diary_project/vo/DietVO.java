package com.example.student.diary_project.vo;

/**
 * Created by student on 2018-01-23.
 */

public class DietVO {
    private String writeDate;
    private float weight;
    private String photo;
    private String menu1;
    private float kcal1;
    private String menu2;
    private float kcal2;
    private String menu3;
    private float kcal3;
    private String memo;

    //테마 필요해서 생성했어요
    private int theme;
    ////////////////////////////////
    public DietVO() {}

    public DietVO(String writeDate, float weight) {
        this.writeDate = writeDate;
        this.weight = weight;
    }

    public String getWriteDate() {
        return writeDate;
    }

    public void setWriteDate(String writeDate) {
        this.writeDate = writeDate;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getMenu1() {
        return menu1;
    }

    public void setMenu1(String menu1) {
        this.menu1 = menu1;
    }

    public float getKcal1() {
        return kcal1;
    }

    public void setKcal1(float kcal1) {
        this.kcal1 = kcal1;
    }

    public String getMenu2() {
        return menu2;
    }

    public void setMenu2(String menu2) {
        this.menu2 = menu2;
    }

    public float getKcal2() {
        return kcal2;
    }

    public void setKcal2(float kcal2) {
        this.kcal2 = kcal2;
    }

    public String getMenu3() {
        return menu3;
    }

    public void setMenu3(String menu3) {
        this.menu3 = menu3;
    }

    public float getKcal3() {
        return kcal3;
    }

    public void setKcal3(float kcal3) {
        this.kcal3 = kcal3;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    //테마 생성
    public int getTheme() {
        return theme;
    }

    public void setTheme(int theme) {
        this.theme = theme;
    }
    ///////////////////////////////////////////////
    @Override
    public String toString() {
        return "DietVO{" +
                "writeDate='" + writeDate + '\'' +
                ", weight=" + weight +
                ", photo='" + photo + '\'' +
                ", menu1='" + menu1 + '\'' +
                ", kcal1=" + kcal1 +
                ", menu2='" + menu2 + '\'' +
                ", kcal2=" + kcal2 +
                ", menu3='" + menu3 + '\'' +
                ", kcal3=" + kcal3 +
                ", memo='" + memo + '\'' +
                '}';
    }
}
