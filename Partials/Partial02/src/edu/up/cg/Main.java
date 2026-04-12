package edu.up.cg;

import edu.up.cg.metadata.MetadataReader;
import edu.up.cg.models.MediaItem;
import edu.up.cg.models.RawMetadata;

import java.io.IOException;

public class Main {







    public static void main(String[] args) {
        try{
            MediaItem mi = new MediaItem("test.heic");


        }
        catch (IOException e) {
            e.printStackTrace();
        }




    }

}
