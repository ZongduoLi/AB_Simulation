package edu.neu.csye6200;

public class ABRule {

    public int x = 30;
    public int y = 5;

    public int speed = 1;
    public double degree = 3.14/4;

    //Hit the upper or lower boundary
    public void changeDirectionX(){
        degree = -degree;
    }

    //Hit the left or right boundary
    public void changeDirectionY(){
        degree = 3.14-degree;
    }

    public ABRule(){
    }
}
