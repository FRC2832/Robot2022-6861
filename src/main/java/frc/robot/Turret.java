package frc.robot;

import com.revrobotics.CANSparkMax;
import com.revrobotics.SparkMaxPIDController;
import com.revrobotics.CANSparkMax.ControlType;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMax.SoftLimitDirection;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class Turret extends SubsystemBase {
    private CANSparkMax turretMotor;
    private SparkMaxPIDController pid;
    private double lastCenterX;
    private Pi pi;
    private double aimAngle;
    public final double SAFE_CLIMB_ANGLE = 170;

    public Turret(Pi pi) {
        this.pi = pi;
        turretMotor = new CANSparkMax(30,MotorType.kBrushless);
        turretMotor.getEncoder().setPositionConversionFactor(180./108.);
        turretMotor.getEncoder().setVelocityConversionFactor(1.0/31);
        turretMotor.setSoftLimit(SoftLimitDirection.kReverse, 0);
        turretMotor.setSoftLimit(SoftLimitDirection.kForward, 180);
        turretMotor.enableSoftLimit(SoftLimitDirection.kReverse, true);
        turretMotor.enableSoftLimit(SoftLimitDirection.kForward, true);
        turretMotor.setIdleMode(IdleMode.kBrake);

        pid = turretMotor.getPIDController();
        // set PID coefficients
        pid.setP(0.012);
        pid.setI(2e-6); //1e-4
        pid.setD(3); //1
        pid.setIZone(45);
        pid.setFF(0);
        pid.setOutputRange(-0.3, 0.3);

        final double MAX_SPEED = 10;    //degrees/sec
        int smartMotionSlot = 0;
        pid.setSmartMotionMaxVelocity(MAX_SPEED, smartMotionSlot);
        pid.setSmartMotionMinOutputVelocity(0, smartMotionSlot);
        pid.setSmartMotionMaxAccel(MAX_SPEED*10, smartMotionSlot);
        pid.setSmartMotionAllowedClosedLoopError(2, smartMotionSlot);
    }

    public void setTurretSpeed(double voltPct) {
        turretMotor.set(voltPct);
    }

    public void setTurretPosition(double pos) {
        pid.setReference(pos, ControlType.kPosition);
        SmartDashboard.putNumber("Turret SetPos", pos);
    }
    public double getAngle() {
        return turretMotor.getEncoder().getPosition();
    }

    public boolean resetClimber() {
        return getAngle() < SAFE_CLIMB_ANGLE;
    }

    public void updateTurretAimAngle() {
        //check if PI saw target
        double centerX = pi.getCenterX();
        if(pi.canSeeHub()) {
            //if this many pixels off from center, fix it
            if(!pi.centeredOnHub()) {
                if (Math.abs(centerX - lastCenterX) > 1e-4) {
                    //value changed, update the pid
                    var delta =  (pi.TARGET_CENTER_X - centerX);
                    double newAngle = getAngle();
                    newAngle += delta / (pi.CAM_X_RES/90);  //90 is Field of View of the camera
                    aimAngle = newAngle;
                }
            } else {
                aimAngle = -1;
            }
        } else {
            //pi is not seeing hub
            aimAngle = 90;
        }
        lastCenterX = centerX;
    }

    public double getTurretAimAngle() {
        return aimAngle;
    }

    @Override
    public void periodic() {
        SmartDashboard.putNumber("Turret Encoder", getAngle());
        SmartDashboard.putNumber("Turret Velocity", turretMotor.getEncoder().getVelocity());
    }
}
