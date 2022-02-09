package frc.robot;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class Climber extends SubsystemBase 
{
    private CANSparkMax middleClimb;
    private CANSparkMax reachClimb;

    public Climber() {
        middleClimb = new CANSparkMax(20,MotorType.kBrushless);
        middleClimb.setIdleMode(IdleMode.kBrake);

        reachClimb = new CANSparkMax(22,MotorType.kBrushless);
        reachClimb.setIdleMode(IdleMode.kBrake);
    }

    @Override
    public void periodic() {
        SmartDashboard.putNumber("Middle Climb Position", getMiddleClimbPosition());
        SmartDashboard.putNumber("Reach Climb Position", getReachClimbPosition());
    }

    public void setMiddleClimbPower(double pct) {
        middleClimb.set(pct);
    }

    public void setReachClimbPower(double pct) {
        reachClimb.set(pct);
    }

    public double getMiddleClimbPosition() {
        return middleClimb.getEncoder().getPosition();
    }

    public double getReachClimbPosition() {
        return reachClimb.getEncoder().getPosition();
    }
}
