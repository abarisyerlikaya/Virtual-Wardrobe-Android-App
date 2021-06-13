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
import androidx.recyclerview.widget.RecyclerView.Adapter;

import java.util.ArrayList;

import tr.edu.yildiz.sanaldolabim_18011063.R;
import tr.edu.yildiz.sanaldolabim_18011063.activities.CabinetRoomActivity;
import tr.edu.yildiz.sanaldolabim_18011063.activities.DrawersActivity;
import tr.edu.yildiz.sanaldolabim_18011063.activities.EditOutfitActivity;
import tr.edu.yildiz.sanaldolabim_18011063.models.Outfit;

import static android.content.Context.MODE_PRIVATE;
import static android.widget.Toast.LENGTH_SHORT;

public class OutfitRecyclerAdapter extends Adapter<OutfitRecyclerAdapter.OutfitViewHolder> {
    private final ArrayList<Outfit> outfits;
    private final Context context;

    public OutfitRecyclerAdapter(ArrayList<Outfit> outfits, Context context) {
        this.outfits = outfits;
        this.context = context;
    }

    @NonNull
    @Override
    public OutfitViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.single_item_outfit, parent, false);
        return new OutfitRecyclerAdapter.OutfitViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OutfitRecyclerAdapter.OutfitViewHolder holder, int position) {
        Outfit outfit = outfits.get(position);
        String name = outfit.getName();
        int id = outfit.getId();

        holder.outfitNameTextView.setText(name);
        holder.outfitItemRemoveIcon.setOnClickListener(v -> showRemoveOutfitDialog(id));
        holder.itemCardView.setOnClickListener(v -> goToEditOutfit(id));
    }

    private void goToEditOutfit(int id) {
        Intent intent = new Intent(context, EditOutfitActivity.class);
        intent.putExtra("id", id);
        context.startActivity(intent);
    }

    private void showRemoveOutfitDialog(int id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Kombin Siliniyor");
        builder.setMessage("Bu kombini silmek istediğinizden emin misiniz?");
        builder.setPositiveButton("Eminim, sil", (_d, _w) -> removeOutfit(id));
        builder.setNegativeButton("Hayır, silme", (dialog, _w) -> dialog.cancel());
        builder.show();
    }

    private void removeOutfit(int id) {
        SQLiteDatabase db = null;
        String message = null;
        try {
            db = context.openOrCreateDatabase("Database", MODE_PRIVATE, null);
            db.execSQL("DELETE FROM outfits WHERE id = " + id);
            message = "Kombin başarıyla silindi!";
        } catch (Exception e) {
            message = "Bir hata oluştu!";
        } finally {
            if (db != null) db.close();
            Toast.makeText(context, message, LENGTH_SHORT).show();
            ((CabinetRoomActivity) context).fetchOutfits();
        }
    }

    @Override
    public int getItemCount() { return outfits.size(); }

    public class OutfitViewHolder extends RecyclerView.ViewHolder {
        private CardView itemCardView;
        private TextView outfitNameTextView;
        private ImageView outfitItemRemoveIcon;

        public OutfitViewHolder(@NonNull View itemView) {
            super(itemView);
            itemCardView = itemView.findViewById(R.id.itemCardView);
            outfitNameTextView = itemView.findViewById(R.id.outfitNameTextView);
            outfitItemRemoveIcon = itemView.findViewById(R.id.outfitItemRemoveIcon);
        }
    }
}
