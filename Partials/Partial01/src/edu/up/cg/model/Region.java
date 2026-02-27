package edu.up.cg.model;


//class to represent a region of an image
public class Region {

private final int x1 ;
private final int y1;
private final int x2 ;
private final int y2 ;

//catch error if the coordinates are not valid
    public Region(int x1, int y1, int x2, int y2) {
        if (x1 < 0 || y1 < 0 || x2 < 0 || y2 < 0 || x1 >= x2 || y1 >= y2) {
            throw new IllegalArgumentException("Invalid coordinates for region");
        }
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }


    public int getX1() {
        return x1;
    }

    public int getY1() {
        return y1;
    }

    public int getX2() {
        return x2;
    }

    public int getY2() {
        return y2;
    }
}
