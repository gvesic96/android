/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package fgserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;



/**
 *
 * @author Gile
 */
public class FGServer {

    private ServerSocket ssocket;               //atribut ssocket tipa ServerSocket u kome ce smestiti socket preko kog se povezuje
    private int port;                           //atribut port sadrzi broj porta na koji se povezuje
    private ArrayList<ConnectedF_GClient> clients; //LISTA POVEZANIH KLIJENATA
    
    
    
    /**
     * @param args the command line arguments
     */
    
    //metoda za pribavljanje socketa, odnosno vraca socket
    public ServerSocket getSsocket() {
        return ssocket;
    }

    //metoda za postavljanje socketa, prima socket servera i postavlja njegovu vrednost u klasi
    public void setSsocket(ServerSocket ssocket) {
        this.ssocket = ssocket;
    }

    //metoda koja pribavlja port, odnosno vraca vrednost porta servera
    public int getPort() {
        return port;
    }

    //metoda koja postavlja port, odnosno prosledjuje vrednost koja ce biti postavljena kao port 
    public void setPort(int port) {
        this.port = port;
    }

    /**
     * Prihvata u petlji klijente i za svakog novog klijenta kreira novu nit. Iz
     * petlje se moze izaci tako sto se na tastaturi otkuca Exit.
     */
    
    //METODA KOJA CE U WHILE PETLJI PRIHVATITI NOVOG KLIJENTA
    public void acceptClients() {
        Socket client = null; //postavio client referencu na null za objekat tipa Socket
        Thread thr;
        //PETLJA ZA PRIHVATANJE KLIJENATA
        while (true) {
            
            try {
                System.out.println("Waiting for new clients..");
                client = this.ssocket.accept();//prihvatanje klijenta??? accept() je gotova metoda za prihvatanje klijenta na objektu tipa Socket
            } catch (IOException ex) {
                Logger.getLogger(FGServer.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            //OVAJ IF SLUZI AKO SE KLIJENT POVEZAO -> REFERENCA client JE RAZLICITA OD null ako je povezan
            if (client != null) {
                //Povezao se novi klijent, kreiraj objekat klase ConnectedChatRoomClient
                //koji ce biti zaduzen za komunikaciju sa njim
                ConnectedF_GClient clnt = new ConnectedF_GClient(client, clients); //prosledi konstruktoru socket privacenog klijenta i listu klijenata
                //i dodaj ga na listu povezanih klijenata jer ce ti trebati kasnije
                clients.add(clnt);//dodao objekat klase ConnectedChatRoomClient koji ce komunicirati sa klijentom na listu CLIENTS
                //kreiraj novu nit (konstruktoru prosledi klasu koja implementira Runnable interfejs)
                thr = new Thread(clnt);//kreira thread na kom ce se izvrsavati taj objekat i pokrene thread
                //..i startuj ga
                thr.start();//pokretanje RUN() metode u objektu clnt tipa ConnectedChatRoomClient 
            } else {
                break;
            }
        }//while
    }

    public FGServer(int port) {
        this.clients = new ArrayList<>(); //kreira novi objekat tipa ArrayList??? CEMU OVO SLUZI????
        try {
            this.port = port;                       //postavi port u ovaj objekat
            this.ssocket = new ServerSocket(port);  //kreira novi objekat klase ServerSocket sa datim portom i smesta ga u ssocket atribut
        } catch (IOException ex) {
            Logger.getLogger(FGServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    
    public static void main(String[] args) {
        //kreira objekat server klase ChatRoomServer koristeci konstruktor sa parametrom kome prosledi port 6001
        FGServer server = new FGServer(6001);//kreirao objekat klase chatroomserver kome je prosledio port 6001

        System.out.println("Server pokrenut, slusam na portu 6001");

        //Prihvataj klijente u beskonacnoj petlji
        server.acceptClients();
        //SA OVOM METODOM PRIHVATA KLIJENTE I POKRECE RUN metodu iz ConnectedChatRoomClient??
    }
    
}
