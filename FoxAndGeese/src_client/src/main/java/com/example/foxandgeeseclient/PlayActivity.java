package com.example.foxandgeeseclient;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.lang.Math;


public class PlayActivity extends AppCompatActivity {

    private Socket socket;
    private BufferedReader br;
    private PrintWriter pw;
    private String serverIP = "10.0.2.2";


    private String role;//ovde ce se podesavati trenutna uloga igraca
    private String opponent;//ovde ce se smestiti protivnik koji je poslao reqest / prihvatio request
    private String userName = "User";

    private String fieldType;
    private int foxPosition[] = {0,0};
    private int geesePosition[][] = {{0,0},{0,0},{0,0},{0,0}};

    int numRows = 8;
    int numColumns = 8;//5 ako su oba parna broja mogu se poklopiti
    HashMap<String, ImageView> images;

    private boolean field = true;
    private String selectedPos = "";
    private boolean playPermit = false;
    private boolean gameOver = false;

    public boolean getGameOver(){
        return this.gameOver;
    }
    public void setGameOver(boolean value){
        this.gameOver=value;
    }

    public void setPlayPermit(boolean playPermit) {
        this.playPermit = playPermit;
    }



    Intent intent;

    LinearLayout llmain;
    TextView tvExtras;
    Button btnPlayagain;
    Button btnYesAgain;
    Button btnNoAgain;



    public BufferedReader getBr(){
        return this.br;
    }

    //public static String ROLE_MESSAGE = "Role";
    //public static String REQUEST_MESSAGE = "Request_key";
    //public static String OPPONENT_MESSAGE = "Opponent";
    public static String USER_MESSAGE = "Username";
    public static String REFRESH_MESSAGE = "REFRESH";
    public static String IPADDRESS_MESSAGE = "ip";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_play);

            tvExtras = (TextView) findViewById(R.id.tvExtras);
            btnPlayagain = (Button) findViewById(R.id.btnPlayagain);
            btnYesAgain = (Button) findViewById(R.id.btnYesAgain);
            btnNoAgain = (Button) findViewById(R.id.btnNoAgain);

            btnPlayagain.setEnabled(false);
            btnYesAgain.setEnabled(false);
            btnNoAgain.setEnabled(true);

            images = new HashMap<String, ImageView>();
            llmain = (LinearLayout) findViewById(R.id.lvmain);


            intent = getIntent();
            String startMessage = (String) intent.getExtras().getString(MainActivity.REQUEST_MESSAGE);
            PlayActivity.this.role = (String) intent.getExtras().getString(MainActivity.ROLE_MESSAGE);
            PlayActivity.this.opponent = (String) intent.getExtras().getString(MainActivity.OPPONENT_MESSAGE);
            PlayActivity.this.userName = (String) intent.getExtras().getString(MainActivity.USERNAME_MESSAGE);
            PlayActivity.this.serverIP = (String) intent.getExtras().getString(MainActivity.IPADDRESS_MESSAGE);



            if(startMessage.startsWith("Start:")){
                String[] infoSt = startMessage.split(":");
                String[] foxSt = infoSt[2].split(",");

                for(int i=0;i<2;i++) {
                    foxPosition[i] = Integer.parseInt(foxSt[i]);
                }
                //geeseSt = 0,1,0,3,0,5,0,7
                String[] geeseSt = infoSt[4].split(",");//fieldType is leftover at geeseSt[8]

                int offset=0;
                for(int i=0;i<4;i++){
                    for(int j=0;j<2;j++){
                        geesePosition[i][j]=Integer.parseInt(geeseSt[j+offset]);
                    }
                    offset+=2;
                }

                if(role.equals("fox")){playPermit=true;}
                if(role.equals("geese")){playPermit=false;}

                fieldType = geeseSt[8];//black/white
            }

            //TESTING
            String foxString = Integer.toString(foxPosition[0]) + "," + Integer.toString(foxPosition[1]);
            String geeseString = "";
            for(int i=0; i<4; i++){
                for(int j=0; j<2; j++){
                    geeseString= geeseString +Integer.toString(geesePosition[i][j]) + ",";

                }
            }
            PlayActivity.this.tvExtras.setText(startMessage + role + opponent + fieldType + "fox"+foxString+"geese"+geeseString);

            //PlayActivity.this.connectToServer();//konektuj se na server// ponovo NE TREBA PONOVO DA SE KONEKTUJE

        SingletonSocket singleton = SingletonSocket.getInstance(serverIP);
        PlayActivity.this.socket = singleton.getSocket();
        PlayActivity.this.br = singleton.getBr();
        PlayActivity.this.pw = singleton.getPw();

        new Thread(new ReceiveMessageFromServer(null, PlayActivity.this)).start();



        //-----------------------------LINEAR LAYOUT/GAME BOARD-------------------------------------
        for (int row = 0; row < numRows; row++){
            LinearLayout llrow = new LinearLayout(this);
            llrow.setOrientation(LinearLayout.HORIZONTAL);
            for (int col = 0; col < numColumns; col++){
                ImageView iv = new ImageView(this);
                iv.setTag(row+","+col);
                images.put(row+","+col, iv);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0,120);
                layoutParams.weight = 1;
                iv.setLayoutParams(layoutParams);

                if(field) {
                    iv.setBackgroundColor(0xFFFFFF00);//YELLOW/WHITE
                    field=false;
                }else{
                    iv.setBackgroundColor(0xFFAD000F);//RED/BLACK
                    field=true;
                }
                if(col==7){
                    if(field==true){field=false;}
                    else{field=true;}
                }
                //nacrtaj lisicu na datim koordinatama
                if(foxPosition[0]==row && foxPosition[1]==col){iv.setImageResource(R.drawable.fox);}

                for(int i=0; i<4; i++){
                    if(geesePosition[i][0]==row && geesePosition[i][1]==col){
                        iv.setImageResource(R.drawable.goose);
                    }

                }

                iv.setOnClickListener((View v) ->{
                    //Toast.makeText(PlayActivity.this, "Kliknuo si na sliku "+v.getTag().toString(), Toast.LENGTH_SHORT).show();
                    String selectedTag = v.getTag().toString();

                    int command=-1;
                    if(selectedPos.equals("") && playPermit==true){command=0;}//pocetna pozicija nije selektovana
                    if(selectedPos.equals(selectedTag)){command=1;}//ponovo selektovana pocetna pozicija//deselektuj poziciju
                    if(!selectedPos.equals("") && !selectedPos.equals(selectedTag)){command=2;}//pocetna pozicija je selektovana

                    switch (command){
                        case 0:{
                            ImageView im = images.get(selectedTag);
                            if(role.equals("fox")) {
                                if(checkFoxSelect(selectedTag)) {
                                    im.setBackgroundColor(0xFFAAFFAA);//oboji pozadinu selektovane pocetne pozicije
                                    selectedPos = selectedTag;//sacuva selektovanu pocetnu poziciju u atribut
                                }
                            }
                            if(role.equals("geese")){
                                if(checkGeeseSelect(selectedTag)){
                                    im.setBackgroundColor(0xFFAAFFAA);//oboji pozadinu selektovane pocetne pozicije
                                    selectedPos = selectedTag;
                                }
                            }
                            break;
                        }
                        case 1:{
                            ImageView iv3 = images.get(selectedTag);
                            restoreField(selectedTag, iv3);
                            selectedPos="";//deselektovano polje

                            if(role.equals("fox")) {
                                iv3.setImageResource(R.drawable.fox);
                            }
                            if(role.equals("geese")){
                                iv3.setImageResource(R.drawable.goose);
                            }
                            break;
                        }
                        case 2:{
                            if(role.equals("fox") && playPermit){
                                ImageView iv1, iv2;
                                if(collisionCheck(selectedTag) && stepPermission(selectedTag, selectedPos)){
                                    //permitted movement
                                    iv1=images.get(selectedTag);
                                    iv1.setImageResource(R.drawable.fox);
                                    iv2=images.get(selectedPos);
                                    iv2.setImageResource(0);
                                    restoreField(selectedPos, iv2);//restore polja na kom se nalazila figura
                                    setFoxPosition(selectedTag);//NAKON OVOGA ISPITIVATI POBEDNIKA
                                    String moveMessage="";
                                    if(winnerCheck()){
                                        moveMessage = this.opponent + ":" + "Moved;" + selectedPos + ";" + selectedTag + ";" + role + ";" + "WIN";
                                        gameOver=true;
                                        btnPlayagain.setEnabled(true);
                                        Toast.makeText(PlayActivity.this, "WON", Toast.LENGTH_LONG).show();
                                        PlayActivity.this.tvExtras.setText("Player: "+userName+" has WON.");
                                    }else {
                                        moveMessage = this.opponent + ":" + "Moved;" + selectedPos + ";" + selectedTag + ";" + role;
                                    }
                                    sendMessage(moveMessage);
                                    playPermit=false;
                                    selectedPos="";
                                }
                            }
                            if(role.equals("geese") && playPermit){
                                ImageView iv1, iv2;
                                if(collisionCheck(selectedTag) && stepPermission(selectedTag, selectedPos) && geesePermission(selectedTag, selectedPos)){
                                    //permitted movement
                                    iv1=images.get(selectedTag);
                                    iv1.setImageResource(R.drawable.goose);
                                    iv2=images.get(selectedPos);
                                    iv2.setImageResource(0);
                                    restoreField(selectedPos, iv2);//restore polja na kom se nalazila figura
                                    setGeesePosition(selectedTag, selectedPos);//NAKON OVOGA ISPITIVATI POBEDNIKA
                                    winnerCheck();
                                    String moveMessage="";
                                    if(winnerCheck()){
                                        moveMessage = this.opponent+ ":" + "Moved;" + selectedPos + ";" +selectedTag + ";" + role + ";" + "WIN";
                                        gameOver=true;
                                        btnPlayagain.setEnabled(true);
                                        Toast.makeText(PlayActivity.this, "WON", Toast.LENGTH_LONG).show();
                                        PlayActivity.this.tvExtras.setText("Player: "+userName+" has WON.");
                                    }else {
                                        moveMessage = this.opponent + ":" + "Moved;" + selectedPos + ";" + selectedTag + ";" + role;
                                    }
                                    sendMessage(moveMessage);
                                    playPermit=false;
                                    selectedPos="";

                                }
                            }
                            break;
                        }
                        default: break;
                    }





                });
                llrow.addView(iv);


            }
            llmain.addView(llrow);
        }


        //-----------------------------HERE ENDS LINEAR LAYOUT--------------------------------------

        btnPlayagain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!PlayActivity.this.opponent.equals("")){
                    btnPlayagain.setEnabled(false);
                    String porukaZaSlanje = PlayActivity.this.opponent + ":" + "PlayAgain;Req";
                    //PlayActivity.this.setNewReceivedMessage(porukaZaIspis);
                    sendMessage(porukaZaSlanje);
                }
            }
        });

        btnYesAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!PlayActivity.this.opponent.equals("")){
                    btnYesAgain.setEnabled(false);
                    btnNoAgain.setEnabled(true);
                    String porukaZaSlanje = PlayActivity.this.opponent + ":" + "PlayAgain;YES";
                    //PlayActivity.this.setNewReceivedMessage(porukaZaIspis);
                    sendMessage(porukaZaSlanje);
                    resetTheBoard();//OVA FUNKCIJA RESETUJE TABLU I SVE PONOVO PODESAVA
                }
            }
        });

        btnNoAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!PlayActivity.this.opponent.equals("")){
                    btnPlayagain.setEnabled(false);
                    String porukaZaSlanje = PlayActivity.this.opponent + ":" + "PlayAgain;NO";
                    //PlayActivity.this.setNewReceivedMessage(porukaZaIspis);
                    sendMessage(porukaZaSlanje);

                    sendMessage("Break:Thread");//OVA PORUKA TREBA DA PINGPONGUJE NAZAD Player:PlayAgain;NO i da prekine thread
                    goToMainActivity();//PONOVO PREDJI U MAIN ACTIVITY
                }
            }
        });

    }

    //----------------------------------------------------------------------------------------------
    //--------------------------------------FUNCTIONS-----------------------------------------------

    void updateBoard(String figType, String newCoor, String oldCoor){
        if(figType.equals("fox")){
            ImageView im1 = images.get(newCoor);
            im1.setImageResource(R.drawable.fox);
        }
        if(figType.equals("geese")){
            ImageView im1 = images.get(newCoor);
            im1.setImageResource(R.drawable.goose);
        }
        ImageView im2 = images.get(oldCoor);
        restoreField(oldCoor, im2);
        im2.setImageResource(0);
    }

    boolean checkGeeseSelect(String value){
        boolean check=false;
        String[] infoPos = value.split(",");
        if(!check){
            for(int i=0; i<4; i++){
                if(geesePosition[i][0]==Integer.parseInt(infoPos[0]) && geesePosition[i][1] == Integer.parseInt(infoPos[1])) {
                    check=true;
                    break;
                }
            }
        }
        return check;
    }

    boolean checkFoxSelect(String value){
        boolean check=false;
        String[] infoPos = value.split(",");
        if(foxPosition[0] == Integer.parseInt(infoPos[0]) && foxPosition[1] == Integer.parseInt(infoPos[1])) {
            check = true;
        }
        return check;
    }

    void setFoxPosition(String value){
        String[] infoPos = value.split(",");
        foxPosition[0] = Integer.parseInt(infoPos[0]);
        foxPosition[1] = Integer.parseInt(infoPos[1]);
    }

    void setGeesePosition(String nextPosition, String prevPosition){
        String[] infoPosNext = nextPosition.split(",");
        String[] infoPosPrev = prevPosition.split(",");
        int gooseNumber=0;
        for(int i=0; i<4; i++){//nadji koja guska je pomerana
            if(geesePosition[i][0]==Integer.parseInt(infoPosPrev[0]) && geesePosition[i][1] == Integer.parseInt(infoPosPrev[1])) {
                gooseNumber=i;
                break;
            }
        }
        geesePosition[gooseNumber][0]=Integer.parseInt(infoPosNext[0]);
        geesePosition[gooseNumber][1]=Integer.parseInt(infoPosNext[1]);
    }


    void restoreField(String value, ImageView v){
        if(coordinateSum(value)==0) //suma je potrebna da bi znao kako da oboji u zavisnosti od 0 ili 1
        {
            v.setBackgroundColor(0xFFFFFF00);}
        else{
            v.setBackgroundColor(0xFFAD000F);
        }
    }

    int coordinateSum(String value){
        String[] infoPos = value.split(",");
        int sum=Integer.parseInt(infoPos[0])+Integer.parseInt(infoPos[1]);
        sum=sum%2;

        return sum;
    }

    boolean stepPermission(String nextPosition, String prevPosition){
        String[] infoPosNext = nextPosition.split(",");
        String[] infoPosPrev = prevPosition.split(",");
        boolean permit = false;
        int diff1=Integer.parseInt(infoPosNext[0])-Integer.parseInt(infoPosPrev[0]);
        int diff2=Integer.parseInt(infoPosNext[1])-Integer.parseInt(infoPosPrev[1]);
        diff1=Math.abs(diff1);
        diff2=Math.abs(diff2);
        //treba dodati proveru da li je fox ili goose na polju na koje se zeli preci

        if((diff1+diff2)==2 && diff1==diff2) permit=true;//OBE KOORDINATE SE RAZLIKUJU ZA PO 1 PO APSOLUTNOJ VREDNOSTI



        return permit;
    }

    boolean collisionCheck(String nextPos){
        //napraviti niz stringova od foxPosition i geesePosition pa proveriti
        boolean permit=true;

        String allFigures[] = {"","","","",""};
        for(int i=0; i<4; i++){
            allFigures[i] = geesePosition[i][0]+","+geesePosition[i][1];
        }

        allFigures[4] = foxPosition[0]+","+foxPosition[1];
        for(int i=0;i<5;i++){
            if(nextPos.equals(allFigures[i])){
                permit=false;//KADA NADJE U NIZU SVIH KOORDINATA NEKU DA SE POKLAPA SA nextPos zabrani pomeranje
                break;
            }
        }

        return permit;
    }

    boolean geesePermission(String nextPosition, String prevPosition){
        String[] infoPosNext = nextPosition.split(",");
        String[] infoPosPrev = prevPosition.split(",");
        boolean permit = false;

        if(Integer.parseInt(infoPosNext[0])>Integer.parseInt(infoPosPrev[0])){permit=true;}
        return permit;
    }

    boolean winnerCheck(){
        boolean win = false;
        if(role.equals("fox")){
            if(foxPosition[0]==0) win=true;
        }
        if(role.equals("geese")){
            if(foxCanNotMove()) win=true;
        }


        return win;
    }

    boolean foxCanNotMove(){
        boolean value=false;

        if(foxPosition[0]==7){//bottom side
            if(foxPosition[1]!=0 && foxPosition[1]!=7) {//bottom mid
                int field1[] = {foxPosition[0]-1, foxPosition[1]+1};
                int field2[] = {foxPosition[0]-1, foxPosition[1]-1};
                if(checkForGoose(field1) && checkForGoose(field2)) value=true;
            }
            if(foxPosition[1]==0){//bottom left
                int field[] = {6,1};
                if(checkForGoose(field)) value=true;
            }
            if(foxPosition[1]==7){//bottom right
                int field[] = {6,6};
                if(checkForGoose(field)) value=true;
            }
        }
        if(foxPosition[1]==0 && foxPosition[0]!=7){//left mid
            int field1[] = {foxPosition[0]+1, foxPosition[1]+1};
            int field2[] = {foxPosition[0]-1, foxPosition[1]+1};
            if(checkForGoose(field1) && checkForGoose(field2)) value=true;

        }
        if(foxPosition[1]==7 && foxPosition[0]!=0){//right mid
            int field1[] = {foxPosition[0]+1, foxPosition[1]-1};
            int field2[] = {foxPosition[0]-1, foxPosition[1]-1};
            if(checkForGoose(field1) && checkForGoose(field2)) value=true;

        }
        if(foxPosition[0]!=7 && foxPosition[1]!=0 && foxPosition[1]!=7){
            //ispituje sve 4 strane
            int field1[] = {foxPosition[0]-1, foxPosition[1]+1};
            int field2[] = {foxPosition[0]-1, foxPosition[1]-1};
            int field3[] = {foxPosition[0]+1, foxPosition[1]+1};
            int field4[] = {foxPosition[0]+1, foxPosition[1]-1};
            if(checkForGoose(field1) && checkForGoose(field2) && checkForGoose(field3) && checkForGoose(field4)){
                value=true;
            }

        }


        return value;
    }

    boolean checkForGoose(int pos[]){
        boolean g = false;
        for(int i=0; i<4; i++){//ispitati da li se na prosledjenoj poziciji nalazi goose
            if(pos[0]==geesePosition[i][0] && pos[1]==geesePosition[i][1]){
                g=true;
                break;
            }
        }
        return g;
    }

    void resetTheBoard(){//ova metoda resetuje tablu za ponovnu igru

        //zamena uloga
        if(PlayActivity.this.role.equals("fox")) {
            PlayActivity.this.role="geese";
        }
        else {
            PlayActivity.this.role="fox";
        }


        //sada napraviti niz od svih elemenata i resetovati polja a zatim ponovo podesiti sve
        String allFigures[] = {"","","","",""};
        for(int i=0; i<4; i++){
            allFigures[i] = geesePosition[i][0]+","+geesePosition[i][1];
        }
        allFigures[4] = foxPosition[0]+","+foxPosition[1];

        ImageView im;
        for(int i=0;i<5;i++){
            im = images.get(allFigures[i]);
            im.setImageResource(0);
            restoreField(allFigures[i], im);
        }
        //new positions default
        if(fieldType.equals("black")){
            fieldType="white";
            allFigures = new String[] {"0,0","0,2","0,4","0,6","7,1"};
            geesePosition = new int[][] {{0,0},{0,2},{0,4},{0,6}};
            foxPosition = new int[] {7,1};
        }else{
            fieldType="black";
            allFigures = new String[] {"0,1","0,3","0,5","0,7","7,4"};
            geesePosition = new int[][] {{0,1},{0,3},{0,5},{0,7}};
            foxPosition = new int[] {7,4};
        }
        for(int i=0; i<4; i++){
            im = images.get(allFigures[i]);
            im.setImageResource(R.drawable.goose);
        }
        im=images.get(allFigures[4]);
        im.setImageResource(R.drawable.fox);

        PlayActivity.this.gameOver=false;
        PlayActivity.this.selectedPos=""; //VAZNO VAZNO VAZNO

        if(PlayActivity.this.role.equals("fox")) playPermit=true;


        //uneti koordinate ponovo iz stringa u nizove MOZE HARDCODE GORE


    }


    //----------------------------------------------------------------------------------------------
    //-------------------------------------METHODS--------------------------------------------------
    public void sendMessage(String message){

        new Thread(new Runnable() {
            @Override
            public void run() {
                if (PlayActivity.this.pw != null){
                    PlayActivity.this.pw.println(message);
                }
            }
        }).start();
    }

    public void setNewReceivedMessage(String message){
        this.tvExtras.append(message + "\n");

        final int scrollAmount = this.tvExtras.getLayout().getLineTop(this.tvExtras.getLineCount()) - this.tvExtras.getHeight();
        if (scrollAmount > 0){
            this.tvExtras.scrollTo(0, scrollAmount);
        }
        else{
            this.tvExtras.scrollTo(0, 0);
        }
    }

    public void goToMainActivity() {
        Intent intent = new Intent(PlayActivity.this ,MainActivity.class);
        //intent.putExtra(REQUEST_MESSAGE, value);
        //intent.putExtra(ROLE_MESSAGE, MainActivity.this.role);
        //intent.putExtra(OPPONENT_MESSAGE, MainActivity.this.opponent);
        intent.putExtra(USER_MESSAGE, PlayActivity.this.userName);
        intent.putExtra(IPADDRESS_MESSAGE, PlayActivity.this.serverIP);
        intent.putExtra(REFRESH_MESSAGE, "REFRESH");

        setResult(RESULT_OK, intent);
        finish();

        //startActivity(intent);

    }

}