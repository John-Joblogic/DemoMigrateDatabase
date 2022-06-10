package com.migrate.datbase.room

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

    @Query("SELECT * FROM Note WHERE Note_Id = :id")
    fun getNoteById(id: Int): NoteNewVersion
}
