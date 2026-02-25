package edu.up.cg.io;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageSaver {



    public static void saveImage(File file, BufferedImage image, String format) {

        try{
            ImageIO.write(image, format, file);
        } catch (IOException e) {
            System.out.println("error saving image: " + e.getMessage());
        }



    }

}
