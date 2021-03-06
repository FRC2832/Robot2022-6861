import org.junit.Assert;
import org.junit.Test;

import edu.wpi.first.math.geometry.*;
import frc.robot.Simulation;

public class VisionTest {

    @Test
    public void testCalcHeading() {
        Transform2d ans;

        //start robot at center, facing right
        Pose2d robot = new Pose2d(0,0,Rotation2d.fromDegrees(0));

        //check target in front of robot
        ans = Simulation.calcHeading(robot, new Translation2d(2,0));
        Assert.assertEquals(ans, new Transform2d(new Translation2d(2,0), new Rotation2d()));

        //check target in left of robot, rotation is positive counterclockwise
        ans = Simulation.calcHeading(robot, new Translation2d(0,2));
        Assert.assertEquals(ans, new Transform2d(new Translation2d(0,2), Rotation2d.fromDegrees(90)));

        //check target behind of robot
        ans = Simulation.calcHeading(robot, new Translation2d(-2,0));
        Assert.assertEquals(ans, new Transform2d(new Translation2d(-2,0), Rotation2d.fromDegrees(180)));

        //check target in right of robot
        ans = Simulation.calcHeading(robot, new Translation2d(0,-2));
        Assert.assertEquals(ans, new Transform2d(new Translation2d(0,-2), Rotation2d.fromDegrees(270)));

        //check target in front left of robot
        ans = Simulation.calcHeading(robot, new Translation2d(2,2));
        Assert.assertEquals(ans, new Transform2d(new Translation2d(2,2), Rotation2d.fromDegrees(45)));

        //check target in back left of robot
        ans = Simulation.calcHeading(robot, new Translation2d(-2,2));
        Assert.assertEquals(ans, new Transform2d(new Translation2d(-2,2), Rotation2d.fromDegrees(135)));

        //check target in back right of robot
        ans = Simulation.calcHeading(robot, new Translation2d(-2,-2));
        Assert.assertEquals(ans, new Transform2d(new Translation2d(-2,-2), Rotation2d.fromDegrees(225)));

        //check target in front right of robot
        ans = Simulation.calcHeading(robot, new Translation2d(2,-2));
        Assert.assertEquals(ans, new Transform2d(new Translation2d(2,-2), Rotation2d.fromDegrees(315)));

        //turn robot 45*
        robot = robot.plus(new Transform2d(new Translation2d(),Rotation2d.fromDegrees(45)));

        //check target in front of turned robot
        ans = Simulation.calcHeading(robot, new Translation2d(2,2));
        Assert.assertEquals(ans, new Transform2d(new Translation2d(2,2), new Rotation2d()));

        //check target behind of robot
        ans = Simulation.calcHeading(robot, new Translation2d(-2,-2));
        Assert.assertEquals(ans, new Transform2d(new Translation2d(-2,-2), Rotation2d.fromDegrees(180)));
    }
}
