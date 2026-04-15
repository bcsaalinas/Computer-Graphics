package edu.up.cg.metadata;

import edu.up.cg.models.RawMetadata;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class MetadataReader {
    // read metadata from the file and return it as a RawMetadata object
    public RawMetadata readMetadata(String path) {
        RawMetadata rawMetadata = new RawMetadata();
        ProcessBuilder pb = new ProcessBuilder("exiftool", "-j", path);
        try {
            pb.redirectErrorStream(true);
            Process process = pb.start();
            // read the output from the process and update rawMetadata with the metadata information we just got
            BufferedReader reader = new BufferedReader(new InputStreamReader((process.getInputStream())));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line);
            }

            // wait until exiftool fully exits
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new RuntimeException("exiftool failed : " + exitCode);
            }

            // parse the output and update rawMetadata with the metadata information we just got
            String jsonOutput = output.toString();
            System.out.println("---- exiftool raw output start ----");
            System.out.println(jsonOutput);
            System.out.println("---- exiftool raw output end ----");

            String dateValue = extractValue(jsonOutput, "DateTimeOriginal");
            if (dateValue == null) {
                dateValue = extractValue(jsonOutput, "CreateDate");
            }
            if (dateValue != null) {
                try {
                    rawMetadata.setDate(LocalDateTime.parse(dateValue, DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm:ss")));
                } catch (DateTimeParseException ignored) {
                    //leave date as null when exiftool returns an unexpected format.
                }
            }

            String latitudeValue = extractValue(jsonOutput, "GPSLatitude");
            if (latitudeValue != null) {
                Double latitude = parseCoordinate(latitudeValue);
                if (latitude != null) {
                    rawMetadata.setLatitude(latitude);
                }
            }

            String longitudeValue = extractValue(jsonOutput, "GPSLongitude");
            if (longitudeValue != null) {
                Double longitude = parseCoordinate(longitudeValue);
                if (longitude != null) {
                    rawMetadata.setLongitude(longitude);
                }
            }

            String orientationValue = extractValue(jsonOutput, "Orientation");
            if (orientationValue != null) {
                rawMetadata.setOrientation(parseOrientation(orientationValue));
            }

            return rawMetadata;


        } catch (Exception e) {
            e.printStackTrace();
        }


        System.out.println("Raw orientation value: " + rawMetadata.getOrientation());
        return rawMetadata;

    }


    private String extractValue(String jsonOutput, String key) {
        //parse the json output and extract the value for the given key

        String quotedKey = "\"" + key + "\"";
        int keyIndex = jsonOutput.indexOf(quotedKey);
        if (keyIndex == -1) {
            return null;
        }

        //find the colon after we found the key, then extract the value
        int colonIndex = jsonOutput.indexOf(':', keyIndex + quotedKey.length());


        if (colonIndex == -1) {
            return null;
        }

        //skip whitespace or unnecessary stuff
        int startIndex = colonIndex + 1;
        while (startIndex < jsonOutput.length() && Character.isWhitespace(jsonOutput.charAt(startIndex))) {
            startIndex++;
        }

        //if there is no value after the colon
        if (startIndex >= jsonOutput.length()) {
            return null;
        }


        //check if value is a string and build new string if it is
        char firstChar = jsonOutput.charAt(startIndex);
        if (firstChar == '"') {
            StringBuilder value = new StringBuilder();
            boolean escaped = false;
            for (int i = startIndex + 1; i < jsonOutput.length(); i++) {
                char current = jsonOutput.charAt(i);

                //handle escaped characters and end of string
                if (escaped) {
                    value.append(current);
                    escaped = false;
                } else if (current == '\\') {
                    escaped = true;
                } else if (current == '"') {
                    return value.toString();
                } else {
                    value.append(current);
                }
            }
            return value.toString();
        }


        //if value not a string, read until we hit a comma, closing brace, or closing bracket
        int endIndex = startIndex;
        while (endIndex < jsonOutput.length()) {
            char current = jsonOutput.charAt(endIndex);
            if (current == ',' || current == '}' || current == ']') {
                break;
            }
            endIndex++;
        }

        return jsonOutput.substring(startIndex, endIndex).trim();
    }

    private int parseOrientation(String value){
        if (value == null){
            return 1 ;  // default
        }
        String normalized = value.trim().toLowerCase();

        //if already a number, keep only the codes

        try {
            int numeric = Integer.parseInt(normalized);
            if (numeric >= 1 && numeric <= 8) {
                return numeric;
            }
        } catch (NumberFormatException ignored) {

        }

        //go through the possible values and return the corresponding code
        switch(normalized){
            case "horizontal (normal)":
                return 1;
            case "mirror horizontal":
                return 2;
            case "rotate 180":
                return 3;
            case "mirror vertical":
                return 4;
            case "mirror horizontal and rotate 270 cw":
                return 5;
            case "rotate 90 cw":
                return 6;
            case "mirror horizontal and rotate 90 cw":
                return 7;
            case "rotate 270 cw":
                return 8;
            default:
                return 1; // default
        }

    }

    //format coordinates in dms or decimal to decimal format
    private Double parseCoordinate(String value) {
        if (value == null) {
            return null;
        }

        String normalized = value.trim();
        if (normalized.isEmpty()) {
            return null;
        }

        // if it is already a decimal number, return it directly
        try {
            return Double.parseDouble(normalized);
        } catch (NumberFormatException ignored) {
            // not a plain number, continue with dms parsing
        }

        // extract up to 3 numeric parts, degrees, minutes, seconds
        double[] parts = new double[3];
        int count = 0;
        StringBuilder current = new StringBuilder();

        for (int i = 0; i < normalized.length(); i++) {
            char c = normalized.charAt(i);
            boolean numberChar = Character.isDigit(c) || c == '.'
                    || ((c == '-' || c == '+') && current.length() == 0);

            if (numberChar) {
                current.append(c);
            } else if (current.length() > 0) {
                try {
                    parts[count] = Double.parseDouble(current.toString());
                    count++;
                } catch (NumberFormatException ignored) {
                    // skip invalid piece
                }
                current.setLength(0);
                if (count == 3) {
                    break;
                }
            }
        }

        // parse last piece if string ended with a number
        if (count < 3 && current.length() > 0) {
            try {
                parts[count] = Double.parseDouble(current.toString());
                count++;
            } catch (NumberFormatException ignored) {
                // skip invalid piece
            }
        }

        if (count == 0) {
            return null;
        }

        double degrees = parts[0];
        double minutes = count > 1 ? parts[1] : 0.0;
        double seconds = count > 2 ? parts[2] : 0.0;

        // dms to decimal: deg + min/60 + sec/3600
        double decimal = Math.abs(degrees) + (minutes / 60.0) + (seconds / 3600.0);

        // south and west must be negative
        String upper = normalized.toUpperCase();
        if (upper.contains("S") || upper.contains("W")) {
            decimal = -decimal;
        } else if (degrees < 0) {
            decimal = -decimal;
        }

        return decimal;
    }

}
