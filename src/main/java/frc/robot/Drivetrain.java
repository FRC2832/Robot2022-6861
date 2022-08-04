package frc.robot;

import com.ctre.phoenix.sensors.PigeonIMU;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.math.filter.LinearFilter;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj.BuiltInAccelerometer;
import edu.wpi.first.wpilibj.RobotController;
import edu.wpi.first.wpilibj.drive.MecanumDrive;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class Drivetrain extends SubsystemBase {
    private static final int kFrontLeftChannel = 35;
    private static final int kRearLeftChannel = 34;
    private static final int kFrontRightChannel = 20;
    private static final int kRearRightChannel = 21;
    public static final int FL = 0;
    public static final int FR = 1;
    public static final int RL = 2;
    public static final int RR = 3;
    private CANSparkMax motors[];
    private MecanumDrive m_robotDrive;
    private final PigeonIMU pigeon = new PigeonIMU(0);
    private BuiltInAccelerometer accelerometer;
    private LinearFilter axFilter, ayFilter;
    private double axValue, ayValue;

    public Drivetrain() {
        motors = new CANSparkMax[4];
        motors[FL] = new CANSparkMax(kFrontLeftChannel,MotorType.kBrushless);
        motors[FR] = new CANSparkMax(kFrontRightChannel,MotorType.kBrushless);
        motors[RL] = new CANSparkMax(kRearLeftChannel,MotorType.kBrushless);
        motors[RR] = new CANSparkMax(kRearRightChannel,MotorType.kBrushless);
    
        // Invert the right side motors.
        // You may need to change or remove this to match your robot.
        motors[FR].setInverted(true);
        motors[RR].setInverted(true);
    
        for(int i=0; i<motors.length;i++) {
            //encoders set to meters traveled
            motors[i].getEncoder().setPositionConversionFactor(1/21.);
            motors[i].getEncoder().setPosition(0);
        }
        m_robotDrive = new MecanumDrive(motors[FL], motors[RL], motors[FR], motors[RR]);
        m_robotDrive.setSafetyEnabled(false);

        accelerometer = new BuiltInAccelerometer();
        axFilter = LinearFilter.movingAverage(10);
        ayFilter = LinearFilter.movingAverage(10);
    }

    public void setBrakeMode(boolean brake) {
        IdleMode idleMode;

        if(brake) {
            idleMode = IdleMode.kBrake;
        } else {
            idleMode = IdleMode.kCoast;
        }
        for(int i=0; i<motors.length; i++) {
            motors[i].setIdleMode(idleMode);
        }
    }

    public void drive(double xSpeed, double ySpeed, double rot, boolean fieldRelative) {
        // Use the joystick X axis for lateral movement, Y axis for forward
        // movement, and Z axis for rotation.
        m_robotDrive.driveCartesian(ySpeed, xSpeed, rot, 0.0);
    }

    public void driveMechanumTank(double inputX0, double inputY0, double inputX1, double inputY1) {
        //0 is left stick, 1 is right stick

        double[] wheelSpeeds = new double[motors.length];
        
        wheelSpeeds[FL] = -inputY0 + inputX0;
        wheelSpeeds[FR] = -inputY1 - inputX1;
        wheelSpeeds[RL] = -inputY0 - inputX0;
        wheelSpeeds[RR] = -inputY1 + inputX1;
    
        normalize(wheelSpeeds);
        for(int i=0; i<motors.length; i++) {
            motors[i].set(wheelSpeeds[i]);
        }
    }

    private void normalize(double[] wheelSpeeds) {
        double maxMagnitude = Math.abs(wheelSpeeds[0]);
        for (int i = 1; i < wheelSpeeds.length; i++) {
          double temp = Math.abs(wheelSpeeds[i]);
          if (maxMagnitude < temp) {
            maxMagnitude = temp;
          }
        }
        if (maxMagnitude > 1.0) {
          for (int i = 0; i < wheelSpeeds.length; i++) {
            wheelSpeeds[i] = wheelSpeeds[i] / maxMagnitude;
          }
        }
      }
    
    public double getAngle() {
        if (Robot.isReal()) {
            double[] ypr_deg = new double[3];
            pigeon.getYawPitchRoll(ypr_deg);
            return -ypr_deg[0];
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

    /**
     * Returns the acceleration in the forwards/backwards direction of the robot
     * @return Long accel/Ax of the robot
     */
    public double getAxFiltered() {
        return axValue;
    }

    /**
     * Returns the acceleration in the left/right direction of the robot
     * @return Lat accel/Ay of the robot
     */
    public double getAyFiltered() {
        return ayValue;
    }

    @Override
    public void periodic() {
        axValue = axFilter.calculate(accelerometer.getZ());
        ayValue = ayFilter.calculate(accelerometer.getX());

        SmartDashboard.putNumber("Gyro Angle", getRotation().getDegrees());
        SmartDashboard.putNumber("Drive FL Encoder", getDistance(FL));
        SmartDashboard.putNumber("Drive FR Encoder", getDistance(FR));
        SmartDashboard.putNumber("Drive RL Encoder", getDistance(RL));
        SmartDashboard.putNumber("Drive RR Encoder", getDistance(RR));
        SmartDashboard.putNumber("Ax Filtered", axValue);
        SmartDashboard.putNumber("Ay Filtered", ayValue);
    }

    public void saveConfig() {
        motors[FL].burnFlash();
        motors[FR].burnFlash();
        motors[RL].burnFlash();
        motors[RR].burnFlash();
    }
}
