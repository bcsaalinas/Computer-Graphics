package shapes;

public class Pentagon implements Shape{
private final double side;
    public Pentagon(double side) {
        this.side = side;

    }

    @Override
    public double area() {
        return (1.0 / 4.0) * Math.sqrt(25 + 10 * Math.sqrt(5)) * Math.pow(side, 2);
    }

    @Override
    public double perimeter() {
        return 5 * side;
    }
}
