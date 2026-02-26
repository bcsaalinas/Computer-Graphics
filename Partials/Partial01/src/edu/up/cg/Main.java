package edu.up.cg;

import edu.up.cg.io.ImageLoader;
import edu.up.cg.io.ImageSaver;
import edu.up.cg.model.ImageWrapper;
import edu.up.cg.model.Region;
import edu.up.cg.operations.ImageCrop;

import java.awt.image.BufferedImage;
import java.io.File;

public class Main {


    public static void main(String[] args) {


    ImageLoader loader = new ImageLoader("test.jpeg");
    BufferedImage image = loader.loadImage();




        ImageWrapper wrapper = new ImageWrapper(image);

        ImageCrop crop = new ImageCrop(new Region(900,700,1300,1700));
        wrapper = crop.apply(wrapper);
        image = wrapper.getImage();

    ImageSaver.saveImage(new File("test_output.jpg") , image, "jpg");




    }

}
