package ma.ac.uit.ensa.ssi.Booku;


import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import ma.ac.uit.ensa.ssi.Booku.adapter.BookRecycler;
import ma.ac.uit.ensa.ssi.Booku.component.GridSpacingItemDecoration;
import ma.ac.uit.ensa.ssi.Booku.model.Book;
import ma.ac.uit.ensa.ssi.Booku.storage.BookDAO;
import ma.ac.uit.ensa.ssi.Booku.utils.OnItemSelectedListener;

public class MainActivity extends AppCompatActivity implements OnItemSelectedListener {
    private BookDAO book_access;

    RecyclerView books_view;
    BookRecycler adapter;

    ActivityResultLauncher<Intent> add_book_activity_ret;

    enum MenuType {
        Normal,
        Action
    }

    private MenuType menuType = MenuType.Normal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Dialog dialog = new Dialog(MainActivity.this, android.R.style.Theme_Light_NoTitleBar_Fullscreen);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.splash_screen);
        dialog.setCancelable(true);
        dialog.show();

        new Thread(() -> {
            book_access = new BookDAO(this.getBaseContext());

            books_view = findViewById(R.id.book_view);
            books_view.setLayoutManager(new GridLayoutManager(this, 2));

            int spacing = getResources().getDimensionPixelSize(R.dimen.book_grid_spacing);
            books_view.addItemDecoration(new GridSpacingItemDecoration(2, spacing, true));
            adapter = new BookRecycler(getApplicationContext(), this, book_access);
            books_view.setAdapter(adapter);

            runOnUiThread(() -> dialog.dismiss());
        }).start();

        add_book_activity_ret = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == AppCompatActivity.RESULT_OK) {
                        Intent data = result.getData();
                        Book book = (Book)data.getSerializableExtra("addBook");
                        adapter.addBook(book);
                        books_view.smoothScrollToPosition(0);
                    }
                }
        );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        if (menuType == MenuType.Normal) {
            getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE);
            getSupportActionBar().setCustomView(null);
            inflater.inflate(R.menu.main_activity_top_bar, menu);
        } else {
            getSupportActionBar().setCustomView(R.layout.book_action);
            ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(false);
            actionBar.setDisplayShowHomeEnabled (false);
            actionBar.setDisplayUseLogoEnabled(false);
            actionBar.setDisplayShowCustomEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);

            ImageView cancel = findViewById(R.id.action_cancel);
            cancel.setOnClickListener(v -> {
                onRelease();
                adapter.unselectAll();
            });
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int i = item.getItemId();
        if (i == R.id.action_add) {
            Intent addBookIntent = new Intent(this, ma.ac.uit.ensa.ssi.Booku.ui.AddBookActivity.class);
            add_book_activity_ret.launch(addBookIntent);
        }
        return super.onOptionsItemSelected(item);
    }

    public void onItemSelected() {
        menuType = MenuType.Action;
        invalidateOptionsMenu();

        RelativeLayout v = findViewById(R.id.bottom_action);
        v.setVisibility(View.VISIBLE);
        books_view.setPadding(0, 0,0, v.getHeight());
    }

    public void onRelease() {
        menuType = MenuType.Normal;
        invalidateOptionsMenu();

        RelativeLayout v = findViewById(R.id.bottom_action);
        v.setVisibility(View.INVISIBLE);
        books_view.setPadding(0, 0,0, 0);
    }
}