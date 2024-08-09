package com.example.musiclibrarydb.sqlite.model;

public class Song {

    //VAZNO NEMA PRIVATE/PUBLIC/PROTECTED JER SU POLJA PACKAGE PRIVATE OVAKO !?!?!?
    //OVAKO SE POLJIMA MOZE PRISTUPITI IZ ISTOG PACKAGE-a STO JE RAZLIKA U ODNOSU NA PRIVATE !!!!!!
    long id;
    String name;
    String genre;
    String artist;

    //Public konstruktor bez parametara za kreiranje reference
    public Song(){

    }

    //konstruktor sa parametrima cime cu kreirati odgovarajuci objekat
    public Song(String name, String genre, String artist){
        this.name = name;
        this.artist = artist;
        this.genre = genre;

    }

    //ZASTO JE OVAJ KONSTRUKTOR DRUGACIJI??????? SA I BEZ ID???????????????
    public Song(long id, String name, String artist, String genre){
        this.id = id;
        this.name = name;
        this.artist = artist;
        this.genre = genre;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId(){
        return this.id;
    }

    public void setName(String name){
        this.name = name;
    }
    public String getName(){
        return this.name;
    }

    public void setGenre(String genre){
        this.genre=genre;
    }

    public String getGenre(){
        return this.genre;
    }

    public void setArtist(String artist){
        this.artist=artist;
    }

    public String getArtist(){
        return this.artist;
    }

}
