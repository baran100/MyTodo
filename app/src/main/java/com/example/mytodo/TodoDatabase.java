package com.example.mytodo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class TodoDatabase extends SQLiteOpenHelper {

    private static final String TABLE_TASKS = "tbl_tasks";
    private static final String TAG = "SQLiteHelper";

    public TodoDatabase(@Nullable Context context) {
        super(context, "DB_APP", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        try {
            db.execSQL("CREATE TABLE " + TABLE_TASKS +" (id INTEGER PRIMARY KEY AUTOINCREMENT , title TEXT, completed BOOLEAN )");

        } catch (SQLiteException e){
            Log.e(TAG, "onCreate: " + e.toString() );
        }


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public long addTask(Task task){
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("title",task.getTitle());
        contentValues.put("completed",task.isCompleted());
        long result = sqLiteDatabase.insert(TABLE_TASKS,null,contentValues);
        sqLiteDatabase.close();
        return result;
    }

    public List<Task> getTasks(){
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + TABLE_TASKS, null);
        List<Task> tasks = new ArrayList<>();
        if (cursor.moveToFirst()){
            do {
                Task task = new Task();
                task.setId(cursor.getLong(0));
                task.setTitle(cursor.getString(1));
                task.setCompleted(cursor.getInt(2)==1);
                tasks.add(task);
            }while (cursor.moveToNext());
        }
        sqLiteDatabase.close();
        return tasks;
    }

    public int updateTak(Task task){
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("title",task.getTitle());
        contentValues.put("completed",task.isCompleted());
        int result = sqLiteDatabase.update(TABLE_TASKS,contentValues,"id = ?",new String[]{String.valueOf(task.getId())});
        sqLiteDatabase.close();
        return result;
    }

    public void searchTask(String query){

    }

    public int deleteTask(Task task){
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        int result = sqLiteDatabase.delete(TABLE_TASKS,"id = ?",new String[]{String.valueOf(task.getId())});
        sqLiteDatabase.close();
        return result;
    }

    public void clearAllTask(){
        try {
            SQLiteDatabase sqLiteDatabase= getWritableDatabase();
            sqLiteDatabase.execSQL("DELETE FROM " +TABLE_TASKS);
        }catch (SQLiteException e){
            Log.i(TAG, "clearAllTask: ");
        }


    }
}
