# android
Some android projects.

- MusicLibraryDB
  MusicLibrary apllication implemented using Android Studio for Android 10 and newer versions of OS.
  Contains implemented database using SQLite for storing songs, artists, genres and playlists.
  Implemented with multiple activities that allow browsing, adding, editing and removing songs, artists, genres and playlists.

- FoxAndGeese game
  Client server game thats allows multiple clients to play at the same time communicating through server. Server is created in NetBeans and clients in Android Studio.
  Two players play one with fox, other with geese. Idea is that fox needs to pass to other side of the table, and geese needs to prevent it.
  Game is playing on chess board, and both fox and geese has to be placed on same colour, and can move only to fields with same colour, diagonally.
  Geese can go only forward, and fox can go forward or backward.
  Server is implemted in Java using NetBeans, and creates a new thread for every client that connects, it can be played remotely online on a wifi or with public IP adress.
  Client application is running on Android phone and sends messages to another client through server and receives responses. Chess board is implemented as a ListView.
