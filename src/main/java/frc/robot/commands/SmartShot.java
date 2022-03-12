package frc.robot.commands;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.*;

public class SmartShot extends CommandBase {
    private Drivetrain drive;
    private Shooter shooter;
    private Pi pi;
    private Intake intake;
    private Turret turret;

    public SmartShot(Drivetrain drive, Shooter shooter, Pi pi, Intake intake, Turret turret) {
        this.drive = drive;
        this.shooter = shooter;
        this.pi = pi;
        this.intake = intake;
        this.turret = turret;

        addRequirements(drive);
        addRequirements(shooter);
    }

    public void execute() {
        shooter.calcShot();
        String error = "";

        //check hood angle is more than 3* off
        shooter.setHoodAngle(shooter.getTargetHoodAngle());
        if(Math.abs(shooter.getHoodAngle()-shooter.getTargetHoodAngle()) > 0.5)
        {
            //TODO: turned off hood since it's broke
            error = String.join(error, "Hood ");
        }

        //check shot speed is within 30 RPM
        shooter.setShooterRpm(shooter.getTargetRpm());
        if(Math.abs(shooter.getShooterVelocity()-shooter.getTargetRpm()) > 20)
        {
            error = String.join(error, "RPM ");
        }

        //check if PI saw target
        if(pi.getCenterX() > 0) {
            if(Pi.getTargetMoveLeft()) {
                error = String.join(error, "TurnL ");
                //left is positive turn
                turret.setTurretSpeed(0.08);
            } else if (Pi.getTargetMoveRight()) {
                error = String.join(error, "TurnR ");
                turret.setTurretSpeed(-0.08);
            } else {
                //robot centered, stop driving
                turret.setTurretSpeed(0);
            }
        } else {
            //pi is not seeing hub
            //TODO: Rumble driver controller?
            error = String.join(error, "Vision ");
            turret.setTurretSpeed(0);
        }

        //check for driving (0.15m/s == 6in/s)
        if(Math.abs(drive.getVelocity(0)) > 0.15) {
            error = String.join(error, "Driving ");
            //driving might be because of centering, so don't stop it
        } 

        if(error.length() ==0) {
            //TODO: SHOOT!!!
            error = "SHOOT!!!";
            intake.setIntake(intake.INTAKE_SPEED);
            intake.setUpMotor(intake.UP_SPEED);
        }
        SmartDashboard.putString("Auto Shoot Error", error);
    }
}
