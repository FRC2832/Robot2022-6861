package frc.robot;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class Turret extends SubsystemBase {
    private CANSparkMax turretMotor;

    public Turret() {
        turretMotor = new CANSparkMax(30,MotorType.kBrushless);
    }

    public void setTurretSpeed(double voltPct) {
        turretMotor.set(voltPct);
    }
}
