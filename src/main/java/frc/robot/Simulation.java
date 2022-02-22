package frc.robot;

import java.util.ArrayList;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Transform2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.MecanumDriveKinematics;
import edu.wpi.first.math.kinematics.MecanumDriveOdometry;
import edu.wpi.first.math.kinematics.MecanumDriveWheelSpeeds;
import edu.wpi.first.math.system.plant.DCMotor;
import edu.wpi.first.math.system.plant.LinearSystemId;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
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
    private Translation2d redBalls[];
    private Translation2d blueBalls[];
    NetworkTable visionTable;

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

        redBalls = new Translation2d[8];
        redBalls[0] = new Translation2d(8.57, 7.53);
        redBalls[1] = new Translation2d(10.85, 6.13);
        redBalls[2] = new Translation2d(10.95, 2.24);
        redBalls[3] = new Translation2d(14.4, 6.77);
        redBalls[4] = new Translation2d(14.89, 5.85);  //virtual ball returned from human player near driver station
        //far side
        redBalls[5] = new Translation2d(8.75, 0.68);
        redBalls[6] = new Translation2d(4.57, 3.34);
        redBalls[7] = new Translation2d(5.99, 6.95);

        blueBalls = new Translation2d[8];
        blueBalls[0] = new Translation2d(5.06, 5.96);
        blueBalls[1] = new Translation2d(5.13, 2.06);
        blueBalls[2] = new Translation2d(7.38, 0.66);
        blueBalls[3] = new Translation2d(1.55, 1.43);
        blueBalls[4] = new Translation2d(1.08, 2.31);  //virtual ball returned from human player near driver station
        //far side
        blueBalls[5] = new Translation2d(7.23, 7.48);
        blueBalls[6] = new Translation2d(11.40, 4.87);
        blueBalls[7] = new Translation2d(10.80, 1.27);

        NetworkTableInstance inst = NetworkTableInstance.getDefault();
        visionTable = inst.getTable("/vision");
    }

    private String lastAuto = "";
    private Alliance lastAlli = Alliance.Red;
    public void periodic() {
        boolean reset = SmartDashboard.getBoolean("Reset Position", false);
        String currentAuto = SmartDashboard.getString("SendableChooser[0]/active", "");
        Alliance curAlli = DriverStation.getAlliance();
        
        if ( (reset == true) || !(lastAuto.equals(currentAuto)) || (lastAlli != curAlli)) {
            //find autonomous selected option
            String[] options = SmartDashboard.getStringArray("SendableChooser[0]/options", new String[] {""});
            int i=0;
            //loop through to find the selected value
            for(i=0; i<options.length; i++) {
                if(options[i].equals(currentAuto)) break;
            }
            //if not found, set the index to the first element on the list
            if (i==options.length) i=0;

            if(curAlli == Alliance.Blue)
            {
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
            }
            else 
            {
                //red
                if(i==0) {
                    //drive back and shoot
                    odometry.resetPosition(new Pose2d(9.4, 3.4, Rotation2d.fromDegrees(320)), drive.getRotation());
                } else if (i==1) {
                    //grab 3
                    odometry.resetPosition(new Pose2d(8.45, 5.9, Rotation2d.fromDegrees(90)), drive.getRotation());
                } else {
                    //default in starting zone
                    odometry.resetPosition(new Pose2d(6.5, 4.72, Rotation2d.fromDegrees(135)), drive.getRotation());
                }
            }
            SmartDashboard.putBoolean("Reset Position", false);
        }
        lastAuto = currentAuto;
        lastAlli = curAlli;

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
        updateVision(pose);
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

    public double getVelocity(int wheel) {
        return driveSim[wheel].getAngularVelocityRadPerSec();
    }

    public void updateVision(Pose2d robot) {
        Translation2d balls[];
        final double MAX_SIGHT_DIST = 1.219;  //48"

        var cameraMove = new Translation2d(0.381, new Rotation2d());//move the camera 15" forward to be at the front of the robot
        var cameraPose = robot.transformBy(new Transform2d(cameraMove,new Rotation2d()));  

        if(DriverStation.getAlliance() == Alliance.Red) {
            balls = redBalls;
        } else {
            balls = blueBalls;
        }

        ArrayList<Double> centerX = new ArrayList<Double>();
        ArrayList<Double> centerY = new ArrayList<Double>();
        for(Translation2d ball : balls) {
            var heading = calcHeading(cameraPose, ball);

            //ball must be within 48" and within a 90* FOV to be seen
            var angle = heading.getRotation().getDegrees();
            if (Math.abs(angle) < 45) {
                var dist = heading.getTranslation().getNorm();
                var x = Math.sin(Math.toRadians(angle)) * dist;
                var y = Math.cos(Math.toRadians(angle)) * dist;

                //48" check
                if(y < MAX_SIGHT_DIST) {
                    //top left is 0,0
                    centerX.add((MAX_SIGHT_DIST-x)/MAX_SIGHT_DIST*320);  //since we are 90*, a 45* max triangle has equal sides, so we assumed max distance side to side also.  640 max pixels divided by 2
                    centerY.add((MAX_SIGHT_DIST-y)/MAX_SIGHT_DIST*480);
                }
            }
        }

        visionTable.getEntry("cargoX").setDoubleArray(centerX.stream().mapToDouble(d->d).toArray());
        visionTable.getEntry("cargoY").setDoubleArray(centerY.stream().mapToDouble(d->d).toArray());
    }

    public static Transform2d calcHeading(Pose2d robot, Translation2d target) {
        Translation2d trans = target.minus(robot.getTranslation());
        double x = trans.getX();
        double y = trans.getY();
        double h = trans.getNorm();

        double angle;
        if(Math.abs(y) < 1e-9 && x<0) {
            //handle 180* case
            angle = Math.PI;
        } else if (x>= 0) {
            //handle quadrants 1 and 4
            angle = Math.asin(y/h);
        } else if (y >= 0) {
            //handle quadrant 2
            angle = Math.acos(x/h);
        } else {
            //handle quadrant 3
            angle = Math.asin(-y/h) + Math.PI;
        }
        return new Transform2d(trans, (new Rotation2d(angle)).minus(robot.getRotation()));
    }
}
