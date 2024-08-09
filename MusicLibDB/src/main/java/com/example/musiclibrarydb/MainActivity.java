package com.example.musiclibrarydb;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import com.example.musiclibrarydb.sqlite.helper.DatabaseHandler;
import com.example.musiclibrarydb.sqlite.model.Artist;
import com.example.musiclibrarydb.sqlite.model.Genre;
import com.example.musiclibrarydb.sqlite.model.User;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    DatabaseHandler databaseHandler;

    Button btnLogin;
    Button btnSignUp;
    EditText etUsername;
    EditText etPass;


    private String username;
    private String password;
    private long id;



    //PROBLEM JE STO PRI IZLASKU IZ AKTIVNOSTI ZATVORI BAZU sa closeDB();
    /*@Override
    protected void onStop() {
        super.onStop();
        databaseHandler.closeDB();

    }*/

    public static String USERNAME_MESSAGE = "username";
    public static String ID_MESSAGE = "user_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        etPass = (EditText) MainActivity.this.findViewById(R.id.etPass);
        etUsername = (EditText) MainActivity.this.findViewById(R.id.etUsername);
        btnLogin = (Button) MainActivity.this.findViewById(R.id.btnLogin);
        btnSignUp = (Button) MainActivity.this.findViewById(R.id.btnSignUp);



        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.this.username = MainActivity.this.etUsername.getText().toString();
                MainActivity.this.password = MainActivity.this.etPass.getText().toString();

                boolean permit = false;

                ArrayList<User> allUsers;
                allUsers = databaseHandler.getAllUsers();

                for(User el : allUsers){
                    if(el.getName().equals(MainActivity.this.username) && el.getPassword().equals(MainActivity.this.password)){
                        permit=true;
                        MainActivity.this.id = el.getId();
                        break;
                    }
                }
                if(permit){
                    goToSelectorActivity();
                }else{
                    Toast.makeText(MainActivity.this, "Wrong username or password. Sign up if you already arent!", Toast.LENGTH_LONG).show();

                }

            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.this.username = MainActivity.this.etUsername.getText().toString();
                MainActivity.this.password = MainActivity.this.etPass.getText().toString();

                ArrayList<User> allUsers;
                allUsers = databaseHandler.getAllUsers();

                boolean permit = false;//po defaultu je true
                if(!MainActivity.this.password.equals("")) permit=true;//ako je unet password

                for(User el : allUsers){
                    if(el.getName().equals(MainActivity.this.username)){
                        permit=false;
                        break;
                    }
                }

                if(permit){
                    //ovde treba dodati novog korisnika u bazu
                    User u = new User(MainActivity.this.username, MainActivity.this.password);
                    MainActivity.this.id = databaseHandler.createUser(u);
                    goToSelectorActivity();
                    Toast.makeText(MainActivity.this, "You successfully signed in!", Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(MainActivity.this, "Username is taken, try another one!", Toast.LENGTH_LONG).show();

                }

            }
        });


        databaseHandler = new DatabaseHandler(getApplicationContext());
        createAllTables();


        /*ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;


        });
        */
    }

    //ovo je za vracanje iz druge aktivnosti???
    ActivityResultLauncher<Intent> activity2Launcher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {

                    if (result.getResultCode() == RESULT_OK){
                        Intent data = result.getData();
                        String msg="";
                        if(data.getExtras() != null) {
                            msg = (String) data.getExtras().getString(SelectorActivity.LOGOUT_MESSAGE);
                        }
                        if(msg.equals("LOGOUT")){
                            MainActivity.this.id = 0;//najnizi ID iz tabele je 1, ne moze biti nula
                            MainActivity.this.username = "";
                            MainActivity.this.password = "";
                            MainActivity.this.etUsername.setText("");
                            MainActivity.this.etPass.setText("");
                            Toast.makeText(MainActivity.this, "Successfully logged out!", Toast.LENGTH_LONG).show();
                        }
                    }
                }
            }
    );

    public void goToSelectorActivity() {

        Intent intentSelector = new Intent(MainActivity.this, SelectorActivity.class);
        intentSelector.putExtra(USERNAME_MESSAGE, MainActivity.this.username);
        intentSelector.putExtra(ID_MESSAGE, MainActivity.this.id);

        activity2Launcher.launch(intentSelector);
        //startActivity(intentSelector);

    }


    void createAllTables(){
        databaseHandler.createTables();

        //ako je baza PRAZNA dodaj glumce i rezisere kao objekte klasa Actor i Director
        if (databaseHandler.getAllUsers().size() == 0) {
            User u1 = new User("Brad Pitt", "omg");//prosledjuje parametre konstruktoru
            User u2 = new User("Edward Norton", "ffs");
            User u3 = new User("Samuel L. Jackson", "rofl");
            User u4 = new User("Bruce Willis", "lol");
            User u5 = new User("Leonardo DiCaprio", "fag");
            User u6 = new User("Matt Damon", "will");

            //kreira 6 koristnika u tabeli users
            databaseHandler.createUser(u1);
            databaseHandler.createUser(u2);
            databaseHandler.createUser(u3);
            databaseHandler.createUser(u4);
            databaseHandler.createUser(u5);
            databaseHandler.createUser(u6);


        }

    }





}