package frc.robot.commands;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Climber;

public class DriveClimber extends CommandBase{
    private Climber climber;
    private Joystick driveStick;

    public DriveClimber(Climber climber, Joystick driveStick) {
        this.climber = climber;
        this.driveStick = driveStick;
        addRequirements(climber);
    }
    
    @Override
    public void execute() {
        int pov = driveStick.getPOV();
        if( pov == 0) {
            //going up
            climber.setMiddleClimbPower(0.20);
        } else if (pov == 180) {
            climber.setMiddleClimbPower(-0.20); 
        } else {
            climber.setMiddleClimbPower(0);
        }
    }
}
