package edu.up.cg;

import edu.up.cg.map.MapGenerator;

public class Main {

    public static void main(String[] args) {
        try {
            // Guadalajara as the first location, Mexico City as the last
            double firstLat =  20.6597;
            double firstLon = -103.3496;
            double lastLat  =  19.4326;
            double lastLon  =  -99.1332;

            MapGenerator mapGenerator = new MapGenerator();
            String mapPath = mapGenerator.generateMap(firstLat, firstLon, lastLat, lastLon, "test_map.png");
            System.out.println("Map saved to: " + mapPath);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
