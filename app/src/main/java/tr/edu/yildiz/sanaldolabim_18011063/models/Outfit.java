package tr.edu.yildiz.sanaldolabim_18011063.models;

public class Outfit {
    int id;
    String name;
    int hatId;
    int faceAccessoryId;
    int topId;
    int jacketId;
    int handArmAccessoryId;
    int bottomsId;
    int shoesId;

    public Outfit(int id, String name, int hatId, int faceAccessoryId, int topId, int jacketId, int handArmAccessoryId, int bottomsId, int shoesId) {
        this.id = id;
        this.name = name;
        this.hatId = hatId;
        this.faceAccessoryId = faceAccessoryId;
        this.topId = topId;
        this.jacketId = jacketId;
        this.handArmAccessoryId = handArmAccessoryId;
        this.bottomsId = bottomsId;
        this.shoesId = shoesId;
    }

    public int getId() { return id; }

    public String getName() { return name; }

    public int getHatId() { return hatId; }

    public int getFaceAccessoryId() { return faceAccessoryId; }

    public int getTopId() { return topId; }

    public int getJacketId() { return jacketId; }

    public int getHandArmAccessoryId() { return handArmAccessoryId; }

    public int getBottomsId() { return bottomsId; }

    public int getShoesId() { return shoesId; }
}
