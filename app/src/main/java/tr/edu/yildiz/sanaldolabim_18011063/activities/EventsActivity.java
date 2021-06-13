package tr.edu.yildiz.sanaldolabim_18011063.activities;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
import tr.edu.yildiz.sanaldolabim_18011063.adapters.DrawerRecyclerAdapter;
import tr.edu.yildiz.sanaldolabim_18011063.adapters.EventRecyclerAdapter;
import tr.edu.yildiz.sanaldolabim_18011063.models.Drawer;
import tr.edu.yildiz.sanaldolabim_18011063.models.Event;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static android.widget.Toast.LENGTH_SHORT;

public class EventsActivity extends AppCompatActivity {
    private RecyclerView eventsRecycler;
    private TextView noItemText;
    private ArrayList<Event> events;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initialize();
    }

    private void initialize() {
        setContentView(R.layout.activity_events);
        eventsRecycler = findViewById(R.id.eventsRecyclerView);
        noItemText = findViewById(R.id.noItemText);
    }

    @Override
    protected void onStart() {
        super.onStart();
        fetchEvents();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_add, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        startActivity(new Intent(this, CreateEventActivity.class));
        return true;
    }

    public void fetchEvents() {
        events = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = openOrCreateDatabase("Database", MODE_PRIVATE, null);
            cursor = db.rawQuery("SELECT * FROM events", null);

            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                String name = cursor.getString(cursor.getColumnIndex("name"));
                String type = cursor.getString(cursor.getColumnIndex("type"));
                String date = cursor.getString(cursor.getColumnIndex("date"));
                String location = cursor.getString(cursor.getColumnIndex("location"));
                int outfitId = cursor.getInt(cursor.getColumnIndex("outfit"));

                Event event = new Event(id, name, type, date, location, outfitId);
                events.add(event);
            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Bir hata oluştu!", LENGTH_SHORT);
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();
            EventRecyclerAdapter adapter = new EventRecyclerAdapter(events, this);
            eventsRecycler.setAdapter(adapter);
            eventsRecycler.setLayoutManager(new LinearLayoutManager(this));
            adapter.notifyDataSetChanged();
            noItemText.setVisibility(events.size() == 0 ? VISIBLE : GONE);
        }
    }
}