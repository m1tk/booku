package ma.ac.uit.ensa.ssi.Booku.storage;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import ma.ac.uit.ensa.ssi.Booku.model.Book;

public class BookDAO {
    public Database sdb;

    static final String TABLE_NAME  = "book";
    static final String COLUMN_ID   = "id";
    static final String COLUMN_ISBN = "isbn";
    static final String COLUMN_NAME = "name";

    public BookDAO(Context ctx) {
        this.sdb = new Database(ctx);
    }

    public void close() { this.sdb.close(); }

    public long addBook(Book book) throws DatabaseError {
        SQLiteDatabase db = sdb.get_write();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ISBN, book.getIsbn());
        values.put(COLUMN_NAME, book.getName());
        try {
            long id = db.insertOrThrow(TABLE_NAME, null, values);
            db.close();
            return id;
        } catch (SQLiteConstraintException e) {
            db.close();
            throw new DatabaseError(DatabaseError.ExceptionType.Constraint, "ISBN already exists");
        } catch (SQLException e) {
            db.close();
            throw new DatabaseError(DatabaseError.ExceptionType.Constraint, e.getMessage());
        }
    }

    public void deleteBook(long id) throws DatabaseError {
        SQLiteDatabase db = sdb.getWritableDatabase();
        int affected      = db.delete(TABLE_NAME, COLUMN_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
        if (affected <= 0) {
            throw new DatabaseError(DatabaseError.ExceptionType.NoMatch, "No book to delete with given id");
        }
    }

    public void updateBook(Book book) throws DatabaseError {
        SQLiteDatabase db    = sdb.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ISBN, book.getIsbn());
        values.put(COLUMN_NAME, book.getName());

        int affected = db.update(
                TABLE_NAME,
                values,
                COLUMN_ID + "=?",
                new String[]{String.valueOf(book.getId())}
        );
        db.close();

        if (affected <= 0) {
            throw new DatabaseError(DatabaseError.ExceptionType.NoMatch, "No book to update with given id");
        }
    }

    public Book getBook(long id) throws DatabaseError {
        SQLiteDatabase db = sdb.getReadableDatabase();
        Cursor cursor     = db.query(
                TABLE_NAME,
                new String[] {COLUMN_ISBN, COLUMN_NAME},
                COLUMN_ID + "=?",
                new String[] {String.valueOf(id)},
                null, null, null
        );

        if (cursor == null || !cursor.moveToFirst()) {
            db.close();
            throw new DatabaseError(DatabaseError.ExceptionType.NoMatch, "Book with given id not found");
        }

        Book b = new Book(
                id,
                cursor.getString(1),
                cursor.getString(0)
        );
        cursor.close();
        db.close();
        return b;
    }

    @SuppressLint("Range")
    public List<Book> getAllBooks() {
        SQLiteDatabase db   = sdb.getReadableDatabase();
        List<Book> bookList = new ArrayList<>();

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " ORDER BY ID DESC", null);

        if (cursor.moveToFirst()) {
            do {
                long id     = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
                String isbn = cursor.getString(cursor.getColumnIndex(COLUMN_ISBN));
                String name = cursor.getString(cursor.getColumnIndex(COLUMN_NAME));

                Book book = new Book(id, name, isbn);
                bookList.add(book);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return bookList;
    }
}
