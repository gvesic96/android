package com.example.musiclibrarydb.sqlite.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.musiclibrarydb.sqlite.model.Artist;
import com.example.musiclibrarydb.sqlite.model.Genre;
import com.example.musiclibrarydb.sqlite.model.Playlist;
import com.example.musiclibrarydb.sqlite.model.Song;
import com.example.musiclibrarydb.sqlite.model.User;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler extends SQLiteOpenHelper {

    //OVDE KREIRA REFERENCU NA OBJEKAT KLASE SQLiteDatabase koju nazove db
    SQLiteDatabase db;

    // Logcat tag
    private static final String LOG = "DatabaseHandler";

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "MusicManager";
    //KONSTRUKTOR
    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        db = getWritableDatabase();
    }

    // Table Names
    private static final String TABLE_USERS = "users";
    private static final String TABLE_SONGS = "songs";
    private static final String TABLE_ARTISTS = "artists";
    private static final String TABLE_GENRES = "genres";

    private static final String TABLE_PLAYLISTS = "playlists";
    private static final String TABLE_PLAYLIST_ENTRIES = "playlistEntries";


    // Common column names
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";


    //----------------------------------------------- do ovoga sam promenio ------------------------

    //USERS table - column names
    private static final String KEY_PASSWORD = "password";

    //SONGS table - column names
    private static final String KEY_ARTIST_ID = "artist_id";
    private static final String KEY_GENRE_ID = "genre_id"; //GENRE_ID ce biti koriscen i u ARTISTS

    //ARTISTS table no need for special fields
    //KORISTI SE I GENRE_ID

    //GENRES table no need for special fields


    //PLAYLISTS table - column names
    private static final String KEY_USER_ID = "user_id";

    //PLAYLIST ENTRY table - column names
    private static final String KEY_SONG_ID = "song_id";
    private static final String KEY_PLAYLIST_ID = "playlist_id";

    // Table Create Statements

    //Users table create statement
    private static final String CREATE_TABLE_USERS = "CREATE TABLE IF NOT EXISTS "
            + TABLE_USERS + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT,"
            + KEY_PASSWORD + " TEXT" + ")";

    //Artists table create statement
    private static final String CREATE_TABLE_ARTISTS = "CREATE TABLE IF NOT EXISTS "
            + TABLE_ARTISTS + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME
            + " TEXT," + KEY_GENRE_ID + " INTEGER" + ")";

    //Genres table create statement
    private static final String CREATE_TABLE_GENRES = "CREATE TABLE IF NOT EXISTS "
            + TABLE_GENRES + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT" + ")";

    //Songs table create statement
    private static final String CREATE_TABLE_SONGS = "CREATE TABLE IF NOT EXISTS "
            + TABLE_SONGS + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT,"
            + KEY_ARTIST_ID + " INTEGER," + KEY_GENRE_ID + " INTEGER" + ")";

    //Playlists table create statement
    private static final String CREATE_TABLE_PLAYLISTS = "CREATE TABLE IF NOT EXISTS "
            + TABLE_PLAYLISTS + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT,"
            + KEY_USER_ID + " INTEGER" + ")";

    //Playlists table create statement
    private static final String CREATE_TABLE_PLAYLIST_ENTRIES = "CREATE TABLE IF NOT EXISTS "
            + TABLE_PLAYLIST_ENTRIES + "(" + KEY_ID + " INTEGER PRIMARY KEY,"
            + KEY_PLAYLIST_ID + " INTEGER," + KEY_SONG_ID + " INTEGER" + ")";


    @Override
    public void onCreate(SQLiteDatabase db) {
        // creating required tables
        db.execSQL(CREATE_TABLE_USERS);
        db.execSQL(CREATE_TABLE_ARTISTS);
        db.execSQL(CREATE_TABLE_GENRES);
        db.execSQL(CREATE_TABLE_SONGS);
        db.execSQL(CREATE_TABLE_PLAYLISTS);
        db.execSQL(CREATE_TABLE_PLAYLIST_ENTRIES);
    }
    //onCreate pri pokretanju kreira tabele u bazi

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ARTISTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GENRES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SONGS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PLAYLISTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PLAYLIST_ENTRIES);

        // create new tables
        onCreate(db);
    }

    //This to be used when needed to create tables from scratch (maybe after tables are deleted)
    public void createTables() {
        if (db == null)
            db = getWritableDatabase();

        // creating required tables
        db.execSQL(CREATE_TABLE_USERS);
        db.execSQL(CREATE_TABLE_ARTISTS);
        db.execSQL(CREATE_TABLE_GENRES);
        db.execSQL(CREATE_TABLE_SONGS);
        db.execSQL(CREATE_TABLE_PLAYLISTS);
        db.execSQL(CREATE_TABLE_PLAYLIST_ENTRIES);


    }

    //Delete all tables
    public void dropTables(){
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ARTISTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GENRES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SONGS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PLAYLISTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PLAYLIST_ENTRIES);
    }

    /*
     * Creating an user
     */
    //ovoj metodi cu proslediti objekat klase User koji cu napraviti u MainActivity? i onda ce ona kreirati Usera u bazi? Trebalo bi
    public long createUser(User user) {

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, user.getName());//kljuc mora da se slaze sa nazivom kolone
        values.put(KEY_PASSWORD, user.getPassword());

        // insert row
        long user_id = db.insert(TABLE_USERS, null, values);


        //now we know id obtained after writing actor to a database, update existing actor
        user.setId(user_id);//ovom metodom u objektu User koji sam prosledio postavljam ID u atribut id

        return user_id;
    }

    /*
     * Creating a genre
     */
    public long createGenre(Genre genre) {

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, genre.getName());

        // insert row
        long genre_id = db.insert(TABLE_GENRES, null, values);
        genre.setId(genre_id);

        return genre_id;
    }

    /*
     * Creating an artist
     */
    public long createArtist(Artist artist) {

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, artist.getName());
        values.put(KEY_GENRE_ID, getGenreID(artist.getGenre()));//nad objektom artist pozovem getGenre koji vrati String genre prema kome se nadje ID tog zanra

        // insert row
        long artist_id = db.insert(TABLE_ARTISTS, null, values);
        artist.setId(artist_id);//postavim ID atribut u objektu klase Artist koji sam napravio u MainActivity-u

        return artist_id;
    }

    /*
     * Creating a song
     */
    public long createSong(Song song) {
        //ako se pri kreiranju prosledjuje objekat song on u sebi sadrzi stringove koji su genre i artist, treba po stringovima pretraziti tabele i uzeti id???
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, song.getName());

        //nedostaje mi metoda koja pribavlja artist ili genre preko SELECT rada sa bazom gde je upit artistName ili genreType

        values.put(KEY_ARTIST_ID, getArtistID(song.getArtist()));
        values.put(KEY_GENRE_ID, getGenreID(song.getGenre()));

        // insert row
        long song_id = db.insert(TABLE_SONGS, null, values);

        song.setId(song_id);

        return song_id;
    }


    //get Genre ID for specified passed genre type (String) from an song or artist object

    public long getGenreID(String genreType){

        String selectQuery = "SELECT * FROM " + TABLE_GENRES + " WHERE "
                + KEY_NAME + "=" + genreType;

        Log.e(LOG, selectQuery);
        Cursor c = db.rawQuery(selectQuery, null);
        if (c != null)
            c.moveToFirst();

        //create actor based on data read from a database
        long fetchedID = 1;
        fetchedID = c.getInt(c.getColumnIndex(KEY_ID));

        return fetchedID;
    }

    public long getArtistID(String artistName){

        String selectQuery = "SELECT * FROM " + TABLE_ARTISTS + " WHERE "
                + KEY_NAME + "=" + artistName;

        Log.e(LOG, selectQuery);
        Cursor c = db.rawQuery(selectQuery, null);
        if (c != null)
            c.moveToFirst();

        //create actor based on data read from a database
        long fetchedID = 1;
        fetchedID = c.getInt(c.getColumnIndex(KEY_ID));

        return fetchedID;
    }


    /*
     * Creating a playlist
     */
    public long createPlaylist(Playlist playlist) {
        //ova metoda
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, playlist.getName());

        values.put(KEY_USER_ID, getUserID(playlist.getUserName()));

        // insert row
        long playlist_id = db.insert(TABLE_PLAYLISTS, null, values);


        return playlist_id;
    }

    public long getUserID(String userName){

        String selectQuery = "SELECT * FROM " + TABLE_USERS + " WHERE "
                + KEY_NAME + "=" + userName;

        Log.e(LOG, selectQuery);
        Cursor c = db.rawQuery(selectQuery, null);
        if (c != null)
            c.moveToFirst();


        long fetchedID = 1;
        fetchedID = c.getInt(c.getColumnIndex(KEY_ID));

        return fetchedID;
    }

    /*
     * Creating a playlist entry - 1 entry is one row in PLAYLIST_ENTRIES TABLE
     */
    public long createPlaylistEntry(Playlist playlist, Song song) {
        //ova metoda
        ContentValues values = new ContentValues();

        values.put(KEY_PLAYLIST_ID, getPlaylistID(playlist.getName()));
        values.put(KEY_SONG_ID, getSongID(song.getName()));

        // insert row
        long playlist_id = db.insert(TABLE_PLAYLIST_ENTRIES, null, values);


        return playlist_id;
    }

    public long getPlaylistID(String playlistName){

        String selectQuery = "SELECT * FROM " + TABLE_PLAYLISTS + " WHERE "
                + KEY_NAME + "=" + playlistName;

        Log.e(LOG, selectQuery);
        Cursor c = db.rawQuery(selectQuery, null);
        if (c != null)
            c.moveToFirst();


        long fetchedID = 1;
        fetchedID = c.getInt(c.getColumnIndex(KEY_ID));

        return fetchedID;
    }

    public long getSongID(String songName){

        String selectQuery = "SELECT * FROM " + TABLE_SONGS + " WHERE "
                + KEY_NAME + "=" + songName;

        Log.e(LOG, selectQuery);
        Cursor c = db.rawQuery(selectQuery, null);
        if (c != null)
            c.moveToFirst();


        long fetchedID = 1;
        fetchedID = c.getInt(c.getColumnIndex(KEY_ID));

        return fetchedID;
    }


    //bice ovde jos da se doda pribavljanje svih pesama kao i pribavljanje svih zanrova, izvodjaca i odgovarajucih plejlista za datog korisnitka po USER_IDu


    public ArrayList<User> getAllUsers(){
        ArrayList<User> users = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_USERS;

        Log.e(LOG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        if(c.moveToFirst()){
            do{
                User u = new User();
                u.setId(c.getInt(c.getColumnIndex(KEY_ID)));
                u.setName(c.getString(c.getColumnIndex(KEY_NAME)));
                u.setPassword(c.getString(c.getColumnIndex(KEY_PASSWORD)));

                users.add(u);
            } while (c.moveToNext());

        }
        return users;

    }

    public ArrayList<Artist> getAllArtists(){
        ArrayList<Artist> artists = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_ARTISTS;

        Log.e(LOG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        if(c.moveToFirst()){
            do{
                Artist a = new Artist();
                a.setId(c.getInt(c.getColumnIndex(KEY_ID)));
                a.setName(c.getString(c.getColumnIndex(KEY_NAME)));

                artists.add(a);
            } while (c.moveToNext());

        }
        return artists;

    }

    public ArrayList<Genre> getAllGenres(){
        ArrayList<Genre> genres = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_GENRES;

        Log.e(LOG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        if(c.moveToFirst()){
            do{
                Genre g = new Genre();
                g.setId(c.getInt(c.getColumnIndex(KEY_ID)));//OVDE POSTAVIM ID U OBJEKTU U AKTIVNOSTI I POSLE MI JE DOVOLJNO DA PREKO IDa UPDATUJEM
                g.setName(c.getString(c.getColumnIndex(KEY_NAME)));

                genres.add(g);
            } while (c.moveToNext());

        }
        return genres;

    }

    //------------------------------------------------------------- UPDATE -------------------------

    public int updateGenre(Genre genre){
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, genre.getName());//menjam KEY_NAME odnosno naziv zanra

        // updating row
        return db.update(TABLE_GENRES, values, KEY_ID + " = ?",
                new String[] { String.valueOf(genre.getId()) }); //ID OSTAJE ISTI JER JE VEC RANIJE PRI CITANJU IZ TABELE UNET U OBJEKAT? ZATO MENJA PREKO ID-a

    }

    public void removeGenre(long g_id){
        db.delete(TABLE_GENRES, KEY_ID + " = ?",
                new String[] { String.valueOf(g_id)});
    }

    // closing database
    public void closeDB() {
        if (db != null && db.isOpen())
            db.close();
    }




}
