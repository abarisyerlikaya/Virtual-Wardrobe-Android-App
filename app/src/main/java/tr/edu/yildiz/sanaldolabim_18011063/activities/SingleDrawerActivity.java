package tr.edu.yildiz.sanaldolabim_18011063.activities;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import tr.edu.yildiz.sanaldolabim_18011063.R;
import tr.edu.yildiz.sanaldolabim_18011063.adapters.WearRecyclerAdapter;
import tr.edu.yildiz.sanaldolabim_18011063.models.Wear;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static android.widget.Toast.LENGTH_SHORT;
import static tr.edu.yildiz.sanaldolabim_18011063.Utils.convertBytesToBitmap;

public class SingleDrawerActivity extends AppCompatActivity {
    private RecyclerView wearsRecycler;
    private TextView noItemText;
    private ArrayList<Wear> wears;
    private int drawerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initialize();
    }

    private void initialize() {
        setContentView(R.layout.activity_single_drawer);
        drawerId = getIntent().getIntExtra("id", -1);
        wearsRecycler = findViewById(R.id.wearsRecyclerView);
        noItemText = findViewById(R.id.noItemText);
    }

    @Override
    protected void onStart() {
        super.onStart();
        fetchWears();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_add, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent = new Intent(this, CreateWearActivity.class);
        intent.putExtra("id", getIntent().getIntExtra("id", -1));
        startActivity(intent);
        return true;
    }

    public void fetchWears() {
        wears = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = openOrCreateDatabase("Database", MODE_PRIVATE, null);
            cursor = db.rawQuery("SELECT * FROM wears WHERE drawer_id = " + drawerId, null);
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                String name = cursor.getString(cursor.getColumnIndex("name"));
                String color = cursor.getString(cursor.getColumnIndex("color"));
                String pattern = cursor.getString(cursor.getColumnIndex("pattern"));
                int type = cursor.getInt(cursor.getColumnIndex("type"));
                float price = cursor.getFloat(cursor.getColumnIndex("price"));
                String datePurchased = cursor.getString(cursor.getColumnIndex("date_purchased"));
                Bitmap photo = convertBytesToBitmap(cursor.getBlob(cursor.getColumnIndex("photo")));
                Wear wear = new Wear(id, name, color, pattern, type, price, datePurchased, photo);
                wears.add(wear);
            }
        } catch (Exception e) {
            Toast.makeText(this, "Bir hata oluştu!", LENGTH_SHORT);
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();
            WearRecyclerAdapter adapter = new WearRecyclerAdapter(wears, this);
            wearsRecycler.setAdapter(adapter);
            wearsRecycler.setLayoutManager(new LinearLayoutManager(this));
            adapter.notifyDataSetChanged();
            noItemText.setVisibility(wears.size() == 0 ? VISIBLE : GONE);
        }
    }
}