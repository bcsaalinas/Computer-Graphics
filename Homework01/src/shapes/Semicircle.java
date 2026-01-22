package shapes;

public class Semicircle implements Shape {
private final double radius;
    public Semicircle(double radius) {
    this.radius = radius;

    }

    @Override
    public double area() {
        return 0.5 * Math.PI * radius * radius;
    }

    @Override
    public double perimeter() {
        return Math.PI * radius + 2 * radius;
    }
}
