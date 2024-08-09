package com.example.musiclibrarydb.sqlite.model;

public class Playlist {
    long id;
    String name;
    String userName;

    public Playlist(){

    }

    public Playlist(String name){
        this.name = name;
    }

    public Playlist(long id, String name){
        this.id = id;
        this.name = name;
    }

    public long getId(){return id;}

    public void setId(long id){this.id = id;}

    public String getName(){return name;}

    public void setName(String name){this.name=name;}

    public String getUserName(){return userName;}

    public void setUserName(String userName){this.userName=userName;}

}
