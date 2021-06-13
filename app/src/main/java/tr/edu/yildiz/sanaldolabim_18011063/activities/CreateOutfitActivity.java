package tr.edu.yildiz.sanaldolabim_18011063.activities;

import android.app.AlertDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

import tr.edu.yildiz.sanaldolabim_18011063.R;
import tr.edu.yildiz.sanaldolabim_18011063.models.Wear;

import static android.R.layout.simple_spinner_item;
import static android.widget.Toast.LENGTH_SHORT;
import static tr.edu.yildiz.sanaldolabim_18011063.Utils.WearTypes.BOTTOMS;
import static tr.edu.yildiz.sanaldolabim_18011063.Utils.WearTypes.FACE_ACCESSORY;
import static tr.edu.yildiz.sanaldolabim_18011063.Utils.WearTypes.HAND_ARM_ACCESSORY;
import static tr.edu.yildiz.sanaldolabim_18011063.Utils.WearTypes.HAT;
import static tr.edu.yildiz.sanaldolabim_18011063.Utils.WearTypes.JACKET;
import static tr.edu.yildiz.sanaldolabim_18011063.Utils.WearTypes.SHOES;
import static tr.edu.yildiz.sanaldolabim_18011063.Utils.WearTypes.TOP;
import static tr.edu.yildiz.sanaldolabim_18011063.Utils.convertBytesToBitmap;

public class CreateOutfitActivity extends AppCompatActivity {
    private int hatId;
    private int faceId;
    private int topId;
    private int jacketId;
    private int handArmId;
    private int bottomsId;
    private int shoesId;
    private EditText outfitNameEditText;
    private ImageView hatImageButton;
    private ImageView faceImageButton;
    private ImageView topImageButton;
    private ImageView jacketImageButton;
    private ImageView handArmImageButton;
    private ImageView bottomsImageButton;
    private ImageView shoesImageButton;
    private ArrayList<Wear> wears;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initialize();
    }

    private void initialize() {
        setContentView(R.layout.activity_create_outfit);

        hatId = -1;
        faceId = -1;
        topId = -1;
        jacketId = -1;
        handArmId = -1;
        bottomsId = -1;
        shoesId = -1;

        wears = new ArrayList<>();
        fetchWears();

        outfitNameEditText = findViewById(R.id.outfitNameEditText);
        hatImageButton = findViewById(R.id.hatImageButton);
        faceImageButton = findViewById(R.id.faceImageButton);
        topImageButton = findViewById(R.id.topImageButton);
        jacketImageButton = findViewById(R.id.jacketImageButton);
        handArmImageButton = findViewById(R.id.handArmImageButton);
        bottomsImageButton = findViewById(R.id.bottomsImageButton);
        shoesImageButton = findViewById(R.id.shoesImageButton);
        Button saveOutfitButton = findViewById(R.id.saveOutfitButton);

        hatImageButton.setOnClickListener(_v -> showSelectWearPopup(HAT));
        faceImageButton.setOnClickListener(_v -> showSelectWearPopup(FACE_ACCESSORY));
        topImageButton.setOnClickListener(_v -> showSelectWearPopup(TOP));
        jacketImageButton.setOnClickListener(_v -> showSelectWearPopup(JACKET));
        handArmImageButton.setOnClickListener(_v -> showSelectWearPopup(HAND_ARM_ACCESSORY));
        bottomsImageButton.setOnClickListener(_v -> showSelectWearPopup(BOTTOMS));
        shoesImageButton.setOnClickListener(_v -> showSelectWearPopup(SHOES));
        saveOutfitButton.setOnClickListener(_v -> saveOutfit());
    }

    private void showSelectWearPopup(int wearType) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Kıyafet Seç");

        ArrayList<String> items = new ArrayList<>();

        for (Wear wear : wears)
            if (wear.getType() == wearType) items.add(wear.getName() + " - " + wear.getId());

        Spinner input = new Spinner(this);
        input.setPadding(20, 20, 20, 20);

        input.setAdapter(new ArrayAdapter<>(this, simple_spinner_item, items));
        builder.setView(input);

        builder.setPositiveButton("Tamam", (_d, _w) -> assignWear(input.getSelectedItem().toString(), wearType));
        builder.setNegativeButton("İptal", (dialog, _w) -> dialog.cancel());

        builder.show();
    }


    private void assignWear(String item, int wearType) {
        int id;
        String idString = item.split(" - ")[1];

        if (idString != null) id = Integer.parseInt(idString);
        else return;

        Wear wear = null;
        for (Wear i : wears)
            if (i.getId() == id) wear = i;

        if (wear == null) return;

        if (wearType == HAT) {
            hatId = id;
            hatImageButton.setImageBitmap(wear.getPhoto());
        } else if (wearType == FACE_ACCESSORY) {
            faceId = id;
            faceImageButton.setImageBitmap(wear.getPhoto());
        } else if (wearType == TOP) {
            topId = id;
            topImageButton.setImageBitmap(wear.getPhoto());
        } else if (wearType == JACKET) {
            jacketId = id;
            jacketImageButton.setImageBitmap(wear.getPhoto());
        } else if (wearType == HAND_ARM_ACCESSORY) {
            handArmId = id;
            handArmImageButton.setImageBitmap(wear.getPhoto());
        } else if (wearType == BOTTOMS) {
            bottomsId = id;
            bottomsImageButton.setImageBitmap(wear.getPhoto());
        } else if (wearType == SHOES) {
            shoesId = id;
            shoesImageButton.setImageBitmap(wear.getPhoto());
        }
    }

    private void saveOutfit() {
        String name = outfitNameEditText.getText().toString();

        String errorMessage = getErrorMessage(name, hatId, faceId, topId, jacketId, handArmId, bottomsId, shoesId);
        if (errorMessage != null) {
            Toast.makeText(getApplicationContext(), errorMessage, LENGTH_SHORT).show();
            return;
        }

        SQLiteDatabase db = null;
        String message = null;
        try {
            db = openOrCreateDatabase("Database", MODE_PRIVATE, null);
            String sql = "INSERT INTO outfits (name, hat, face_accessory, top, jacket, hand_arm_accessory, bottoms, shoes) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            SQLiteStatement statement = db.compileStatement(sql);
            statement.bindString(1, name);
            statement.bindLong(2, hatId);
            statement.bindLong(3, faceId);
            statement.bindLong(4, topId);
            statement.bindLong(5, jacketId);
            statement.bindLong(6, handArmId);
            statement.bindLong(7, bottomsId);
            statement.bindLong(8, shoesId);
            statement.executeInsert();
            message = "Kombin başarıyla eklendi!";
        } catch (Exception e) {
            e.printStackTrace();
            message = "Bir hata oluştu!";
        } finally {
            if (db != null) db.close();
            Toast.makeText(getApplicationContext(), message, LENGTH_SHORT).show();
            finish();
        }
    }

    private String getErrorMessage(String name, int hat, int face, int top, int jacket, int handArm, int bottoms, int shoes) {
        if (name == null || name.length() < 0) return "Lütfen bir isim giriniz!";
        if (hat < 0) return "Lütfen bir şapka seçiniz!";
        if (face < 0) return "Lütfen bir yüz - yüz aksesuarı seçiniz!";
        if (top < 0) return "Lütfen bir üst seçiniz!";
        if (jacket < 0) return "Lütfen bir ceket seçiniz!";
        if (handArm < 0) return "Lütfen bir el-kol aksesuarı seçiniz!";
        if (bottoms < 0) return "Lütfen bir alt seçiniz!";
        if (shoes < 0) return "Lütfen bir ayakkabı seçiniz!";
        return null;
    }

    private void fetchWears() {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = openOrCreateDatabase("Database", MODE_PRIVATE, null);
            cursor = db.rawQuery("SELECT * FROM wears", null);
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
                System.out.println("ITEM: " + name + " " + id);
                wears.add(wear);
            }
        } catch (Exception e) {
            Toast.makeText(this, "Bir hata oluştu!", LENGTH_SHORT);
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();
        }
    }

}