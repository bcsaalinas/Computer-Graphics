package edu.up.cg.pipeline;

import edu.up.cg.ai.OpenAIClient;
import edu.up.cg.ai.OpenAIImage;
import edu.up.cg.ai.OpenAITTS;
import edu.up.cg.image.ImageScaler;
import edu.up.cg.io.MediaCollection;
import edu.up.cg.map.MapGenerator;
import edu.up.cg.models.MediaItem;
import edu.up.cg.models.RawMetadata;
import edu.up.cg.video.VideoAssembler;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

// orchestrates the full media-to-video pipeline and reports progress through a listener
public class VideoPipeline {

    // callback invoked at each major step so the caller can update a progress bar or log
    public interface ProgressListener {
        void onProgress(int percent, String message);
    }

    // runs the full pipeline on the given files and returns the path of the final video
    // all generated assets (output video, opening image, map) are written into outputFolder
    public String run(List<String> filePaths, String outputFolder, ProgressListener listener) throws Exception {
        listener.onProgress(0, "Loading media files...");

        MediaCollection collection = new MediaCollection();
        List<MediaItem> items = collection.loadFromFiles(filePaths);

        if (items.isEmpty()) {
            throw new RuntimeException("No valid media files provided");
        }

        // all intermediate files go here and are deleted after the final video is assembled
        String tempFolder = outputFolder + "/temp";
        new File(tempFolder).mkdirs();

        OpenAIClient client = new OpenAIClient();
        OpenAITTS tts = new OpenAITTS();
        OpenAIImage imageGen = new OpenAIImage();
        VideoAssembler assembler = new VideoAssembler();
        List<String> descriptions = new ArrayList<>();
        List<String> clipPaths = new ArrayList<>();

        int totalItems = items.size();

        // --- step 1: describe each item and generate its narration audio (5-40%) ---
        for (int i = 0; i < totalItems; i++) {
            MediaItem item = items.get(i);
            int percent = 5 + (int) (35.0 * i / totalItems);
            listener.onProgress(percent, "Describing item " + (i + 1) + " of " + totalItems);

            String description = client.generateMediaDescription(item);
            descriptions.add(description);

            String audioPath = tempFolder + "/audio_" + (i + 1) + ".wav";
            tts.generateAudio(description, audioPath);
            item.setAudioPath(audioPath);
        }

        // --- step 2: generate opening image and its narration (40-55%) ---
        listener.onProgress(40, "Creating opening image...");
        String essencePrompt = client.generateEssencePrompt(descriptions);
        String openingImagePath = outputFolder + "/opening_image.png";
        imageGen.generateImage(essencePrompt, openingImagePath);

        // DALL-E returns 1024x1792 — scale to 1080x1920 so this clip matches all others
        BufferedImage openingRaw = ImageIO.read(new File(openingImagePath));
        BufferedImage openingScaled = new ImageScaler().scaleImage(openingRaw);
        ImageIO.write(openingScaled, "png", new File(openingImagePath));

        listener.onProgress(48, "Narrating opening image...");
        String openingAudioPath = tempFolder + "/audio_0.wav";
        tts.generateAudio(essencePrompt, openingAudioPath);

        String openingClipPath = tempFolder + "/clip_0.mp4";
        assembler.imageToClip(openingImagePath, openingAudioPath, openingClipPath);
        clipPaths.add(openingClipPath);

        // --- step 3: turn each media item into a clip (55-80%) ---
        for (int i = 0; i < totalItems; i++) {
            MediaItem item = items.get(i);
            int percent = 55 + (int) (25.0 * i / totalItems);
            listener.onProgress(percent, "Creating clip " + (i + 1) + " of " + totalItems);

            String clipPath = tempFolder + "/clip_" + (i + 1) + ".mp4";
            if (item.isVideo()) {
                assembler.videoToClip(item.getPath(), item.getAudioPath(), clipPath);
            } else {
                assembler.imageToClip(item.getPath(), item.getAudioPath(), clipPath);
            }
            clipPaths.add(clipPath);
        }

        // --- step 4: generate map and its inspiration phrase audio (80-95%) ---
        MediaItem firstWithGps = null;
        MediaItem lastWithGps = null;
        for (MediaItem item : items) {
            if (item.getRawMetadata().hasGps()) {
                if (firstWithGps == null) firstWithGps = item;
                lastWithGps = item;
            }
        }

        if (firstWithGps != null && lastWithGps != null) {
            listener.onProgress(80, "Generating map...");
            RawMetadata first = firstWithGps.getRawMetadata();
            RawMetadata last  = lastWithGps.getRawMetadata();

            String inspirationPhrase = client.generateInspirationPhrase(
                    first.getLatitude(), first.getLongitude(),
                    last.getLatitude(),  last.getLongitude()
            );

            String mapPath = outputFolder + "/map.png";
            new MapGenerator().generateMap(
                    first.getLatitude(), first.getLongitude(),
                    last.getLatitude(),  last.getLongitude(),
                    mapPath
            );

            listener.onProgress(88, "Narrating map...");
            String mapAudioPath = tempFolder + "/audio_map.wav";
            tts.generateAudio(inspirationPhrase, mapAudioPath);

            String mapClipPath = tempFolder + "/clip_map.mp4";
            assembler.imageToClip(mapPath, mapAudioPath, mapClipPath);
            clipPaths.add(mapClipPath);
        }

        // --- step 5: concatenate into the final video (95-100%) ---
        listener.onProgress(95, "Finalizing video...");
        String finalVideoPath = outputFolder + "/output.mp4";
        assembler.concatenate(clipPaths, finalVideoPath);

        // clean up all intermediate files
        File[] tempFiles = new File(tempFolder).listFiles();
        if (tempFiles != null) {
            for (File f : tempFiles) {
                f.delete();
            }
        }
        new File(tempFolder).delete();

        listener.onProgress(100, "Done!");
        return finalVideoPath;
    }
}
