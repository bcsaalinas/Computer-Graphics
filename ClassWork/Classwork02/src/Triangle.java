import java.awt.*;

public class Triangle {

    private Point2D pointA;
    private Point2D pointB;
    private Point2D pointC;

    //each point color
    private Color colorA;
    private Color colorB;
    private Color colorC;


    //denominator used for barycentric coordinates calculation
    private double denominator;


    public Triangle(Point2D pointA, Point2D pointB, Point2D pointC, Color colorA, Color colorB, Color colorC) {
        this.pointA = pointA;
        this.pointB = pointB;
        this.pointC = pointC;
        this.colorA = colorA;
        this.colorB = colorB;
        this.colorC = colorC;

        //only calculate the denominator once since it never changes
        this.denominator = getDenominator();
    }

    private double getDenominator(){
        int xA = pointA.getX();
        int yA = pointA.getY();
        int xB = pointB.getX();
        int yB = pointB.getY();
        int xC = pointC.getX();
        int yC = pointC.getY();

        denominator = (yB - yC) * (xA - xC) + (xC - xB) * (yA - yC);
        return denominator;
    }


    //calculate lamda1
    private double getLamda1(int x, int y){
        int xB = pointB.getX();
        int yB = pointB.getY();

        int xC = pointC.getX();
        int yC = pointC.getY();

        double numerator = (yB - yC) * (x - xC) + (xC - xB) * (y - yC);

        return numerator / denominator;
    }


    private double getLamda2(int x , int y){
        int xA = pointA.getX(), yA = pointA.getY();
        int xC = pointC.getX(), yC = pointC.getY();

        double numerator = (yC - yA) * (x - xC) + (xA - xC) * (y - yC);
        return numerator / denominator;
    }


    //check if a point is inside the triangle in a separate method
    public boolean contains(int x , int y ){
        double lambda1 = getLamda1(x, y);
        double lambda2 = getLamda2(x, y);
        double lambda3 = 1 - lambda1 - lambda2;

        //all lambdas should range 0-1
        return lambda1 >= 0 && lambda1 <= 1 &&
                lambda2 >= 0 && lambda2 <= 1 &&
                lambda3 >= 0 && lambda3 <= 1;
    }


    public Color getPointColor(int x, int y ){
        double lambda1 = getLamda1(x,y);
        double lambda2 = getLamda2(x,y);
        double lambda3 = 1 - lambda1 - lambda2;

        //blend red
        int red = (int)(lambda1 * colorA.getRed() +
                lambda2 * colorB.getRed() +
                lambda3 * colorC.getRed());

        //blend green
        int green = (int)(lambda1 * colorA.getGreen() +
                lambda2 * colorB.getGreen() +
                lambda3 * colorC.getGreen());

        //blend blue
        int blue = (int)(lambda1 * colorA.getBlue() +
                lambda2 * colorB.getBlue() +
                lambda3 * colorC.getBlue());

        return new Color(red, green, blue);
    }

}