package frc.robot.commands;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Intake;

public class SmartIntake extends CommandBase{
    private Intake intake;

    public SmartIntake(Intake intake) {
        this.intake = intake;
        addRequirements(intake);
    }
    
    @Override
    public void execute() {
        double upSpeed = 0;
        double intakeSpeed = 0;

        if(intake.getColorSensor() == Intake.CargoColor.Unknown) {
            upSpeed = intake.UP_SPEED;
        } else {
            //keep up speed zero since ball is seen
        }

        if(intake.getProxSensor() == false) {
            intakeSpeed = intake.INTAKE_SPEED;
        } else {
            //keep speed zero if the proximity sensor detects
        }
        
        intake.setIntake(intakeSpeed);
        intake.setUpMotor(upSpeed);
    }

    @Override
    public boolean isFinished() {
        if(DriverStation.isAutonomous()) {
            return intake.getProxSensor();
        }
        return false;
    }
}
