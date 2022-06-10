package com.migrate.datbase

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.migrate.datbase.model.NoteNewVersion
import com.migrate.datbase.model.NoteOldVersion
import com.migrate.datbase.openhelper.SQLiteOpenHelperTest
import com.migrate.datbase.room.NoteDatabase


class MainActivity : AppCompatActivity() {
    val oldDatabase = SQLiteOpenHelperTest(this)
    var newDatabase: NoteDatabase? = null
    var listOldAdapter: ArrayAdapter<NoteOldVersion>? = null
    var listNewAdapter: ArrayAdapter<NoteNewVersion>? = null
    var edtNoteTitle: EditText? = null
    var edtNoteDes: EditText? = null
    var listView: ListView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        edtNoteTitle = findViewById(R.id.edtNoteTitle)
        edtNoteDes = findViewById(R.id.edtNoteDes)
        listView = findViewById(R.id.listView)

        // Handle old database
        handleDatabaseOldVersion()


        // Handle new database
//        handleDatabaseNewVersion()
    }

    private fun handleDatabaseOldVersion() {
        oldDatabase.createDefaultNotesIfNeed()

        listOldAdapter = ArrayAdapter<NoteOldVersion>(
            this,
            android.R.layout.simple_list_item_1, android.R.id.text1, oldDatabase.allNotes
        )

        listView?.adapter = listOldAdapter
        registerForContextMenu(listView)


        findViewById<Button>(R.id.btnSave).setOnClickListener {
            oldDatabase.addNote(NoteOldVersion(edtNoteTitle?.text.toString()))
            listOldAdapter!!.apply {
                clear()
                addAll(oldDatabase.allNotes)
                notifyDataSetChanged()
            }
        }
    }

    private fun handleDatabaseNewVersion() {
        newDatabase = Room.databaseBuilder(this, NoteDatabase::class.java, NoteDatabase.DB_NAME)
            .allowMainThreadQueries()
            .addMigrations(MIGRATION_1_2)
            .build()

        listNewAdapter = ArrayAdapter<NoteNewVersion>(
            this,
            android.R.layout.simple_list_item_1,
            android.R.id.text1,
            newDatabase?.getNoteDao()?.getNotes()!!
        )
        listView?.adapter = listNewAdapter
        registerForContextMenu(listView)

        findViewById<Button>(R.id.btnSave).setOnClickListener {
            newDatabase?.getNoteDao()?.insertNote(
                NoteNewVersion(
                    null,
                    edtNoteTitle?.text.toString(),
                    edtNoteDes?.text.toString()
                )
            )

            listNewAdapter!!.apply {
                clear()
                addAll(newDatabase?.getNoteDao()?.getNotes()!!)
                notifyDataSetChanged()
            }
        }
    }

    var MIGRATION_1_2: Migration = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("ALTER TABLE `Note` ADD COLUMN `Note_Des` TEXT;")
        }
    }

}