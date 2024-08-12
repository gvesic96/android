package com.example.musiclibrarydb;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.musiclibrarydb.sqlite.helper.DatabaseHandler;

public class SelectorActivity extends AppCompatActivity {

    private String username;
    private long id;

    Intent intent;

    Button btnGenreManager;
    Button btnArtistManager;
    Button btnSongManager;
    Button btnPlaylistManager;

    Button btnBack;
    Button btnLogout;

    DatabaseHandler databaseHandler;

    public static String LOGOUT_MESSAGE = "LOGOUT";
    public static String USER_ID_MESSAGE = "USER_ID";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_selector);

        btnGenreManager = SelectorActivity.this.findViewById(R.id.btnGenreManager);
        btnArtistManager = SelectorActivity.this.findViewById(R.id.btnArtistManager);
        btnSongManager = SelectorActivity.this.findViewById(R.id.btnSongManager);
        btnPlaylistManager = SelectorActivity.this.findViewById(R.id.btnPlaylistManager);
        btnBack = SelectorActivity.this.findViewById(R.id.btnBack);
        btnLogout = SelectorActivity.this.findViewById(R.id.btnLogout);


        intent = getIntent();
        SelectorActivity.this.username = (String) intent.getExtras().getString(MainActivity.USERNAME_MESSAGE);
        SelectorActivity.this.id = (long) intent.getExtras().getLong(MainActivity.ID_MESSAGE);

        //dobio novu instancu klase za rad sa bazom
        databaseHandler = new DatabaseHandler(getApplicationContext());


        btnGenreManager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //String helper = username + id;
                //helper = helper.toString();

                //btnArtistManager.setText(helper);//PRENEO SAM USERNAME I ID USPESNO !!! TREBA NAPRAVITI EVENTUALNO I VRACANJE i LOGOUT DUGME
                //Intent intentGenre = new Intent(SelectorActivity.this, GenreManagerActivity.class);
                //startActivity(intentGenre);
                goToGenreManagerActivity();

            }
        });

        btnArtistManager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //btnSongManager.setText(username+id);
                goToArtistManagerActivity();
            }
        });


        btnSongManager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToSongManagerActivity();
            }
        });

        btnPlaylistManager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToPlaylistManagerActivity();
            }
        });


        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToMainActivity();
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
            }
        });




        /*ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });*/
    }

    ActivityResultLauncher<Intent> activity2Launcher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {

                    if (result.getResultCode() == RESULT_OK){
                        Intent data = result.getData();

                        //String msg = (String) data.getExtras().getString(SelectorActivity.LOGOUT_MESSAGE);
                    }
                }
            }
    );

    public void goToGenreManagerActivity() {

        Intent intentGenreManager = new Intent(SelectorActivity.this, GenreManagerActivity.class);

        activity2Launcher.launch(intentGenreManager);


    }

    public void goToArtistManagerActivity() {

        Intent intentArtistManager = new Intent(SelectorActivity.this, ArtistManagerActivity.class);

        activity2Launcher.launch(intentArtistManager);


    }

    public void goToSongManagerActivity() {

        Intent intentSongManager = new Intent(SelectorActivity.this, SongManagerActivity.class);

        activity2Launcher.launch(intentSongManager);


    }

    public void goToPlaylistManagerActivity() {

        Intent intentPlaylistManager = new Intent(SelectorActivity.this, PlaylistManagerActivity.class);
        intentPlaylistManager.putExtra(USER_ID_MESSAGE, SelectorActivity.this.id);
        activity2Launcher.launch(intentPlaylistManager);


    }


    public void goToMainActivity(){
        Intent intent = new Intent(SelectorActivity.this, MainActivity.class);
        setResult(RESULT_OK, intent);
        finish();
    }

    public void logout(){
        Intent intent = new Intent(SelectorActivity.this, MainActivity.class);
        intent.putExtra(LOGOUT_MESSAGE, "LOGOUT");
        setResult(RESULT_OK, intent);
        finish();
    }
}