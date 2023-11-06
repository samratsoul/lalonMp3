package com.mp3.musicplayer.Model;

public class Song {
    private long id;
    private String title;
    private String artist, album, generes, length, dateAdded, data;


    public Song(long id, String title, String artist, String album, String generes, String length, String dateAdded, String data) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.generes = generes;
        this.length = length;
        this.dateAdded = dateAdded;
        this.data = data;
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public String getAlbum() {
        return album;
    }

    public String getGeneres() {
        return generes;
    }

    public String getLength() {
        return length;
    }

    public String getDateAdded() {
        return dateAdded;
    }

    public String getData() {
        return data;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public void setGeneres(String generes) {
        this.generes = generes;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public void setDateAdded(String dateAdded) {
        this.dateAdded = dateAdded;
    }

    public void setData(String data) {
        this.data = data;
    }
}

