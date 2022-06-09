package com.migrate.datbase

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.migrate.datbase.model.Note
import com.migrate.datbase.openhelper.MyDatabaseHelper


class MainActivity : AppCompatActivity() {
    var db = MyDatabaseHelper(this)
    private var listViewAdapter: ArrayAdapter<Note>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val edtNoteTitle = findViewById<EditText>(R.id.edtNoteTitle)
        val edtNoteDes = findViewById<EditText>(R.id.edtNoteDes)
        val listView = findViewById<ListView>(R.id.listView)

        db.createDefaultNotesIfNeed()

        listViewAdapter = ArrayAdapter<Note>(
            this,
            android.R.layout.simple_list_item_1, android.R.id.text1, db.allNotes
        )

        listView?.adapter = listViewAdapter
        registerForContextMenu(listView)


        findViewById<Button>(R.id.btnSave).setOnClickListener {
            db.addNote(Note(edtNoteTitle.text.toString()))
        }
    }
}