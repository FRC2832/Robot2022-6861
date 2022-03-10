package frc.robot;

import com.revrobotics.CANSparkMax;
import com.revrobotics.ColorMatch;
import com.revrobotics.ColorMatchResult;
import com.revrobotics.ColorSensorV3;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

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

    private enum CargoColor {
        Red,
        Blue,
        Unknown
    }

    public Intake() {
        intakeMotor = new CANSparkMax(24,MotorType.kBrushless);
        intakeMotor.setInverted(true);
        
        upMotor = new CANSparkMax(31,MotorType.kBrushless);
        upMotor.setInverted(true);
        
        colorSensor = new ColorSensorV3(Port.kOnboard);
        m_colorMatcher.addColorMatch(kBlueTarget);
        m_colorMatcher.addColorMatch(kRedTarget);
    }

    public void setIntake(double voltPct) {
        intakeMotor.set(voltPct);
    }

    public void  setUpMotor(double pct) {
        upMotor.set(pct);
    }

    @Override
    public void periodic() {
        SmartDashboard.putNumber("Intake Speed%", intakeMotor.get());

        Color color = colorSensor.getColor();
        ColorMatchResult match = m_colorMatcher.matchClosestColor(color);
        CargoColor colorEnum;
        if (match.color == kBlueTarget) {
            colorEnum = CargoColor.Blue;
        } else if (match.color == kRedTarget) {
            colorEnum = CargoColor.Red;
        } else {
            colorEnum = CargoColor.Unknown;
        }

        SmartDashboard.putNumber("Sense Red", color.red);
        SmartDashboard.putNumber("Sense Green", color.green);
        SmartDashboard.putNumber("Sense Blue", color.blue);
        SmartDashboard.putString("Ball Color", colorEnum.toString());
    }
}
