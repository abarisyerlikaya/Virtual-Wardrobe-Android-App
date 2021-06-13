package tr.edu.yildiz.sanaldolabim_18011063;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import static android.R.style.Theme_Holo_Light_Dialog_MinWidth;
import static android.graphics.Bitmap.CompressFormat.PNG;
import static android.widget.Toast.LENGTH_LONG;
import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;

public class Utils {
    public static final double INVALID_COORDINATE = 91;
    public static final String MIME_TYPE_IMAGE = "image/*";
    private static final String DATE_REGEX = "^\\d{4}-\\d{1,2}-\\d{1,2}$";

    public static void pickDate(OnDateSetListener listener, Context context) {
        Calendar calendar = Calendar.getInstance();
        int y = calendar.get(YEAR);
        int m = calendar.get(MONTH);
        int d = calendar.get(DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(context, Theme_Holo_Light_Dialog_MinWidth, listener, y, m, d);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();
    }

    public static String getAddressFromCoordinates(double lat, double lng, Context context) {
        String addressStr = null;
        try {
            Geocoder myLocation = new Geocoder(context, Locale.getDefault());
            List<Address> myList = myLocation.getFromLocation(lat, lng, 1);
            Address address = (Address) myList.get(0);
            addressStr = address.getAddressLine(0);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Bir hata oluştu!", Toast.LENGTH_SHORT).show();
        } finally { return addressStr; }
    }

    public static Bitmap getBitmapFromUri(Uri uri, Context context) {
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
        } catch (IOException e) {
            Toast.makeText(context, "Bir hata oluştu!", LENGTH_LONG).show();
        }
        return bitmap;
    }

    public static Uri convertBitmapToUri(Context context, Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String title = "IMG_" + new Date().getTime();
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, title, null);
        return Uri.parse(path);
    }

    public static Bitmap convertBytesToBitmap(byte[] bytes) {
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    public static byte[] convertBitmapToBytes(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(PNG, 0, outputStream);
        return outputStream.toByteArray();
    }

    public static boolean isDateStringValid(String date) {
        return Pattern.compile(DATE_REGEX).matcher(date).matches();
    }

    public static class WearTypes {
        public static final String[] WEAR_TYPES = new String[]{"Şapka", "Yüz aksesuarı", "Üst", "Ceket", "El-kol aksesuarı", "Alt", "Ayakkabı"};
        public static final int HAT = 0;
        public static final int FACE_ACCESSORY = 1;
        public static final int TOP = 2;
        public static final int JACKET = 3;
        public static final int HAND_ARM_ACCESSORY = 4;
        public static final int BOTTOMS = 5;
        public static final int SHOES = 6;
    }

    public static class SQLCodes {
        public static final String CREATE_DRAWERS_TABLE = "CREATE TABLE IF NOT EXISTS drawers (id INTEGER PRIMARY KEY AUTOINCREMENT, name VARCHAR);";
        public static final String CREATE_WEARS_TABLE = "CREATE TABLE IF NOT EXISTS wears (id INTEGER PRIMARY KEY AUTOINCREMENT, name VARCHAR, color VARCHAR, pattern VARCHAR, type INTEGER, price FLOAT, date_purchased DATE, photo BLOB, drawer_id INTEGER, FOREIGN KEY (drawer_id) REFERENCES drawers(id));";
        public static final String CREATE_OUTFITS_TABLE = "CREATE TABLE IF NOT EXISTS outfits (id INTEGER PRIMARY KEY AUTOINCREMENT, name VARCHAR, hat INTEGER, face_accessory INTEGER, top INTEGER, jacket INTEGER, hand_arm_accessory INTEGER, bottoms INTEGER, shoes INTEGER, FOREIGN KEY(hat) REFERENCES wears(id), FOREIGN KEY(face_accessory) REFERENCES wears(id), FOREIGN KEY(top) REFERENCES wears(id), FOREIGN KEY(jacket) REFERENCES wears(id), FOREIGN KEY(hand_arm_accessory) REFERENCES wears(id), FOREIGN KEY(bottoms) REFERENCES wears(id), FOREIGN KEY(shoes) REFERENCES wears(id));";
        public static final String CREATE_EVENTS_TABLE = "CREATE TABLE IF NOT EXISTS events (id INTEGER PRIMARY KEY AUTOINCREMENT, name VARCHAR, type VARCHAR, date DATE, location VARCHAR, outfit INTEGER, FOREIGN KEY(outfit) REFERENCES outfits(id));";
    }
}