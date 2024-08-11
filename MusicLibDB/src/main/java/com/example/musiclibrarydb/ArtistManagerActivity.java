package com.example.musiclibrarydb;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
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
import com.example.musiclibrarydb.sqlite.model.Artist;
import com.example.musiclibrarydb.sqlite.model.Genre;

import java.util.ArrayList;

public class ArtistManagerActivity extends AppCompatActivity {

    private ArrayList<Genre> availableGenres; // = new ArrayList<>();
    private ArrayList<Artist> allArtists;

    //Intent intent;

    DatabaseHandler databaseHandler;

    Button btnRemoveArtist;
    Button btnEditArtist;
    Button btnAddArtist;
    Button btnBackArtist;

    Spinner spnArtists;
    Spinner spnAvailableGenres;

    EditText etEditArtist;
    EditText etAddArtist;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_artist_manager);

        databaseHandler = new DatabaseHandler(getApplicationContext());

        btnRemoveArtist = (Button) ArtistManagerActivity.this.findViewById(R.id.btnRemoveArtist);
        btnEditArtist = (Button) ArtistManagerActivity.this.findViewById(R.id.btnEditArtist);
        btnAddArtist = (Button) ArtistManagerActivity.this.findViewById(R.id.btnAddArtist);
        btnBackArtist = (Button) ArtistManagerActivity.this.findViewById(R.id.btnBackArtist);

        spnArtists = (Spinner) ArtistManagerActivity.this.findViewById(R.id.spnArtists);
        spnAvailableGenres = (Spinner) ArtistManagerActivity.this.findViewById(R.id.spnAvailableGenres);

        etAddArtist = (EditText) ArtistManagerActivity.this.findViewById(R.id.etAddArtist);
        etEditArtist = (EditText) ArtistManagerActivity.this.findViewById(R.id.etEditArtist);

        //read all available artists and genres from tables
        allArtists = databaseHandler.getAllArtists();
        availableGenres = databaseHandler.getAllGenres();

        //load all genres into genre spinner
        loadGenreSpinner(availableGenres);
        //load all initial artists into spinner
        loadArtistSpinner(allArtists);
        if(spnArtists.getSelectedItem() != null) {
            etEditArtist.setText(spnArtists.getSelectedItem().toString());
        }

        btnAddArtist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(spnAvailableGenres.getSelectedItem() != null) {
                    Artist a = new Artist();
                    a.setName(etAddArtist.getText().toString());
                    a.setGenre_id(getGenreID(spnAvailableGenres.getSelectedItem().toString()));
                    databaseHandler.createArtist(a);
                    allArtists=databaseHandler.getAllArtists();
                    loadArtistSpinner(allArtists);
                }

            }
        });


        spnArtists.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedGenre = readSpinner(spnArtists);
                etEditArtist.setText(selectedGenre);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        btnBackArtist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToSelectorActivity();
            }
        });





    }//onCreate

    long getGenreID(String selectedGenreName){
        long fetchedID = 0;
        for(Genre el:availableGenres){
            if(el.getName().equals(selectedGenreName)){
                fetchedID=el.getId();
                break;
            }
        }

        return fetchedID;
    }

    void goToSelectorActivity(){
        Intent intent = new Intent(ArtistManagerActivity.this, SelectorActivity.class);
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

    void addArtist(){
        Artist a = new Artist();
        a.setName(etAddArtist.getText().toString());

        databaseHandler.createArtist(a);

    }

    void loadGenreSpinner(ArrayList<Genre> genres){
        ArrayList<String> genreNames = new ArrayList<>();
        for (Genre el : genres){
            genreNames.add(el.getName());
        }
        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, genreNames);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spnAvailableGenres.setAdapter(dataAdapter);

    }

    void loadArtistSpinner(ArrayList<Artist> artists){
        ArrayList<String> genreNames = new ArrayList<>();
        for (Artist el : artists){
            genreNames.add(el.getName());
        }
        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, genreNames);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spnArtists.setAdapter(dataAdapter);

    }

}