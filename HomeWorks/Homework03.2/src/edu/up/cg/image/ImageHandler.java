package edu.up.cg.image;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageHandler {

    //load image
    public BufferedImage load(String path) throws IOException{
        BufferedImage image = ImageIO.read(new File(path));
        if(image == null){
            throw new IOException("Failed to load image from path: " + path);
        }
        return image;
    }



    //save the image properly with its format
    public void save(BufferedImage image,  String path) throws IOException{
        String format = path.substring(path.lastIndexOf('.') + 1);
        ImageIO.write(image, format, new File(path));
    }

    //return the file size in bytes
    public long fileSize(String path){
        return new File(path).length();
    }


}
