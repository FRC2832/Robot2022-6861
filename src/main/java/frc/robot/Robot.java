// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.util.datalog.DataLog;
import edu.wpi.first.util.datalog.DoubleLogEntry;
import edu.wpi.first.wpilibj.DataLogManager;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PowerDistribution;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.*;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;
import frc.robot.commands.*;

/**
 * This is a demo program showing how to use Mecanum control with the
 * MecanumDrive class.
 */
public class Robot extends TimedRobot {
    private Drivetrain drive;
    private Intake intake;
    private Shooter shooter;
    private Climber climber;
    private Pi pi;
    private Turret turret;
	
    private boolean lastEnabled = false;
	
    private static Simulation sim;
    private Joystick leftStick;
    private Joystick rightStick;
    private XboxController operatorController;
    private SendableChooser<Command> m_chooser;
    private PowerDistribution pdp;
    private DoubleLogEntry[] pdpChannels;
    private DoubleLogEntry pdpBatteryVoltage;

    @Override
    public void robotInit() {
		ShooterConstants.LoadConstants();
		pi = new Pi();
		leftStick = new Joystick(0);
        rightStick = new Joystick(1);
        operatorController = new XboxController(2);

		//initialize subsystems
        drive = new Drivetrain();
        drive.register();
        drive.setDefaultCommand(new DriveStick(drive,leftStick,rightStick));
        intake = new Intake();
        intake.register();
        intake.setDefaultCommand(new DriveIntake(intake, operatorController));
        shooter = new Shooter(pi);
        shooter.setDefaultCommand(new DriveHood(shooter,operatorController));
        climber = new Climber();
        climber.register();
        climber.setDefaultCommand(new DriveClimber(climber,leftStick,rightStick));
        turret = new Turret();
        turret.register();
        turret.setDefaultCommand(new DriveTurret(turret,operatorController));

        //move color sensor read to seperate thread since it sometimes locks up
        this.addPeriodic(() -> {
            intake.updateColorSensor();
        }, 0.02, 0.005);

        JoystickButton triggerButton = new JoystickButton(rightStick, 1);  //1 = trigger
        triggerButton.whileActiveContinuous(new SmartIntake(intake));

        JoystickButton lbButton = new JoystickButton(operatorController, 5);  //5 = left bumper button
        lbButton.whileActiveContinuous(new LowShot(shooter,intake));

        JoystickButton selectButton = new JoystickButton(operatorController, 7);  //7 = select button
        selectButton.whileActiveContinuous(new DashboardShoot(shooter));
		
		JoystickButton startButton = new JoystickButton(operatorController, 8);  //8 = start button
        startButton.whileActiveContinuous(new SmartShot(drive,shooter,pi,intake,turret));

        // this.setNetworkTablesFlushEnabled(true); //turn off 20ms Dashboard update
        // rate
        LiveWindow.setEnabled(false);

        // Starts recording to data log
        DataLogManager.start();
        DataLog log = DataLogManager.getLog();
        // Record both DS control and joystick data
        DriverStation.startDataLog(DataLogManager.getLog());
        pdp = new PowerDistribution();
        pdpChannels = new DoubleLogEntry[pdp.getNumChannels()];
        pdpBatteryVoltage = new DoubleLogEntry(log, "/pdp/Vbat");
        for(int i=0; i<pdpChannels.length;i++){
            pdpChannels[i] = new DoubleLogEntry(log, "/pdp/channel" + i);
        }
        
        //ParallelCommandGroup must wait till ALL finish, 
        //ParallelRace waits for FIRST to finish, 
        //Deadline waits for the specified command to finish
        SequentialCommandGroup backUpShoot = new SequentialCommandGroup(
            new ParallelRaceGroup(
                new AutoDrive(drive,2.0),
                new SmartIntake(intake),
                new AutoTurret(turret, 50.)
            ),
            new AutoTurn(drive, 110),
            new SmartShot(drive,shooter,pi,intake,turret)
        );

        SequentialCommandGroup grab3 = new SequentialCommandGroup(
            new ParallelRaceGroup(
                new AutoDrive(drive,1.8),
                new SmartIntake(intake)
            ),
            new AutoDriveDiagonal(drive, 0.46, -0.385, 0.5),  //should be 60% power
            new SmartShot(drive,shooter,pi,intake,turret),
            new AutoTurn(drive, 110),
            new ParallelRaceGroup(
                new AutoDrive(drive,1.0),
                new SmartIntake(intake)
            )
        );

        // A chooser for autonomous commands
        m_chooser = new SendableChooser<>();

        // Add commands to the autonomous command chooser
        m_chooser.setDefaultOption("Back Up and Shoot", backUpShoot);
        m_chooser.addOption("Grab 3", grab3);

        // Put the chooser on the dashboard
        SmartDashboard.putData(m_chooser);
    }

    @Override
    public void disabledInit() {
        drive.setBrakeMode(false);
    }

    @Override
    public void autonomousInit() {
        drive.setBrakeMode(true);
        CommandScheduler.getInstance().cancelAll();

        Command m_autonomousCommand = m_chooser.getSelected();

        // schedule the autonomous command (example)
        if (m_autonomousCommand != null) {
            m_autonomousCommand.schedule();
        }
    }

    @Override
    public void autonomousPeriodic() {

    }

    @Override
    public void teleopInit() {
        drive.setBrakeMode(true);
        CommandScheduler.getInstance().cancelAll();
        SmartDashboard.putNumber("Target RPM", ShooterConstants.DEFAULT_SHOT_RPM);
    }

    @Override
    public void teleopPeriodic() {
    }

    @Override
    public void testInit() {
        teleopInit();
    }

    @Override
    public void testPeriodic() {
        teleopPeriodic();
    }

    @Override
    public void robotPeriodic() {
        CommandScheduler.getInstance().run();
		
		// automatically turn on/off recording
        if (lastEnabled != isEnabled()) {
            // we know the enabled status changed
            if (lastEnabled == false) {
                // robot started, start recording
                Shuffleboard.startRecording();
            } else {
                // robot stopped, stop recording
                Shuffleboard.stopRecording();
            }
        }
        // save the result for next loop
        lastEnabled = isEnabled();

        pi.sendAlliance();
		pi.processCargo();
        pi.processTargets();
        shooter.calcShot();
        SmartDashboard.putNumber("Vision CenterX", pi.getCenterX());
        SmartDashboard.putNumber("Vision CenterY", pi.getCenterY());

        //log battery voltage
        pdpBatteryVoltage.append(pdp.getVoltage());
        for(int i=0; i<pdpChannels.length;i++){
            pdpChannels[i].append(pdp.getCurrent(i));
        }
    }

    @Override
    public void simulationInit() {
        sim = new Simulation(drive);
        sim.init();
    }

    @Override
    public void simulationPeriodic() {
        sim.periodic();
    }

    public static Simulation getSim() {
        return sim;
    }
}
