
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Clock {
    public static void main(String[] args){

        // what time are we showing on the clock
        int hours = 10;
        int minutes = 45;


        int width = 400;
        int height = 300;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        // where everything is centered
        int centerX = width / 2;
        int centerY = height / 2;
        int radius = (int)(height * 0.4);

        // how thick things are
        int circleThickness = 2;
        int hourMarkerRadius = 6;
        int handThickness = 3;

        // figure out where all 12 hour dots go
        int[] hourX = new int[12];
        int[] hourY = new int[12];

        for(int hour = 0; hour < 12; hour++){
            // each hour is 30 degrees apart start from top
            double angleDegrees = (hour * 30) - 90;
            double angleRadians = Math.toRadians(angleDegrees);

            // place dots inside the circle edge a bit
            int markerRadius = radius - 20;

            // convert polar to cartesian
            hourX[hour] = (int)(centerX + markerRadius * Math.cos(angleRadians));
            hourY[hour] = (int)(centerY + markerRadius * Math.sin(angleRadians));
        }

        // calculate where minute hand points
        double minuteAngleDegrees = (minutes * 6) - 90;
        double minuteAngleRadians = Math.toRadians(minuteAngleDegrees);

        // normalize to -pi to pi range (this is the key fix)
        while(minuteAngleRadians > Math.PI) minuteAngleRadians -= 2 * Math.PI;
        while(minuteAngleRadians < -Math.PI) minuteAngleRadians += 2 * Math.PI;

        int minuteHandLength = radius - 25;

        // calculate where hour hand points
        double hourAngleDegrees = ((hours % 12) + (minutes / 60.0)) * 30 - 90;
        double hourAngleRadians = Math.toRadians(hourAngleDegrees);

        // normalize to -pi to pi range
        while(hourAngleRadians > Math.PI) hourAngleRadians -= 2 * Math.PI;
        while(hourAngleRadians < -Math.PI) hourAngleRadians += 2 * Math.PI;

        int hourHandLength = radius - 45;

        // now visit every pixel and decide its color
        for (int i = 0; i < width; i++){
            for(int j = 0; j < height; j++){

                // how far is this pixel from center
                int dx = i - centerX;
                int dy = j - centerY;
                double distanceFromCenter = Math.sqrt(dx*dx + dy*dy);

                // if pixel on the circle outline
                boolean onMainCircle = (distanceFromCenter >= radius - circleThickness &&
                        distanceFromCenter <= radius + circleThickness);

                // if pixel on any hour marker dot
                boolean onHourMarker = false;
                for(int hour = 0; hour < 12; hour++){
                    int dxHour = i - hourX[hour];
                    int dyHour = j - hourY[hour];
                    double distFromHour = Math.sqrt(dxHour*dxHour + dyHour*dyHour);

                    if(distFromHour <= hourMarkerRadius){
                        onHourMarker = true;
                        break;
                    }
                }

                // if pixel on the minute hand
                boolean onMinuteHand = false;
                if(distanceFromCenter <= minuteHandLength && distanceFromCenter > 5){
                    // what angle is this pixel at
                    double pixelAngle = Math.atan2(dy, dx);

                    // how different is it from minute hand angle
                    double angleDiff = Math.abs(pixelAngle - minuteAngleRadians);

                    // handle wraparound (angles near -180 and 180 are close)
                    if(angleDiff > Math.PI) angleDiff = 2 * Math.PI - angleDiff;

                    // if angle is close enough, pixel is on hand
                    if(angleDiff < Math.atan2(handThickness, distanceFromCenter)){
                        onMinuteHand = true;
                    }
                }

                // if pixel on the hour hand
                boolean onHourHand = false;
                if(distanceFromCenter <= hourHandLength && distanceFromCenter > 5){
                    // what angle is this pixel at
                    double pixelAngle = Math.atan2(dy, dx);

                    // how different is it from hour hand angle
                    double angleDiff = Math.abs(pixelAngle - hourAngleRadians);

                    // handle wraparound
                    if(angleDiff > Math.PI) angleDiff = 2 * Math.PI - angleDiff;

                    // if angle is close enough, pixel is on hand
                    if(angleDiff < Math.atan2(handThickness, distanceFromCenter)){
                        onHourHand = true;
                    }
                }

                // draw
                if(onMainCircle || onHourMarker || onMinuteHand || onHourHand){
                    image.setRGB(i, j, Color.WHITE.getRGB());
                } else {
                    image.setRGB(i, j, Color.BLACK.getRGB());
                }
            }
        }


        try{
            ImageIO.write(image, "png", new File("clock.png"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}