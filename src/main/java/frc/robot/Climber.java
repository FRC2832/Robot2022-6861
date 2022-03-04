package frc.robot;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class Climber extends SubsystemBase 
{
    public static final double MIDDLE_CLIMB_SPEED = 0.5;
    public static final double REACH_CLIMB_SPEED = 0.5;

    private CANSparkMax middleClimb;
    private CANSparkMax leftClimb;
    private CANSparkMax rightClimb;

    public Climber() {
        middleClimb = new CANSparkMax(32,MotorType.kBrushless);
        middleClimb.setIdleMode(IdleMode.kBrake);
        middleClimb.setInverted(true);

        leftClimb = new CANSparkMax(33,MotorType.kBrushless);
        leftClimb.setIdleMode(IdleMode.kBrake);

        rightClimb = new CANSparkMax(22,MotorType.kBrushless);
        rightClimb.setIdleMode(IdleMode.kBrake);
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
        leftClimb.set(pct);
        rightClimb.set(pct);
    }

    public double getMiddleClimbPosition() {
        return middleClimb.getEncoder().getPosition();
    }

    public double getReachClimbPosition() {
        return leftClimb.getEncoder().getPosition();
    }
}
