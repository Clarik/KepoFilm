package com.teammoviealley.moviealleyapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.teammoviealley.moviealleyapp.model.FavoriteMovie;

import java.util.Vector;

public class DatabaseHandler extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String NAME = "FavoriteDatabase";
    private static final String FAVORITE_TABLE = "Favorite";
    private static final String ID = "id";
    private static final String EMAIL = "email";
    private static final String MOVIE_ID = "movie_id";
    private static final String MOVIE_TITLE = "movie_title";
    private static final String MOVIE_PATH = "movie_path";

    private SQLiteDatabase db;

    public  DatabaseHandler(Context context){
        super(context, NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_FAVORITE_TABLE = "CREATE TABLE " + FAVORITE_TABLE
                + "("
                + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + EMAIL + " TEXT, "
                + MOVIE_ID + " INTEGER, "
                + MOVIE_TITLE +" TEXT, "
                + MOVIE_PATH + " TEXT "
                + ")";
        db.execSQL(CREATE_FAVORITE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + FAVORITE_TABLE);
        onCreate(db);
    }

    public void insertFavorite(String email, Integer movie_id, String movie_title, String movie_path){
        db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(EMAIL, email);
        cv.put(MOVIE_ID, movie_id);
        cv.put(MOVIE_TITLE, movie_title);
        cv.put(MOVIE_PATH, movie_path);
        db.insert(FAVORITE_TABLE, null, cv);
    }

    public Vector<FavoriteMovie> getMovieFavorite(String email){
        db = this.getReadableDatabase();
        Vector<FavoriteMovie> favList = new Vector<>();
        Log.d("Movmov", "ABC");
        Cursor cur = null;
        db.beginTransaction();
        try{
            cur = db.query(FAVORITE_TABLE, null, null, null, null, null, null, null);
            if(cur != null){
                if(cur.moveToFirst()){
                    do{
                        if(cur.getString(Math.abs(cur.getColumnIndex(EMAIL))).equalsIgnoreCase(email)){
                            Integer movie_id = cur.getInt(Math.abs(cur.getColumnIndex(MOVIE_ID)));
                            String title = cur.getString(Math.abs(cur.getColumnIndex(MOVIE_TITLE)));
                            String path = cur.getString(Math.abs(cur.getColumnIndex(MOVIE_PATH)));
                            favList.add(new FavoriteMovie(movie_id, title, path));
                        }
                    }
                    while(cur.moveToNext());
                }
            }
        }
        finally {
            db.endTransaction();
            assert cur != null;
            cur.close();
        }
        return favList;
    }
}
