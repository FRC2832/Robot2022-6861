package frc.robot;

import com.revrobotics.CANSparkMax;
import com.revrobotics.ColorMatch;
import com.revrobotics.ColorMatchResult;
import com.revrobotics.ColorSensorV3;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.I2C.Port;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.util.Color;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class Intake extends SubsystemBase {
    public final double INTAKE_SPEED = 0.5;
    public final double UP_SPEED = 0.35;    //if you change this, all shooting calibrations must be checked

    private CANSparkMax intakeMotor;
    private CANSparkMax upMotor;
    private ColorSensorV3 colorSensor;
    private ColorMatch m_colorMatcher = new ColorMatch();
    private final Color kBlueTarget = new Color(0.171, 0.421, 0.406);
    private final Color kRedTarget = new Color(0.499, 0.362, 0.138);
    private final Color kUnknownTarget = new Color(0.269, 0.481, 0.249);
    private CargoColor colorMatch;
    private Color color;
    private DigitalInput proxSensor;

    public enum CargoColor {
        Red,
        Blue,
        Unknown
    }

    public Intake() {
        intakeMotor = new CANSparkMax(24,MotorType.kBrushless);
        intakeMotor.setInverted(true);
        
        upMotor = new CANSparkMax(31,MotorType.kBrushless);
        upMotor.setInverted(true);
        upMotor.setIdleMode(IdleMode.kBrake);
        
        colorSensor = new ColorSensorV3(Port.kOnboard);
        m_colorMatcher.addColorMatch(kBlueTarget);
        m_colorMatcher.addColorMatch(kRedTarget);
        m_colorMatcher.addColorMatch(kUnknownTarget);
        colorMatch = CargoColor.Unknown;
        color = Color.kBlack;

        proxSensor = new DigitalInput(0);

        //move color sensor read to seperate thread since it sometimes locks up reading I2C
        ReadColorSensorThread thread = new ReadColorSensorThread();
        thread.start();
    }

    public void setIntake(double voltPct) {
        intakeMotor.set(voltPct);
    }

    public void setUpMotor(double pct) {
        upMotor.set(pct);
    }

    public CargoColor getColorSensor() {
        return colorMatch;
    }

    public boolean getProxSensor() {
        return !proxSensor.get();
    }

    public boolean intakeFull() {
        return getProxSensor() && (colorMatch != CargoColor.Unknown);
    }

    private class ReadColorSensorThread extends Thread {
        public void run() {
            while(true) {
                //moved to a seperate thread because the color sensor sometimes lags
                color = colorSensor.getColor();
                ColorMatchResult match = m_colorMatcher.matchClosestColor(color);
                if (match.color == kBlueTarget) {
                    colorMatch = CargoColor.Blue;
                } else if (match.color == kRedTarget) {
                    colorMatch = CargoColor.Red;
                } else {
                    colorMatch = CargoColor.Unknown;
                }
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    // do nothing
                }
            }
        }
    }

    @Override
    public void periodic() {
        SmartDashboard.putNumber("Intake Speed%", intakeMotor.get());
        SmartDashboard.putString("Ball Color", colorMatch.toString());
        SmartDashboard.putBoolean("Prox Sensor", getProxSensor());
        if(DriverStation.isTest()) {
            SmartDashboard.putNumber("Sense Red", color.red);
            SmartDashboard.putNumber("Sense Green", color.green);
            SmartDashboard.putNumber("Sense Blue", color.blue);
        }
    }

    public void saveConfig() {
        upMotor.burnFlash();
        intakeMotor.burnFlash();
    }
}
