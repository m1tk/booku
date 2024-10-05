package ma.ac.uit.ensa.ssi.Booku.ui;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import ma.ac.uit.ensa.ssi.Booku.R;
import ma.ac.uit.ensa.ssi.Booku.model.Book;
import ma.ac.uit.ensa.ssi.Booku.storage.BookDAO;
import ma.ac.uit.ensa.ssi.Booku.storage.DatabaseError;
import ma.ac.uit.ensa.ssi.Booku.utils.FileUtils;
import ma.ac.uit.ensa.ssi.Booku.utils.ImagePicker;
import ma.ac.uit.ensa.ssi.Booku.utils.Isbn;

public class EditBookActivity extends AppCompatActivity {
    ImageView book_cover;
    boolean cover_picked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        EdgeToEdge.enable(this);
        setContentView(R.layout.edit_book_activity);

        EditText name  = findViewById(R.id.edit_name);
        EditText isbn  = findViewById(R.id.edit_isbn);
        CardView cover = findViewById(R.id.edit_cover);
        Button submit  = findViewById(R.id.edit_button);
        book_cover     = findViewById(R.id.book_cover);

        Book book     = (Book)getIntent().getSerializableExtra("book");
        name.setText(book.getName());
        isbn.setText(book.getIsbn());
        isbn.setEnabled(false);
        FileUtils.setImageFromPath(this, book_cover, book.getIsbn() + ".jpg", R.drawable.no_cover);

        cover.setOnClickListener(b -> {
            ImagePicker.openImagePicker(this);
        });

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

            book.setName(name.getText().toString());
            book.setIsbn(isbn.getText().toString());

            if (cover_picked) {
                try {
                    FileUtils.write(this, book.getIsbn() + ".jpg", ImagePicker.to_bytes(book_cover));
                } catch (Exception e) {
                    Toast.makeText(
                            this,
                            e.toString(),
                            Toast.LENGTH_SHORT
                    ).show();
                }
            }

            BookDAO book_access = new BookDAO(this.getBaseContext());
            try {
                book_access.updateBook(book);
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
                    String.format(getString(R.string.book_edited), isbn.getText()),
                    Toast.LENGTH_LONG
            ).show();

            Intent ret = new Intent();
            ret.putExtra("editBook", book);
            setResult(Activity.RESULT_OK, ret);
            finish();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ImagePicker.PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            book_cover.setImageURI(imageUri);
            cover_picked = true;
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item){
        if (item.getItemId() == android.R.id.home) {
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }
}