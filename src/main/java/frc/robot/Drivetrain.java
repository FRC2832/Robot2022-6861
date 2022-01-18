package frc.robot;

import edu.wpi.first.wpilibj.drive.MecanumDrive;
import edu.wpi.first.wpilibj.motorcontrol.PWMSparkMax;
import edu.wpi.first.wpilibj2.command.Subsystem;

public class Drivetrain implements Subsystem {
    private static final int kFrontLeftChannel = 2;
    private static final int kRearLeftChannel = 3;
    private static final int kFrontRightChannel = 1;
    private static final int kRearRightChannel = 0;

    private MecanumDrive m_robotDrive;
    
    public Drivetrain() {
        PWMSparkMax frontLeft = new PWMSparkMax(kFrontLeftChannel);
        PWMSparkMax rearLeft = new PWMSparkMax(kRearLeftChannel);
        PWMSparkMax frontRight = new PWMSparkMax(kFrontRightChannel);
        PWMSparkMax rearRight = new PWMSparkMax(kRearRightChannel);
    
        // Invert the right side motors.
        // You may need to change or remove this to match your robot.
        frontRight.setInverted(true);
        rearRight.setInverted(true);
    
        m_robotDrive = new MecanumDrive(frontLeft, rearLeft, frontRight, rearRight);
    }

    public void drive(double xSpeed, double ySpeed, double rot, boolean fieldRelative) {
        // Use the joystick X axis for lateral movement, Y axis for forward
        // movement, and Z axis for rotation.
        m_robotDrive.driveCartesian(ySpeed, xSpeed, rot, 0.0);
    }
}
