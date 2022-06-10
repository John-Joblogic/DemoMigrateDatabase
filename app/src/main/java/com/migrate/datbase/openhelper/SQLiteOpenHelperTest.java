package com.migrate.datbase.openhelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.migrate.datbase.model.NoteOldVersion;

import java.util.ArrayList;
import java.util.List;

public class SQLiteOpenHelperTest extends SQLiteOpenHelper {

    private static final String TAG = "SQLite";

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "Note_Manager";

    // Table name: Note.
    private static final String TABLE_NOTE = "Note";
    private static final String COLUMN_NOTE_ID = "Note_Id";
    private static final String COLUMN_NOTE_TITLE = "Note_Title";

    private static final String TABLE_BOOK = "Book";
    private static final String COLUMN_BOOK_ID = "Book_Id";
    private static final String COLUMN_BOOK_TITLE = "Book_Title";

    public SQLiteOpenHelperTest(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Create table
    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i(TAG, "MyDatabaseHelper.onCreate ... ");
        // Script.
        String script = "CREATE TABLE " + TABLE_NOTE + "("
                + COLUMN_NOTE_ID + " INTEGER PRIMARY KEY," + COLUMN_NOTE_TITLE + " TEXT"
                + ")";
        // Execute Script.
        db.execSQL(script);

        // For test error when migrate miss table
        String script2 = "CREATE TABLE " + TABLE_BOOK + "("
                + COLUMN_BOOK_ID + " INTEGER PRIMARY KEY," + COLUMN_BOOK_TITLE + " TEXT"
                + ")";

        db.execSQL(script2);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        Log.i(TAG, "MyDatabaseHelper.onUpgrade ... ");
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTE);

        // Create tables again
        onCreate(db);
    }


    // If Note table has no data
    // default, Insert 2 records.
    public void createDefaultNotesIfNeed() {
        int count = this.getNotesCount();
        if (count == 0) {
            NoteOldVersion noteOldVersion1 = new NoteOldVersion("Firstly see Android ListView");
            NoteOldVersion noteOldVersion2 = new NoteOldVersion("Learning Android SQLite");
            this.addNote(noteOldVersion1);
            this.addNote(noteOldVersion2);
        }
    }


    public void addNote(NoteOldVersion noteOldVersion) {
        Log.i(TAG, "MyDatabaseHelper.addNote ... " + noteOldVersion.getNoteTitle());

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_NOTE_TITLE, noteOldVersion.getNoteTitle());

        // Inserting Row
        db.insert(TABLE_NOTE, null, values);

        // Closing database connection
        db.close();
    }


    public NoteOldVersion getNote(int id) {
        Log.i(TAG, "MyDatabaseHelper.getNote ... " + id);

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_NOTE, new String[]{COLUMN_NOTE_ID,
                        COLUMN_NOTE_TITLE}, COLUMN_NOTE_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        return new NoteOldVersion(Integer.parseInt(cursor.getString(0)),
                cursor.getString(1));
    }


    public List<NoteOldVersion> getAllNotes() {
        Log.i(TAG, "MyDatabaseHelper.getAllNotes ... ");

        List<NoteOldVersion> noteOldVersionList = new ArrayList<NoteOldVersion>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_NOTE;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                NoteOldVersion noteOldVersion = new NoteOldVersion();
                noteOldVersion.setNoteId(Integer.parseInt(cursor.getString(0)));
                noteOldVersion.setNoteTitle(cursor.getString(1));
                // Adding note to list
                noteOldVersionList.add(noteOldVersion);
            } while (cursor.moveToNext());
        }

        // return note list
        return noteOldVersionList;
    }

    public int getNotesCount() {
        Log.i(TAG, "MyDatabaseHelper.getNotesCount ... ");

        String countQuery = "SELECT  * FROM " + TABLE_NOTE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();

        cursor.close();

        // return count
        return count;
    }


    public int updateNote(NoteOldVersion noteOldVersion) {
        Log.i(TAG, "MyDatabaseHelper.updateNote ... " + noteOldVersion.getNoteTitle());

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_NOTE_TITLE, noteOldVersion.getNoteTitle());

        // updating row
        return db.update(TABLE_NOTE, values, COLUMN_NOTE_ID + " = ?",
                new String[]{String.valueOf(noteOldVersion.getNoteId())});
    }

    public void deleteNote(NoteOldVersion noteOldVersion) {
        Log.i(TAG, "MyDatabaseHelper.updateNote ... " + noteOldVersion.getNoteTitle());

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NOTE, COLUMN_NOTE_ID + " = ?",
                new String[]{String.valueOf(noteOldVersion.getNoteId())});
        db.close();
    }

}
