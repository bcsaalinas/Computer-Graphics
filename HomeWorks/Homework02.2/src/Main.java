import java.util.Scanner;

public class Main {
    public static void main(String[] args){

        //get both dimensions and validate errors
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter width:");
        if(!scanner.hasNextInt()){
            throw new IllegalArgumentException("Invalid input");
        }
        int width = scanner.nextInt();
        System.out.println("Enter height:");
        if(!scanner.hasNextInt()){
            throw new IllegalArgumentException("Invalid input");
        }
        int height = scanner.nextInt();

        if(width <= 0 || height <= 0){
            throw new IllegalArgumentException("Invalid input");
        }

        //calculate the greatest common divisor
        int GCD = gcd(width, height);

        //divide each dimension by the gcd
        int newWidth = width / GCD;
        int newHeight = height / GCD;

        System.out.println("Aspect ratio: " + newWidth + ":" + newHeight);




    }

    //Euclidean algorithm to get gcd in recursive
    public static int gcd(int a, int b) {
        if (b == 0) return a;
        return gcd(b, a % b);
    }
}
