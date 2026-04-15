package edu.up.cg;

import edu.up.cg.io.MediaLoader;
import edu.up.cg.metadata.MetadataReader;
import edu.up.cg.models.MediaItem;
import edu.up.cg.models.RawMetadata;

import javax.imageio.ImageIO;
import java.io.IOException;

public class Main {







    public static void main(String[] args) {
        try{
            MediaLoader loader = new MediaLoader();
            MediaItem item = loader.loadMedia("test.jpeg");

            System.out.println("Width: " + item.getWidth());
            System.out.println("Height: " + item.getHeight());
            System.out.println("Filetype: " + item.getFiletype());
            System.out.println("Date: " + item.getRawMetadata().getDate());
            System.out.println("Latitude: " + item.getRawMetadata().getLatitude());
            System.out.println("Longitude: " + item.getRawMetadata().getLongitude());
            System.out.println("Orientation: " + item.getRawMetadata().getOrientation());


            if (item.getWidth() == 1080 && item.getHeight() == 1920){
                System.out.println("scaled correctly");
            } else {
                System.out.println("scaling failed");
            }

            ImageIO.write(item.getImage(), "jpeg", new java.io.File("scaled_test.jpeg"));
            System.out.println("scaled image saved as scaled_test.jpeg");

        }
        catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }

}
