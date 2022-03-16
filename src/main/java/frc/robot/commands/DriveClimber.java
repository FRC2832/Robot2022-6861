package frc.robot.commands;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Climber;
import frc.robot.Turret;

public class DriveClimber extends CommandBase{
    private Climber climber;
    private Turret turret;
    private Joystick leftStick;
    private Joystick rightStick;
    private double position;

    public DriveClimber(Climber climber, Turret turret, Joystick leftStick, Joystick rightStick) {
        this.climber = climber;
        this.leftStick = leftStick;
        this.rightStick = rightStick;
        this.turret = turret;
        addRequirements(climber);
        position = Climber.CLIMB_START;
    }
    
    @Override
    public void execute() {
        double newPosition;
        //center climb
        int pov = rightStick.getPOV();

        //get climb command
        if(rightStick.getRawButton(6)) {
            newPosition = Climber.CLIMB_TOP;
        } else if(rightStick.getRawButton(4)) {
            newPosition = Climber.CLIMB_BOTTOM;
        } else if(rightStick.getRawButton(2)) {
            newPosition = Climber.CLIMB_START;
        } else if( pov == 0) {
            //going up
            newPosition = climber.getMiddleClimbPosition() + Climber.CLIMB_STEP;
        } else if (pov == 180) {
            newPosition = climber.getMiddleClimbPosition() - Climber.CLIMB_STEP;
        } else {
            //do nothing
            newPosition = position;
        }
        
        //check if hood is in position
        boolean turretAtEnd = turret.resetClimber();
        if(newPosition!=position && turretAtEnd) {
            //we asked to climb, but turret is not at right position, so move turret instead
            turret.setTurretSpeed(0.25);
        } else if(turretAtEnd) {
            //turret moved, but climber not in position, so we need to move climber
            position = Climber.CLIMB_START;
            climber.setMiddleClimbPosition(position);
        } else {
            //move climber if things are in position
            position = newPosition;
            climber.setMiddleClimbPosition(position);
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
