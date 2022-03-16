package frc.robot;

import com.revrobotics.CANSparkMax;
import com.revrobotics.SparkMaxPIDController;
import com.revrobotics.CANSparkMax.ControlType;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMax.SoftLimitDirection;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class Climber extends SubsystemBase 
{
    public static final double MIDDLE_CLIMB_SPEED = 0.5;
    public static final double REACH_CLIMB_SPEED = 0.5;
    public static final float CLIMB_BOTTOM = 34.4f;
    public static final float CLIMB_START = 42.125f;
    public static final float CLIMB_TOP = 62.1f;
    public static final float CLIMB_STEP = 0.2f;

    private CANSparkMax middleClimb;
    private CANSparkMax leftClimb;
    private CANSparkMax rightClimb;
    private SparkMaxPIDController pid;

    public Climber() {
        middleClimb = new CANSparkMax(32,MotorType.kBrushless);
        middleClimb.setIdleMode(IdleMode.kBrake);
        middleClimb.setInverted(true);
        //20" of travel = 84.66 counts
        middleClimb.getEncoder().setPositionConversionFactor(20./84.66);
        //starting position is 42.125"
        middleClimb.getEncoder().setPosition(CLIMB_START);
        //lowest climber will go
        middleClimb.setSoftLimit(SoftLimitDirection.kReverse, 33.5f);
        //high limit is 66", so we set it lower just in case
        middleClimb.setSoftLimit(SoftLimitDirection.kForward, 64.5f);
        middleClimb.enableSoftLimit(SoftLimitDirection.kReverse, true);
        middleClimb.enableSoftLimit(SoftLimitDirection.kForward, true);
        pid = middleClimb.getPIDController();
        pid.setP(8);
        pid.setI(0.);
        pid.setD(0.);
        pid.setFF(0.);
        //stall current 80a, run limit 20a
        middleClimb.setSmartCurrentLimit(80, 20);

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

    public void setMiddleClimbPosition(double position) {
        pid.setReference(position, ControlType.kPosition);
    }

    public void setReachClimbPower(double pct) {
        leftClimb.set(pct);
        rightClimb.set(-pct);
    }

    public double getMiddleClimbPosition() {
        return middleClimb.getEncoder().getPosition();
    }

    public double getReachClimbPosition() {
        return leftClimb.getEncoder().getPosition();
    }
}
