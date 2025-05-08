package com.example.foxandgeeseclient;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {

    TextView tvOutputMessages;
    EditText etNickName;
    EditText etMessage;
    Button btnEnterRoom;
    Button btnConnect;

    Spinner spnUsers;
    Button btnGameRequest;
    Button btnGameReqYes;
    Button btnGameReqNo;
    TextView tvGameReq;

    private Socket socket;
    private BufferedReader br;
    private PrintWriter pw;
    private String serverIP = "10.0.2.2";

    private String gameInitiator;//ovde smestiti username onoga koji je poslao zahtev za igru
    private String role;//ovde ce se podesavati trenutna uloga igraca
    private String opponent;//ovde ce se smestiti protivnik koji je poslao reqest / prihvatio reqest
    private String userName;

    private int foxPosition[] = {0,0};
    private int geesePosition[][] = {{0,0},{0,0},{0,0},{0,0}};

    Intent intent;
    private String refreshCommand = "";

//    private ReceiveMessageFromServer rmfs;

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

    public void setOpponent(String value){
        MainActivity.this.opponent = value;
    }

    public void setGameInitiator(String username){
        MainActivity.this.gameInitiator = username;
    }

    public void setRole(String value){
        MainActivity.this.role = value;
    }

    public void setResponseState(boolean value){
        MainActivity.this.btnGameReqYes.setEnabled(value);
        MainActivity.this.btnGameReqNo.setEnabled(value);
        String defaultMessage = "No pending game request.";
        if(!value) MainActivity.this.tvGameReq.setText(defaultMessage);
    }


    public static String ROLE_MESSAGE = "Role";
    public static String REQUEST_MESSAGE = "Request_key";
    public static String OPPONENT_MESSAGE = "Opponent";
    public static String USERNAME_MESSAGE = "Username";
    public static String IPADDRESS_MESSAGE = "IPADDRESS";


    //OCREATE METODA
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvOutputMessages = (TextView) findViewById(R.id.tvOutputMessages);
        tvOutputMessages.setMovementMethod(new ScrollingMovementMethod());
        etNickName = (EditText) findViewById(R.id.etNickname);
        etNickName.setEnabled(false);
        etMessage = (EditText) findViewById(R.id.etMessage);
        etMessage.setEnabled(true);
        btnEnterRoom = (Button) findViewById(R.id.btnEnterRoom);
        btnEnterRoom.setEnabled(false);
        btnConnect = (Button) findViewById(R.id.btnConnect);

        spnUsers = (Spinner) findViewById(R.id.spinner);
        spnUsers.setEnabled(false);

        btnGameRequest = (Button) findViewById(R.id.btnGameRequest);
        btnGameReqYes = (Button) findViewById(R.id.btnGameReqYes);
        btnGameReqYes.setEnabled(false);
        btnGameReqNo = (Button) findViewById(R.id.btnGameReqNo);
        btnGameReqNo.setEnabled(false);
        tvGameReq = (TextView) findViewById(R.id.tvGameReq);
        tvGameReq.setEnabled(false);


        //intent = getIntent();//prihvati intent pri lansiranju main aktivitija ponovo nakon play aktivitija//mozda ne treba?
        //if(intent.getExtras().getString(PlayActivity.USER_MESSAGE)!=null){
        //    MainActivity.this.userName = (String) intent.getExtras().getString(PlayActivity.USER_MESSAGE);
        //    MainActivity.this.refreshCommand = (String) intent.getExtras().getString(PlayActivity.REFRESH_MESSAGE);
        //}



        /*
        if(refreshCommand.equals("REFRESH")){
            MainActivity.this.etNickName.setText(MainActivity.this.userName);
            MainActivity.this.etNickName.setEnabled(false);
            MainActivity.this.btnGameReqNo.setEnabled(false);
            MainActivity.this.btnGameReqYes.setEnabled(false);
            MainActivity.this.btnConnect.setEnabled(false);
            MainActivity.this.btnEnterRoom.setEnabled(false);

            MainActivity.this.pw.println("Spinner:Update");
            refreshCommand="";

        }*/


        btnGameReqYes.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                if(!MainActivity.this.gameInitiator.equals("")){
                    String respMessage = MainActivity.this.gameInitiator + ":" + "GameRespYES";
                    sendMessage(respMessage);
                    MainActivity.this.setRole("geese");
                    MainActivity.this.setResponseState(false);
                    MainActivity.this.opponent=gameInitiator;//inicijator igre postaje protivnik ukoliko se prihvati zahtev za igru
                    MainActivity.this.setNewReceivedMessage("Protivnik je : " + MainActivity.this.opponent);//samo za testiranje
                }else {
                    if (MainActivity.this.etNickName.getText().toString().equals(MainActivity.this.spnUsers.getSelectedItem().toString())) {
                        Toast.makeText(MainActivity.this, "Ne mozete igrati sami sa sobom", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        btnGameReqNo.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                if(!MainActivity.this.gameInitiator.equals("")){
                    String respMessage = MainActivity.this.gameInitiator + ":" + "GameRespNO";
                    sendMessage(respMessage);
                    MainActivity.this.setResponseState(false);
                }else {
                    if (MainActivity.this.etNickName.getText().toString().equals(MainActivity.this.spnUsers.getSelectedItem().toString())) {
                        Toast.makeText(MainActivity.this, "Ne mozete igrati sami sa sobom", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        this.btnGameRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(MainActivity.this.etNickName.getText().toString().equals("")==false &&
                        !MainActivity.this.etNickName.getText().toString().equals(MainActivity.this.spnUsers.getSelectedItem().toString())){
                    String reqMessage = MainActivity.this.spnUsers.getSelectedItem().toString() + ":" +"GameReq";
                    sendMessage(reqMessage);
                }else {
                    if (MainActivity.this.etNickName.getText().toString().equals(MainActivity.this.spnUsers.getSelectedItem().toString())) {
                        Toast.makeText(MainActivity.this, "Ne mozete igrati sami sa sobom", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                serverIP = MainActivity.this.etMessage.getText().toString();
                connectToServer();
                MainActivity.this.etMessage.setEnabled(false);
                MainActivity.this.btnEnterRoom.setEnabled(true);
                MainActivity.this.etNickName.setEnabled(true);
                MainActivity.this.btnConnect.setEnabled(false);

            }
        });

        this.btnEnterRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!MainActivity.this.etNickName.getText().toString().equals("")){
                    MainActivity.this.userName = MainActivity.this.etNickName.getText().toString();
                    sendMessage(MainActivity.this.etNickName.getText().toString());

                    //----------------------------------------------------------------------------------------KREIRA THREAD-----------
                    //--------------------------------------------------------------------------------------------THREAD THREAD THREAD
                    new Thread(new ReceiveMessageFromServer(MainActivity.this, null)).start();

                    //Dozvoli koriscenje odredjenih komponenti, a zabrani ostale
                    MainActivity.this.spnUsers.setEnabled(true);

                    MainActivity.this.etNickName.setEnabled(false);
                    MainActivity.this.btnEnterRoom.setEnabled(false);
                }
                else {
                    //azuriraj status komponenti
                    MainActivity.this.spnUsers.setEnabled(false);
                    MainActivity.this.etMessage.setEnabled(false);

                }
            }
        });


    }

    ActivityResultLauncher<Intent> activity2Launcher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {

                    if (result.getResultCode() == RESULT_OK){
                        Intent data = result.getData();
                        //String response;
                        //response = data.getStringExtra(PlayActivity.RESPONSE_MESSAGE);
                        MainActivity.this.userName = (String) data.getExtras().getString(PlayActivity.USER_MESSAGE);
                        MainActivity.this.refreshCommand = (String) data.getExtras().getString(PlayActivity.REFRESH_MESSAGE);
                        MainActivity.this.serverIP = (String) data.getExtras().getString(PlayActivity.IPADDRESS_MESSAGE);

                        if(refreshCommand.equals("REFRESH")){
                            MainActivity.this.etNickName.setText(MainActivity.this.userName);
                            MainActivity.this.etNickName.setEnabled(false);
                            MainActivity.this.btnGameReqNo.setEnabled(false);
                            MainActivity.this.btnGameReqYes.setEnabled(false);
                            MainActivity.this.btnConnect.setEnabled(false);
                            MainActivity.this.btnEnterRoom.setEnabled(false);
                            MainActivity.this.refreshCommand="";

                            new Thread(new ReceiveMessageFromServer(MainActivity.this, null)).start(); //------------------- PONOVO KREIRA THREAD

                            SingletonSocket singleton = SingletonSocket.getInstance(serverIP);
                            MainActivity.this.socket = singleton.getSocket();
                            MainActivity.this.br = singleton.getBr();
                            MainActivity.this.pw = singleton.getPw();

                            //MainActivity.this.pw.println("Spinner:Update");//PORUKE SE SALJU SAMO U POSEBNOM THREADU ZATO CRASHUJE !!!!
                            sendMessage("Spinner:Update");
                        }
                    }
                }
            }
    );

    public BufferedReader getBr(){
        return this.br;
    }

    public Spinner getUsers(){
        return this.spnUsers;
    }

    public void setNewReceivedMessage(String message){
        this.tvOutputMessages.append(message + "\n");

        final int scrollAmount = this.tvOutputMessages.getLayout().getLineTop(this.tvOutputMessages.getLineCount()) - this.tvOutputMessages.getHeight();
        if (scrollAmount > 0){
            this.tvOutputMessages.scrollTo(0, scrollAmount);
        }
        else{
            this.tvOutputMessages.scrollTo(0, 0);
        }
    }

    public void connectToServer(){

        new Thread(new Runnable() {
            @Override
            public void run() {

                SingletonSocket singleton = SingletonSocket.getInstance(serverIP);
                MainActivity.this.socket = singleton.getSocket();
                MainActivity.this.br = singleton.getBr();
                MainActivity.this.pw = singleton.getPw();

            }
        }).start();
    }

    //metoda za prebacivanje u drugu aktivnost
    public void goToPlayActivity(String value) {
        String msg=value;
        Intent intentPlay = new Intent(MainActivity.this, PlayActivity.class);
        intentPlay.putExtra(REQUEST_MESSAGE, msg);
        intentPlay.putExtra(ROLE_MESSAGE, MainActivity.this.role);
        intentPlay.putExtra(OPPONENT_MESSAGE, MainActivity.this.opponent);
        intentPlay.putExtra(USERNAME_MESSAGE, MainActivity.this.userName);
        intentPlay.putExtra(IPADDRESS_MESSAGE, MainActivity.this.serverIP);

        activity2Launcher.launch(intentPlay);
        //startActivity(intentPlay);

    }

    public void sendMessage(String message){

        new Thread(new Runnable() {
            @Override
            public void run() {
                if (MainActivity.this.pw != null){
                    MainActivity.this.pw.println(message);
                }
            }
        }).start();
    }
}