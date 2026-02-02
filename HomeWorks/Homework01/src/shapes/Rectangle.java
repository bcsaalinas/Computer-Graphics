package shapes;

public class Rectangle implements Shape{
   private final double length;
   private final double width;

    public Rectangle(double length,double width){
        this.length = length;
        this.width = width;
    }

    @Override
    public double area() {
        return length * width;
    }

    @Override
    public double perimeter() {
        return 2 * (width + length);
    }
}
