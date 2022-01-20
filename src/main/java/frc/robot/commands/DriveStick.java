package frc.robot.commands;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Drivetrain;

public class DriveStick extends CommandBase {
    private Joystick m_stick;
    private Drivetrain drive;

    public DriveStick(Drivetrain drive) {
        this.drive = drive;
        m_stick = new Joystick(0);
        addRequirements(drive);
    }

    @Override
    public void initialize() {
        
    }

    @Override
    public void execute() {
        //axis 0 = left stick left/right, 1 = left stick up/down, 4 = right stick left/right
        drive.drive(m_stick.getRawAxis(0), -m_stick.getRawAxis(1), m_stick.getRawAxis(4), false);
    }
}
