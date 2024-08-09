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
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.musiclibrarydb.sqlite.helper.DatabaseHandler;
import com.example.musiclibrarydb.sqlite.model.Genre;
import com.example.musiclibrarydb.sqlite.model.User;

import java.util.ArrayList;


public class GenreManagerActivity extends AppCompatActivity {


    private ArrayList<Genre> allGenres; // = new ArrayList<>();

    //Intent intent;

    DatabaseHandler databaseHandler;

    Button btnRemoveGenre;
    Button btnEditGenre;
    Button btnAddGenre;
    Button btnBackGenre;

    Spinner spnGenres;

    EditText etEditGenre;
    EditText etAddGenre;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_genre_manager);


        //getIntent();
        databaseHandler = new DatabaseHandler(getApplicationContext());

        btnRemoveGenre = (Button) GenreManagerActivity.this.findViewById(R.id.btnRemoveGenre);
        btnEditGenre = (Button) GenreManagerActivity.this.findViewById(R.id.btnEditGenre);
        btnAddGenre = (Button) GenreManagerActivity.this.findViewById(R.id.btnAddGenre);
        btnBackGenre = (Button) GenreManagerActivity.this.findViewById(R.id.btnBackGenre);

        spnGenres = (Spinner) GenreManagerActivity.this.findViewById(R.id.spnGenres);

        etAddGenre = (EditText) GenreManagerActivity.this.findViewById(R.id.etAddGenre);
        etEditGenre = (EditText) GenreManagerActivity.this.findViewById(R.id.etEditGenre);




        //get all loaded genres from the TABLE_GENRES
        allGenres = databaseHandler.getAllGenres();
        loadGenreSpinner(allGenres);
        if(spnGenres.getSelectedItem() != null) {
            etEditGenre.setText(spnGenres.getSelectedItem().toString());
        }

        btnAddGenre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newGenre;
                newGenre = GenreManagerActivity.this.etAddGenre.getText().toString();

                if(!newGenre.equals("")) {
                    addGenre(newGenre);

                    allGenres = databaseHandler.getAllGenres();
                    loadGenreSpinner(allGenres);

                    etAddGenre.setText("");
                }
            }
        });





        spnGenres.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedGenre = readSpinner();
                etEditGenre.setText(selectedGenre);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        btnEditGenre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String oldGName;
                String newGName;
                newGName = etEditGenre.getText().toString();
                oldGName = readSpinner();
                //ArrayList<Genre> allG = databaseHandler.getAllGenres();
                for(Genre el : allGenres){
                    if(el.getName().equals(oldGName)){
                        el.setName(newGName);
                        databaseHandler.updateGenre(el);
                        allGenres = databaseHandler.getAllGenres();
                        loadGenreSpinner(allGenres);
                    }
                }


            }
        });


        btnRemoveGenre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String removeGName = readSpinner();
                for(Genre el:allGenres){
                    if(el.getName().equals(removeGName)) {
                        databaseHandler.removeGenre(el.getId());
                        allGenres = databaseHandler.getAllGenres();
                        loadGenreSpinner(allGenres);
                    }
                }
            }
        });

        btnBackGenre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            goToSelectorActivity();

            }
        });



    }

    void goToSelectorActivity(){
        Intent intent = new Intent(GenreManagerActivity.this, SelectorActivity.class);
        setResult(RESULT_OK, intent);
        finish();
    }


    String readSpinner(){
        String selectedItem="NULL";
        if(spnGenres.getSelectedItem() != null){
            selectedItem = spnGenres.getSelectedItem().toString();
        }
        return selectedItem;
    }

    //za addGenre je dovoljno pokupiti string iz etAddGenre i samo ga proslediti funkciji addGenre
    void addGenre(String gName){
        Genre g = new Genre();
        g.setName(gName);
        databaseHandler.createGenre(g);

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
        spnGenres.setAdapter(dataAdapter);

    }




}