package edu.neu.csye6200;

/**
 * @author Zongduo Li
 */
public class Boat {
    private static int idCounter = 0;
    private int boatId;
    private String boatName;
    private int boatPositionX;
    private int boatPositionY;
    private double boatDirection;
    private int boatSpeed;
    private ABRule abRule;

    public Boat(){
        boatId = idCounter++;
    }
    public Boat(String boatName, int boatPositionX, int boatPositionY, double boatDirection, int boatSpeed){
        boatId = idCounter++;
        this.boatName = boatName;
        this.boatPositionX = boatPositionX;
        this.boatPositionY = boatPositionY;
        this.boatDirection = boatDirection;
        this.boatSpeed = boatSpeed;
    }
    public Boat(String boatName){
        abRule = new ABRule();
        boatId = idCounter++;
        this.boatName = boatName;
        this.boatPositionX = abRule.x;
        this.boatPositionY = abRule.y;
        this.boatDirection = abRule.degree;
        this.boatSpeed = abRule.speed;
    }

    public static int getIdCounter() {
        return idCounter;
    }

    public static void setIdCounter(int idCounter) {
        Boat.idCounter = idCounter;
    }

    public int getBoatId() {
        return boatId;
    }

    public void setBoatId(int boatId) {
        this.boatId = boatId;
    }

    public String getBoatName() {
        return boatName;
    }

    public void setBoatName(String boatName) {
        this.boatName = boatName;
    }

    public int getBoatPositionX() {
        return boatPositionX;
    }

    public void setBoatPositionX(int boatPositionX) {
        this.boatPositionX = boatPositionX;
    }

    public int getBoatPositionY() {
        return boatPositionY;
    }

    public void setBoatPositionY(int boatPositionY) {
        this.boatPositionY = boatPositionY;
    }

    public double getBoatDirection() {
        return boatDirection;
    }

    public void setBoatDirection(double boatDirection) {
        this.boatDirection = boatDirection;
    }

    public int getBoatSpeed() {
        return boatSpeed;
    }

    public void setBoatSpeed(int boatSpeed) {
        this.boatSpeed = boatSpeed;
    }

    public ABRule getAbRule() {
        return abRule;
    }

    public void setAbRule(ABRule abRule) {
        this.abRule = abRule;
    }
}
