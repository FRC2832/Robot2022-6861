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

    public Turret() {
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
        pid.setI(5e-6); //1e-4
        pid.setD(0.2); //1
        pid.setIZone(7);
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
        return getAngle() < 170;
    }

    @Override
    public void periodic() {
        SmartDashboard.putNumber("Turret Encoder", getAngle());
        SmartDashboard.putNumber("Turret Velocity", turretMotor.getEncoder().getVelocity());
    }
}
