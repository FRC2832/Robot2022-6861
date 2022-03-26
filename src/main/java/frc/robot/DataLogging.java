package frc.robot;

import edu.wpi.first.util.datalog.DataLog;
import edu.wpi.first.util.datalog.DoubleLogEntry;
import edu.wpi.first.wpilibj.DataLogManager;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.PowerDistribution;
import edu.wpi.first.wpilibj.Timer;

public class DataLogging {
    private PowerDistribution pdp;
    private DoubleLogEntry[] pdpChannels;
    private DoubleLogEntry pdpBatteryVoltage;
    private DoubleLogEntry loopTime;
    private double startTime;

    public void Init() {
        // Starts recording to data log
        DataLogManager.start();
        DataLog log = DataLogManager.getLog();
        // Record both DS control and joystick data
        DriverStation.startDataLog(DataLogManager.getLog());
        pdp = new PowerDistribution();
        pdpChannels = new DoubleLogEntry[pdp.getNumChannels()];
        pdpBatteryVoltage = new DoubleLogEntry(log, "/pdp/Vbat");
        for(int i=0; i<pdpChannels.length;i++) {
            pdpChannels[i] = new DoubleLogEntry(log, "/pdp/channel" + i);
        }
        loopTime = new DoubleLogEntry(log, "/robot/LoopTime");
    }

    public void Periodic() {
        //log battery voltage
        pdpBatteryVoltage.append(pdp.getVoltage());
        for(int i=0; i<pdpChannels.length;i++){
            pdpChannels[i].append(pdp.getCurrent(i));
        }
        loopTime.append(Timer.getFPGATimestamp() - startTime);
    }

    public void StartLoopTime() {
        startTime = Timer.getFPGATimestamp();
    }
}
