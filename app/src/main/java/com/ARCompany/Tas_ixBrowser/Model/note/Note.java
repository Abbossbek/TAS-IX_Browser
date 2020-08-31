package com.ARCompany.Tas_ixBrowser.Model.note;

import android.graphics.Bitmap;

import java.util.Date;

public class Note {
    //fields
    private int id;
    private String name;
    private String url;

    //constructors
    public Note(){}
    public Note(int id, String name, String url){
        this.id=id;
        this.name=name;
        this.url=url;
    }

    //proparties
    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }
}
