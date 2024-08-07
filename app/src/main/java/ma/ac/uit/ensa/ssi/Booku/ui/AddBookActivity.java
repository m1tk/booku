package ma.ac.uit.ensa.ssi.Booku.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import ma.ac.uit.ensa.ssi.Booku.R;
import ma.ac.uit.ensa.ssi.Booku.model.Book;
import ma.ac.uit.ensa.ssi.Booku.storage.BookDAO;
import ma.ac.uit.ensa.ssi.Booku.storage.DatabaseError;
import ma.ac.uit.ensa.ssi.Booku.utils.Isbn;

public class AddBookActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        EdgeToEdge.enable(this);
        setContentView(R.layout.add_book_activity);

        EditText name = findViewById(R.id.add_name);
        EditText isbn = findViewById(R.id.add_isbn);
        Button submit = findViewById(R.id.add_button);

        submit.setOnClickListener(b -> {
            if (name.getText().length() == 0) {
                Toast.makeText(this, getString(R.string.empty_book_name), Toast.LENGTH_SHORT).show();
                return;
            } else if (isbn.getText().length() == 0) {
                Toast.makeText(this, getString(R.string.empty_book_isbn), Toast.LENGTH_SHORT).show();
                return;
            }

            if (!Isbn.is_valid(isbn.getText().toString())) {
                Toast.makeText(this, getString(R.string.invalid_book_isbn), Toast.LENGTH_SHORT).show();
                return;
            }

            Book book           = new Book(0L, name.getText().toString(), isbn.getText().toString());
            BookDAO book_access = new BookDAO(this.getBaseContext());
            try {
                long id = book_access.addBook(book);
                book.setId(id);
            } catch (DatabaseError e) {
                if (e.getType() == DatabaseError.ExceptionType.Constraint) {
                    Toast.makeText(
                            this,
                            String.format(getString(R.string.isbn_exists), isbn.getText()),
                            Toast.LENGTH_SHORT
                    ).show();
                } else {
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                book_access.close();
                return;
            }
            book_access.close();

            Toast.makeText(
                    this,
                    String.format(getString(R.string.book_added), isbn.getText()),
                    Toast.LENGTH_LONG
            ).show();

            Intent ret = new Intent();
            ret.putExtra("addBook", book);
            setResult(Activity.RESULT_OK, ret);
            finish();
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item){
        if (item.getItemId() == android.R.id.home) {
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }
}