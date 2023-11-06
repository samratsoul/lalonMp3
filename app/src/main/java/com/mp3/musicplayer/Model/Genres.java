package com.mp3.musicplayer.Model;

public class Genres {
    private long id;
    private String title;
    private String artist, album, generes, length;


    public Genres(long id, String title, String artist, String album, String generes, String length) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.generes = generes;
        this.length = length;
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
}

