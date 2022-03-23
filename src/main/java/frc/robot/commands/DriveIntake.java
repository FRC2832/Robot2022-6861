package frc.robot.commands;

import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Intake;
import frc.robot.Shooter;
import frc.robot.ShooterConstants;

public class DriveIntake extends CommandBase{
    private Intake intake;
    private Shooter shooter;
    private XboxController operatorController;

    public DriveIntake(Intake intake, Shooter shooter, XboxController operatorController) {
        this.intake = intake;
        this.shooter = shooter;
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
            shooter.setShooterRpm(ShooterConstants.DEFAULT_SHOT_RPM);
        } else if (operatorController.getLeftTriggerAxis() > 0.5) {
            //if left trigger pressed, run balls out backwards
            intakeSpeed = -intake.INTAKE_SPEED;
            upSpeed = -intake.UP_SPEED;
            shooter.setShooterRpm(-4500);
        } else {
            //do nothing
        }
        
        intake.setIntake(intakeSpeed);
        intake.setUpMotor(upSpeed);
    }
}
