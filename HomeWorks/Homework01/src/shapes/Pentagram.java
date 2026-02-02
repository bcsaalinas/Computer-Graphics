package shapes;

public class Pentagram implements Shape{
    private final double side;

    public Pentagram(double side) {
        this.side = side;
    }

    @Override
    public double area() {
        return (Math.sqrt(5 * (5 - 2 * Math.sqrt(5))) / 2) * side * side;
    }

    @Override
    public double perimeter() {
        double phi = (1 + Math.sqrt(5)) / 2;
        double b = side / phi;
        return 10 * b;
    }
}
