package edu.up.cg;

import edu.up.cg.io.ImageLoader;
import edu.up.cg.io.ImageSaver;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class Main {


    public static void main(String[] args) {

        BufferedImage image = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);

        for (int i = 0; i < 100 ; i++) {

            for (int j = 0; j < 100 ; j++) {

                image.setRGB(i, j, Color.RED.getRGB());

            }

        }

        File file = new File("test_output.png");

        ImageSaver.saveImage(file, image, "png");

        ImageLoader loader = new ImageLoader("test_output.png");

         BufferedImage loadedImage = loader.loadImage();


         if(loadedImage != null) System.out.println("Loaded image dimensions: " + loadedImage.getWidth() + "x" + loadedImage.getHeight());








    }

}
