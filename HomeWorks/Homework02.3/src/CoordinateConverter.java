public class CoordinateConverter {

    public static double[] cartesianToPolar(double x, double y){
        //formula for polar coordinates given x and y
        double r = Math.sqrt(x*x + y*y);
        double theta = Math.atan2(y, x);

        //create a double array with both coordinates inside
        return new double[]{r, theta};
    }


    public static double[] polarToCartesian(double r, double theta){
        double x = r * Math.cos(theta);
        double y = r * Math.sin(theta);

        //same as above
        return new double[]{x, y};

    }



}
