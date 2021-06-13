package tr.edu.yildiz.sanaldolabim_18011063.activities;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import tr.edu.yildiz.sanaldolabim_18011063.R;

import static tr.edu.yildiz.sanaldolabim_18011063.Utils.SQLCodes.CREATE_DRAWERS_TABLE;
import static tr.edu.yildiz.sanaldolabim_18011063.Utils.SQLCodes.CREATE_EVENTS_TABLE;
import static tr.edu.yildiz.sanaldolabim_18011063.Utils.SQLCodes.CREATE_OUTFITS_TABLE;
import static tr.edu.yildiz.sanaldolabim_18011063.Utils.SQLCodes.CREATE_WEARS_TABLE;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initialize();
    }

    private void initialize() {
        setContentView(R.layout.activity_main);
        Button goToDrawersButton = findViewById(R.id.goToDrawersButton);
        Button goToCabinetRoomButton = findViewById(R.id.goToCabinetRoomButton);
        Button goToEventsButton = findViewById(R.id.goToEventsButton);

        goToDrawersButton.setOnClickListener(v -> goToDrawers());
        goToCabinetRoomButton.setOnClickListener(v -> goToCabinetRoom());
        goToEventsButton.setOnClickListener(v -> goToEvents());

        createDatabaseIfNotExists();
    }

    private void createDatabaseIfNotExists() {
        SQLiteDatabase db = null;
        try {
            db = openOrCreateDatabase("Database", MODE_PRIVATE, null);
            db.execSQL(CREATE_DRAWERS_TABLE);
            db.execSQL(CREATE_WEARS_TABLE);
            db.execSQL(CREATE_OUTFITS_TABLE);
            db.execSQL(CREATE_EVENTS_TABLE);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "An error occurred!", Toast.LENGTH_SHORT).show();
        } finally {
            if (db != null) db.close();
        }
    }

    private void goToDrawers() {
        startActivity(new Intent(this, DrawersActivity.class));
    }

    private void goToCabinetRoom() {
        startActivity(new Intent(this, CabinetRoomActivity.class));
    }

    private void goToEvents() { startActivity(new Intent(this, EventsActivity.class)); }
}