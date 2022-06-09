package com.migrate.datbase.model;

import java.io.Serializable;

public class Note implements Serializable {
    private int noteId;
    private String noteTitle;

    public Note()  {
    }

    public Note(String noteTitle) {
        this.noteTitle= noteTitle;
    }

    public Note(int noteId, String noteTitle) {
        this.noteId= noteId;
        this.noteTitle= noteTitle;
    }

    public int getNoteId() {
        return noteId;
    }

    public void setNoteId(int noteId) {
        this.noteId = noteId;
    }
    public String getNoteTitle() {
        return noteTitle;
    }

    public void setNoteTitle(String noteTitle) {
        this.noteTitle = noteTitle;
    }

    @Override
    public String toString()  {
        return this.noteTitle;
    }

}
