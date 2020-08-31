package com.ARCompany.Tas_ixBrowser.Model.webpage;

public class WebPage {
    //fields
    private int id;
    private String url;

    //constructors
    public WebPage(){}
    public WebPage(int id, String url){
        this.id=id;
        this.url=url;
    }

    //proparties
    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }
}
