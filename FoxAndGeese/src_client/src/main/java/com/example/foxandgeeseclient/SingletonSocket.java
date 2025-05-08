package com.example.foxandgeeseclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class SingletonSocket {

    private static SingletonSocket single_instance = null;

    //public String s;

    private Socket socket;
    private BufferedReader br;
    private PrintWriter pw;

    //Kreirati getter i setter za br i pw??? kako bi moglo da se poziva iz MainActivity preko objekta klase SingletonSocket
    public Socket getSocket(){
        return this.socket;
    }

    public BufferedReader getBr(){
        return this.br;
    }

    public PrintWriter getPw(){
        return this.pw;
    }

    public void setPw(String value){
        this.pw.println(value);
    }

    //KONSTRUKTOR
    private SingletonSocket(String serverIP){
        //s="Hello I am a astring part of Singleton class";

        try {
            //kreira socket
            this.socket = new Socket(serverIP, 6001);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            //kreira bufferReader i printWriter
            this.br = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            this.pw = new PrintWriter(new OutputStreamWriter(this.socket.getOutputStream()), true);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static synchronized SingletonSocket getInstance(String serverIP){
        if(single_instance == null)
            single_instance = new SingletonSocket(serverIP);

        return single_instance;

    }

}
