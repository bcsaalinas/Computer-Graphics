package edu.up.cg;

import edu.up.cg.ai.OpenAIClient;
import edu.up.cg.ai.OpenAIImage;
import edu.up.cg.ai.OpenAITTS;
import edu.up.cg.io.MediaCollection;
import edu.up.cg.map.MapGenerator;
import edu.up.cg.models.MediaItem;
import edu.up.cg.models.RawMetadata;

import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        try {
            MediaCollection collection = new MediaCollection();
            List<MediaItem> items = collection.loadFromFolder("test_media");

            System.out.println("Found " + items.size() + " media items");

            OpenAIClient client = new OpenAIClient();
            OpenAITTS tts = new OpenAITTS();
            OpenAIImage imageGen = new OpenAIImage();
            List<String> descriptions = new ArrayList<>();

            for (int i = 0; i < items.size(); i++) {
                MediaItem item = items.get(i);

                System.out.println("\n--- Item " + (i + 1) + " ---");
                System.out.println("File:    " + item.getPath());
                System.out.println("Video:   " + item.isVideo());
                System.out.println("Date:    " + item.getRawMetadata().getDate());
                System.out.println("Has GPS: " + item.getRawMetadata().hasGps());

                String description = client.generateMediaDescription(item);
                descriptions.add(description);
                System.out.println("Description: " + description);

                String audioPath = "audio_" + (i + 1) + ".wav";
                tts.generateAudio(description, audioPath);
                item.setAudioPath(audioPath);
                System.out.println("Audio saved to: " + audioPath);
            }

            String essencePrompt = client.generateEssencePrompt(descriptions);
            System.out.println("\nEssence prompt: " + essencePrompt);

            String openingImagePath = imageGen.generateImage(essencePrompt, "opening_image.png");
            System.out.println("Opening image saved to: " + openingImagePath);

            // find the first and last items that have GPS data
            MediaItem firstWithGps = null;
            MediaItem lastWithGps = null;
            for (MediaItem item : items) {
                if (item.getRawMetadata().hasGps()) {
                    if (firstWithGps == null) firstWithGps = item;
                    lastWithGps = item;
                }
            }

            if (firstWithGps != null && lastWithGps != null) {
                RawMetadata first = firstWithGps.getRawMetadata();
                RawMetadata last  = lastWithGps.getRawMetadata();

                String inspirationPhrase = client.generateInspirationPhrase(
                        first.getLatitude(), first.getLongitude(),
                        last.getLatitude(),  last.getLongitude()
                );
                System.out.println("\nInspiration phrase: " + inspirationPhrase);

                String mapPath = new MapGenerator().generateMap(
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
