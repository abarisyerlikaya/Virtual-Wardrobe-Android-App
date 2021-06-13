package tr.edu.yildiz.sanaldolabim_18011063.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import java.util.ArrayList;

import tr.edu.yildiz.sanaldolabim_18011063.R;
import tr.edu.yildiz.sanaldolabim_18011063.adapters.DrawerRecyclerAdapter;
import tr.edu.yildiz.sanaldolabim_18011063.adapters.OutfitRecyclerAdapter;
import tr.edu.yildiz.sanaldolabim_18011063.models.Outfit;
import tr.edu.yildiz.sanaldolabim_18011063.models.Wear;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static android.widget.Toast.LENGTH_SHORT;
import static tr.edu.yildiz.sanaldolabim_18011063.Utils.convertBytesToBitmap;

public class CabinetRoomActivity extends AppCompatActivity {
    private RecyclerView outfitsRecycler;
    private TextView noItemText;
    private ArrayList<Outfit> outfits;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initialize();
    }

    private void initialize() {
        setContentView(R.layout.activity_cabinet_room);
        outfitsRecycler = findViewById(R.id.outfitsRecyclerView);
        noItemText = findViewById(R.id.noItemText);
    }

    @Override
    protected void onStart() {
        super.onStart();
        fetchOutfits();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_add, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        startActivity(new Intent(this, CreateOutfitActivity.class));
        return true;
    }

    public void fetchOutfits() {
        outfits = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = openOrCreateDatabase("Database", MODE_PRIVATE, null);
            cursor = db.rawQuery("SELECT * FROM outfits", null);
            while (cursor.moveToNext()) {
                String name = cursor.getString(cursor.getColumnIndex("name"));
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                int hatId = cursor.getInt(cursor.getColumnIndex("hat"));
                int faceId = cursor.getInt(cursor.getColumnIndex("face_accessory"));
                int topId = cursor.getInt(cursor.getColumnIndex("top"));
                int jacketId = cursor.getInt(cursor.getColumnIndex("jacket"));
                int handArmId = cursor.getInt(cursor.getColumnIndex("hand_arm_accessory"));
                int bottomsId = cursor.getInt(cursor.getColumnIndex("bottoms"));
                int shoesId = cursor.getInt(cursor.getColumnIndex("shoes"));
                Outfit outfit = new Outfit(id, name, hatId, faceId, topId, jacketId, handArmId, bottomsId, shoesId);
                outfits.add(outfit);
            }
        } catch (Exception e) {
            Toast.makeText(this, "Bir hata oluştu!", LENGTH_SHORT);
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();
            OutfitRecyclerAdapter adapter = new OutfitRecyclerAdapter(outfits, this);
            outfitsRecycler.setAdapter(adapter);
            outfitsRecycler.setLayoutManager(new LinearLayoutManager(this));
            adapter.notifyDataSetChanged();
            noItemText.setVisibility(outfits.size() == 0 ? VISIBLE : GONE);
        }
    }
}