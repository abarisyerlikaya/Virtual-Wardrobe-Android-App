package tr.edu.yildiz.sanaldolabim_18011063.models;

public class Event {
    private int id;
    private String name;
    private String type;
    private String date;
    private String location;
    private int outfitId;

    public Event(int id, String name, String type, String date, String location, int outfitId) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.date = date;
        this.location = location;
        this.outfitId = outfitId;
    }

    public int getId() { return id; }

    public String getName() { return name; }

    public String getType() { return type; }

    public String getDate() { return date; }

    public String getLocation() { return location; }

    public int getOutfitId() { return outfitId; }
}
