import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        // main menu
        System.out.println("Choose:");
        System.out.println("1) Cartesian to Polar");
        System.out.println("2) Polar to Cartesian");
        System.out.print("Choice: ");

        // validate menu input
        if (!scanner.hasNextInt()) {
            System.out.println("Invalid input. Choice must be a number.");
            return;
        }

        int choice = scanner.nextInt();

        switch (choice) {

            case 1:
                // cartesian to polar
                System.out.println("Cartesian to Polar");

                // read x
                System.out.print("Enter coordinate x: ");
                if (!scanner.hasNextDouble()) {
                    System.out.println("Invalid input. x must be a number.");
                    return;
                }
                double x = scanner.nextDouble();

                // read y
                System.out.print("Enter coordinate y: ");
                if (!scanner.hasNextDouble()) {
                    System.out.println("Invalid input. y must be a number.");
                    return;
                }
                double y = scanner.nextDouble();

                // convert and print result
                double[] polarCoordinates = CoordinateConverter.cartesianToPolar(x, y);
                System.out.println("r = " + polarCoordinates[0]);
                System.out.println("theta = " + polarCoordinates[1]);
                break;

            case 2:
                // polar to cartesian
                System.out.println("Polar to Cartesian");

                // read r
                System.out.print("Enter coordinate r: ");
                if (!scanner.hasNextDouble()) {
                    System.out.println("Invalid input. r must be a number.");
                    return;
                }
                double r = scanner.nextDouble();

                // validate r
                if (r < 0) {
                    System.out.println("Invalid input. r must be >= 0.");
                    return;
                }

                // choose angle units
                System.out.println("Select angle unit:");
                System.out.println("1) Degrees");
                System.out.println("2) Radians");
                System.out.print("Choice: ");

                if (!scanner.hasNextInt()) {
                    System.out.println("Invalid input. Choose 1 or 2.");
                    return;
                }
                int units = scanner.nextInt();

                if (units != 1 && units != 2) {
                    System.out.println("Invalid choice. Choose 1 or 2.");
                    return;
                }

                // read theta
                System.out.print("Enter coordinate theta: ");
                if (!scanner.hasNextDouble()) {
                    System.out.println("Invalid input. theta must be a number.");
                    return;
                }
                double theta = scanner.nextDouble();

                // convert degrees to radians if needed
                if (units == 1) {
                    theta = Math.toRadians(theta);
                }

                // convert and print result
                double[] cartesianCoordinates =
                        CoordinateConverter.polarToCartesian(r, theta);

                System.out.println("x = " + cartesianCoordinates[0]);
                System.out.println("y = " + cartesianCoordinates[1]);
                break;

            default:
                // invalid menu option
                System.out.println("Invalid option. Choose 1 or 2.");
        }
    }
}
