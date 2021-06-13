package tr.edu.yildiz.sanaldolabim_18011063.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import java.util.ArrayList;

import tr.edu.yildiz.sanaldolabim_18011063.R;
import tr.edu.yildiz.sanaldolabim_18011063.activities.DrawersActivity;
import tr.edu.yildiz.sanaldolabim_18011063.activities.EditWearActivity;
import tr.edu.yildiz.sanaldolabim_18011063.activities.SingleDrawerActivity;
import tr.edu.yildiz.sanaldolabim_18011063.models.Wear;

import static android.content.Context.MODE_PRIVATE;
import static android.widget.Toast.LENGTH_SHORT;
import static tr.edu.yildiz.sanaldolabim_18011063.Utils.WearTypes.WEAR_TYPES;

public class WearRecyclerAdapter extends RecyclerView.Adapter<WearRecyclerAdapter.WearViewHolder> {
    private final ArrayList<Wear> wears;
    private final Context context;

    public WearRecyclerAdapter(ArrayList<Wear> wears, Context context) {
        this.wears = wears;
        this.context = context;
    }

    @NonNull
    @Override
    public WearViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.single_item_wear, parent, false);
        return new WearRecyclerAdapter.WearViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WearViewHolder holder, int position) {
        Wear wear = wears.get(position);
        holder.wearItemImageView.setImageBitmap(wear.getPhoto());
        holder.wearItemNameTextView.setText(wear.getName());
        holder.wearItemTypeTextView.setText(WEAR_TYPES[wear.getType()]);
        holder.wearItemColorPatternTextView.setText(wear.getColor() + " | " + wear.getPattern());
        holder.wearItemPriceTextView.setText(Float.toString(wear.getPrice()));
        holder.wearItemPurchaseDateTextView.setText(wear.getDatePurchased());
        holder.itemCardView.setOnClickListener(_v -> goToEditWear(wear.getId()));
        holder.wearItemRemoveIcon.setOnClickListener(_v -> showRemoveWearDialog(wear.getId()));
    }

    private void goToEditWear(int id) {
        Intent intent = new Intent(context, EditWearActivity.class);
        intent.putExtra("id", id);
        context.startActivity(intent);
    }

    private void removeWear(int id) {
        SQLiteDatabase db = null;
        String message = null;
        try {
            db = context.openOrCreateDatabase("Database", MODE_PRIVATE, null);
            db.execSQL("DELETE FROM wears WHERE id = " + id);
            message = "Kıyafet başarıyla silindi!";
        } catch (Exception e) {
            message = "Bir hata oluştu!";
        } finally {
            if (db != null) db.close();
            Toast.makeText(context, message, LENGTH_SHORT).show();
            ((SingleDrawerActivity) context).fetchWears();
        }
    }

    private void showRemoveWearDialog(int id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Kıyafet Siliniyor");
        builder.setMessage("Bu kıyafeti silmek istediğinizden emin misiniz?");
        builder.setPositiveButton("Eminim, sil", (_d, _w) -> removeWear(id));
        builder.setNegativeButton("Hayır, silme", (dialog, _w) -> dialog.cancel());
        builder.show();
    }

    @Override
    public int getItemCount() { return wears.size(); }

    public class WearViewHolder extends ViewHolder {
        private CardView itemCardView;
        private ImageView wearItemImageView;
        private ImageView wearItemRemoveIcon;
        private TextView wearItemNameTextView;
        private TextView wearItemTypeTextView;
        private TextView wearItemColorPatternTextView;
        private TextView wearItemPriceTextView;
        private TextView wearItemPurchaseDateTextView;

        public WearViewHolder(@NonNull View itemView) {
            super(itemView);
            itemCardView = itemView.findViewById(R.id.itemCardView);
            wearItemImageView = itemView.findViewById(R.id.wearItemImageView);
            wearItemRemoveIcon = itemView.findViewById(R.id.wearItemRemoveIcon);
            wearItemNameTextView = itemView.findViewById(R.id.wearItemNameTextView);
            wearItemTypeTextView = itemView.findViewById(R.id.wearItemTypeTextView);
            wearItemColorPatternTextView = itemView.findViewById(R.id.wearItemColorPatternTextView);
            wearItemPriceTextView = itemView.findViewById(R.id.wearItemPriceTextView);
            wearItemPurchaseDateTextView = itemView.findViewById(R.id.wearItemPurchaseDateTextView);
        }
    }
}
