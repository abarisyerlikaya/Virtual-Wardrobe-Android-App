package tr.edu.yildiz.sanaldolabim_18011063.activities;

import android.app.AlertDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import tr.edu.yildiz.sanaldolabim_18011063.R;
import tr.edu.yildiz.sanaldolabim_18011063.adapters.DrawerRecyclerAdapter;
import tr.edu.yildiz.sanaldolabim_18011063.models.Drawer;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static android.widget.Toast.LENGTH_SHORT;

public class DrawersActivity extends AppCompatActivity {
    private RecyclerView drawersRecycler;
    private TextView noItemText;
    private ArrayList<Drawer> drawers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initialize();
    }

    private void initialize() {
        setContentView(R.layout.activity_drawers);
        drawersRecycler = findViewById(R.id.drawersRecyclerView);
        noItemText = findViewById(R.id.noItemText);
    }

    @Override
    protected void onStart() {
        super.onStart();
        fetchDrawers();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_add, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Çekmece Oluştur");

        EditText input = new EditText(this);
        input.setHint("Çekmece Adı");
        input.setPadding(20, 20, 20, 20);
        builder.setView(input);

        builder.setPositiveButton("Oluştur", (_d, _w) -> createDrawer(input.getText().toString()));
        builder.setNegativeButton("İptal", (dialog, _w) -> dialog.cancel());

        builder.show();
        return true;
    }

    private void createDrawer(String name) {
        if (name == null || name.length() <= 0) {
            Toast.makeText(getApplicationContext(), "Lütfen bir çekmece adı giriniz!", LENGTH_SHORT).show();
            return;
        }

        SQLiteDatabase db = null;
        String message = null;
        try {
            db = openOrCreateDatabase("Database", MODE_PRIVATE, null);
            String sql = "INSERT INTO drawers (name) VALUES (?)";
            SQLiteStatement statement = db.compileStatement(sql);
            statement.bindString(1, name);
            statement.executeInsert();
            message = "Çekmece başarıyla eklendi!";
        } catch (Exception e) {
            message = "Bir hata oluştu!";
        } finally {
            if (db != null) db.close();
            Toast.makeText(getApplicationContext(), message, LENGTH_SHORT).show();
            fetchDrawers();
        }
    }

    public void fetchDrawers() {
        drawers = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = openOrCreateDatabase("Database", MODE_PRIVATE, null);
            cursor = db.rawQuery("SELECT * FROM drawers", null);

            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                String name = cursor.getString(cursor.getColumnIndex("name"));
                System.out.println("NAME: " + name);
                Drawer drawer = new Drawer(id, name);

                Cursor countCursor = db.rawQuery("SELECT count(*) AS count FROM wears WHERE drawer_id = " + id, null);
                countCursor.moveToFirst();
                int wearCount = countCursor.getInt(countCursor.getColumnIndex("count"));
                countCursor.close();

                drawer.setWearCount(wearCount);
                drawers.add(drawer);
            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Bir hata oluştu!", LENGTH_SHORT);
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();
            DrawerRecyclerAdapter adapter = new DrawerRecyclerAdapter(drawers, this);
            drawersRecycler.setAdapter(adapter);
            drawersRecycler.setLayoutManager(new LinearLayoutManager(this));
            adapter.notifyDataSetChanged();
            noItemText.setVisibility(drawers.size() == 0 ? VISIBLE : GONE);
        }
    }
}