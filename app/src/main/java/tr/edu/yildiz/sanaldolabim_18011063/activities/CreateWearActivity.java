package tr.edu.yildiz.sanaldolabim_18011063.activities;

import android.app.DatePickerDialog.OnDateSetListener;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts.GetContent;
import androidx.appcompat.app.AppCompatActivity;

import tr.edu.yildiz.sanaldolabim_18011063.R;

import static android.R.layout.simple_spinner_item;
import static android.widget.Toast.LENGTH_SHORT;
import static tr.edu.yildiz.sanaldolabim_18011063.Utils.MIME_TYPE_IMAGE;
import static tr.edu.yildiz.sanaldolabim_18011063.Utils.WearTypes.WEAR_TYPES;
import static tr.edu.yildiz.sanaldolabim_18011063.Utils.convertBitmapToBytes;
import static tr.edu.yildiz.sanaldolabim_18011063.Utils.getBitmapFromUri;
import static tr.edu.yildiz.sanaldolabim_18011063.Utils.isDateStringValid;
import static tr.edu.yildiz.sanaldolabim_18011063.Utils.pickDate;

public class CreateWearActivity extends AppCompatActivity {
    private ImageView wearImageView;
    private EditText wearNameEditText;
    private EditText colorEditText;
    private EditText patternEditText;
    private EditText priceEditText;
    private EditText datePurchasedEditText;
    private Spinner wearTypeSpinner;
    private Button selectWearImageButton;
    private Button saveWearButton;
    private ActivityResultLauncher<String> launcher;
    private Bitmap imageBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initialize();
    }

    private void initialize() {
        setContentView(R.layout.activity_create_wear);
        imageBitmap = null;
        wearImageView = findViewById(R.id.wearImageView);
        wearNameEditText = findViewById(R.id.wearNameEditText);
        colorEditText = findViewById(R.id.colorEditText);
        patternEditText = findViewById(R.id.patternEditText);
        priceEditText = findViewById(R.id.priceEditText);
        datePurchasedEditText = findViewById(R.id.datePurchasedEditText);
        wearTypeSpinner = findViewById(R.id.wearTypeSpinner);
        wearTypeSpinner.setAdapter(new ArrayAdapter<>(this, simple_spinner_item, WEAR_TYPES));
        selectWearImageButton = findViewById(R.id.selectWearImageButton);
        saveWearButton = findViewById(R.id.saveWearButton);
        launcher = registerForActivityResult(new GetContent(), uri -> assignImage(uri));
        selectWearImageButton.setOnClickListener(_v -> launcher.launch(MIME_TYPE_IMAGE));
        saveWearButton.setOnClickListener(_v -> saveWear());

        OnDateSetListener dateSetListener = (_v, y, m, d) -> {
            String date = "" + y + "-" + (m + 1) + "-" + d;
            datePurchasedEditText.setText(date);
        };
        datePurchasedEditText.setOnFocusChangeListener((_v, hasFocus) -> {
            if (hasFocus) pickDate(dateSetListener, this);
        });
    }

    private void saveWear() {
        String name = wearNameEditText.getText().toString();
        String color = colorEditText.getText().toString();
        String pattern = patternEditText.getText().toString();
        int type = wearTypeSpinner.getSelectedItemPosition();
        float price;
        try {
            price = Float.parseFloat(priceEditText.getText().toString());
        } catch (Exception e) { price = -1; }
        String datePurchased = datePurchasedEditText.getText().toString();
        byte[] imageBytes;
        try {
            imageBytes = convertBitmapToBytes(imageBitmap);
        } catch (Exception e) { imageBytes = null; }
        int drawerId = getIntent().getIntExtra("id", -1);

        String errorMessage = getErrorMessage(name, color, pattern, type, price, datePurchased, imageBitmap, drawerId);
        if (errorMessage != null) {
            Toast.makeText(getApplicationContext(), errorMessage, LENGTH_SHORT).show();
            return;
        }

        SQLiteDatabase db = null;
        String message = null;
        try {
            db = openOrCreateDatabase("Database", MODE_PRIVATE, null);
            String sql = "INSERT INTO wears (name, color, pattern, type, price, date_purchased, photo, drawer_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            SQLiteStatement statement = db.compileStatement(sql);
            statement.bindString(1, name);
            statement.bindString(2, color);
            statement.bindString(3, pattern);
            statement.bindLong(4, type);
            statement.bindDouble(5, price);
            statement.bindString(6, datePurchased);
            statement.bindBlob(7, imageBytes);
            statement.bindLong(8, drawerId);
            statement.executeInsert();
            message = "Kıyafet başarıyla eklendi!";
        } catch (Exception e) {
            e.printStackTrace();
            message = "Bir hata oluştu!";
        } finally {
            if (db != null) db.close();
            Toast.makeText(getApplicationContext(), message, LENGTH_SHORT).show();
            finish();
        }
    }

    private String getErrorMessage(String name, String color, String pattern, int type, float price, String datePurchased, Bitmap imageBitmap, int drawerId) {
        if (name == null || name.length() <= 0) return "Lütfen bir kıyafet ismi giriniz!";
        if (color == null || color.length() <= 0) return "Lütfen bir renk giriniz!";
        if (pattern == null || pattern.length() <= 0) return "Lütfen bir desen giriniz!";
        if (type < 0 || type > 6) return "Lütfen bir kıyafet tipi seçiniz!";
        if (price < 0) return "Lütfen bir fiyat giriniz!";
        if (!isDateStringValid(datePurchased)) return "Lütfen geçerli bir tarih giriniz!";
        if (imageBitmap == null) return "Lütfen bir resim seçiniz!";
        if (drawerId < 0) return "Bir hata oluştu";
        return null;
    }

    private void assignImage(Uri uri) {
        Bitmap bitmap = getBitmapFromUri(uri, this);
        imageBitmap = bitmap;
        wearImageView.setImageBitmap(bitmap);
    }
}