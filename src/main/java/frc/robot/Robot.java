// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.util.Color;
import edu.wpi.first.wpilibj2.command.*;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;
import frc.robot.Intake.CargoColor;
import frc.robot.LightDrive.LightDrivePWM;
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
	
    private static Simulation sim;
    private Joystick leftStick;
    private Joystick rightStick;
    private XboxController operatorController;
    private SendableChooser<Command> m_chooser;
    private LightDrivePWM ldrive;
    private REVDigitBoard digit;
    private DataLogging datalog;
    private boolean lastLsPressed;
    private static boolean autoShotEnabled;
    private static Snapshot snapHub;
    private static Snapshot snapCargo;

    @Override
    public void robotInit() {
        GitVersion vers = GitVersion.loadVersion();
        vers.printVersions();
        snapHub = new Snapshot();
        snapHub.start("http://10.68.61.8:1181/stream.mjpg");
        snapCargo = new Snapshot();
        snapCargo.start("http://10.68.61.8:1182/stream.mjpg");
        SmartDashboard.putBoolean("Take Snapshot", false);

        ShooterConstants.LoadConstants();
		pi = new Pi();
		leftStick = new Joystick(0);
        rightStick = new Joystick(1);
        operatorController = new XboxController(2);
        autoShotEnabled = false;

		//initialize subsystems
        drive = new Drivetrain();
        drive.register();
        drive.setDefaultCommand(new DriveStick(drive,leftStick,rightStick));
        shooter = new Shooter(pi);
        shooter.setDefaultCommand(new DriveHood(shooter,operatorController));
        intake = new Intake();
        intake.register();
        intake.setDefaultCommand(new DriveIntake(intake, shooter, operatorController));
        turret = new Turret(pi);
        turret.register();
        turret.setDefaultCommand(new DriveTurret(turret,operatorController));
        climber = new Climber();
        climber.register();
        climber.setDefaultCommand(new DriveClimber(climber,turret,leftStick,rightStick));

        //Initialize a new PWM LightDrive
		ldrive = new LightDrivePWM(new Servo(7), new Servo(8));
        digit = new REVDigitBoard();
        datalog = new DataLogging();
        datalog.Init();

        JoystickButton triggerButton = new JoystickButton(rightStick, 1);  //1 = trigger
        triggerButton.whileActiveContinuous(new SmartIntake(intake));

        JoystickButton aButton = new JoystickButton(operatorController, 1);  //1 = A button
        aButton.whileActiveContinuous(new ManualShot(shooter,ShooterConstants.DEFAULT_SHOT_RPM,ShooterConstants.DEFAULT_SHOT_ANGLE));

        JoystickButton bButton = new JoystickButton(operatorController, 2);  //1 = B button
        bButton.whileActiveContinuous(new ManualShot(shooter,2550,40));

        JoystickButton yButton = new JoystickButton(operatorController, 4);  //1 = B button
        yButton.whileActiveContinuous(new ManualShot(shooter,1900,18));

        JoystickButton lbButton = new JoystickButton(operatorController, 5);  //5 = left bumper button
        lbButton.whileActiveContinuous(new LowShot(shooter,intake));

        JoystickButton selectButton = new JoystickButton(operatorController, 7);  //7 = select button
        selectButton.whileActiveContinuous(new DashboardShoot(shooter));
		
		JoystickButton startButton = new JoystickButton(operatorController, 8);  //8 = start button
        startButton.whileActiveContinuous(new SmartShot(drive,shooter,pi,intake,turret));

        LiveWindow.setEnabled(false);
        
        //ParallelCommandGroup must wait till ALL finish, 
        //ParallelRace waits for FIRST to finish, 
        //Deadline waits for the specified command to finish
        SequentialCommandGroup backUpShoot = new SequentialCommandGroup(
            new ParallelDeadlineGroup(
                new AutoDrive(drive,2.0),
                new SmartIntake(intake),
                new AutoTurret(turret, 70.)
            ),
            new ParallelDeadlineGroup(
                new AutoTurn(drive, -160),
                new AutoTurret(turret, 70.)
            ),
            new SmartShot(drive,shooter,pi,intake,turret)
        );

        SequentialCommandGroup shortWall = new SequentialCommandGroup(
            new ParallelDeadlineGroup(
                new SlowDrive(drive,1.45),
                new SmartIntake(intake),
                new AutoTurret(turret, 70.)
            ),
            new SlowDrive(drive,-0.2),
            new ParallelDeadlineGroup(
                new AutoTurn(drive, -160),
                new AutoTurret(turret, 70.),
                new SmartIntake(intake)
            ),
            new SmartShot(drive,shooter,pi,intake,turret)
        );

        SequentialCommandGroup grab3 = new SequentialCommandGroup(
            new ParallelDeadlineGroup(
                new SlowDrive(drive,1.45),
                new SmartIntake(intake),
                new AutoTurret(turret, 20.)
            ),
            new SlowDrive(drive,-0.2),
            new ParallelDeadlineGroup(
                new AutoTurn(drive, 130),
                new AutoTurret(turret, 20.),
                new SmartIntake(intake)
            ),
            new SmartShot(drive,shooter,pi,intake,turret),
            new AutoTurn(drive, -15),
            new AutoDrive(drive, 1.0),
            new ParallelDeadlineGroup(
                new AimDrive(drive,pi,2.0),
                new AutoTurret(turret, 30.),
                new SmartIntake(intake)
            ),
            new ParallelDeadlineGroup(
                new AutoTurn(drive, 120),
                new SmartIntake(intake)
            ),
            new SmartShot(drive,shooter,pi,intake,turret)
        );

        SequentialCommandGroup grab4 = new SequentialCommandGroup(
            new ParallelRaceGroup(
                new AutoDrive(drive,1.8),
                new SmartIntake(intake),
                new AutoTurret(turret, 140.)
            ),
            new ParallelRaceGroup(
                new AutoTurn(drive, -80),
                new AutoTurret(turret, 140.)
            ),
            new AutoTurret(turret, 135.),
            new SmartShot(drive,shooter,pi,intake,turret),
            new AutoDrive(drive,3.5),
            new AutoTurn(drive, 10),
            //drive into ball
            new AimDrive(drive, pi, 3.2),
            new ParallelRaceGroup(
                new SmartIntake(intake),
                new WaitCommand(2)
            ),
            new AutoDrive(drive,-3.0),
            new AutoTurn(drive, -110),
            new AutoTurret(turret, 90.),
            new SmartShot(drive,shooter,pi,intake,turret)
        );

        // A chooser for autonomous commands
        m_chooser = new SendableChooser<>();

        // Add commands to the autonomous command chooser
        m_chooser.setDefaultOption("Back Up and Shoot", backUpShoot);
        m_chooser.addOption("Short Wall", shortWall);
        m_chooser.addOption("Grab 3", grab3);
        m_chooser.addOption("Grab 4", grab4);

        // Put the chooser on the dashboard
        SmartDashboard.putData(m_chooser);
    }

    @Override
    public void disabledInit() {
        drive.setBrakeMode(false);
        climber.resetClimberWarning();
        autoShotEnabled = false;
    }

    @Override
    public void autonomousInit() {
        drive.setBrakeMode(true);
        autoShotEnabled = true;
        CommandScheduler.getInstance().cancelAll();

        Command m_autonomousCommand = m_chooser.getSelected();

        // schedule the autonomous command (example)
        if (m_autonomousCommand != null) {
            m_autonomousCommand.schedule();
        }
    }

    @Override
    public void autonomousPeriodic() {
        datalog.StartLoopTime();
    }

    @Override
    public void teleopInit() {
        drive.setBrakeMode(true);
        CommandScheduler.getInstance().cancelAll();
        SmartDashboard.putNumber("Target RPM", ShooterConstants.DEFAULT_SHOT_RPM);
    }

    @Override
    public void teleopPeriodic() {
        datalog.StartLoopTime();
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
        //update these before schedule to make sure data is fresh
        shooter.calcShot();
        turret.updateTurretAimAngle();

        CommandScheduler.getInstance().run();

        pi.sendAlliance();
		pi.processCargo();
        pi.processTargets();
        SmartDashboard.putNumber("Vision CenterX", pi.getCenterX());
        SmartDashboard.putNumber("Vision CenterY", pi.getCenterY());

        boolean takeSnap = SmartDashboard.getBoolean("Take Snapshot", false);
        if(takeSnap) {
            SmartDashboard.putBoolean("Take Snapshot", false);
            snapHub("MANUAL");
        }

        //run lights
        CargoColor color = intake.getColorSensor();
        Color output;
        if(intake.intakeFull()) {
            output = Color.kWhite;
        } else if (color == CargoColor.Blue) {
            output = Color.kBlue;
        } else if (color == CargoColor.Red) {
            output = Color.kRed;
        } else {
            output = Color.kBlack;
        }
        ldrive.SetColor(1, output);
        ldrive.SetColor(2, output);

        String msg;
        if (DriverStation.isDisabled() && !shooter.hoodBottom()) {
            output = Color.kCyan;
            msg = "HOOD";
        } else if (DriverStation.isDisabled() && intake.getColorSensor() == CargoColor.Unknown) {
            output = Color.kPurple;
            msg = "BALL";
        } else if (pi.piOn() == false) {
            output = Color.kChartreuse;
            msg = " PI ";
        } else if (pi.canSeeHub() == false) {
            output = Color.kRed;
            msg = "MISS";
        } else if (pi.centeredOnHub() == false) {
            output = Color.kYellow;
            msg = "TURN";
        } else {
            output = Color.kGreen;
            msg = "SHOT";
        }
        ldrive.SetColor(4, output);
        ldrive.Update();
        digit.display(msg);

        //check to see if autoshot should be enabled
        boolean lsPressed = operatorController.getRawButton(XboxController.Button.kLeftStick.value);
        //rising edge check
        if(lastLsPressed == false && lsPressed == true) {
            autoShotEnabled = !autoShotEnabled;
        }
        lastLsPressed = lsPressed;

        //must be at end
        datalog.Periodic();
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

    public static boolean isAutoShootEnabled() {
        return autoShotEnabled;
    }

    public static void setAutoShootEnabled(boolean value) {
        autoShotEnabled = value;
    }

    public static void snapHub(String msg) {
        snapHub.TakeSnapshot(msg);
    }

    public static void snapCargo(String msg) {
        snapCargo.TakeSnapshot("C" + msg);
    }
}
