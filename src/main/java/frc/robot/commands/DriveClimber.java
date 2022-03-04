package frc.robot.commands;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Climber;

public class DriveClimber extends CommandBase{
    private Climber climber;
    private Joystick leftStick;
    private Joystick rightStick;

    public DriveClimber(Climber climber, Joystick leftStick, Joystick rightStick) {
        this.climber = climber;
        this.leftStick = leftStick;
        this.rightStick = rightStick;
        addRequirements(climber);
    }
    
    @Override
    public void execute() {
        //center climb
        int pov = rightStick.getPOV();
        if( pov == 0) {
            //going up
            climber.setMiddleClimbPower(Climber.MIDDLE_CLIMB_SPEED);
        } else if (pov == 180) {
            climber.setMiddleClimbPower(-Climber.MIDDLE_CLIMB_SPEED); 
        } else {
            climber.setMiddleClimbPower(0);
        }

        //reach climb
        pov = leftStick.getPOV();
        if( pov == 0) {
            //going up
            climber.setReachClimbPower(Climber.REACH_CLIMB_SPEED);
        } else if (pov == 180) {
            climber.setReachClimbPower(-Climber.REACH_CLIMB_SPEED); 
        } else {
            climber.setReachClimbPower(0);
        }
    }
}
