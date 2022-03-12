package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Intake;
import frc.robot.Shooter;
import frc.robot.ShooterConstants;

public class LowShot extends CommandBase {
    private Shooter shooter;
    private Intake intake;

    public LowShot(Shooter shooter,Intake intake) {
        this.shooter = shooter;
        this.intake = intake;
        addRequirements(shooter);
        addRequirements(intake);
    }
    
    @Override
    public void execute() {
        shooter.setShooterRpm(ShooterConstants.LOW_SHOT_RPM);
        shooter.setHoodAngle(ShooterConstants.LOW_SHOT_ANGLE);

        if (  Math.abs(shooter.getShooterVelocity() - ShooterConstants.LOW_SHOT_RPM) < ShooterConstants.LOW_SHOT_RPM_ERROR
           && Math.abs(shooter.getHoodAngle() - ShooterConstants.LOW_SHOT_ANGLE) < ShooterConstants.LOW_SHOT_ANGLE_ERROR
           )
        {
            intake.setUpMotor(intake.UP_SPEED);
            intake.setIntake(0);
        }
        else {
            intake.setUpMotor(0);
            intake.setIntake(0);
        }
    }
}
