package edu.up.cg;

import edu.up.cg.io.ImageLoader;
import edu.up.cg.io.ImageSaver;

import java.awt.image.BufferedImage;
import java.io.File;

public class Main {


    public static void main(String[] args) {


    ImageLoader loader = new ImageLoader("test.jpeg");
    BufferedImage image = loader.loadImage();


    BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());

    //example of reconstructing an image read from disk
    for (int x = 0; x < image.getWidth(); x++) {
        for (int y = 0; y < image.getHeight(); y++) {

            int color = image.getRGB(x,y);
            newImage.setRGB(x,y,color);



        }
    }


    ImageSaver.saveImage(new File("test_output.jpg") , newImage, "jpg");





    }

}
