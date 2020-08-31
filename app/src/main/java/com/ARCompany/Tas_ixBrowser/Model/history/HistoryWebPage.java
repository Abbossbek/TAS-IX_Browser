package com.ARCompany.Tas_ixBrowser.Model.history;

import java.util.Date;

public class HistoryWebPage {
    //fields
    private int id;
    private String name;
    private String url;
    private Date date;

    //constructors
    public HistoryWebPage(){}
    public HistoryWebPage(int id, String name, String url, Date date){
        this.id=id;
        this.name=name;
        this.url=url;
        this.date=date;
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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
