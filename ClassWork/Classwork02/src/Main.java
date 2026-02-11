import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Main{
    public static void main(String[] args) {

        int width = 400;
        int height = 400;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        Point2D pointA = new Point2D(200, 50);    // top (with margin)
        Point2D pointB = new Point2D(350, 350);   // bottom-right (with margin)
        Point2D pointC = new Point2D(50, 350);

        Color colorA = Color.RED;
        Color colorB = Color.GREEN;
        Color colorC = Color.BLUE;

        Triangle triangle = new Triangle(pointA, pointB, pointC, colorA, colorB, colorC);

        drawTriangle(image, triangle);
        saveImage(image, "gradient-triangle.png");

    }

    //draw a triangle on image
    public static void drawTriangle(BufferedImage image,  Triangle triangle){
        for (int i = 0; i < image.getWidth(); i++) {
            for (int j = 0; j < image.getHeight(); j++) {
                if(triangle.contains(i,j)){
                    Color color = triangle.getPointColor(i,j);
                    image.setRGB(i, j, color.getRGB());
                }
            }

        }
    }

    public static void saveImage(BufferedImage image, String filename){
        try {
            ImageIO.write(image, "png", new File(filename));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}