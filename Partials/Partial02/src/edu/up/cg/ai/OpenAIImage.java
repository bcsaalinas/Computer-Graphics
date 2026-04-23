package edu.up.cg.ai;

import java.io.*;
import java.nio.file.Files;
import java.util.Base64;

// generates images from text prompts using the OpenAI DALL-E API
public class OpenAIImage {

    private static final String IMAGE_URL = "https://api.openai.com/v1/images/generations";

    private final String apiKey;

    public OpenAIImage() {
        this.apiKey = System.getenv("OPEN_API_KEY");
        if (this.apiKey == null || this.apiKey.isEmpty()) {
            throw new RuntimeException("OPEN_API_KEY environment variable is not set");
        }
    }

    // generates an image from the given prompt and saves it as a PNG at outputPath
    public String generateImage(String prompt, String outputPath) throws Exception {
        String requestBody = buildRequest(prompt);
        String response = sendRequest(requestBody);
        byte[] imageBytes = extractImageBytes(response);
        saveImage(imageBytes, outputPath);
        return outputPath;
    }

    // builds the JSON request body for the DALL-E API
    private String buildRequest(String prompt) {
        // 1024x1792 is the only portrait size DALL-E 3 supports
        return "{\"model\":\"dall-e-3\","
                + "\"prompt\":\"" + escapeJson(prompt) + "\","
                + "\"n\":1,"
                + "\"size\":\"1024x1792\","
                + "\"response_format\":\"b64_json\"}";
    }

    // writes the request body to a temp file and calls the DALL-E API via curl
    private String sendRequest(String requestBody) throws Exception {
        File tempFile = File.createTempFile("dalle_request", ".json");
        Files.writeString(tempFile.toPath(), requestBody);

        ProcessBuilder pb = new ProcessBuilder(
                "curl", "-s", "--max-time", "120", "-X", "POST",
                IMAGE_URL,
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

    // pulls the base64 image data out of the DALL-E JSON response and decodes it to bytes
    private byte[] extractImageBytes(String jsonResponse) {
        String key = "\"b64_json\":";
        int keyIndex = jsonResponse.indexOf(key);
        if (keyIndex == -1) {
            throw new RuntimeException("DALL-E response did not contain image data: " + jsonResponse);
        }

        // move past the key and the opening quote, then read until the closing quote
        int start = jsonResponse.indexOf('"', keyIndex + key.length()) + 1;
        int end = jsonResponse.indexOf('"', start);

        return Base64.getDecoder().decode(jsonResponse.substring(start, end));
    }

    // writes the decoded image bytes to disk as a PNG file
    private void saveImage(byte[] imageBytes, String outputPath) throws Exception {
        FileOutputStream fos = new FileOutputStream(outputPath);
        fos.write(imageBytes);
        fos.close();
    }

    // escapes special characters so the prompt can be safely embedded inside a JSON value
    private String escapeJson(String text) {
        return text
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
