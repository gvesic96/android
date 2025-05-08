package com.example.foxandgeeseclient;


import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;

public class ReceiveMessageFromServer implements Runnable {

    MainActivity parent;
    BufferedReader br;

    PlayActivity parent2;

    private boolean playAgainButton = true;
    private boolean yesAgainButton = true;
    private boolean noAgainButton = true;
    private String msg = " ";
    private boolean resetBoardPermit = false;

    private boolean breakTheThread = false;


    public ReceiveMessageFromServer(MainActivity parent, PlayActivity parent2){
        if(parent!=null) {
            this.parent = parent;
            this.br = parent.getBr();
        }
        if(parent2!=null){
            this.parent2 = parent2;
            this.br = parent2.getBr();
        }
    }

    @Override
    public void run(){
        while(true){
            String line;
            //--------------------------------------------- GAME INITIALIZATION --------------------------------------------
            if(parent!=null) {//OVAJ IF SLUZI DA SE ODREDI KO JE KREIRAO THREAD

                try {
                    line = this.br.readLine();


                    if (line.startsWith("Users: ")) {


                        String[] imena = line.split(":")[1].trim().split(" ");


                        parent.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // Brisanje trenutnog sadrzaja Spinner-a
                                parent.getUsers().setAdapter(null);
                                // Popunjavanje Spinner-a sa novim podacima u vezi sa prisutnim korisnicima
                                // Prvo uzmi referencu na spiner iz glavne aktivnosti
                                Spinner spinner = parent.getUsers();
                                // Napravi ArrayAdapter na osnovu imena korisnika prepoznatih iz poruke servera
                                // i postavi taj adapter da bude adapter zeljenog spiner-a
                                ArrayAdapter<String> adapter = new ArrayAdapter<>(parent, android.R.layout.simple_spinner_item, imena);
                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                spinner.setAdapter(adapter);
                                parent.setNewReceivedMessage("Novi clan se prikljucio ili je postojeci napustio sobu! Tretnutni clanovi su: " + line.split(": ")[1].toString());
                            }
                        });
                    } else {
                        //ovde se obradjuju poruke koje NE POCINJU sa Users:
                        //parsiranje poruke - username:GameReq
                        String[] infoSt = line.split(":");


                        //------------------------------------------------ GAME INITIALIZATION -----------------------------------------------
                        if (line.startsWith("Start:")) {
                            String[] foxSt = infoSt[2].split(",");
                            int foxCoor[] = {0, 0};
                            for (int i = 0; i < 2; i++) {
                                foxCoor[i] = Integer.parseInt(foxSt[i]);
                            }
                            //geeseSt = 0,1,0,3,0,5,0,7
                            String[] geeseSt = infoSt[4].split(",");//fieldType is leftover at geeseSt[8]
                            int geeseCoor[][] = {{0, 0}, {0, 0}, {0, 0}, {0, 0}};
                            int offset = 0;
                            for (int i = 0; i < 4; i++) {
                                for (int j = 0; j < 2; j++) {
                                    geeseCoor[i][j] = Integer.parseInt(geeseSt[j + offset]);
                                }
                                offset += 2;
                            }//for

                            //ovde treba proslediti
                            parent.setFoxPosition(foxCoor);
                            parent.setGeesePosition(geeseCoor);

                            parent.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    parent.setNewReceivedMessage(line);
                                    parent.goToPlayActivity(line);
                                }

                            });
                            break;//OVAJ BREAK BI TREBALO DA PREKINE I UKLONI THREAD KOJI JE KREIRAN IZ MAIN ACTIVITY
                        }

                        //infoSt[0] je ime onoga koji salje GameReq zahtev i njega smestam u gameInitiator string glavne aktivnosti
                        if (infoSt.length == 2 && infoSt[1].equals("GameReq")) {
                            parent.setGameInitiator(infoSt[0]);
                            String reqMsg = infoSt[0] + " is asking for game...";

                            parent.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    parent.tvGameReq.setText(reqMsg);
                                    parent.setResponseState(true);
                                }
                            });

                        }

                        if (infoSt.length == 2 && infoSt[1].equals("GameRespYES")) {
                            parent.setRole("fox");
                            parent.setOpponent(infoSt[0]);//taj koji je odgovorio potvrdno na GameReq
                            //sada jos treba serveru javiti koja je uloga
                            parent.sendMessage("Role:fox" + ":" + infoSt[0]); //sending message Role:fox:OpponentName for server
                        }

                        // Poruka koja je stigla je zapravo poruka koja je stigla za nas od nekog
                        // drugog korisnika. Prikazi poruku koja je primljena u polju za prijem poruka
                        parent.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //parent.setResponseState(true);
                                parent.setNewReceivedMessage(line);
                            }

                        });

                    }


                } catch (IOException ex) {
                    Toast.makeText(parent, "Ne mogu da primim poruku!", Toast.LENGTH_LONG).show();

                }
            }
            //---------------------------------------------- GAME RUNNING -----------------------------------------------

            String line2;
            if(parent2!=null){//THREAD KREIRAN IZ DRUGE AKTIVNOSTI
                try{

                    line2 = this.br.readLine();

                    String[] infoSt = line2.split(":");
                    if(infoSt[1].startsWith("Moved")){
                        String[] infoSt2 = infoSt[1].split(";");
                        String oldCoor = infoSt2[1];
                        String newCoor = infoSt2[2];
                        String oppRole = infoSt2[3];//potrebno da bi znao koju sliku da postavi na novu poziciju

                        parent2.setPlayPermit(true);//kada primi poruku postavi dozvolu na true

                        String result = "";
                        if(infoSt2.length==5){
                            result=infoSt2[4];
                        }
                        if(result.equals("WIN")){
                            parent2.setPlayPermit(false);//ukoliko je odredjen pobednik zabrani pomeranje figura
                            parent2.setGameOver(true);
                        }

                        if(oppRole.equals("fox")){
                            parent2.setFoxPosition(newCoor);
                        }
                        if(oppRole.equals("geese")){
                            parent2.setGeesePosition(newCoor, oldCoor);//potrebne i stare koordinate kako bi znao koja je guska pomerana
                        }


                        parent2.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(parent2.getGameOver()){
                                    parent2.btnPlayagain.setEnabled(true);
                                    parent2.setNewReceivedMessage("Player: "+ infoSt[0] + " has WON.");
                                    Toast.makeText(parent2, "LOST", Toast.LENGTH_LONG).show();
                                }

                                parent2.updateBoard(oppRole, newCoor, oldCoor);
                                //parent2.setNewReceivedMessage(line2);//zakomentarisati kasnije
                            }
                        });

                    }


                    if(infoSt[1].startsWith("PlayAgain")){
                        String[] infoPA = infoSt[1].split(";");

                        int comm = 0;
                        if(infoPA[1].equals("Req")) comm=1;
                        if(infoPA[1].equals("YES")) comm=2;
                        if(infoPA[1].equals("NO")) comm=3;


                        switch(comm){
                            case 1: {//Player:PlayAgain;Req

                                msg=infoSt[0]+" is requesting to REMATCH.";
                                playAgainButton = false;
                                yesAgainButton = true;
                                noAgainButton = true;
                                //Toast.makeText(parent2, infoSt[0]+" is requesting to REMATCH.", Toast.LENGTH_LONG).show();

                                break;
                            }
                            case 2: {//Player:PlayAgain;YES
                                msg=infoSt[0]+" accepted REMATCH.";
                                playAgainButton = false;
                                yesAgainButton = false;
                                noAgainButton = true;
                                resetBoardPermit = true;
                                break;
                            }
                            case 3: {//Player:PlayAgain;NO
                                if(parent2.getGameOver()) {
                                    msg=infoSt[0]+" did not accept REMATCH.";
                                }else{
                                    msg=infoSt[0]+" left the game. Switching to MainActivity.";
                                }

                                break;
                            }
                            default: break;
                        }

                        if(comm==3){
                            parent2.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(parent2, msg, Toast.LENGTH_LONG).show();
                                    parent2.setNewReceivedMessage(msg);
                                    parent2.goToMainActivity();
                                }
                            });

                            break; //OVAJ BREAK TREBA DA UKLONI THREAD KOJI JE KREIRAN IZ PLAY ACTIVITY KADA PRIMI PORUKU
                        }

                        if(comm!=3) {
                            parent2.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(parent2, msg, Toast.LENGTH_LONG).show();
                                    parent2.btnYesAgain.setEnabled(yesAgainButton);
                                    parent2.btnNoAgain.setEnabled(noAgainButton);
                                    parent2.btnPlayagain.setEnabled(playAgainButton);
                                    parent2.setNewReceivedMessage(msg);

                                    //ovde treba dodati resetTheBoard koji ce se takodje pozivati i iz playActivitya klikom na YES
                                    if (resetBoardPermit) {
                                        parent2.resetTheBoard();
                                        resetBoardPermit = false;
                                    }
                                }
                            });
                        }
                    }

                } catch (IOException ex){
                    Toast.makeText(parent2, "Ne mogu da primim poruku!", Toast.LENGTH_LONG).show();
                }


            }
        }

    }


}
