package com.example.musiclibrarydb.sqlite.model;

public class User {
    long id;
    String name;
    String password;

    public User(){

    }

    public User(String name, String password){
        this.name = name;
        this.password = password;
    }

    public User(long id, String name, String password){
        this.id = id;
        this.name = name;
        this.password = password;
    }

    public long getId(){return id;}

    public void setId(long id){this.id = id;}

    public String getName(){return name;}

    public void setName(String name){this.name=name;}

    public String getPassword(){return password;}

    public void setPassword(String password){this.password=password;}

}
