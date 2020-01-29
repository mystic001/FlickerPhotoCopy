package com.example.flickerphotocopy;

import android.net.Uri;


public class GalleryItem {
    private String id;
    private String Url;
    private String Caption;
    private String owner;

    public GalleryItem() {
    }

    public String getOwner() {

        return owner;
    }

    public void setOwner(String owner) {

        this.owner = owner;
    }
    public Uri getPageUrl(){
         Uri uri = Uri.parse("https://www.flickr.com/photos/").buildUpon().appendPath(owner).appendPath(id).build();
        return uri;

    }
    public String getCaption() {

        return Caption;
    }

    public void setCaption(String caption) {

        Caption = caption;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return Url;
    }

    public void setUrl(String url) {
        Url = url;
    }

    public String toString(){
        return Caption;
    }
}
