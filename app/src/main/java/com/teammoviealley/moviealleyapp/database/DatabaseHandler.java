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
    private static final String NAME = "favoriteDatabase";
    private static final String FAV_TABLE = "favoritemovie";
    private static final String ID = "id";
    private static final String MOVIEID = "movieid";
    private static final String EMAIL = "email";
    private static final String TITLE = "title";
    private static final String POSTERPATH = "posterpath";

    private SQLiteDatabase db;

    public  DatabaseHandler(Context context){
        super(context, NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_FAV_TABLE = "CREATE TABLE " + FAV_TABLE
                + "(" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + MOVIEID + " INTEGER, "
                + EMAIL + " TEXT, "
                + TITLE + " TEXT, "
                + POSTERPATH + " TEXT "
                + ")";
        db.execSQL(CREATE_FAV_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + FAV_TABLE);
        onCreate(db);
    }

    public void insertFav(FavoriteMovie fav){
        db = this.getWritableDatabase();

        String sql = "INSERT INTO " + FAV_TABLE + " VALUES ( NULL, "
                + fav.getId() + ", \'"
                + fav.getEmail() + "\', \'"
                + fav.getTitle() + "\', \'"
                + fav.getPosterPath() + "\' "
                + ")";
        Log.d("Movmov", sql);
        db.execSQL(sql);
    }

    public void deleteTask(int id){
        db.delete(FAV_TABLE, ID + "= ?", new String[] {String.valueOf(id)});
    }

    public Vector<FavoriteMovie> getFav(){
        db = this.getReadableDatabase();
        Vector<FavoriteMovie> favMov = new Vector<>();
        Cursor cur = null;
        db.beginTransaction();
        try{
            cur = db.query(FAV_TABLE, null, null, null, null, null, null, null);
            if(cur != null){
                if(cur.moveToFirst()){
                    do{
                        String email = cur.getString(Math.abs(cur.getColumnIndex(EMAIL)));
                        Integer movie_id = cur.getInt(Math.abs(cur.getColumnIndex(MOVIEID)));
                        String title = cur.getString(Math.abs(cur.getColumnIndex(TITLE)));
                        String poster = cur.getString(Math.abs(cur.getColumnIndex(POSTERPATH)));
                        favMov.add(new FavoriteMovie(email, movie_id, title, poster));
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
        return favMov;
    }

}
