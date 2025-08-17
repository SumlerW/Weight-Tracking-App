package com.example.projectagain;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "UserDatabase.db";
    private static final int DATABASE_VERSION = 1;

    // Table and column names
    private static final String TABLE_USERS = "users";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_PASSWORD = "password";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Called when the database is created for the first time
    @Override
    public void onCreate(SQLiteDatabase db) {
        String createUserTable = "CREATE TABLE " + TABLE_USERS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_USERNAME + " TEXT UNIQUE, " +
                COLUMN_PASSWORD + " TEXT)";
        db.execSQL(createUserTable);

        // Create daily weights table
        db.execSQL("CREATE TABLE daily_weights_table (daily_weight REAL)");

        // Create goal weight table
        db.execSQL("CREATE TABLE weight_goal_table (goal_weight REAL)");
    }

    // Called when the database needs to be upgraded
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS daily_weights_table");
        db.execSQL("DROP TABLE IF EXISTS weight_goal_table");
        onCreate(db);
    }

    // Insert a new user into the database
    public boolean addUser(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_PASSWORD, password);

        long result = db.insert(TABLE_USERS, null, values);
        db.close();
        return result != -1; // true if insert successful
    }

    // Check if a user exists with the given credentials
    public boolean checkUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_USERS + " WHERE " +
                COLUMN_USERNAME + "=? AND " + COLUMN_PASSWORD + "=?";
        Cursor cursor = db.rawQuery(query, new String[]{username, password});

        boolean exists = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return exists;
    }

    // Save or update goal weight (only one entry)
    public boolean saveGoalWeight(float goalWeight) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("goal_weight", goalWeight);
        long result = db.replace("weight_goal_table", null, values); // replaces existing value
        db.close();
        return result != -1;
    }

    // Delete goal weight entry
    public boolean deleteGoalWeight() {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete("weight_goal_table", null, null);
        db.close();
        return result > 0;
    }

    // Save a daily weight record
    public boolean saveDailyWeight(float dailyWeight) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("daily_weight", dailyWeight);
        long result = db.insert("daily_weights_table", null, values);
        db.close();
        return result != -1;
    }

    // Delete all daily weight records
    public boolean deleteAllDailyWeights() {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete("daily_weights_table", null, null);
        db.close();
        return result > 0;
    }

    // Retrieve all saved daily weight entries
    public ArrayList<Object> getAllDailyWeights() {
        ArrayList<Object> weightList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT daily_weight FROM daily_weights_table", null);

        while (cursor.moveToNext()) {
            weightList.add(cursor.getFloat(0));
        }

        cursor.close();
        db.close();
        return weightList;
    }
}
