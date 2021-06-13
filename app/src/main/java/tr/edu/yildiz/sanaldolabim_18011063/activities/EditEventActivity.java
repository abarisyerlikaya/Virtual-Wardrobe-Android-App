package tr.edu.yildiz.sanaldolabim_18011063.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

import tr.edu.yildiz.sanaldolabim_18011063.R;
import tr.edu.yildiz.sanaldolabim_18011063.Utils;
import tr.edu.yildiz.sanaldolabim_18011063.models.Event;

import static android.R.layout.simple_spinner_item;
import static android.widget.Toast.LENGTH_SHORT;
import static tr.edu.yildiz.sanaldolabim_18011063.Utils.INVALID_COORDINATE;
import static tr.edu.yildiz.sanaldolabim_18011063.Utils.getAddressFromCoordinates;
import static tr.edu.yildiz.sanaldolabim_18011063.Utils.pickDate;

public class EditEventActivity extends AppCompatActivity {
    private int eventId;
    private EditText eventNameEditText;
    private EditText eventTypeEditText;
    private EditText eventDateEditText;
    private Spinner eventOutfitSpinner;
    private Button pickLocationButton;
    private Button saveEventButton;
    private ArrayList<String> outfits;
    private static final int REQUEST_CODE = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initialize();
    }

    private void initialize() {
        setContentView(R.layout.activity_edit_event);
        fetchOutfits();
        eventId = getIntent().getIntExtra("id", -1);

        eventNameEditText = findViewById(R.id.eventNameEditText);
        eventTypeEditText = findViewById(R.id.eventTypeEditText);
        eventDateEditText = findViewById(R.id.eventDateEditText);
        eventOutfitSpinner = findViewById(R.id.eventOutfitSpinner);
        pickLocationButton = findViewById(R.id.pickLocationButton);
        saveEventButton = findViewById(R.id.saveEventButton);

        eventOutfitSpinner.setAdapter(new ArrayAdapter<>(this, simple_spinner_item, outfits));
        pickLocationButton.setOnClickListener(_v -> goToMap());
        saveEventButton.setOnClickListener(_v -> saveEvent());

        Event event = findEventFromDb(eventId);
        eventNameEditText.setText(event.getName());
        eventTypeEditText.setText(event.getType());
        eventDateEditText.setText(event.getDate());
        pickLocationButton.setText(event.getLocation());
        selectOutfitFromSpinner(eventOutfitSpinner, event.getOutfitId());

        DatePickerDialog.OnDateSetListener dateSetListener = (_v, y, m, d) -> {
            String date = "" + y + "-" + (m + 1) + "-" + d;
            eventDateEditText.setText(date);
        };
        eventDateEditText.setOnFocusChangeListener((_v, hasFocus) -> {
            if (hasFocus) pickDate(dateSetListener, this);
        });
    }

    @SuppressWarnings("deprecation")
    private void goToMap() {
        Intent intent = new Intent(this, MapActivity.class);
        startActivityForResult(intent, REQUEST_CODE);
    }

    private void saveEvent() {
        String name = eventNameEditText.getText().toString();
        String type = eventTypeEditText.getText().toString();
        String date = eventDateEditText.getText().toString();
        String location = pickLocationButton.getText().toString();
        int outfitId = -1;
        try {
            outfitId = Integer.parseInt(((String) eventOutfitSpinner.getSelectedItem()).split(" - ")[1]);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Bir hata oluştu!", LENGTH_SHORT).show();
        }

        String errorMessage = getErrorMessage(name, type, date, location, outfitId);
        if (errorMessage != null) {
            Toast.makeText(getApplicationContext(), errorMessage, LENGTH_SHORT).show();
            return;
        }

        SQLiteDatabase db = null;
        String message = null;
        try {
            db = openOrCreateDatabase("Database", MODE_PRIVATE, null);
            String sql = "UPDATE events SET name = ?, type = ?, date = ?, location = ?, outfit = ?";
            SQLiteStatement statement = db.compileStatement(sql);
            statement.bindString(1, name);
            statement.bindString(2, type);
            statement.bindString(3, date);
            statement.bindString(4, location);
            statement.bindLong(5, outfitId);
            statement.executeInsert();
            message = "Etkinlik başarıyla kaydedildi!";
        } catch (Exception e) {
            message = "Bir hata oluştu!";
        } finally {
            if (db != null) db.close();
            Toast.makeText(getApplicationContext(), message, LENGTH_SHORT).show();
            finish();
        }
    }

    private String getErrorMessage(String name, String type, String date, String location, int outfitId) {
        if (name == null || name.length() <= 0) return "Lütfen bir isim giriniz!";
        if (type == null || type.length() <= 0) return "Lütfen bir etkinlik tipi giriniz!";
        if (!Utils.isDateStringValid(date)) return "Lütfen geçerli bir tarih seçiniz!";
        if (location == null || location.length() <= 0) return "Lütfen bir konum seçiniz!";
        if (outfitId < 0) return "Lütfen bir kombin seçiniz!";
        return null;
    }

    private void fetchOutfits() {
        outfits = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = openOrCreateDatabase("Database", MODE_PRIVATE, null);
            cursor = db.rawQuery("SELECT * FROM outfits", null);
            while (cursor.moveToNext()) {
                String name = cursor.getString(cursor.getColumnIndex("name"));
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                String option = name + " - " + id;
                outfits.add(option);
            }
        } catch (Exception e) {
            Toast.makeText(this, "Bir hata oluştu!", LENGTH_SHORT);
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();
        }
    }

    private Event findEventFromDb(int id) {
        Event result = null;
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = openOrCreateDatabase("Database", MODE_PRIVATE, null);
            cursor = db.rawQuery("SELECT * FROM events WHERE id = " + id, null);
            cursor.moveToFirst();

            String name = cursor.getString(cursor.getColumnIndex("name"));
            String type = cursor.getString(cursor.getColumnIndex("type"));
            String date = cursor.getString(cursor.getColumnIndex("date"));
            String location = cursor.getString(cursor.getColumnIndex("location"));
            int outfitId = cursor.getInt(cursor.getColumnIndex("outfit"));

            result = new Event(id, name, type, date, location, outfitId);
        } catch (Exception e) {
            Toast.makeText(this, "Bir hata oluştu!", LENGTH_SHORT);
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();
            return result;
        }
    }

    private void selectOutfitFromSpinner(Spinner spinner, int outfitId) {
        int itemCount = spinner.getAdapter().getCount();
        for (int i = 0; i < itemCount; i++) {
            try {
                String item = (String) spinner.getItemAtPosition(i);
                int itemId = Integer.parseInt(item.split(" - ")[1]);
                if (outfitId == itemId) {
                    spinner.setSelection(i);
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Bir hata oluştu!", LENGTH_SHORT);
            }

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            double lat = data.getDoubleExtra("latitude", INVALID_COORDINATE);
            double lng = data.getDoubleExtra("longitude", INVALID_COORDINATE);
            String address = getAddressFromCoordinates(lat, lng, getApplicationContext());
            pickLocationButton.setText(address);
        }
    }
}