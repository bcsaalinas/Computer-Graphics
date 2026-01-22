import javax.swing.JOptionPane;
import shapes.*;

public class Main {

    public static void main(String[] args) {
        //list of all options
    String[] options = {
            "Square",
            "Rectangle",
            "Triangle",
            "Circle",
            "Pentagon",
            "Pentagram",
            "Pentagram",
            "Semicircle"

    };


    //initial message
    Object choice = JOptionPane.showInputDialog(
            null, //no parent component
            "Choose a shape",
            "Calculator",
            JOptionPane.QUESTION_MESSAGE,
            null, //no icon
            options,
            options[0]
    );

    String selectedShape = choice.toString();
    //convert it to string bc it returns an object

   Shape shape = buildShape(selectedShape);    //build shape outside

        //display generated shape area and perimeter
        JOptionPane.showMessageDialog(
                null,
                "Perimeter=" + shape.perimeter() +
                        "\nArea=" + shape.area(),
                "Result",
                JOptionPane.INFORMATION_MESSAGE
        );
    }
    //helper to convert to a double what user types
    static double ask(String label) {
        String text = JOptionPane.showInputDialog(label + ":");
        return Double.parseDouble(text);
    }


    static Shape buildShape(String selectedShape) {

        switch (selectedShape) {

            case "Square" -> {
                double s = ask("Side");
                return new Square(s);
            }

            case "Rectangle" -> {
                double w = ask("Width");
                double h = ask("Height");
                return new Rectangle(w, h);
            }

            case "Triangle" -> {
                double a = ask("Side A");
                double b = ask("Side B");
                double c = ask("Side C");
                return new Triangle(a, b, c);
            }

            case "Circle" -> {
                double r = ask("Radius");
                return new Circle(r);
            }

            case "Pentagon" -> {
                double s = ask("Side");
                return new Pentagon(s);
            }

            case "Pentagram" -> {
                double s = ask("Side");
                return new Pentagram(s);
            }

            case "Semicircle" -> {
                double r = ask("Radius");
                return new Semicircle(r);
            }
        }

        return null; // wont happen if user always chooses from dropdown
    }
}