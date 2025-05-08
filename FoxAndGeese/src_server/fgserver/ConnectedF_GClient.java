/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package fgserver;

import java.io.BufferedReader;
import java.io.IOException;
//import java.io.InputStream;
import java.io.InputStreamReader;
//import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
//import java.net.SocketException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import java.util.Random;

/**
 *
 * @author Gile
 */
public class ConnectedF_GClient implements Runnable{
    private Socket socket;
    private String userName;
    private BufferedReader br;
    private PrintWriter pw;
    private ArrayList<ConnectedF_GClient> allClients;
    
    private String activeRole = "";
    private String activeOpponent = "";
    private String fieldType;
    
    private int foxPosition[] = {7,0};
    private int geesePosition[][] = {{0,1},{0,3},{0,5},{0,7}};
    
    
    private boolean playerInGame = false;
    
    public boolean getPlayerInGame(){
        return this.playerInGame;
    }
    
    public void setPlayerInGame(boolean val){
        this.playerInGame = val;
    }
    
//getters and setters
    
    public void setFoxPosition(int pos[]){
        this.foxPosition[0]=pos[0];
        this.foxPosition[1]=pos[1];
    }
    
    public void setGeesePosition(int pos[][]){
        for(int i=0;i<4;i++){
            for(int j=0; j<2; j++){
                this.geesePosition[i][j]=pos[i][j];
            }
        }
    }
    
    public String getActiveRole(){
        return activeRole;
    }
    
    public String getOpponent(){
        return activeOpponent;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    
    void setStartPositionsBlack(Random rand){
        boolean permit=true;
        while(permit){
            int m=rand.nextInt(100)%8;//opseg od 0 do 100 kako ne bi generisao negativne
            if(m%2==0){
                permit=false;
                this.foxPosition[0] = 7;
                this.foxPosition[1] = m;
                int j=1;//different color on other side
                for(int i=0;i<4;i++){
                    this.geesePosition[i][1]=j;
                    j=j+2;
                }
            }
        }    
    }
    
    void setStartPositionsWhite(Random rand){
        boolean permit=true;
        while(permit){
            int m=rand.nextInt(100)%8;//opseg od 0 do 100 kako ne bi generisao negativne
            if(m%2==1){
                permit=false;
                this.foxPosition[0] = 7;
                this.foxPosition[1] = m;
                int j=0;//different color on other side
                for(int i=0;i<4;i++){
                    this.geesePosition[i][1]=j;
                    j=j+2;
                }
            }
        }    
    }
    
    
    //Konstruktor klase, prima kao argument socket kao vezu sa uspostavljenim klijentom i prima listu svih klijenata
    public ConnectedF_GClient(Socket socket, ArrayList<ConnectedF_GClient> allClients) {
        this.socket = socket; //postavlja vrednost pribavljenog socketa
        this.allClients = allClients; //drugi argument koji prima je lista svih klijenata

        //iz socket-a preuzmi InputStream i OutputStream
        try {
            //posto se salje tekst, napravi BufferedReader i PrintWriter
            //kojim ce se lakse primati/slati poruke (bolje nego da koristimo Input/Output stream
            this.br = new BufferedReader(new InputStreamReader(this.socket.getInputStream(), "UTF-8"));
            this.pw = new PrintWriter(new OutputStreamWriter(this.socket.getOutputStream()), true);
            //zasad ne znamo user name povezanog klijenta
            this.userName = "";
        } catch (IOException ex) {
            Logger.getLogger(ConnectedF_GClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Metoda prolazi i pravi poruku sa trenutno povezanik korisnicima u formatu
     * Users: ImePrvog ImeDrugog ImeTreceg ... kada se napravi poruka tog
     * formata, ona se salje svim povezanim korisnicima
     */
    void connectedClientsUpdateStatus() {
        //priprema string sa trenutno povezanim korisnicima u formatu 
        //Users: Milan Dusan Petar
        //i posalji svim korisnicima koji se trenutno nalaze u chat room-u
        String connectedUsers = "Users:";
        //ovom petljom dodaje imena korisnika na Users: i tako formira string koji ce poslati
        for (ConnectedF_GClient c : this.allClients) {
            connectedUsers += " " + c.getUserName();
        }

        //prodji kroz sve klijente i svakom posalji info o novom stanju u sobi
        //Prolazi kroz sve klijente sa liste i salje svima string o pridruzenim korisnicima, to jest klijentima
        for (ConnectedF_GClient svimaUpdateCB : this.allClients) {
            if(svimaUpdateCB.getPlayerInGame()==false){
            svimaUpdateCB.pw.println(connectedUsers);//SALJE PORUKU SAMO ONIMA KOJI NISU TRENUTNO U IGRI
            }
        }

        System.out.println(connectedUsers);//ispisuje string connectedUsers koji sadrzi Users: i imena svih klijenata
    }

    @Override
    public void run() {
        
        while (true) {
            try {
                //ako nije poslato ime, najpre cekamo na njega
                if (this.userName.equals("")) {
                    this.userName = this.br.readLine();
                    //ukoliko je smo dobili ime userName != null
                    if (this.userName != null) {
                        System.out.println("Connected user: " + this.userName);
                        connectedClientsUpdateStatus();//updateovanje statusa kod svih klijenata
                    } else {
                        //ako je userName null to znaci da je terminiran klijent thread
                        System.out.println("Disconnected user: " + this.userName);
                        for (ConnectedF_GClient cl : this.allClients) {
                            if (cl.getUserName().equals(this.userName)) {
                                this.allClients.remove(cl); //ovde uklanja klijenta iz liste ukoliko je terminiran njegov thread
                                break;
                            }
                        }
                        connectedClientsUpdateStatus();
                        break;  //prekida while petlju jer je terminiran client thread
                    }
                    ////////CEKAMO PORUKU/////////
                } else {
                    //vec nam je korisnik poslao korisnicko ime, poruka koja je 
                    //stigla je za nekog drugog korisnika iz chat room-a (npr Milana) u 
                    //formatu Milan: Cao Milane, kako si?
                    System.out.println("cekam poruku");
                    String line = this.br.readLine();
                    System.out.println(line);
                    System.out.println("stigla poruka");
                    
                    
                    
                    
                    if (line != null) {
                        
                        
                        String[] infoSt = line.split(":");
                        String primacKorisnik = infoSt[0].trim();  
                        System.out.println(primacKorisnik);             
                        String poruka = infoSt[1];                
                        System.out.println(poruka);                     

                        
                        if(infoSt.length==2 && infoSt[1].equals("Thread")){
                            this.playerInGame = false; //kada je igrac kliknuo NO/BACK dugme
                            this.pw.println("Player:PlayAgain;NO");// OVA PORUKA JE NEOPHODNA DA SE PREKINE THREAD
                        }
                        
                        if(infoSt.length==2 && infoSt[1].equals("PlayAgain;NO")){
                            for (ConnectedF_GClient clnt : this.allClients) {
                                if (clnt.getUserName().equals(infoSt[0])) {
                                    clnt.playerInGame = false; //IGRAC KOME JE POSLATO PLAYAGAIN;NO JE TAKODJE POSTAO DOSTUPAN
                                }
                            }
                        }
                        
                        if(infoSt.length==2 && infoSt[1].equals("Update")){
                            System.out.println("Updating users: " + this.userName);
                            connectedClientsUpdateStatus();//updateovanje statusa kod svih klijenata
                        }
                        
                        //INITIALIZATION
                            //GEESE
                        if(infoSt.length==2 && infoSt[1].equals("GameRespYes")){
                            this.activeRole="geese";//podesava ulogu onoga koji je prihvatio na GEESE -> uvek onaj koji prihvata bude GEESE
                            this.activeOpponent=infoSt[0];//podesio protivnika nakon poslate GameRespYES
                            //this.playerInGame=true;
                        }
                        
                            //FOX
                        if(line.startsWith("Role:")){//kada inicijator igre primi potvrdu salje serveru poruku Role:fox:opponentName sto inicijalizuje igru
                            this.activeRole=infoSt[1];//ovde se podesava uloga FOXa koji je inicirao igru -> uvek onaj koji inicira bude FOX
                            this.activeOpponent=infoSt[2];//podesio protivnika nakon primljene GameRespYES i poslate Role:fox:opponentName
                            this.playerInGame=true;
                            
                            for(ConnectedF_GClient el:this.allClients){
                                if(el.getUserName().equals(infoSt[2])){
                                    el.setPlayerInGame(true);//podesi kod protivnika da je u igri
                                }
                            }
                            
                            //ovde bi trebalo podesiti inicijalne parametre igre, boju polja i pocetne pozicije, kao i ko prvi igra
                            //random odlucivanje o boji polja, 0 crno, 1 belo
                            Random rand = new Random();
                            int n = (rand.nextInt(10))%2;//opseg od 0 do 10 da ne bi generisao negativne
                            String color;
                            if(n==0) {color="black";}
                            else {color="white";}
                            this.fieldType=color;
                            
                            switch(n){
                                case 0:{
                                    //random fox starting point black
                                    setStartPositionsBlack(rand);
                                    break;
                                }
                                case 1:{
                                    //random fox starting point white
                                    setStartPositionsWhite(rand);
                                    break;
                                }
                                default:{
                                    setStartPositionsBlack(rand);
                                    break;
                                }
                            }
                            String foxCoor = "fox:" + this.foxPosition[0]+","+this.foxPosition[1];
                            String geeseCoor = "geese:";
                            for(int i=0;i<4;i++){
                                geeseCoor = geeseCoor + geesePosition[i][0] + "," + geesePosition[i][1] + ",";
                                }
                            //format of message Start:fox:7,0:geese:0,1,0,3,0,5,0,7,fieldType(black/white)
                            String startMessage = "Start:"+ foxCoor + ":" + geeseCoor + this.fieldType;
                            
                            for (ConnectedF_GClient clnt : this.allClients) {
                                if (clnt.getUserName().equals(this.activeOpponent)) {
                                        System.out.println(clnt.getUserName());
                                        clnt.setFoxPosition(this.foxPosition);//inicijalno postavljanje pozicije fox u ConnectedFGClient objektu protivnika 
                                        clnt.setGeesePosition(this.geesePosition);//inicijalno postavljanje pozicije geese u ConnectedFGClient objektu protivnika
                                        //slanje start poruke
                                        clnt.pw.println(startMessage);
                                        //console print for debug
                                        //System.out.println(clnt.getUserName()+startMessage+"protivnik"+this.activeOpponent);//treba da se poklope korisnici
                                        
                                        //clnt.pw.println(this.userName + ":" + poruka);//doda poruku na ime korisnika koji salje jer taj korisnik je u ovom objektu
                                                                                //this je zato sto samo posiljalac komunicira sa serverom u ovom objektu
                                    //System.out.println(primacKorisnik + ":" + poruka);//ovo ispisuje server na konzolu da obavesti
                                }
                            }
                            
                            //slanje startne poruke klijentu povezanom na ovaj objekat ConnectedFGClient
                            this.pw.println(startMessage);
                            //console print for debug
                            //System.out.println(this.userName+startMessage+this.activeOpponent);
                        }
                        
                        //AFTER ROLES ARE SET GAME CAN START
                        
                        
                        //------------------------ PLAY FUNCTIONALITY GOES HERE -----------------------
                        
                        
                        
                        
                        //ovo ce sluziti za prosledjivanje poruka sa koordinatama ANDROID APLIKACIJAMA
                        for (ConnectedF_GClient clnt : this.allClients) {
                            //for petlja prolazi kroz sve prihvacene klijente i uporedjuje klijenta sa onim kome se salje
                            if (clnt.getUserName().equals(primacKorisnik)) {
                                System.out.println(clnt.getUserName());
                                //prosledi poruku namenjenom korisniku
                                if(clnt.getPlayerInGame()==true && poruka.equals("GameReq")){
                                    this.pw.println("PLAYER IS IN GAME ! NOT AVAILABLE !");
                                    
                                }else{
                                    clnt.pw.println(this.userName + ":" + poruka);//doda poruku na ime korisnika koji salje jer taj korisnik je u ovom objektu
                                }                                                //this je zato sto samo posiljalac komunicira sa serverom u ovom objektu
                                System.out.println(primacKorisnik + ":" + poruka);//ovo ispisuje server na konzolu da obavesti
                            } else {
                                //ispisi da je korisnik kome je namenjena poruka odsutan
                                if (primacKorisnik.equals("")) {
                                    this.pw.println("Korisnik " + primacKorisnik + " je odsutan!");
                                }
                            }
                        }//for petlja koja prosledjuje poruku drugom korisniku iz formata ImePrimaoca:Poruka
                        //---------------------------------    for    --------------------------------------
                        
                    } else {
                        //slicno kao gore, ako je line null, klijent se diskonektovao
                        //ukloni tog korisnika iz liste povezanih korisnika u chat room-u
                        //i obavesti ostale da je korisnik napustio sobu
                        System.out.println("Disconnected user: " + this.userName);

                        //Ovako se uklanja element iz kolekcije 
                        //ne moze se prolaziti kroz kolekciju sa foreach a onda u 
                        //telu petlje uklanjati element iz te iste kolekcije
                        Iterator<ConnectedF_GClient> it = this.allClients.iterator();
                        while (it.hasNext()) {
                            if (it.next().getUserName().equals(this.userName)) {
                                it.remove();
                            }
                        }
                        connectedClientsUpdateStatus();

                        this.socket.close();
                        break;
                    }

                }
            } catch (IOException ex) {
                System.out.println("Disconnected user: " + this.userName);
                //npr, ovakvo uklanjanje moze dovesti do izuzetka, pogledajte kako je 
                //to gore uradjeno sa iteratorom
                for (ConnectedF_GClient cl : this.allClients) {
                    if (cl.getUserName().equals(this.userName)) {
                        this.allClients.remove(cl);
                        connectedClientsUpdateStatus();
                        return;
                    }
                }

            }

        }
    }
}
