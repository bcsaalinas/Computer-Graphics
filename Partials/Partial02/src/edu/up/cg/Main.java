package edu.up.cg;

import edu.up.cg.ai.GeminiClient;
import edu.up.cg.ai.GeminiImage;
import edu.up.cg.ai.GeminiTTS;
import edu.up.cg.io.MediaCollection;
import edu.up.cg.map.MapGenerator;
import edu.up.cg.models.MediaItem;
import edu.up.cg.models.RawMetadata;

import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        try {
            // load and sort all media from the test folder
            MediaCollection collection = new MediaCollection();
            List<MediaItem> items = collection.loadFromFolder("test_media");

            System.out.println("Found " + items.size() + " media items");

            GeminiClient gemini = new GeminiClient();
            GeminiTTS tts = new GeminiTTS();
            GeminiImage imageGen = new GeminiImage();
            List<String> descriptions = new ArrayList<>();

            for (int i = 0; i < items.size(); i++) {
                MediaItem item = items.get(i);

                System.out.println("\n--- Item " + (i + 1) + " ---");
                System.out.println("File:    " + item.getPath());
                System.out.println("Video:   " + item.isVideo());
                System.out.println("Date:    " + item.getRawMetadata().getDate());
                System.out.println("Has GPS: " + item.getRawMetadata().hasGps());

                // generate spoken description for this item
                String description = gemini.generateMediaDescription(item);
                descriptions.add(description);
                System.out.println("Description: " + description);

                // convert the description to audio, save it, and store the path on the item
                String audioPath = "audio_" + (i + 1) + ".wav";
                tts.generateAudio(description, audioPath);
                item.setAudioPath(audioPath);
                System.out.println("Audio saved to: " + audioPath);
            }

            // generate the essence prompt and use it to create the opening image
            String essencePrompt = gemini.generateEssencePrompt(descriptions);
            System.out.println("\nEssence prompt: " + essencePrompt);

            String openingImagePath = imageGen.generateImage(essencePrompt, "opening_image.png");
            System.out.println("Opening image saved to: " + openingImagePath);

            // find the first and last items that actually have GPS data for the map
            MediaItem firstWithGps = null;
            MediaItem lastWithGps = null;
            for (MediaItem item : items) {
                if (item.getRawMetadata().hasGps()) {
                    if (firstWithGps == null) {
                        firstWithGps = item;
                    }
                    lastWithGps = item;
                }
            }

            if (firstWithGps != null && lastWithGps != null) {
                RawMetadata first = firstWithGps.getRawMetadata();
                RawMetadata last  = lastWithGps.getRawMetadata();

                String inspirationPhrase = gemini.generateInspirationPhrase(
                        first.getLatitude(), first.getLongitude(),
                        last.getLatitude(),  last.getLongitude()
                );
                System.out.println("\nInspiration phrase: " + inspirationPhrase);

                MapGenerator mapGenerator = new MapGenerator();
                String mapPath = mapGenerator.generateMap(
                        first.getLatitude(), first.getLongitude(),
                        last.getLatitude(),  last.getLongitude(),
                        "map.png"
                );
                System.out.println("Map saved to: " + mapPath);
            } else {
                System.out.println("\nNo GPS data found — skipping map and inspiration phrase.");
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
