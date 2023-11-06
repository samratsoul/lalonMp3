package com.mp3.musicplayer.Model;

public class Multiple_Selection {
    int position;
    private long id;
    private String data;

    public Multiple_Selection(int position, long id, String data) {
        this.id = id;
        this.position = position;
        this.data = data;
    }

    public long getId() {
        return id;
    }

    public int getPosition() {
        return position;
    }

    public String getData() {
        return data;
    }
}
