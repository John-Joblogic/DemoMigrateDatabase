package com.migrate.datbase

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.migrate.datbase.model.NoteNewVersion
import com.migrate.datbase.model.NoteOldVersion
import com.migrate.datbase.openhelper.SQLiteOpenHelperTest
import com.migrate.datbase.room.NoteDatabase
import junit.framework.Assert.assertEquals
import org.junit.*
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters
import java.io.IOException

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(AndroidJUnit4::class)
class MigrationTest {
    // Helper for creating Room databases and migrations
    @get:Rule
    var mMigrationTestHelper: MigrationTestHelper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        NoteDatabase::class.java.canonicalName,
        FrameworkSQLiteOpenHelperFactory()
    )

    // Helper for creating SQLite database in version 1
    private var mSqliteTestDbHelper: SQLiteOpenHelperTest? = null

    @Before
    fun setUp() {
        mSqliteTestDbHelper = SQLiteOpenHelperTest(ApplicationProvider.getApplicationContext())
    }

    @After
    fun tearDown() {
        // Clear the database after every test
        mSqliteTestDbHelper?.close()
    }

    @Test
    @Throws(IOException::class)
    fun testFirst_migrationFrom1To2_containsCorrectData() {
        // Create the database with the initial version 1 schema and insert a note
        mSqliteTestDbHelper?.addNote(noteOldVerSion)

        mMigrationTestHelper.runMigrationsAndValidate(TEST_DB, 2, true, MIGRATION_1_2)

        // Get the latest, migrated, version of the database
        val latestDb: NoteDatabase = migratedRoomDatabase

        // Check that the correct data is in the database
        val note: NoteNewVersion = latestDb.getNoteDao().getNoteById(noteOldVerSion.noteId)
        assertEquals(note.noteId, noteOldVerSion.noteId)
        assertEquals(note.noteTitle, noteOldVerSion.noteTitle)
    }

    @Test
    @Throws(IOException::class)
    fun testSecond_startInVersion2_containsCorrectData() {
        // Create the database with version 2
        mMigrationTestHelper.createDatabase(
            TEST_DB, 2
        ).apply {
            insert("Note", SQLiteDatabase.CONFLICT_REPLACE, noteValues())
            close()
        }

        // open the db with Room
        val noteDatabase: NoteDatabase = migratedRoomDatabase

        // verify that the data is correct
        val note: NoteNewVersion = noteDatabase.getNoteDao().getNoteById(noteOldVerSion.noteId)
        assertEquals(note.noteId, noteOldVerSion.noteId)
        assertEquals(note.noteTitle, noteOldVerSion.noteTitle)
    }

    @Test
    @Throws(IOException::class)
    fun testThird_startInVersion2_insertData() {

        // open the db with Room
        val noteDatabase: NoteDatabase = migratedRoomDatabase
        noteDatabase.getNoteDao().insertNote(noteNewVerSion)

        // verify that the data is correct
        val note: NoteNewVersion = noteDatabase.getNoteDao().getNoteById(noteNewVerSion.noteId!!)
        assertEquals(note.noteId, noteNewVerSion.noteId)
        assertEquals(note.noteTitle, noteNewVerSion.noteTitle)
        assertEquals(note.noteDes, noteNewVerSion.noteDes)
    }

    // close the database and release any stream resources when the test finishes
    private val migratedRoomDatabase: NoteDatabase
        private get() {
            val database: NoteDatabase = Room.databaseBuilder(
                ApplicationProvider.getApplicationContext(),
                NoteDatabase::class.java, TEST_DB
            )
                .addMigrations(MIGRATION_1_2)
                .build()
            // close the database and release any stream resources when the test finishes
            mMigrationTestHelper.closeWhenFinished(database)
            return database
        }

    var MIGRATION_1_2: Migration = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("ALTER TABLE `Note` ADD COLUMN `Note_Des` TEXT;")
        }
    }

    companion object {
        private const val TEST_DB = "Note_Manager"
        private val noteNewVerSion = NoteNewVersion(1, "note 1 title", "note 1 des")
        private val noteOldVerSion = NoteOldVersion(1, "note 1 title")

        private fun noteValues(): ContentValues {
            val values = ContentValues()
            values.put("Note_Id", noteOldVerSion.noteId)
            values.put("Note_Title", noteOldVerSion.noteTitle)
            return values
        }
    }
}
