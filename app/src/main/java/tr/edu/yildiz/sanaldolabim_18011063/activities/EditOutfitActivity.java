package tr.edu.yildiz.sanaldolabim_18011063.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

import tr.edu.yildiz.sanaldolabim_18011063.R;
import tr.edu.yildiz.sanaldolabim_18011063.models.Outfit;
import tr.edu.yildiz.sanaldolabim_18011063.models.Wear;

import static android.R.layout.simple_spinner_item;
import static android.content.Intent.ACTION_SEND_MULTIPLE;
import static android.content.Intent.EXTRA_STREAM;
import static android.widget.Toast.LENGTH_SHORT;
import static tr.edu.yildiz.sanaldolabim_18011063.Utils.WearTypes.BOTTOMS;
import static tr.edu.yildiz.sanaldolabim_18011063.Utils.WearTypes.FACE_ACCESSORY;
import static tr.edu.yildiz.sanaldolabim_18011063.Utils.WearTypes.HAND_ARM_ACCESSORY;
import static tr.edu.yildiz.sanaldolabim_18011063.Utils.WearTypes.HAT;
import static tr.edu.yildiz.sanaldolabim_18011063.Utils.WearTypes.JACKET;
import static tr.edu.yildiz.sanaldolabim_18011063.Utils.WearTypes.SHOES;
import static tr.edu.yildiz.sanaldolabim_18011063.Utils.WearTypes.TOP;
import static tr.edu.yildiz.sanaldolabim_18011063.Utils.convertBitmapToUri;
import static tr.edu.yildiz.sanaldolabim_18011063.Utils.convertBytesToBitmap;

public class EditOutfitActivity extends AppCompatActivity {
    private int hatId;
    private int faceId;
    private int topId;
    private int jacketId;
    private int handArmId;
    private int bottomsId;
    private int shoesId;
    private Bitmap hatBitmap;
    private Bitmap faceBitmap;
    private Bitmap topBitmap;
    private Bitmap jacketBitmap;
    private Bitmap handArmBitmap;
    private Bitmap bottomsBitmap;
    private Bitmap shoesBitmap;
    private EditText outfitNameEditText;
    private ImageView hatImageButton;
    private ImageView faceImageButton;
    private ImageView topImageButton;
    private ImageView jacketImageButton;
    private ImageView handArmImageButton;
    private ImageView bottomsImageButton;
    private ImageView shoesImageButton;
    private ArrayList<Wear> wears;
    private int outfitId;
    private boolean isGranted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initialize();
    }

    private ActivityResultLauncher<String> permissionLauncher = registerForActivityResult(
            new RequestPermission(),
            isGranted -> this.isGranted = isGranted
    );


    private void initialize() {
        setContentView(R.layout.activity_edit_outfit);

        permissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);

        outfitId = getIntent().getIntExtra("id", -1);
        Outfit outfit = findOutfitFromDb(outfitId);

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

        hatId = outfit.getHatId();
        faceId = outfit.getFaceAccessoryId();
        topId = outfit.getTopId();
        jacketId = outfit.getJacketId();
        handArmId = outfit.getHandArmAccessoryId();
        bottomsId = outfit.getBottomsId();
        shoesId = outfit.getShoesId();

        outfitNameEditText.setText(outfit.getName());
        assignWear("  - " + hatId, HAT);
        assignWear("  - " + faceId, FACE_ACCESSORY);
        assignWear("  - " + topId, TOP);
        assignWear("  - " + jacketId, JACKET);
        assignWear("  - " + handArmId, HAND_ARM_ACCESSORY);
        assignWear("  - " + bottomsId, BOTTOMS);
        assignWear("  - " + shoesId, SHOES);

        hatImageButton.setOnClickListener(_v -> showSelectWearPopup(HAT));
        faceImageButton.setOnClickListener(_v -> showSelectWearPopup(FACE_ACCESSORY));
        topImageButton.setOnClickListener(_v -> showSelectWearPopup(TOP));
        jacketImageButton.setOnClickListener(_v -> showSelectWearPopup(JACKET));
        handArmImageButton.setOnClickListener(_v -> showSelectWearPopup(HAND_ARM_ACCESSORY));
        bottomsImageButton.setOnClickListener(_v -> showSelectWearPopup(BOTTOMS));
        shoesImageButton.setOnClickListener(_v -> showSelectWearPopup(SHOES));
        saveOutfitButton.setOnClickListener(_v -> saveOutfit());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_share, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (!isGranted) {
            Toast.makeText(this, "Paylaşım için izniniz yok!", LENGTH_SHORT).show();
            return true;
        }

        Intent intent = new Intent();
        intent.setAction(ACTION_SEND_MULTIPLE);
        ArrayList<Uri> files = new ArrayList<>();
        files.add(convertBitmapToUri(this, hatBitmap));
        files.add(convertBitmapToUri(this, faceBitmap));
        files.add(convertBitmapToUri(this, topBitmap));
        files.add(convertBitmapToUri(this, jacketBitmap));
        files.add(convertBitmapToUri(this, handArmBitmap));
        files.add(convertBitmapToUri(this, bottomsBitmap));
        files.add(convertBitmapToUri(this, shoesBitmap));
        intent.putParcelableArrayListExtra(EXTRA_STREAM, files);
        intent.setType("message/rfc822");
        startActivity(Intent.createChooser(intent, "Kombin Paylaşımı"));
        return true;
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

        System.out.println("ASSIGNING: " + id + " - " + wearType);

        if (wearType == HAT) {
            hatId = id;
            hatBitmap = wear.getPhoto();
            hatImageButton.setImageBitmap(hatBitmap);
        } else if (wearType == FACE_ACCESSORY) {
            faceId = id;
            faceBitmap = wear.getPhoto();
            faceImageButton.setImageBitmap(faceBitmap);
        } else if (wearType == TOP) {
            topId = id;
            topBitmap = wear.getPhoto();
            topImageButton.setImageBitmap(topBitmap);
        } else if (wearType == JACKET) {
            jacketId = id;
            jacketBitmap = wear.getPhoto();
            jacketImageButton.setImageBitmap(jacketBitmap);
        } else if (wearType == HAND_ARM_ACCESSORY) {
            handArmId = id;
            handArmBitmap = wear.getPhoto();
            handArmImageButton.setImageBitmap(handArmBitmap);
        } else if (wearType == BOTTOMS) {
            bottomsId = id;
            bottomsBitmap = wear.getPhoto();
            bottomsImageButton.setImageBitmap(bottomsBitmap);
        } else if (wearType == SHOES) {
            shoesId = id;
            shoesBitmap = wear.getPhoto();
            shoesImageButton.setImageBitmap(shoesBitmap);
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
            String sql = "UPDATE outfits SET name = ?, hat = ?, face_accessory = ?, top = ?, jacket = ?, hand_arm_accessory = ?, bottoms = ?, shoes = ? WHERE id = " + outfitId;
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
            message = "Kombin başarıyla kaydedildi!";
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

    private Outfit findOutfitFromDb(int id) {
        Outfit result = null;
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = openOrCreateDatabase("Database", MODE_PRIVATE, null);
            cursor = db.rawQuery("SELECT * FROM outfits WHERE id = " + id, null);
            cursor.moveToFirst();

            String name = cursor.getString(cursor.getColumnIndex("name"));
            int hat = cursor.getInt(cursor.getColumnIndex("hat"));
            int face = cursor.getInt(cursor.getColumnIndex("face_accessory"));
            int top = cursor.getInt(cursor.getColumnIndex("top"));
            int jacket = cursor.getInt(cursor.getColumnIndex("jacket"));
            int handArm = cursor.getInt(cursor.getColumnIndex("hand_arm_accessory"));
            int bottoms = cursor.getInt(cursor.getColumnIndex("bottoms"));
            int shoes = cursor.getInt(cursor.getColumnIndex("shoes"));

            result = new Outfit(id, name, hat, face, top, jacket, handArm, bottoms, shoes);
            System.out.println("OUTFIT: " + id + " " + hat + " " + face + " " + top + " " + jacket + " " + handArm + " " + bottoms + " " + shoes);
        } catch (Exception e) {
            Toast.makeText(this, "Bir hata oluştu!", LENGTH_SHORT);
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();
            return result;
        }
    }
}