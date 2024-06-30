package ma.ac.uit.ensa.ssi.Booku.storage;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Database extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "booku.db";
    private static final int DATABASE_VERSION = 1;

    public Database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableQuery = "CREATE TABLE "+ BookDAO.TABLE_NAME+" (" +
                BookDAO.COLUMN_ID+" INTEGER PRIMARY KEY AUTOINCREMENT," +
                BookDAO.COLUMN_ISBN+" TEXT NOT NULL," +
                BookDAO.COLUMN_NAME+" TEXT NOT NULL," +
                "unique("+ BookDAO.COLUMN_ISBN+"));";
        db.execSQL(createTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public SQLiteDatabase get_write() { return this.getWritableDatabase(); }
    public SQLiteDatabase get_read() { return this.getReadableDatabase(); }
}