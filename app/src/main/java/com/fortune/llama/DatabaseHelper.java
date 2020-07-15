// Class that actually creates the database
// SQLite

package com.fortune.llama;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.widget.Toast;

import androidx.annotation.Nullable;

// Starts here.
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String NAME = "database.db";
    private static final int VERSION = 1;
    String createTableUser = "CREATE TABLE if not exists `user` ( `id` INTEGER PRIMARY KEY AUTOINCREMENT, `username` TEXT," +
            " `password` TEXT, `email` TEXT, `country` TEXT, `dob` TEXT, `gender` TEXT )";
    DatabaseHelper dbHelper;

    public DatabaseHelper(Context context) {
        super(context, NAME, null, VERSION);
        getWritableDatabase().execSQL(createTableUser);
    }

    // add a new user to the database
    public void insertUser(ContentValues content) {
        getWritableDatabase().insert("user", "", content);
    }

    // check whether the user is in the database or not, for login.
    public boolean isLoginValid(String username, String password) {
        String sql = "Select count(*) from user where username='" + username + "' and password='" + password + "'";
        SQLiteStatement statement = getReadableDatabase().compileStatement(sql);
        long l = statement.simpleQueryForLong();
        statement.close();

        if (l == 1) {
            return true;
        } else {
            return false;
        }
    }

    // check if the user is already registered
    public boolean checkRegister(String username, String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "Select * from user where username='" + username + "' and email='" + email + "'";
        Cursor c = db.rawQuery(sql, null);
        int count = c.getCount();
        c.close();
        db.close();
        return count > 0;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
