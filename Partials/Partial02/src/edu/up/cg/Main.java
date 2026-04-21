package edu.up.cg;

import edu.up.cg.ai.GeminiClient;
import edu.up.cg.ai.GeminiTTS;
import edu.up.cg.io.MediaCollection;
import edu.up.cg.models.MediaItem;

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

            // generate the essence prompt that will be used for the opening image
            String essencePrompt = gemini.generateEssencePrompt(descriptions);
            System.out.println("\nEssence prompt: " + essencePrompt);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
