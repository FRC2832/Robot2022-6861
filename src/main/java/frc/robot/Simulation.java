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
    private double angle = 0;
    private double encoders[] = new double[4];
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
        odometry = new MecanumDriveOdometry(kinematics, drive.getRotation());
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

    private String lastAuto = "";
    public void periodic() {
        boolean reset = SmartDashboard.getBoolean("Reset Position", false);
        String currentAuto = SmartDashboard.getString("SendableChooser[0]/active", "");

        if ( (reset == true) || !(lastAuto.equals(currentAuto)) ) {
            //find autonomous selected option
            String[] options = SmartDashboard.getStringArray("SendableChooser[0]/options", new String[] {""});
            int i=0;
            //loop through to find the selected value
            for(i=0; i<options.length; i++) {
                if(options[i].equals(currentAuto)) break;
            }
            //if not found, set the index to the first element on the list
            if (i==options.length) i=0;

            if(i==0) {
                //drive back and shoot
                odometry.resetPosition(new Pose2d(6.5, 4.72, Rotation2d.fromDegrees(135)), drive.getRotation());
            } else if (i==1) {
                //grab 3
                odometry.resetPosition(new Pose2d(7.3, 2.56, Rotation2d.fromDegrees(270)), drive.getRotation());
            } else {
                //default in starting zone
                odometry.resetPosition(new Pose2d(6.5, 4.72, Rotation2d.fromDegrees(135)), drive.getRotation());
            }
            SmartDashboard.putBoolean("Reset Position", false);
        }
        lastAuto = currentAuto;

        double rate = Robot.kDefaultPeriod;
        MecanumDriveWheelSpeeds wheelSpeeds = new MecanumDriveWheelSpeeds();
        
        for (int i = 0; i < 4; i++) {
            var volt = drive.getMotorVoltage(i);
            driveSim[i].setInputVoltage(drive.getMotorVoltage(i));
            driveSim[i].update(Robot.kDefaultPeriod);
            encoders[i] += driveSim[i].getAngularVelocityRadPerSec() * Robot.kDefaultPeriod;
            SmartDashboard.putNumber("MecanumWheel_" + i + "/rawVolt", volt);
            SmartDashboard.putNumber("MecanumWheel_" + i + "/rawAngRadSec", driveSim[i].getAngularVelocityRadPerSec());
        }
        wheelSpeeds.frontLeftMetersPerSecond = driveSim[0].getAngularVelocityRadPerSec();
        wheelSpeeds.frontRightMetersPerSecond = driveSim[1].getAngularVelocityRadPerSec();
        wheelSpeeds.rearLeftMetersPerSecond = driveSim[2].getAngularVelocityRadPerSec();
        wheelSpeeds.rearRightMetersPerSecond = driveSim[3].getAngularVelocityRadPerSec();
        odometry.update(getRotation(), wheelSpeeds);
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
        double deltaAngle = Math.toDegrees(omega * rate);
        rotation = rotation.plus(Rotation2d.fromDegrees(deltaAngle));
        angle += deltaAngle;
        SmartDashboard.putNumber("DrivetrainSim/omega", omega);
        SmartDashboard.putNumber("DrivetrainSim/angle", deltaAngle);
    }

    public double getAngle() {
        return angle;
    }

    public Rotation2d getRotation() {
        return rotation;
    }

    public double getDistance(int wheel) {
        return encoders[wheel];
    }
}
