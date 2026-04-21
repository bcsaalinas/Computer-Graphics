package edu.up.cg.map;

import java.io.*;

// fetches a static map image from Mapbox with pins at the first and last GPS locations
public class MapGenerator {

    private static final String MAP_STYLE = "mapbox/streets-v12";

    private final String token;

    public MapGenerator() {
        this.token = System.getenv("MAPBOX_TOKEN");
        if (this.token == null || this.token.isEmpty()) {
            throw new RuntimeException("MAPBOX_TOKEN environment variable is not set");
        }
    }

    // downloads the map and saves it as a 1080x1920 portrait png at outputPath
    public String generateMap(double firstLat, double firstLon, double lastLat, double lastLon, String outputPath) throws Exception {
        String url = buildUrl(firstLat, firstLon, lastLat, lastLon);
        downloadMap(url, outputPath);
        return outputPath;
    }

    // builds the Mapbox static map URL with two labeled pins
    private String buildUrl(double firstLat, double firstLon, double lastLat, double lastLon) {
        // Mapbox expects longitude before latitude in coordinates
        String markers = "pin-s-a+ff0000(" + firstLon + "," + firstLat + "),"
                       + "pin-s-b+0000ff(" + lastLon  + "," + lastLat  + ")";

        // 540x960@2x tells Mapbox to deliver exactly 1080x1920 pixels in portrait
        // auto fits both pins within that frame — no post-processing or cropping needed
        return "https://api.mapbox.com/styles/v1/" + MAP_STYLE + "/static/"
                + markers + "/auto/540x960@2x?padding=40&access_token=" + token;
    }

    // runs curl to download the map image and save it directly to outputPath
    private void downloadMap(String url, String outputPath) throws Exception {
        ProcessBuilder pb = new ProcessBuilder(
                "curl", "-g", "-s", "--max-time", "30",
                url, "--output", outputPath
        );
        pb.redirectErrorStream(true);
        Process process = pb.start();

        // drain stdout so curl never blocks on a full pipe buffer
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        while (reader.readLine() != null) {}

        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new RuntimeException("curl failed with exit code: " + exitCode);
        }
    }
}
