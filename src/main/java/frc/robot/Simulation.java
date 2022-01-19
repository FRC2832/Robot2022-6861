package frc.robot;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.MecanumDriveKinematics;
import edu.wpi.first.math.kinematics.MecanumDriveOdometry;
import edu.wpi.first.math.kinematics.MecanumDriveWheelSpeeds;
import edu.wpi.first.math.system.plant.DCMotor;
import edu.wpi.first.math.system.plant.LinearSystemId;
import edu.wpi.first.wpilibj.simulation.FlywheelSim;
import edu.wpi.first.wpilibj.smartdashboard.Field2d;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Simulation {
    private MecanumDriveOdometry odometry;
    private MecanumDriveKinematics kinematics;
    private FlywheelSim driveSim[];
    private Field2d field = new Field2d();
    private Drivetrain drive;
    private Rotation2d rotation = new Rotation2d(0);
    private Translation2d corners[];
    public Simulation(Drivetrain drivetrain)
    {
        drive = drivetrain;
    }

    public void init() {
        // 20.625" front to back
        // 23" left to right
        corners = new Translation2d[4];
        corners[0] = new Translation2d(0.262, 0.292);  
        corners[1] = new Translation2d(0.262, -0.292);
        corners[2] = new Translation2d(-0.262, 0.292);  
        corners[3] = new Translation2d(-0.262, -0.292);  
        
        kinematics = new MecanumDriveKinematics(
                 corners[0],
                 corners[1],
                 corners[2],
                 corners[3]);
        odometry = new MecanumDriveOdometry(kinematics, drive.getAngle());
        SmartDashboard.putBoolean("Reset Position", false);

        driveSim = new FlywheelSim[4];
        for (int i = 0; i < 4; i++) {
            driveSim[i] = new FlywheelSim(
                    LinearSystemId.identifyVelocitySystem(
                            2.741, // kvVoltSecondsPerMeter (default = 12/kMaxSpeed), max speed = 4.377m/s based on
                                   // NEO 5880 RPM, 6" mecanum, and 10.7:1 gearbox
                            0.0917), // kaVoltSecondsSquaredPerMeter, just made up, get from robot
                    DCMotor.getNEO(1), // motor
                    10.7 // gear ratio
            );
        }
        SmartDashboard.putData("Field", field);
    }

    public void periodic() {
        boolean reset = SmartDashboard.getBoolean("Reset Position", false);
        if (reset == true) {
            // set the robot to x=0.5m, y=4m, rot=0*
            odometry.resetPosition(new Pose2d(0.5, 4, new Rotation2d()), new Rotation2d());
            SmartDashboard.putBoolean("Reset Position", false);
        }

        double rate = Robot.kDefaultPeriod;
        MecanumDriveWheelSpeeds wheelSpeeds = new MecanumDriveWheelSpeeds();
        
        for (int i = 0; i < 4; i++) {
            var volt = drive.getMotorVoltage(i);
            driveSim[i].setInputVoltage(drive.getMotorVoltage(i));
            driveSim[i].update(Robot.kDefaultPeriod);
            SmartDashboard.putNumber("MecanumWheel_" + i + "/rawVolt", volt);
            SmartDashboard.putNumber("MecanumWheel_" + i + "/rawAngRadSec", driveSim[i].getAngularVelocityRadPerSec());
        }
        wheelSpeeds.frontLeftMetersPerSecond = driveSim[0].getAngularVelocityRadPerSec();
        wheelSpeeds.frontRightMetersPerSecond = driveSim[1].getAngularVelocityRadPerSec();
        wheelSpeeds.rearLeftMetersPerSecond = driveSim[2].getAngularVelocityRadPerSec();
        wheelSpeeds.rearRightMetersPerSecond = driveSim[3].getAngularVelocityRadPerSec();
        odometry.update(drive.getAngle(), wheelSpeeds);
        var pose = odometry.getPoseMeters();

        field.setRobotPose(pose);
        var poses = new Pose2d[4];
        for (int i=0; i<4; i++) {
            poses[i] = new Pose2d(corners[i].plus(pose.getTranslation()),new Rotation2d(0));
        }
        //field.getObject("Mecanum Wheels").setPoses(poses);
        
        // calculate the robot's speed and angle (we only care about angle here)
        var temp = kinematics.toChassisSpeeds(wheelSpeeds);
        double omega = temp.omegaRadiansPerSecond;
        // set the IMU to the calculated robot rotation
        double angle = Math.toDegrees(omega * rate);
        rotation = rotation.plus(Rotation2d.fromDegrees(angle));
        SmartDashboard.putNumber("DrivetrainSim/omega", omega);
        SmartDashboard.putNumber("DrivetrainSim/angle", angle);
    }

    public Rotation2d getAngle() {
        return rotation;
    }
}
