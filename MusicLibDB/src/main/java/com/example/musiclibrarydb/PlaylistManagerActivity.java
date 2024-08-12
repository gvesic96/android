package com.example.musiclibrarydb;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.musiclibrarydb.sqlite.helper.DatabaseHandler;
import com.example.musiclibrarydb.sqlite.model.Playlist;
import com.example.musiclibrarydb.sqlite.model.Song;

import java.util.ArrayList;

public class PlaylistManagerActivity extends AppCompatActivity {

    private ArrayList<Playlist> allPlaylists;

    DatabaseHandler databaseHandler;

    Button btnAddPlaylist;
    Button btnEditPlaylist;
    Button btnRemovePlaylist;
    Button btnPutSongPL;
    Button btnRemoveSongPL;
    Button btnBackPlaylist;

    Spinner spnPlaylists;
    Spinner spnGenresPL;
    Spinner spnArtistsPL;
    Spinner spnAvailableSongsPL;
    Spinner spnSongsInPL;

    EditText etEditAddPlaylist;

    Intent intent;

    private long user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_playlist_manager);

        intent = getIntent();
        //PlaylistManagerActivity.this.username = (String) intent.getExtras().getString(MainActivity.USERNAME_MESSAGE);
        PlaylistManagerActivity.this.user_id = (long) intent.getExtras().getLong(SelectorActivity.USER_ID_MESSAGE);

        databaseHandler = new DatabaseHandler(getApplicationContext());

        btnAddPlaylist = (Button) PlaylistManagerActivity.this.findViewById(R.id.btnAddPlaylist);
        btnEditPlaylist = (Button) PlaylistManagerActivity.this.findViewById(R.id.btnEditPlaylist);
        btnRemovePlaylist = (Button) PlaylistManagerActivity.this.findViewById(R.id.btnRemovePlaylist);

        btnPutSongPL = (Button) PlaylistManagerActivity.this.findViewById(R.id.btnPutSongPL);
        btnRemoveSongPL = (Button) PlaylistManagerActivity.this.findViewById(R.id.btnRemoveSongPL);
        btnBackPlaylist = (Button) PlaylistManagerActivity.this.findViewById(R.id.btnBackPlaylist);


        spnPlaylists = (Spinner) PlaylistManagerActivity.this.findViewById(R.id.spnPlaylists);
        spnGenresPL = (Spinner) PlaylistManagerActivity.this.findViewById(R.id.spnGenresPL);
        spnArtistsPL = (Spinner) PlaylistManagerActivity.this.findViewById(R.id.spnArtistsPL);
        spnAvailableSongsPL = (Spinner) PlaylistManagerActivity.this.findViewById(R.id.spnAvailableSongsPL);
        spnSongsInPL = (Spinner) PlaylistManagerActivity.this.findViewById(R.id.spnSongsInPL);


        etEditAddPlaylist = (EditText) PlaylistManagerActivity.this.findViewById(R.id.etEditAddPlaylist);


        allPlaylists = databaseHandler.getAllPlaylists(user_id);
        loadPlaylistSpinner(allPlaylists);




        btnAddPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addPlaylist();
            }
        });

        btnBackPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToSelectorActivity();
            }
        });



        /*ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });*/
    }

    void goToSelectorActivity(){
        Intent intent = new Intent(PlaylistManagerActivity.this, SelectorActivity.class);
        setResult(RESULT_OK, intent);
        finish();
    }

    void addPlaylist(){
        String playlistName = etEditAddPlaylist.getText().toString();
        Playlist pl = new Playlist();
        pl.setName(playlistName);
        pl.setUser_id(PlaylistManagerActivity.this.user_id);
        databaseHandler.createPlaylist(pl);
        allPlaylists = databaseHandler.getAllPlaylists(user_id);//potrebno je selektovati sve plejliste iz baze za prosledjeni USER_ID
        loadPlaylistSpinner(allPlaylists);
    }

    void loadPlaylistSpinner(ArrayList<Playlist> playlists){
        ArrayList<String> playlistNames = new ArrayList<>();
        for (Playlist el : playlists){
            playlistNames.add(el.getName());
        }
        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, playlistNames);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spnPlaylists.setAdapter(dataAdapter);

    }

}