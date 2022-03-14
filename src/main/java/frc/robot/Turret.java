package frc.robot;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMax.SoftLimitDirection;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class Turret extends SubsystemBase {
    private CANSparkMax turretMotor;

    public Turret() {
        turretMotor = new CANSparkMax(30,MotorType.kBrushless);
        turretMotor.getEncoder().setPositionConversionFactor(180./108.);
        turretMotor.setSoftLimit(SoftLimitDirection.kReverse, 0);
        turretMotor.setSoftLimit(SoftLimitDirection.kForward, 180);
        turretMotor.enableSoftLimit(SoftLimitDirection.kReverse, true);
        turretMotor.enableSoftLimit(SoftLimitDirection.kForward, true);
        turretMotor.setIdleMode(IdleMode.kBrake);
    }

    public void setTurretSpeed(double voltPct) {
        turretMotor.set(voltPct);
    }

    public double getAngle() {
        return turretMotor.getEncoder().getPosition();
    }

    @Override
    public void periodic() {
        SmartDashboard.putNumber("Turret Encoder", getAngle());
    }
}
