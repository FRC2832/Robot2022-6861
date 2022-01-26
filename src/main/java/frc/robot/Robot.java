// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.ParallelRaceGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;
import frc.robot.commands.AutoDrive;
import frc.robot.commands.AutoDriveDiagonal;
import frc.robot.commands.AutoShoot;
import frc.robot.commands.AutoTurn;
import frc.robot.commands.DriveStick;
import frc.robot.commands.IntakeBall;

/**
 * This is a demo program showing how to use Mecanum control with the
 * MecanumDrive class.
 */
public class Robot extends TimedRobot {
    private Drivetrain drive;
    private Intake intake;
    private static Simulation sim;
    private Joystick driverController;
    private SendableChooser<Command> m_chooser;

    @Override
    public void robotInit() {
        //initialize subsystems
        drive = new Drivetrain();
        drive.register();
        intake = new Intake();
        intake.register();

        //setup driver controller
        driverController = new Joystick(0);
        JoystickButton xButton = new JoystickButton(driverController, 3); // 3 = X button
        xButton.whenHeld(new IntakeBall(intake));

        drive.setDefaultCommand(new DriveStick(drive,driverController));

        sim = new Simulation(drive);

        //ParallelGroup must wait till ALL finish, ParallelRace waits for FIRST to finish, Deadline waits for the specified command to finish
        SequentialCommandGroup backUpShoot = new SequentialCommandGroup(
            new AutoDrive(drive,1.7),
            new AutoShoot(),
            new AutoDrive(drive,1)
        );

        SequentialCommandGroup grab3 = new SequentialCommandGroup(
            new ParallelRaceGroup(
                new AutoDrive(drive,1.8),
                new IntakeBall(intake)
            ),
            new AutoDriveDiagonal(drive, 0.46, -0.385, 0.5),  //should be 60% power
            new AutoShoot(),
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
    public void autonomousInit() {
        CommandScheduler.getInstance().cancelAll();
        
        Command m_autonomousCommand = m_chooser.getSelected();

        // schedule the autonomous command (example)
        if (m_autonomousCommand != null) {
            m_autonomousCommand.schedule();
        }
    }

    @Override
    public void teleopInit() {
        CommandScheduler.getInstance().cancelAll();
    }

    @Override
    public void robotPeriodic() {
        CommandScheduler.getInstance().run();
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
