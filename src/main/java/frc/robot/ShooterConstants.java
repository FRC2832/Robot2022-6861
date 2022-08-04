package frc.robot;

import java.util.ArrayList;

import edu.wpi.first.math.Pair;
public class ShooterConstants {
    public final static ArrayList<Pair<Double,Double>> VISION_DIST_TABLE = new ArrayList<Pair<Double,Double>>();
    public final static ArrayList<Pair<Double,Double>> DIST_RPM_TABLE = new ArrayList<Pair<Double,Double>>();
    public final static ArrayList<Pair<Double,Double>> DIST_HOOD_TABLE = new ArrayList<Pair<Double,Double>>();

    public final static double DEFAULT_SHOT_RPM=2440;
    public final static double DEFAULT_SHOT_ANGLE=37;
    public final static double LOW_SHOT_RPM=500;
    public final static double LOW_SHOT_ANGLE=30;
    public final static double LOW_SHOT_RPM_ERROR=30;
    public final static double LOW_SHOT_ANGLE_ERROR=1;

    public static void LoadConstants() {
        //table is input: pixel Y, output: inches from target
        VISION_DIST_TABLE.add(new Pair<Double, Double>(47., 65.));
        VISION_DIST_TABLE.add(new Pair<Double, Double>(78., 70.));
        VISION_DIST_TABLE.add(new Pair<Double, Double>(114., 80.));
        VISION_DIST_TABLE.add(new Pair<Double, Double>(175., 95.));
        VISION_DIST_TABLE.add(new Pair<Double, Double>(213., 110.));
        VISION_DIST_TABLE.add(new Pair<Double, Double>(242., 125.));  //263 125?
        VISION_DIST_TABLE.add(new Pair<Double, Double>(267., 140.));
        VISION_DIST_TABLE.add(new Pair<Double, Double>(281., 148.));
        VISION_DIST_TABLE.add(new Pair<Double, Double>(289., 155.));
        VISION_DIST_TABLE.add(new Pair<Double, Double>(307., 166.));
        VISION_DIST_TABLE.add(new Pair<Double, Double>(323., 178.));
        VISION_DIST_TABLE.add(new Pair<Double, Double>(345., 200.));
        
        //table is input: distance in inches, output: rpm
        DIST_RPM_TABLE.add(new Pair<Double, Double>( 70., 2050d));
        DIST_RPM_TABLE.add(new Pair<Double, Double>( 81., 2050d));
        DIST_RPM_TABLE.add(new Pair<Double, Double>( 91., 2100d));
        DIST_RPM_TABLE.add(new Pair<Double, Double>(103., 2200d));
        DIST_RPM_TABLE.add(new Pair<Double, Double>(108., 2200d));
        DIST_RPM_TABLE.add(new Pair<Double, Double>(123., 2250d));
        DIST_RPM_TABLE.add(new Pair<Double, Double>(127., 2260d));
        DIST_RPM_TABLE.add(new Pair<Double, Double>(145., 2356d));
        DIST_RPM_TABLE.add(new Pair<Double, Double>(155., 2400d));
        DIST_RPM_TABLE.add(new Pair<Double, Double>(165., 2500d));
        DIST_RPM_TABLE.add(new Pair<Double, Double>(178., 2650d));

        //table is input: distance in inches, output: angle in degrees
        DIST_HOOD_TABLE.add(new Pair<Double, Double>(70.,  28.));
        DIST_HOOD_TABLE.add(new Pair<Double, Double>(81.,  28.));
        DIST_HOOD_TABLE.add(new Pair<Double, Double>(91.,  28.));
        DIST_HOOD_TABLE.add(new Pair<Double, Double>(103., 30.));
        DIST_HOOD_TABLE.add(new Pair<Double, Double>(108., 31.));
        DIST_HOOD_TABLE.add(new Pair<Double, Double>(123., 33.75));
        DIST_HOOD_TABLE.add(new Pair<Double, Double>(127., 34.25));
        DIST_HOOD_TABLE.add(new Pair<Double, Double>(145., 36.875));
        DIST_HOOD_TABLE.add(new Pair<Double, Double>(165., 41.125));
        DIST_HOOD_TABLE.add(new Pair<Double, Double>(178., 45.));
    }
}
