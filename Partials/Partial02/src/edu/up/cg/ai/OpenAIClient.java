package edu.up.cg.ai;

import edu.up.cg.models.MediaItem;

import javax.imageio.ImageIO;
import java.io.*;
import java.nio.file.Files;
import java.util.Base64;
import java.util.List;

// handles all text generation calls to the OpenAI API
public class OpenAIClient {

    private static final String API_URL = "https://api.openai.com/v1/chat/completions";

    private final String apiKey;

    public OpenAIClient() {
        this.apiKey = System.getenv("OPEN_API_KEY");
        if (this.apiKey == null || this.apiKey.isEmpty()) {
            throw new RuntimeException("OPEN_API_KEY environment variable is not set");
        }
    }

    // generates a short spoken description of a media item for the video narration
    public String generateMediaDescription(MediaItem item) throws Exception {
        String base64Image = encodeImageToBase64(item);
        String prompt = "Describe this photo in 2 to 3 sentences as if you are narrating a travel video. Be vivid and engaging. Do not mention technical details like resolution or file format.";
        String requestBody = buildVisionRequest(prompt, base64Image);
        String response = sendRequest(requestBody);
        return extractText(response);
    }

    // takes all per-item descriptions and returns an image generation prompt that captures the essence of the whole journey
    public String generateEssencePrompt(List<String> descriptions) throws Exception {
        StringBuilder joined = new StringBuilder();
        for (int i = 0; i < descriptions.size(); i++) {
            joined.append(i + 1).append(". ").append(descriptions.get(i)).append(" ");
        }

        String prompt = "You are given a list of descriptions of photos from a trip:\n"
                + joined.toString().trim()
                + "\nWrite a single short image generation prompt (1-2 sentences) that captures the overall essence and mood of this journey. Only output the prompt, nothing else.";

        String requestBody = buildTextRequest(prompt);
        String response = sendRequest(requestBody);
        return extractText(response);
    }

    // generates an inspirational phrase for the map slide based on the first and last GPS coordinates visited
    public String generateInspirationPhrase(double firstLat, double firstLon, double lastLat, double lastLon) throws Exception {
        String prompt = "Write a short inspirational travel phrase of 1 to 2 sentences inspired by a journey that started near coordinates "
                + firstLat + ", " + firstLon + " and ended near " + lastLat + ", " + lastLon
                + ". Make it poetic and uplifting. Only output the phrase, nothing else.";
        String requestBody = buildTextRequest(prompt);
        String response = sendRequest(requestBody);
        return extractText(response);
    }

    // builds the JSON request body for a text-only prompt
    private String buildTextRequest(String prompt) {
        return "{\"model\":\"gpt-4o\","
                + "\"messages\":[{\"role\":\"user\",\"content\":\"" + escapeJson(prompt) + "\"}]}";
    }

    // builds the JSON request body for a prompt that includes an image
    private String buildVisionRequest(String prompt, String base64Image) {
        return "{\"model\":\"gpt-4o\","
                + "\"messages\":[{\"role\":\"user\",\"content\":["
                + "{\"type\":\"text\",\"text\":\"" + escapeJson(prompt) + "\"},"
                + "{\"type\":\"image_url\",\"image_url\":{\"url\":\"data:image/jpeg;base64," + base64Image + "\"}}"
                + "]}]}";
    }

    // writes the request body to a temp file and runs curl to call the OpenAI API
    // we use a file instead of passing the body directly because base64 images can be very large
    private String sendRequest(String requestBody) throws Exception {
        File tempFile = File.createTempFile("openai_request", ".json");
        Files.writeString(tempFile.toPath(), requestBody);

        ProcessBuilder pb = new ProcessBuilder(
                "curl", "-s", "--max-time", "60", "-X", "POST",
                API_URL,
                "-H", "Authorization: Bearer " + apiKey,
                "-H", "Content-Type: application/json",
                "-d", "@" + tempFile.getAbsolutePath()
        );
        pb.redirectErrorStream(true);
        Process process = pb.start();

        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }

        int exitCode = process.waitFor();
        tempFile.delete();

        if (exitCode != 0) {
            throw new RuntimeException("curl failed with exit code: " + exitCode);
        }

        return response.toString();
    }

    // reads the image from the MediaItem and encodes it as a base64 string so it can be sent in JSON
    private String encodeImageToBase64(MediaItem item) throws Exception {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        ImageIO.write(item.getImage(), "jpg", byteStream);
        return Base64.getEncoder().encodeToString(byteStream.toByteArray());
    }

    // pulls the generated text out of the OpenAI JSON response
    private String extractText(String jsonResponse) {
        // the response text lives inside choices[0].message.content
        // find "message" first, then "content" after it to avoid matching other content fields
        String messageKey = "\"message\":";
        int messageIndex = jsonResponse.indexOf(messageKey);
        if (messageIndex == -1) {
            throw new RuntimeException("OpenAI response did not contain a message field: " + jsonResponse);
        }

        String contentKey = "\"content\":";
        int contentIndex = jsonResponse.indexOf(contentKey, messageIndex);
        if (contentIndex == -1) {
            throw new RuntimeException("OpenAI response did not contain a content field: " + jsonResponse);
        }

        // move past the key and the opening quote of the value
        int start = jsonResponse.indexOf('"', contentIndex + contentKey.length()) + 1;

        StringBuilder text = new StringBuilder();
        for (int i = start; i < jsonResponse.length(); i++) {
            char c = jsonResponse.charAt(i);

            if (c == '\\') {
                // the next character is escaped, handle it manually
                i++;
                char escaped = jsonResponse.charAt(i);
                if (escaped == 'n') text.append('\n');
                else if (escaped == '"') text.append('"');
                else text.append(escaped);
            } else if (c == '"') {
                // closing quote means the string value has ended
                break;
            } else {
                text.append(c);
            }
        }

        return text.toString().trim();
    }

    // escapes special characters so the string can be safely embedded inside a JSON value
    private String escapeJson(String text) {
        return text
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
