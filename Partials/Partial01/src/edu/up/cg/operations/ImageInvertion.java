package edu.up.cg.operations;

import edu.up.cg.model.ImageWrapper;

import java.awt.*;

//color inversion operation
public class ImageInvertion implements ImageOperation {




    @Override
    public ImageWrapper apply(ImageWrapper image) {

        int width = image.getImage().getWidth();
        int height = image.getImage().getHeight();

        for (int i = 0; i < width; i++) {

            for (int j = 0; j < height; j++) {

                int color = image.getImage().getRGB(i, j);
              //get the rgb values of the pixel and invert them by subtracting them from 255
                int red = 255 - ((color >> 16) & 0xFF);
                int green = 255 - ((color >> 8) & 0xFF);
                int blue = 255 - (color & 0xFF);

                //create a new color with the inverted rgb values and set the pixel to the new color
                Color invertedColor = new Color(red,green,blue);
                image.getImage().setRGB(i, j, invertedColor.getRGB());

            }

        }

        return image;


    }
}
