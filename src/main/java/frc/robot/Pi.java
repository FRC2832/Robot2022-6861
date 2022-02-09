package frc.robot;

import java.util.ArrayList;

import edu.wpi.first.math.Pair;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;

public class Pi {
    private NetworkTableInstance netTableInstance;
    private NetworkTable table;
    private NetworkTableEntry cargoCenterX;
    private NetworkTableEntry cargoCenterY;
    private NetworkTableEntry allianceColor;
    private static ArrayList<Pair<Double,Double>> distTable;

    public Pi() {
        netTableInstance = NetworkTableInstance.getDefault();
        table = netTableInstance.getTable("vision");
        cargoCenterX = table.getEntry("cargoX");
        cargoCenterY = table.getEntry("cargoY");
        allianceColor = table.getEntry("alliance");

        distTable = new ArrayList<Pair<Double,Double>>();
        //table is input: pixel width, output: meters from target
        distTable.add(new Pair<Double, Double>(94.0, 7.7724));
        distTable.add(new Pair<Double, Double>(100.0, 7.112));
        distTable.add(new Pair<Double, Double>(112.0, 6.35));
        distTable.add(new Pair<Double, Double>(126.0, 5.4864));
        distTable.add(new Pair<Double, Double>(130.0, 5.3086));
        distTable.add(new Pair<Double, Double>(142.0, 4.8768));
        distTable.add(new Pair<Double, Double>(158.0, 4.3688));
        distTable.add(new Pair<Double, Double>(167.0, 4.064));
        distTable.add(new Pair<Double, Double>(188.0, 3.5814));
        distTable.add(new Pair<Double, Double>(211.0, 3.1496));
        distTable.add(new Pair<Double, Double>(245.0, 2.667));
        distTable.add(new Pair<Double, Double>(298.0, 2.1336));
        distTable.add(new Pair<Double, Double>(372.0, 1.651));
    }

    // sends alliance color to the python code so it knows what color cargo to look for
    public void sendAlliance() {
        Alliance alliance = DriverStation.getAlliance();
        if(alliance == Alliance.Red) {
            allianceColor.setString("red");
        } else {
            allianceColor.setString("blue");
        }
    }

    public void processCargo() {
        Number[] cargoCenterXArray = cargoCenterX.getNumberArray(new Number[0]);
        Number[] cargoCenterYArray = cargoCenterY.getNumberArray(new Number[0]);
        System.out.println("cargo x: " + cargoCenterXArray);
        System.out.println("cargo y: " + cargoCenterYArray);
    }

    public static double LinearInterp(ArrayList<Pair<Double,Double>> list, double input) {
        //if input is smaller than the table, return the first element
        if(input < list.get(0).getFirst()) {
            return list.get(0).getSecond();
        }
        //if input is larger than the table, return the last element
        if(input > list.get(list.size()-1).getFirst()) {
            return list.get(list.size()-1).getSecond();
        }
        //otherwise the value is in the table
        for(int i=0; i<list.size()-1; i++) {
            double x0 = list.get(i).getFirst();
            double x1 = list.get(i+1).getFirst();
            if ((x0 <= input) && (input <= x1)) {
                //see https://en.wikipedia.org/wiki/Linear_interpolation
                double y0 = list.get(i).getSecond();
                double y1 = list.get(i+1).getSecond();
                return (y0*(x1-input) + y1*(input-x0))/(x1-x0);
            }
        }
        //should never happen...
        return Double.NaN;
    }
}
