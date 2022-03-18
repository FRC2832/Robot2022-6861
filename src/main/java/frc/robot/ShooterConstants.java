package frc.robot;

import java.util.ArrayList;

import edu.wpi.first.math.Pair;
public class ShooterConstants {
    public final static ArrayList<Pair<Double,Double>> VISION_DIST_TABLE = new ArrayList<Pair<Double,Double>>();
    public final static ArrayList<Pair<Double,Double>> DIST_RPM_TABLE = new ArrayList<Pair<Double,Double>>();
    public final static ArrayList<Pair<Double,Double>> DIST_HOOD_TABLE = new ArrayList<Pair<Double,Double>>();

    public final static double DEFAULT_SHOT_RPM=2550;
    public final static double DEFAULT_SHOT_ANGLE=33;
    public final static double LOW_SHOT_RPM=400;
    public final static double LOW_SHOT_ANGLE=30;
    public final static double LOW_SHOT_RPM_ERROR=30;
    public final static double LOW_SHOT_ANGLE_ERROR=1;

    public static void LoadConstants() {
        //table is input: pixel Y, output: inches from target
        VISION_DIST_TABLE.add(new Pair<Double, Double>(100., 70.));
        VISION_DIST_TABLE.add(new Pair<Double, Double>(147., 80.));
        VISION_DIST_TABLE.add(new Pair<Double, Double>(215., 95.));
        VISION_DIST_TABLE.add(new Pair<Double, Double>(260., 110.));
        VISION_DIST_TABLE.add(new Pair<Double, Double>(290., 125.));  //263 125?
        VISION_DIST_TABLE.add(new Pair<Double, Double>(320., 140.));
        VISION_DIST_TABLE.add(new Pair<Double, Double>(352., 155.));
        VISION_DIST_TABLE.add(new Pair<Double, Double>(374., 166.));

        //table is input: distance in inches, output: rpm
        DIST_RPM_TABLE.add(new Pair<Double, Double>(70., 2150d));
        DIST_RPM_TABLE.add(new Pair<Double, Double>(85., 2200d));
        DIST_RPM_TABLE.add(new Pair<Double, Double>(107., 2300d));
        DIST_RPM_TABLE.add(new Pair<Double, Double>(127., 2369d));
        DIST_RPM_TABLE.add(new Pair<Double, Double>(145., 2300d));
        DIST_RPM_TABLE.add(new Pair<Double, Double>(155., 2400d));
        DIST_RPM_TABLE.add(new Pair<Double, Double>(165., 2500d));

        //table is input: distance in inches, output: angle in degrees
        DIST_HOOD_TABLE.add(new Pair<Double, Double>(70.,  21.375));
        DIST_HOOD_TABLE.add(new Pair<Double, Double>(85.,  28.375));
        DIST_HOOD_TABLE.add(new Pair<Double, Double>(107., 25.125));
        DIST_HOOD_TABLE.add(new Pair<Double, Double>(127., 34.25));
        DIST_HOOD_TABLE.add(new Pair<Double, Double>(145., 36.875));
        DIST_HOOD_TABLE.add(new Pair<Double, Double>(165., 41.125));

    }
}
