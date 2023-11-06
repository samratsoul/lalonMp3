package com.mp3.musicplayer.Model;

import com.mp3.musicplayer.Utils.Utilities;

public class AllSong {
    private Utilities utilities;
    private long id, length;
    private String title;
    private String artist, album, generes, dateAdded, data;


    public AllSong(long id, String title, String artist, String album, String generes, long length, String dateAdded, String data) {
        utilities = new Utilities();
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
        return utilities.milliSecondsToTimer(length);
    }

    public String getDateAdded() {
        return dateAdded;
    }

    public String getData() {
        return data;
    }

}

