package frc.robot;

import com.revrobotics.CANSparkMax;
import com.revrobotics.SparkMaxPIDController;
import com.revrobotics.CANSparkMax.ControlType;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMax.SoftLimitDirection;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class Climber extends SubsystemBase 
{
    public static final double MIDDLE_CLIMB_SPEED = 0.5;
    public static final double REACH_CLIMB_SPEED = 0.75;
    public static final float CLIMB_BOTTOM = 34.4f;
    public static final float CLIMB_START = 42.125f;
    public static final float CLIMB_TOP = 63.625f;
    public static final float CLIMB_STEP = 0.4f;

    private static final int WARN_CURRENT_COUNTS = 50*3;  //50 loops per second, 55 seconds
    private static final int DROP_CURRENT_COUNTS = 50*60;

    private CANSparkMax middleClimb;
    private CANSparkMax leftClimb;
    private CANSparkMax rightClimb;
    private SparkMaxPIDController pid;

    private static int currentCounts;
    private boolean climbRequested;

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
        middleClimb.enableSoftLimit(SoftLimitDirection.kReverse, false);
        middleClimb.enableSoftLimit(SoftLimitDirection.kForward, true);
        pid = middleClimb.getPIDController();
        pid.setP(8);
        pid.setI(0.);
        pid.setD(0.);
        pid.setFF(0.);
        //stall current 80a, run limit 20a
        middleClimb.setSmartCurrentLimit(80, 20);
        currentCounts = 0;
        climbRequested = false;

        leftClimb = new CANSparkMax(33,MotorType.kBrushless);
        leftClimb.setIdleMode(IdleMode.kBrake);

        rightClimb = new CANSparkMax(22,MotorType.kBrushless);
        rightClimb.setIdleMode(IdleMode.kBrake);
    }

    @Override
    public void periodic() {
        SmartDashboard.putNumber("Middle Climb Position", getMiddleClimbPosition());
        SmartDashboard.putNumber("Reach Climb Position", getReachClimbPosition());

        if (middleClimb.getOutputCurrent() > 6) {
            currentCounts++;
        }
    }

    public void resetClimberWarning() {
        currentCounts = 0;
    }

    public static boolean isClimberWarning() {
        return (WARN_CURRENT_COUNTS < currentCounts) && (currentCounts < DROP_CURRENT_COUNTS);
    }

    public void setMiddleClimbPower(double pct) {
        middleClimb.set(pct);
    }

    public void setMiddleClimbPosition(double position) {
        if(DriverStation.isFMSAttached() || currentCounts < DROP_CURRENT_COUNTS) {
            pid.setReference(position, ControlType.kPosition);
        } else {
            //current limit hit, drop robot slowly
            setMiddleClimbPower(0);
        }
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

    public void setClimbRequested(boolean value) {
        climbRequested = value;
    }

    public boolean climbRequested(){
        return climbRequested;
    }
}
