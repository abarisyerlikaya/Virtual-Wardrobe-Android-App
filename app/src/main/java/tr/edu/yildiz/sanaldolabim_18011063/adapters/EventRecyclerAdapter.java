package tr.edu.yildiz.sanaldolabim_18011063.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import tr.edu.yildiz.sanaldolabim_18011063.R;
import tr.edu.yildiz.sanaldolabim_18011063.activities.DrawersActivity;
import tr.edu.yildiz.sanaldolabim_18011063.activities.EditEventActivity;
import tr.edu.yildiz.sanaldolabim_18011063.activities.EventsActivity;
import tr.edu.yildiz.sanaldolabim_18011063.models.Event;

import static android.content.Context.MODE_PRIVATE;
import static android.widget.Toast.LENGTH_SHORT;

public class EventRecyclerAdapter extends RecyclerView.Adapter<EventRecyclerAdapter.EventViewHolder> {
    private final ArrayList<Event> events;
    private final Context context;

    public EventRecyclerAdapter(ArrayList<Event> events, Context context) {
        this.events = events;
        this.context = context;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.single_item_event, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = events.get(position);
        int outfitId = event.getOutfitId();
        String outfitName = findOutfitNameFromDb(outfitId);

        holder.eventNameTextView.setText(event.getName());
        holder.eventTypeTextView.setText(event.getType());
        holder.eventDateTextView.setText(event.getDate());
        holder.eventOutfitTextView.setText(outfitName);
        holder.eventLocationTextView.setText(event.getLocation());
        holder.itemCardView.setOnClickListener(_v -> goToEditEvent(event.getId()));
        holder.eventItemRemoveIcon.setOnClickListener(_v -> showRemoveEventDialog(event.getId()));
    }

    private void goToEditEvent(int id) {
        Intent intent = new Intent(context, EditEventActivity.class);
        intent.putExtra("id", id);
        context.startActivity(intent);
    }

    private void showRemoveEventDialog(int id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Etkinlik Siliniyor");
        builder.setMessage("Etkinliği silmek istediğinizden emin misiniz?");
        builder.setPositiveButton("Eminim, sil", (_d, _w) -> removeEvent(id));
        builder.setNegativeButton("Hayır, silme", (dialog, _w) -> dialog.cancel());
        builder.show();
    }

    private void removeEvent(int id) {
        SQLiteDatabase db = null;
        String message = null;
        try {
            db = context.openOrCreateDatabase("Database", MODE_PRIVATE, null);
            db.execSQL("DELETE FROM events WHERE id = " + id);
            message = "Etkinlik başarıyla silindi!";
        } catch (Exception e) {
            message = "Bir hata oluştu!";
        } finally {
            if (db != null) db.close();
            Toast.makeText(context, message, LENGTH_SHORT).show();
            ((EventsActivity) context).fetchEvents();
        }
    }

    private String findOutfitNameFromDb(int id) {
        String result = null;
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = context.openOrCreateDatabase("Database", MODE_PRIVATE, null);
            cursor = db.rawQuery("SELECT * FROM outfits WHERE id = " + id, null);
            cursor.moveToFirst();
            result = cursor.getString(cursor.getColumnIndex("name"));
        } catch (Exception e) {
            Toast.makeText(context, "Bir hata oluştu!", LENGTH_SHORT);
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();
            return result;
        }
    }

    @Override
    public int getItemCount() { return events.size(); }


    public class EventViewHolder extends RecyclerView.ViewHolder {
        private CardView itemCardView;
        private TextView eventNameTextView;
        private TextView eventTypeTextView;
        private TextView eventDateTextView;
        private TextView eventOutfitTextView;
        private TextView eventLocationTextView;
        private ImageView eventItemRemoveIcon;


        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            itemCardView = itemView.findViewById(R.id.itemCardView);
            eventNameTextView = itemView.findViewById(R.id.eventNameTextView);
            eventTypeTextView = itemView.findViewById(R.id.eventTypeTextView);
            eventDateTextView = itemView.findViewById(R.id.eventDateTextView);
            eventOutfitTextView = itemView.findViewById(R.id.eventOutfitTextView);
            eventLocationTextView = itemView.findViewById(R.id.eventLocationTextView);
            eventItemRemoveIcon = itemView.findViewById(R.id.eventItemRemoveIcon);
        }
    }
}

