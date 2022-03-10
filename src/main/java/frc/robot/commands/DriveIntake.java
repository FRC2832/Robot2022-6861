package frc.robot.commands;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Climber;
import frc.robot.Intake;

public class DriveIntake extends CommandBase{
    private Intake intake;
    private Joystick driveStick;
    private XboxController operatorController;

    public DriveIntake(Intake intake, Joystick driveStick, XboxController operatorController) {
        this.intake = intake;
        this.driveStick = driveStick;
        this.operatorController = operatorController;
        addRequirements(intake);
    }
    
    @Override
    public void execute() {
        double upSpeed = 0;
        double intakeSpeed = 0;

        //if right trigger pressed 50%, run both up motor and intake
        if(operatorController.getRightTriggerAxis() > 0.5) {
            intakeSpeed = intake.INTAKE_SPEED;
            upSpeed = intake.UP_SPEED;
        } else if (operatorController.getLeftTriggerAxis() > 0.5) {
            //if left trigger pressed, run balls out backwards
            intakeSpeed = -intake.INTAKE_SPEED;
            upSpeed = -intake.UP_SPEED;
        } else if(driveStick.getRawButton(1)) {
            //if driver trigger pressed, only run intake motor
            intakeSpeed = intake.INTAKE_SPEED;
            upSpeed = 0;
        } else {
            //do nothing
        }
        
        intake.setIntake(intakeSpeed);
        intake.setUpMotor(upSpeed);
    }
}