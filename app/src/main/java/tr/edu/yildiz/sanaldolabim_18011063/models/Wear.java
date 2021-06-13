package tr.edu.yildiz.sanaldolabim_18011063.models;

import android.graphics.Bitmap;

public class Wear {
    private int id;
    private String name;
    private String color;
    private String pattern;
    private int type;
    private float price;
    private String datePurchased;
    private Bitmap photo;

    public Wear(int id, String name, String color, String pattern, int type, float price, String datePurchased, Bitmap photo) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.pattern = pattern;
        this.type = type;
        this.price = price;
        this.datePurchased = datePurchased;
        this.photo = photo;
    }

    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getColor() { return color; }

    public void setColor(String color) { this.color = color; }

    public String getPattern() { return pattern; }

    public void setPattern(String pattern) { this.pattern = pattern; }

    public int getType() { return type; }

    public void setType(int type) { this.type = type; }

    public float getPrice() { return price; }

    public void setPrice(float price) { this.price = price; }

    public String getDatePurchased() { return datePurchased; }

    public void setDatePurchased(String datePurchased) { this.datePurchased = datePurchased; }

    public Bitmap getPhoto() { return photo; }

    public void setPhoto(Bitmap photo) { this.photo = photo; }
}
