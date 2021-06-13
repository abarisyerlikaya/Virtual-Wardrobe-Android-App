package tr.edu.yildiz.sanaldolabim_18011063.models;

public class Drawer {
    private int id;
    private String name;
    private int wearCount;

    public Drawer(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() { return id; }

    public String getName() { return name; }

    public void setWearCount(int wearCount) { this.wearCount = wearCount; }

    public int getWearCount() { return wearCount; }
}
