package edu.up.cg.image;

import java.awt.*;
import java.awt.image.BufferedImage;

//return a scaled image for the portrait, maintaning the original aspect ratio
public class ImageScaler {

    //target dimensions
    private static final int targetWidth = 1080;
    private static final int targetHeight = 1920;
    public BufferedImage scaleImage(BufferedImage original){

        int originalWidth = original.getWidth();
        int originalHeight = original.getHeight();

        double scaleX = targetWidth / (double) originalWidth;
        double scaleY = targetHeight / (double) originalHeight;

        double scale = Math.max(scaleX, scaleY);

        int newWidth = (int) (originalWidth * scale);
        int newHeight = (int) (originalHeight * scale);

        BufferedImage scaled = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);

        // draw the original image onto the scaled image
        Graphics2D g = scaled.createGraphics();
        g.drawImage(original, 0, 0, newWidth, newHeight, null);
        g.dispose();


        // crop the scaled image to the target dimensions
        int cropX = (newWidth - targetWidth) / 2;
        int cropY = (newHeight - targetHeight) / 2;
        return scaled.getSubimage(cropX, cropY, targetWidth, targetHeight);



    }
    




}
