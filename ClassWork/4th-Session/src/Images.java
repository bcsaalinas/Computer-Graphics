import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Images {
    static void ImageOne(){
        BufferedImage image = new BufferedImage(400, 300, BufferedImage.TYPE_INT_RGB);
        for(int i = 0; i < 400; i++){
            for(int j = 0; j < 300; j++){
                if(4*j > 3 * i){
                    image.setRGB(i, j, Color.RED.getRGB());
                } else{
                    image.setRGB(i, j, Color.BLUE.getRGB());
                }
            }
        }

        File outputImage = new File("ClassWork01.png");
        try{
            ImageIO.write(image, "png", outputImage);
        } catch(IOException e ){
            throw new RuntimeException(e);
        }

    }


    static void ImageTwo(){
       //draw circumference
        int width = 800;
        int height = 600;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        int centerX = width / 2;
        int centerY = height / 2;
        int radius = 250;
        double distanceSquared = Math.pow()
    }
}
