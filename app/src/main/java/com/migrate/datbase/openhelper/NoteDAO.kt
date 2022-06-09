package com.migrate.datbase.openhelper

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.migrate.datbase.model.NoteNewVersion

@Dao
interface NoteDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertNote(note: NoteNewVersion)

    @Query("SELECT * FROM Note")
    fun getNotes(): List<NoteNewVersion>
}