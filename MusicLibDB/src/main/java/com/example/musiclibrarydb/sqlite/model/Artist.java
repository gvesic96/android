package com.example.musiclibrarydb.sqlite.model;

public class Artist {

    long id;
    String name;
    String genre;

    public Artist(){

    }

    public Artist(String name){
        this.name = name;
    }

    public Artist(long id, String name){
        this.id = id;
        this.name = name;
    }

    public long getId(){return id;}

    public void setId(long id){this.id = id;}

    public String getName(){return name;}

    public void setName(String name){this.name=name;}

    public String getGenre(){return genre;}

    public void setGenre(String genre){this.genre=genre;}


}
