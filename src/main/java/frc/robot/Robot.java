// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.ParallelRaceGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
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
	
    private boolean lastEnabled = false;
	
    private static Simulation sim;
    private Joystick leftStick;
    private Joystick rightStick;
    private XboxController operatorController;
    private SendableChooser<Command> m_chooser;

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
        intake.setDefaultCommand(new DriveIntake(intake, rightStick, operatorController));
        shooter = new Shooter(pi);
        shooter.setDefaultCommand(new NoShoot(shooter));
        climber = new Climber();
        climber.register();
        climber.setDefaultCommand(new DriveClimber(climber,leftStick,rightStick));

		JoystickButton selectButton = new JoystickButton(leftStick, 7);  //7 = select button
        selectButton.whileActiveContinuous(new DashboardShoot(shooter));
		
		JoystickButton startButton = new JoystickButton(leftStick, 8);  //7 = select button
        startButton.whileActiveContinuous(new AutoShoot(drive,shooter,pi));

        // this.setNetworkTablesFlushEnabled(true); //turn off 20ms Dashboard update
        // rate
        LiveWindow.setEnabled(false);

        sim = new Simulation(drive);

        //ParallelGroup must wait till ALL finish, ParallelRace waits for FIRST to finish, Deadline waits for the specified command to finish
        SequentialCommandGroup backUpShoot = new SequentialCommandGroup(
            new AutoDrive(drive,1.7),
            new AutoShoot(drive,shooter,pi),
            new AutoDrive(drive,1)
        );

        SequentialCommandGroup grab3 = new SequentialCommandGroup(
            new ParallelRaceGroup(
                new AutoDrive(drive,1.8),
                new IntakeBall(intake)
            ),
            new AutoDriveDiagonal(drive, 0.46, -0.385, 0.5),  //should be 60% power
            new AutoShoot(drive,shooter,pi),
            new AutoTurn(drive, 110),
            new ParallelRaceGroup(
                new AutoDrive(drive,1.0),
                new IntakeBall(intake)
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
    }

    @Override
    public void autonomousInit() {
        CommandScheduler.getInstance().cancelAll();
        //rehome hood if needed
        CommandScheduler.getInstance().schedule(new HomeHood(shooter));

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
    public void teleopPeriodic() {
    }

    @Override
    public void teleopInit() {
        CommandScheduler.getInstance().cancelAll();
        //rehome hood if needed
        CommandScheduler.getInstance().schedule(new HomeHood(shooter));
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
    }

    @Override
    public void simulationInit() {
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
