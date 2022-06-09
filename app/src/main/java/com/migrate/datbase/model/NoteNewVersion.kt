package com.migrate.datbase.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "Note")
@Parcelize
data class NoteNewVersion(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "Note_Id")
    val noteId: Int?,

    @ColumnInfo(name = "Note_Title")
    val noteTitle: String?,

    @ColumnInfo(name = "Note_Des")
    val noteDes: String?,
) : Parcelable