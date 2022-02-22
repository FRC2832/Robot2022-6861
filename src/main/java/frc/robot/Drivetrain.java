package frc.robot;

import com.ctre.phoenix.sensors.PigeonIMU;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj.RobotController;
import edu.wpi.first.wpilibj.drive.MecanumDrive;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class Drivetrain extends SubsystemBase {
    private static final int kFrontLeftChannel = 2;
    private static final int kRearLeftChannel = 3;
    private static final int kFrontRightChannel = 1;
    private static final int kRearRightChannel = 0;
    private CANSparkMax motors[];
    private MecanumDrive m_robotDrive;
    private final PigeonIMU pigeon = new PigeonIMU(13);
    
    public Drivetrain() {
        motors = new CANSparkMax[4];
        motors[0] = new CANSparkMax(kFrontLeftChannel,MotorType.kBrushless);
        motors[1] = new CANSparkMax(kFrontRightChannel,MotorType.kBrushless);
        motors[2] = new CANSparkMax(kRearLeftChannel,MotorType.kBrushless);
        motors[3] = new CANSparkMax(kRearRightChannel,MotorType.kBrushless);
    
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

    public double getAngle() {
        if (Robot.isReal()) {
            double[] ypr_deg = new double[3];
            pigeon.getYawPitchRoll(ypr_deg);
            return ypr_deg[0];
        }
        else {
            return -Robot.getSim().getAngle();
        }
    }

    public Rotation2d getRotation() {
        return Rotation2d.fromDegrees(getAngle());
    }

    /**
     * Returns distance traveled by a wheel
     * @param wheel wheel to query
     * @return distance in meters
     */
    public double getDistance(int wheel) {
        if (Robot.isReal()) {
            return motors[wheel].getEncoder().getPosition();
        }
        else {
            return Robot.getSim().getDistance(wheel);
        }
    }

    /**
     * Returns distance traveled by a wheel
     * @param wheel wheel to query
     * @return distance in meters
     */
    public double getVelocity(int wheel) {
        if (Robot.isReal()) {
            return motors[wheel].getEncoder().getVelocity();
        }
        else {
            return Robot.getSim().getVelocity(wheel);
        }
    }

    public double getMotorVoltage(int wheel) {
        return motors[wheel].get() * RobotController.getBatteryVoltage();
    }

    @Override
    public void periodic() {
        SmartDashboard.putNumber("Gyro Angle", getRotation().getDegrees());
    }
}
