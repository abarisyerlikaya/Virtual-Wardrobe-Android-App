package tr.edu.yildiz.sanaldolabim_18011063.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import java.util.ArrayList;

import tr.edu.yildiz.sanaldolabim_18011063.R;
import tr.edu.yildiz.sanaldolabim_18011063.activities.DrawersActivity;
import tr.edu.yildiz.sanaldolabim_18011063.activities.SingleDrawerActivity;
import tr.edu.yildiz.sanaldolabim_18011063.models.Drawer;

import static android.content.Context.MODE_PRIVATE;
import static android.widget.Toast.LENGTH_SHORT;

public class DrawerRecyclerAdapter extends Adapter<DrawerRecyclerAdapter.DrawerViewHolder> {
    private final ArrayList<Drawer> drawers;
    private final Context context;

    public DrawerRecyclerAdapter(ArrayList<Drawer> drawers, Context context) {
        this.drawers = drawers;
        this.context = context;
    }

    @NonNull
    @Override
    public DrawerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.single_item_drawer, parent, false);
        return new DrawerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DrawerViewHolder holder, int position) {
        Drawer drawer = drawers.get(position);
        holder.drawerNameTextView.setText(drawer.getName());
        holder.wearCountTextView.setText(drawer.getWearCount() + " kıyafet");
        holder.itemCardView.setOnClickListener(_v -> goToSingleDrawer(drawer.getId()));
        holder.drawerItemEditIcon.setOnClickListener(_v -> showEditDrawerDialog(drawer.getId()));
        holder.drawerItemRemoveIcon.setOnClickListener(_v -> showRemoveDrawerDialog(drawer.getId()));
    }

    @Override
    public int getItemCount() { return drawers.size(); }

    private void goToSingleDrawer(int id) {
        Intent intent = new Intent(context, SingleDrawerActivity.class);
        intent.putExtra("id", id);
        context.startActivity(intent);
    }

    private void showEditDrawerDialog(int id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Çekmece Adı Değiştir");

        EditText input = new EditText(context);
        input.setHint("Yeni Çekmece Adı");
        input.setPadding(20, 20, 20, 20);
        builder.setView(input);

        builder.setPositiveButton("Değiştir", (_d, _w) -> changeDrawerName(id, input.getText().toString()));
        builder.setNegativeButton("İptal", (dialog, _w) -> dialog.cancel());

        builder.show();
    }

    private void changeDrawerName(int id, String name) {
        if (name == null || name.length() <= 0)
            return;

        SQLiteDatabase db = null;
        String message = null;
        try {
            db = context.openOrCreateDatabase("Database", MODE_PRIVATE, null);
            db.execSQL("UPDATE drawers SET name = '" + name + "' WHERE id = " + id);
            message = "Çekmece ismi başarıyla değiştirildi!";
        } catch (Exception e) {
            message = "Bir hata oluştu!";
        } finally {
            if (db != null) db.close();
            Toast.makeText(context, message, LENGTH_SHORT).show();
            ((DrawersActivity) context).fetchDrawers();
        }
    }

    private void showRemoveDrawerDialog(int id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Çekmece Siliniyor");
        builder.setMessage("Çekmeceyi ve içindeki tüm kıyafetleri silmek istediğinizden emin misiniz?");
        builder.setPositiveButton("Eminim, sil", (_d, _w) -> removeDrawer(id));
        builder.setNegativeButton("Hayır, silme", (dialog, _w) -> dialog.cancel());
        builder.show();
    }

    private void removeDrawer(int id) {
        SQLiteDatabase db = null;
        String message = null;
        try {
            db = context.openOrCreateDatabase("Database", MODE_PRIVATE, null);
            db.execSQL("DELETE FROM wears WHERE drawer_id = " + id);
            db.execSQL("DELETE FROM drawers WHERE id = " + id);
            message = "Çekmece başarıyla silindi!";
        } catch (Exception e) {
            message = "Bir hata oluştu!";
        } finally {
            if (db != null) db.close();
            Toast.makeText(context, message, LENGTH_SHORT).show();
            ((DrawersActivity) context).fetchDrawers();
        }
    }

    public class DrawerViewHolder extends ViewHolder {
        private TextView drawerNameTextView;
        private TextView wearCountTextView;
        private CardView itemCardView;
        private ImageView drawerItemEditIcon;
        private ImageView drawerItemRemoveIcon;

        public DrawerViewHolder(@NonNull View itemView) {
            super(itemView);
            drawerNameTextView = itemView.findViewById(R.id.drawerNameTextView);
            wearCountTextView = itemView.findViewById(R.id.wearCountTextView);
            itemCardView = itemView.findViewById(R.id.itemCardView);
            drawerItemEditIcon = itemView.findViewById(R.id.drawerItemEditIcon);
            drawerItemRemoveIcon = itemView.findViewById(R.id.drawerItemRemoveIcon);
        }
    }
}
