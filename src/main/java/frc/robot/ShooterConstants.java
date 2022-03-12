package frc.robot;

import java.util.ArrayList;

import edu.wpi.first.math.Pair;
public class ShooterConstants {
    public final static ArrayList<Pair<Double,Double>> VISION_DIST_TABLE = new ArrayList<Pair<Double,Double>>();
    public final static ArrayList<Pair<Double,Double>> DIST_RPM_TABLE = new ArrayList<Pair<Double,Double>>();
    public final static ArrayList<Pair<Double,Double>> DIST_HOOD_TABLE = new ArrayList<Pair<Double,Double>>();

    public final static double DEFAULT_SHOT_RPM=2400;
    public final static double DEFAULT_SHOT_ANGLE=40;
    public final static double LOW_SHOT_RPM=600;
    public final static double LOW_SHOT_ANGLE=65;
    public final static double LOW_SHOT_RPM_ERROR=30;
    public final static double LOW_SHOT_ANGLE_ERROR=1;

    public static void LoadConstants() {
        //table is input: pixel Y, output: inches from target
        VISION_DIST_TABLE.add(new Pair<Double, Double>(54.5, 70.));
        VISION_DIST_TABLE.add(new Pair<Double, Double>(111., 85.));
        VISION_DIST_TABLE.add(new Pair<Double, Double>(186.1, 107.));
        VISION_DIST_TABLE.add(new Pair<Double, Double>(233.25, 127.));
        VISION_DIST_TABLE.add(new Pair<Double, Double>(270.3, 145.));
        VISION_DIST_TABLE.add(new Pair<Double, Double>(299.3, 165.));

        //table is input: distance in inches, output: rpm
        DIST_RPM_TABLE.add(new Pair<Double, Double>(70., 2150d));
        DIST_RPM_TABLE.add(new Pair<Double, Double>(85., 2100d));
        DIST_RPM_TABLE.add(new Pair<Double, Double>(107., 2400d));
        DIST_RPM_TABLE.add(new Pair<Double, Double>(127., 2369d));
        DIST_RPM_TABLE.add(new Pair<Double, Double>(145., 2450d));
        DIST_RPM_TABLE.add(new Pair<Double, Double>(165., 2600d));

        //table is input: distance in inches, output: angle in degrees
        DIST_HOOD_TABLE.add(new Pair<Double, Double>(70.,  21.375));
        DIST_HOOD_TABLE.add(new Pair<Double, Double>(85.,  28.375));
        DIST_HOOD_TABLE.add(new Pair<Double, Double>(107., 25.125));
        DIST_HOOD_TABLE.add(new Pair<Double, Double>(127., 34.25));
        DIST_HOOD_TABLE.add(new Pair<Double, Double>(145., 34.875));
        DIST_HOOD_TABLE.add(new Pair<Double, Double>(165., 42.125));
    }
}
