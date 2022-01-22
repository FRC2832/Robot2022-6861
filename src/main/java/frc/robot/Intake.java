package frc.robot;

import edu.wpi.first.wpilibj.motorcontrol.PWMSparkMax;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class Intake extends SubsystemBase {
    private PWMSparkMax intakeMotor;

    public Intake() {
        intakeMotor = new PWMSparkMax(5);
    }

    public void setIntake(double voltPct) {
        intakeMotor.set(voltPct);
    }

    @Override
    public void periodic() {
        SmartDashboard.putNumber("Intake Speed%", intakeMotor.get());
    }
}
