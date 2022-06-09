package com.migrate.datbase.openhelper

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.migrate.datbase.model.NoteNewVersion


@Database(entities = [NoteNewVersion::class], version = 2, exportSchema = true)
abstract class NoteDatabase : RoomDatabase() {

    abstract fun getNoteDao(): NoteDAO

    private var instance: NoteDatabase? = null


    open fun getInstance(context: Context): NoteDatabase? {
        if (instance == null) {
            instance = Room.databaseBuilder(context, NoteDatabase::class.java, DB_NAME)
                .addMigrations(MIGRATION_1_TO_2)
                .build()
        }
        return instance
    }

    var MIGRATION_1_TO_2: Migration = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("ALTER TABLE question ADD COLUMN noteDes TEXT")
        }
    }

    companion object {
        const val DB_NAME = "Note_Manager"
    }
}