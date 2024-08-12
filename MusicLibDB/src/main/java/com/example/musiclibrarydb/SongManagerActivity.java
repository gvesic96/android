package com.example.musiclibrarydb;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.musiclibrarydb.sqlite.helper.DatabaseHandler;
import com.example.musiclibrarydb.sqlite.model.Artist;
import com.example.musiclibrarydb.sqlite.model.Genre;
import com.example.musiclibrarydb.sqlite.model.Song;

import java.util.ArrayList;

public class SongManagerActivity extends AppCompatActivity {

    private ArrayList<Artist> availableArtists; // = new ArrayList<>();
    private ArrayList<Song> allSongs;

    //Intent intent;

    DatabaseHandler databaseHandler;

    Button btnRemoveSong;
    Button btnEditSong;
    Button btnAddSong;
    Button btnBackSong;

    Spinner spnSongs;
    Spinner spnAvailableArtists;

    EditText etEditSong;
    EditText etAddSong;

    TextView tvGenre;
    TextView tvSong;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_song_manager);



        tvSong = (TextView) SongManagerActivity.this.findViewById(R.id.tvSong);
        tvGenre = (TextView) SongManagerActivity.this.findViewById(R.id.tvGenre);

        databaseHandler = new DatabaseHandler(getApplicationContext());

        btnRemoveSong = (Button) SongManagerActivity.this.findViewById(R.id.btnRemoveSong);
        btnEditSong = (Button) SongManagerActivity.this.findViewById(R.id.btnEditSong);
        btnAddSong = (Button) SongManagerActivity.this.findViewById(R.id.btnAddSong);
        btnBackSong = (Button) SongManagerActivity.this.findViewById(R.id.btnBackSong);

        spnSongs = (Spinner) SongManagerActivity.this.findViewById(R.id.spnSongs);
        spnAvailableArtists = (Spinner) SongManagerActivity.this.findViewById(R.id.spnAvailableArtists);

        etAddSong = (EditText) SongManagerActivity.this.findViewById(R.id.etAddSong);
        etEditSong = (EditText) SongManagerActivity.this.findViewById(R.id.etEditSong);

        //read all available artists and genres from tables
        allSongs = databaseHandler.getAllSongs();
        availableArtists = databaseHandler.getAllArtists();

        //load all artists into artist spinner
        loadArtistSpinner(availableArtists);
        //load all initial Songs into spinner
        loadSongSpinner(allSongs);
        if(spnSongs.getSelectedItem() != null) {
            etEditSong.setText(spnSongs.getSelectedItem().toString());

        }



        btnAddSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(spnAvailableArtists.getSelectedItem() != null) {
                    addSong();
                    etAddSong.setText("");
                }

            }
        });

        spnSongs.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedSong = readSpinner(spnSongs);
                etEditSong.setText(selectedSong);
                if(allSongs!=null) {
                    for (Song el : allSongs) {
                        if (el.getName().equals(selectedSong)) {
                            Artist a1 = databaseHandler.getArtist(el.getArtist_id());
                            Genre g1 = databaseHandler.getGenre(a1.getGenre_id());
                            String songData = el.getName() + ", " + a1.getName() + ", " + g1.getName();
                            tvSong.setText(songData);

                            break;
                        }
                    }
                }else{
                    etEditSong.setText("");
                    tvSong.setText("Song name, artist, genre");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spnAvailableArtists.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedArtist = readSpinner(spnAvailableArtists);
                if(availableArtists!=null) {
                    for (Artist el : availableArtists) {
                        if (el.getName().equals(selectedArtist)) {
                            Artist a1 = databaseHandler.getArtist(el.getId());
                            Genre g1 = databaseHandler.getGenre(a1.getGenre_id());
                            String artistData = a1.getName() + ", " + g1.getName();
                            tvGenre.setText(artistData);

                            break;
                        }
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        btnEditSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(spnSongs.getSelectedItem() != null) {
                    String newSName = etEditSong.getText().toString();
                    String oldSName = readSpinner(spnSongs);

                    for(Song el : allSongs){
                        if(el.getName().equals(oldSName)){
                            el.setName(newSName);
                            databaseHandler.updateSong(el);
                            allSongs = databaseHandler.getAllSongs();
                            loadSongSpinner(allSongs);
                        }
                    }
                }
            }
        });

        btnRemoveSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String removeSName = readSpinner(spnSongs);
                for(Song el:allSongs){
                    if(el.getName().equals(removeSName)) {
                        databaseHandler.removeSong(el.getId());
                        allSongs = databaseHandler.getAllSongs();
                        loadSongSpinner(allSongs);
                    }
                }
                if(spnSongs.getSelectedItem()==null){
                    tvSong.setText("Song name, artist, genre");
                    etEditSong.setText("");
                }

            }
        });




        btnBackSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToSelectorActivity();
            }
        });



        /*
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        */


    }

    long getArtistID(String selectedArtistName){
        long fetchedID = 0;
        for(Artist el:availableArtists){
            if(el.getName().equals(selectedArtistName)){
                fetchedID=el.getId();
                break;
            }
        }

        return fetchedID;
    }

    void goToSelectorActivity(){
        Intent intent = new Intent(SongManagerActivity.this, SelectorActivity.class);
        setResult(RESULT_OK, intent);
        finish();
    }

    //cita prosledjeni spinner i vraca selektovani item u obliku stringa
    String readSpinner(Spinner spn){
        String selectedItem="";
        if(spn.getSelectedItem() != null){
            selectedItem = spn.getSelectedItem().toString();
        }
        return selectedItem;
    }

    void addSong(){
        Song s = new Song();
        s.setName(etAddSong.getText().toString());
        s.setArtist_id(getArtistID(spnAvailableArtists.getSelectedItem().toString()));
        databaseHandler.createSong(s);

        //refresh all artists list
        allSongs=databaseHandler.getAllSongs();
        loadSongSpinner(allSongs);
    }

    void loadArtistSpinner(ArrayList<Artist> artists){
        ArrayList<String> artistNames = new ArrayList<>();
        for (Artist el : artists){
            artistNames.add(el.getName());
        }
        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, artistNames);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spnAvailableArtists.setAdapter(dataAdapter);

    }

    void loadSongSpinner(ArrayList<Song> songs){
        ArrayList<String> songNames = new ArrayList<>();
        for (Song el : songs){
            songNames.add(el.getName());
        }
        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, songNames);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spnSongs.setAdapter(dataAdapter);

    }






}