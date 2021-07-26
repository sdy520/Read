package com.example.read.Entity;

public class BookClassify {
    private int classifyimg;
    private String classifytext;

    public BookClassify() {
    }

    public BookClassify(int classifyimg, String classifytext) {
        this.classifyimg = classifyimg;
        this.classifytext = classifytext;
    }

    public int getClassifyimg() {
        return classifyimg;
    }

    public void setClassifyimg(int classifyimg) {
        this.classifyimg = classifyimg;
    }

    public String getClassifytext() {
        return classifytext;
    }

    public void setClassifytext(String classifytext) {
        this.classifytext = classifytext;
    }
}
