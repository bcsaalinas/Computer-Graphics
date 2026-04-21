package edu.up.cg;

import edu.up.cg.ai.GeminiClient;
import edu.up.cg.io.MediaLoader;
import edu.up.cg.models.MediaItem;

import java.io.IOException;

public class Main {





    public static void main(String[] args) {
        try {
            MediaLoader loader = new MediaLoader();
            MediaItem item = loader.loadMedia("test.jpg");

            GeminiClient gemini = new GeminiClient();
            String description = gemini.generateMediaDescription(item);
            System.out.println("Description: " + description);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
