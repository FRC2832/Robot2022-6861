package frc.robot.commands;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.*;
import frc.robot.Intake.CargoColor;

public class SmartShot extends CommandBase {
    private Drivetrain drive;
    private Shooter shooter;
    private Pi pi;
    private Intake intake;
    private Turret turret;
    private byte counts;
    private short forceAutoShot;
    private boolean lastShot;
    private double lastCenterX;

    public SmartShot(Drivetrain drive, Shooter shooter, Pi pi, Intake intake, Turret turret) {
        this.drive = drive;
        this.shooter = shooter;
        this.pi = pi;
        this.intake = intake;
        this.turret = turret;
        lastShot = false;

        addRequirements(drive);
        addRequirements(shooter);
        addRequirements(turret);
    }

    @Override
    public void initialize() {
        //get the FL wheel distance when we start
        counts = 0;
        forceAutoShot = 0;
        Snapshot.TakeSnapshot("START");
    }

    public void execute() {
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
        double centerX = pi.getCenterX();
        if(pi.canSeeHub()) {
            //if this many pixels off from center, fix it
            if(!pi.centeredOnHub()) {
                if (Math.abs(centerX - lastCenterX) > 1e-4) {
                    //value changed, update the pid
                    var delta =  (pi.TARGET_CENTER_X - centerX);
                    double newAngle = turret.getAngle();
                    newAngle += delta / (pi.CAM_X_RES/90);  //90 is Field of View of the camera
                    turret.setTurretPosition(newAngle);
                }
                error = String.join(error, "Turret ");
            } else {
                turret.setTurretSpeed(0);
            }
        } else {
            //pi is not seeing hub
            //TODO: Rumble driver controller?
            error = String.join(error, "Vision ");
            turret.setTurretPosition(90);
        }
        lastCenterX = centerX;

        //check for driving (0.15m/s == 6in/s)
        drive.driveMechanumTank(0, 0, 0, 0);
        if(Math.abs(drive.getVelocity(0)) > 0.15) {
            error = String.join(error, "Driving ");
            //driving might be because of centering, so don't stop it
        } 

        if(DriverStation.isAutonomous()) {
            forceAutoShot++;
        }

        if(error.length() ==0 || forceAutoShot > 300) {
            error += "SHOOT!!!";
            intake.setIntake(intake.INTAKE_SPEED);
            intake.setUpMotor(intake.UP_SPEED);
            if(lastShot == false) {
                Snapshot.TakeSnapshot("SHOT");
            }
            lastShot = true;
        } else {
            lastShot = false;
        }
        SmartDashboard.putString("Auto Shoot Error", error);

        //count how long we are empty
        if(intake.getColorSensor() == CargoColor.Unknown) {
            counts++;
        } else {
            counts = 0;
        }
    }

    @Override
    public boolean isFinished() {
        if(DriverStation.isAutonomous()) {
            SmartDashboard.putNumber("SmartShot Counts", counts);
            //50 counts = 1 second
            return (counts > 120);
        }
        else {
            return false;
        }
    }

}
