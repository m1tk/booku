package ma.ac.uit.ensa.ssi.Booku;

import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

import ma.ac.uit.ensa.ssi.Booku.model.Book;
import ma.ac.uit.ensa.ssi.Booku.storage.*;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class DatabaseTest {
    private BookDAO bookAccess;

    @Before
    public void setUp(){
        bookAccess = new BookDAO(InstrumentationRegistry.getInstrumentation().getTargetContext());
    }

    @After
    public void finish() {
        bookAccess.close();
    }

    @Test
    public void addBook() throws DatabaseError {
        bookAccess.addBook(new Book(0L, "Book 1", "1-1-1"));
        bookAccess.addBook(new Book(0L, "Book 2", "1-2-2"));
        boolean duplicate = false;
        try {
            bookAccess.addBook(new Book(0L, "Book 3", "1-2-2"));
        } catch (DatabaseError e) {
            if (e.getType() == DatabaseError.ExceptionType.Constraint) {
                duplicate = true;
            }
        }
        assertTrue(duplicate);
    }

    @Test
    public void updateBook() throws DatabaseError {
        Book b  = new Book(0L, "Book 4", "1-3-3");
        long id = bookAccess.addBook(b);
        b.setId(id);

        assertEquals(bookAccess.getBook(id).getName(), b.getName());

        b.setName("Book 5");
        bookAccess.updateBook(b);

        assertEquals(bookAccess.getBook(id).getName(), b.getName());
    }

    @Test
    public void deleteBook() throws DatabaseError {
        Book b  = new Book(0L, "Book 6", "1-4-4");
        long id = bookAccess.addBook(b);
        b.setId(id);

        assertEquals(bookAccess.getBook(id).getName(), b.getName());

        bookAccess.deleteBook(id);

        boolean deleted = false;

        try {
            bookAccess.getBook(id);
        } catch (DatabaseError e) {
            deleted = true;
        }

        assertTrue(deleted);
    }
}