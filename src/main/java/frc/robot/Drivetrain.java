package frc.robot;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj.RobotController;
import edu.wpi.first.wpilibj.drive.MecanumDrive;
import edu.wpi.first.wpilibj.motorcontrol.PWMSparkMax;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class Drivetrain extends SubsystemBase {
    private static final int kFrontLeftChannel = 2;
    private static final int kRearLeftChannel = 3;
    private static final int kFrontRightChannel = 1;
    private static final int kRearRightChannel = 0;
    private PWMSparkMax motors[];
    private MecanumDrive m_robotDrive;
    
    public Drivetrain() {
        motors = new PWMSparkMax[4];
        motors[0] = new PWMSparkMax(kFrontLeftChannel);
        motors[1] = new PWMSparkMax(kFrontRightChannel);
        motors[2] = new PWMSparkMax(kRearLeftChannel);
        motors[3] = new PWMSparkMax(kRearRightChannel);
    
        // Invert the right side motors.
        // You may need to change or remove this to match your robot.
        motors[1].setInverted(true);
        motors[3].setInverted(true);
    
        m_robotDrive = new MecanumDrive(motors[0], motors[2], motors[1], motors[3]);
    }

    public void drive(double xSpeed, double ySpeed, double rot, boolean fieldRelative) {
        // Use the joystick X axis for lateral movement, Y axis for forward
        // movement, and Z axis for rotation.
        m_robotDrive.driveCartesian(ySpeed, xSpeed, rot, 0.0);
    }

    public Rotation2d getAngle() {
        if (Robot.isReal()) {
            //Add Pigeon or other gyro
            return Rotation2d.fromDegrees(0);
        }
        else {
            return Robot.getSim().getAngle();
        }
    }

    public double getMotorVoltage(int wheel) {
        return motors[wheel].get() * RobotController.getBatteryVoltage();
    }
}
