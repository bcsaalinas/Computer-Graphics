package up.edu.cg.images;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Main {

    public static void main(String[] args){
        BufferedImage image = new BufferedImage(400,300, BufferedImage.TYPE_INT_RGB);

        //draw two triangles
        for (int i = 0; i < 400; i++){
            for(int j = 0; j < 300; j++){
                //follow the 4:3 aspect ratio
                if(4 * j > 3 * i){
                    //if the pixel is above the diagonal, then draw it red
                    image.setRGB(i, j, Color.RED.getRGB());
                } else{
                    //else draw it blue
                    image.setRGB(i, j, Color.BLUE.getRGB());
                }
            }
        }

        File outputImage = new File("image.jpg");
        try{
            ImageIO.write(image, "jpg", outputImage);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
