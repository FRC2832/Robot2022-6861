package frc.robot;

import com.revrobotics.CANSparkMax;
import com.revrobotics.ColorSensorV3;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj.I2C.Port;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.util.Color;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class Intake extends SubsystemBase {
    private CANSparkMax intakeMotor;
    private CANSparkMax upMotor;
    private ColorSensorV3 colorSensor;

    public Intake() {
        intakeMotor = new CANSparkMax(24,MotorType.kBrushless);
        intakeMotor.setInverted(true);
        upMotor = new CANSparkMax(31,MotorType.kBrushless);
        upMotor.setInverted(true);
        colorSensor = new ColorSensorV3(Port.kOnboard);
    }

    public void setIntake(double voltPct) {
        intakeMotor.set(voltPct);
    }

    public void setUpMotor(double pct) {
        upMotor.set(pct);
    }

    @Override
    public void periodic() {
        SmartDashboard.putNumber("Intake Speed%", intakeMotor.get());
        Color color = colorSensor.getColor();
        SmartDashboard.putNumber("Sense Red", color.red);
        SmartDashboard.putNumber("Sense Green", color.green);
        SmartDashboard.putNumber("Sense Blue", color.blue);
    }
}
