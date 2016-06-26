package com.storyplayer.models;

import java.io.Serializable;

/**
 * Created by Lincoln on 04/04/16.
 */
public class Image implements Serializable{
    private String name;
    private String urlImage;
    private String timestamp;
    private String size;
    private String urlSong;
    private String length;
    private int id;

    public Image() {
    }

    public Image(int id,String name, String urlImage, String timestamp, String size, String urlSong, String length) {
        this.id = id;
        this.name = name;
        this.urlImage = urlImage;
        this.timestamp = timestamp;
        this.size = size;
        this.urlSong = urlSong;
        this.length = length;

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrlImage() {
        return urlImage;
    }

    public void setUrlImage(String urlImage) {
        this.urlImage = urlImage;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getUrlSong() {
        return urlSong;
    }

    public void setUrlSong(String urlSong) {
        this.urlSong = urlSong;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }
}
