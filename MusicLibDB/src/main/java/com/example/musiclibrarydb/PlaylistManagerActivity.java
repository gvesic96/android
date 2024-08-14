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

import com.example.musiclibrarydb.sqlite.helper.DatabaseHandler;
import com.example.musiclibrarydb.sqlite.model.Artist;
import com.example.musiclibrarydb.sqlite.model.Genre;
import com.example.musiclibrarydb.sqlite.model.Playlist;
import com.example.musiclibrarydb.sqlite.model.Song;

import java.util.ArrayList;

public class PlaylistManagerActivity extends AppCompatActivity {

    private ArrayList<Playlist> allPlaylists;
    private ArrayList<Genre> genresPL;
    private ArrayList<Artist> artistsPL;
    private ArrayList<Song> songsPL;
    private ArrayList<Song> songsInPL;

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
        if(spnPlaylists.getSelectedItem() != null) {
            etEditAddPlaylist.setText(spnPlaylists.getSelectedItem().toString());
            loadPlaylist(spnPlaylists.getSelectedItem().toString()); // OVDE SAM UCITAO SADRZAJ PLAYLISTE INICIJALNO PRI POKRETANJU
            //pesme se sada nalaze u songsInPL
        }

        genresPL = databaseHandler.getAllGenres();
        loadGenresPLSpn(genresPL);



        btnRemoveSongPL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String selectedPlaylist = readSpinner(spnPlaylists);
                String selectedSong = readSpinner(spnSongsInPL);
                if(!selectedSong.equals("") && !selectedPlaylist.equals("")){
                    long pl_id=0;
                    long song_id=0;
                    for(Playlist elPL:allPlaylists){
                        if(elPL.getName().equals(selectedPlaylist)){
                            pl_id=elPL.getId();
                            break;
                        }
                    }
                    for(Song elS:songsInPL){
                        if(elS.getName().equals(selectedSong)){
                            song_id=elS.getId();
                            break;
                        }
                    }
                    if(pl_id!=0 && song_id!=0) databaseHandler.removePlaylistEntry(pl_id, song_id);
                    loadPlaylist(selectedPlaylist);

                }
            }
        });


        btnPutSongPL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String selectedSong = readSpinner(spnAvailableSongsPL);
                String selectedPlaylist = readSpinner(spnPlaylists);
                Song s = new Song();
                Playlist pl = new Playlist();
                if(!selectedSong.equals("") && !selectedPlaylist.equals("")){
                    for(Song elSong:songsPL){
                        if(elSong.getName().equals(selectedSong)){
                            s=elSong;
                            break;
                        }
                    }
                    for(Playlist elPL:allPlaylists){
                        if(elPL.getName().equals(selectedPlaylist)){
                            pl=elPL;
                            break;
                        }
                    }
                    databaseHandler.createPlaylistEntry(pl, s);
                    ArrayList<Song> songIDs = databaseHandler.getSongsByPlaylist(pl.getId());
                    ArrayList<Song> fetchedSongs = new ArrayList<>();
                    for(Song el:songIDs){
                        Song song = databaseHandler.getSong(el.getId());
                        fetchedSongs.add(song);
                    }
                    songsInPL = fetchedSongs;
                    loadSongsInPLSpn(songsInPL);

                }

            }
        });



        spnArtistsPL.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedArtist = readSpinner(spnArtistsPL);
                if(selectedArtist.equals("all")){
                    String g = spnGenresPL.getSelectedItem().toString();
                    if(g.equals("all")){
                        songsPL = databaseHandler.getAllSongs();
                        loadSongsPLSpn(songsPL);
                    }else {
                        for (Genre element : genresPL) {
                            if (element.getName().equals(g)) {
                                songsPL = databaseHandler.getSongsByGenre(element.getId());
                                loadSongsPLSpn(songsPL);
                                break;
                            }
                        }
                    }
                }else{
                    selectedArtist = spnArtistsPL.getSelectedItem().toString();
                    for(Artist el:artistsPL){
                        if(el.getName().equals(selectedArtist)){
                            songsPL = databaseHandler.getSongsByArtist(el.getId());
                            loadSongsPLSpn(songsPL);
                            break;
                        }

                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        spnGenresPL.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedGenre = readSpinner(spnGenresPL);
                if(selectedGenre.equals("all")){
                    artistsPL = databaseHandler.getAllArtists();
                    loadArtistsPLSpn(artistsPL);
                    String a = spnArtistsPL.getSelectedItem().toString();
                    if(a.equals("all")){
                        songsPL = databaseHandler.getAllSongs();
                        loadSongsPLSpn(songsPL);
                    }else{
                        for(Artist element:artistsPL){
                            if(a.equals(element.getName())){
                                songsPL = databaseHandler.getSongsByArtist(element.getId());
                                loadSongsPLSpn(songsPL);
                                break;
                            }
                        }
                    }
                }else{
                    selectedGenre = readSpinner(spnGenresPL);
                    for(Genre el:genresPL){
                        if(el.getName().equals(selectedGenre)){
                            artistsPL = databaseHandler.getArtistsByGenre(el.getId());//getting all artists by genre id
                            loadArtistsPLSpn(artistsPL);
                            songsPL=databaseHandler.getSongsByGenre(el.getId());
                            loadSongsPLSpn(songsPL);
                            break;
                        }

                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spnPlaylists.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedPlaylist = readSpinner(spnPlaylists);
                etEditAddPlaylist.setText(selectedPlaylist);
                if(!selectedPlaylist.equals("")){
                    loadPlaylist(selectedPlaylist);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        btnEditPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(spnPlaylists.getSelectedItem()!=null){
                    if(!etEditAddPlaylist.getText().toString().equals("")){
                        //novo ime mora biti razlicito od praznog stringa
                        String oldPLName = spnPlaylists.getSelectedItem().toString();
                        String newPLName = etEditAddPlaylist.getText().toString();

                        for(Playlist el:allPlaylists){
                            if(el.getName().equals(oldPLName)){
                                el.setName(newPLName);
                                databaseHandler.updatePlaylist(el);
                                allPlaylists = databaseHandler.getAllPlaylists(user_id);
                                loadPlaylistSpinner(allPlaylists);
                                break;
                            }
                        }
                    }
                }



            }
        });

        btnAddPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addPlaylist();
            }
        });

        btnRemovePlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String removePLName = readSpinner(spnPlaylists);
                if(!removePLName.equals("")) {
                    for (Playlist el : allPlaylists) {
                        if (el.getName().equals(removePLName)) {
                            databaseHandler.removePlaylist(el.getId());
                            allPlaylists = databaseHandler.getAllPlaylists(user_id);
                            loadPlaylistSpinner(allPlaylists);
                            etEditAddPlaylist.setText("");
                            break;
                        }
                    }
                }
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

    void loadPlaylist(String selectedPlaylist){
        Playlist pl;
        for(Playlist elPL:allPlaylists){
            if(elPL.getName().equals(selectedPlaylist)){
                pl=elPL;
                ArrayList<Song> songIDs = databaseHandler.getSongsByPlaylist(pl.getId());
                ArrayList<Song> fetchedSongs = new ArrayList<>();
                for(Song el:songIDs){
                    Song song = databaseHandler.getSong(el.getId());
                    fetchedSongs.add(song);
                }
                songsInPL = fetchedSongs;
                loadSongsInPLSpn(songsInPL);
                break;
            }
        }
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

    String readSpinner(Spinner spn){
        String selectedItem="";
        if(spn.getSelectedItem() != null){
            selectedItem = spn.getSelectedItem().toString();
        }
        return selectedItem;
    }

    void loadGenresPLSpn(ArrayList<Genre> genres){
        ArrayList<String> genreNames = new ArrayList<>();
        String firstEl = "all";
        genreNames.add(firstEl);

        for (Genre el : genres){
            genreNames.add(el.getName());
        }
        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, genreNames);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spnGenresPL.setAdapter(dataAdapter);
    }

    void loadArtistsPLSpn(ArrayList<Artist> artists){
        ArrayList<String> artistNames = new ArrayList<>();
        String firstEl = "all";
        artistNames.add(firstEl);

        for (Artist el : artists){
            artistNames.add(el.getName());
        }
        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, artistNames);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spnArtistsPL.setAdapter(dataAdapter);
    }

    void loadSongsPLSpn(ArrayList<Song> songs){
        ArrayList<String> songNames = new ArrayList<>();
        //String firstEl = "all";
        //artistNames.add(firstEl);

        for (Song el : songs){
            songNames.add(el.getName());
        }
        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, songNames);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spnAvailableSongsPL.setAdapter(dataAdapter);
    }

    void loadSongsInPLSpn(ArrayList<Song> songs){
        ArrayList<String> songNames = new ArrayList<>();
        for (Song el : songs){
            songNames.add(el.getName());
        }
        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, songNames);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spnSongsInPL.setAdapter(dataAdapter);
    }

}