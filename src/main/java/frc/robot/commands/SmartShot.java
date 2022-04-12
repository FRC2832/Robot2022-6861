package frc.robot.commands;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.*;
import frc.robot.Intake.CargoColor;

public class SmartShot extends CommandBase {
    private final short STOPPED_TIME = 15;  //takes this many * 20ms to be considered stopped
    private Drivetrain drive;
    private Shooter shooter;
    private Pi pi;
    private Intake intake;
    private Turret turret;
    private byte counts;
    private short forceAutoShot;
    private boolean lastShot;
    private short stoppedCounts;

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
        Robot.snapHub("START");
    }

    public void execute() {
        String error = "";

        //check hood angle is more than 3* off
        shooter.setHoodAngle(shooter.getTargetHoodAngle());
        if(Math.abs(shooter.getHoodAngle()-shooter.getTargetHoodAngle()) > 0.5)
        {
            error = String.join(error, "Hood ");
        }

        //check shot speed is within 30 RPM
        shooter.setShooterRpm(shooter.getTargetRpm());
        if(Math.abs(shooter.getShooterVelocity()-shooter.getTargetRpm()) > 20)
        {
            error = String.join(error, "RPM ");
        }

        //check if PI saw target
        double turretAim = turret.getTurretAimAngle();
        if(turretAim > 0) {
            turret.setTurretPosition(turretAim);
        } else {
            turret.setTurretSpeed(0);
        }
        if(pi.canSeeHub()) {
            if(pi.centeredOnHub()) {
                //pass condition, do nothing
            } else {
                error = String.join(error, "Turret ");
            }
        } else {
            //pi is not seeing hub
            error = String.join(error, "Vision ");
        }

        //check for driving (0.15m/s == 6in/s)
        drive.driveMechanumTank(0, 0, 0, 0);
        boolean isStopped = (Math.abs(drive.getVelocity(0)) < 0.15);
        if(isStopped) {
            stoppedCounts = (short)Math.min(stoppedCounts++,STOPPED_TIME + 3);  //add a 60ms buffer for noise
        } else {
            stoppedCounts = (short)Math.max(stoppedCounts--,0);
        }
        if (stoppedCounts < STOPPED_TIME) {
            error = String.join(error, "Driving ");
            //driving might be because of centering, so don't stop it
        } else {
            //at counts, allow shot
        }

        if(DriverStation.isAutonomous()) {
            forceAutoShot++;
        }

        if(error.length() ==0 || forceAutoShot > 300) {
            error += "SHOOT!!!";
            intake.setIntake(intake.INTAKE_SPEED);
            intake.setUpMotor(intake.UP_SPEED);
            if(lastShot == false) {
            	Robot.snapHub("SHOT");
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
            return (counts > 50);
        }
        else {
            return false;
        }
    }

}
