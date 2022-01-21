// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.commands.AutoDrive;
import frc.robot.commands.AutoShoot;
import frc.robot.commands.DriveStick;

/**
 * This is a demo program showing how to use Mecanum control with the
 * MecanumDrive class.
 */
public class Robot extends TimedRobot {
    private Drivetrain drive;
    private static Simulation sim;
    private SendableChooser<Command> m_chooser;

    @Override
    public void robotInit() {
        drive = new Drivetrain();
        drive.register();
        // drive.setDefaultCommand(new DriveStick(drive));

        sim = new Simulation(drive);

        SequentialCommandGroup backUpShoot = new SequentialCommandGroup(
            new AutoDrive(drive),
            new AutoShoot(),
            new AutoDrive(drive)
        );

        SequentialCommandGroup grab3 = new SequentialCommandGroup(
            new AutoDrive(drive),
            new AutoShoot(),
            new AutoDrive(drive)
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
        CommandScheduler.getInstance().schedule(new DriveStick(drive));
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
