package com.example.musiclibrarydb.sqlite.model;

public class Song {

    //VAZNO NEMA PRIVATE/PUBLIC/PROTECTED JER SU POLJA PACKAGE PRIVATE OVAKO !?!?!?
    //OVAKO SE POLJIMA MOZE PRISTUPITI IZ ISTOG PACKAGE-a STO JE RAZLIKA U ODNOSU NA PRIVATE !!!!!!
    long id;
    String name;
    long artist_id;

    //Public konstruktor bez parametara za kreiranje reference
    public Song(){

    }

    //konstruktor sa parametrima cime cu kreirati odgovarajuci objekat
    public Song(String name,  long artist_id){
        this.name = name;
        this.artist_id = artist_id;

    }

    //ZASTO JE OVAJ KONSTRUKTOR DRUGACIJI??????? SA I BEZ ID???????????????
    public Song(long id, String name, long artist_id){
        this.id = id;
        this.name = name;
        this.artist_id = artist_id;

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

    public void setArtist_id(long artist_id) {
        this.artist_id = artist_id;
    }
    public long getArtist_id(){
        return this.artist_id;
    }


}
