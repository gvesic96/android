package com.example.musiclibrarydb.sqlite.model;

public class Genre {

    long id;
    String name;

    public Genre(){

    }

    public Genre(String name){
        this.name = name;
    }

    public Genre(long id, String name){
        this.id = id;
        this.name = name;
    }

    public long getId(){return id;}

    public void setId(long id){this.id = id;}

    public String getName(){return name;}

    public void setName(String name){this.name=name;}

}
