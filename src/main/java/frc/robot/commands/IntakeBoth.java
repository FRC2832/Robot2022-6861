package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Intake;

public class IntakeBoth extends CommandBase {
    private Intake intake;

    public IntakeBoth(Intake intake) {
        this.intake = intake;
        addRequirements(intake);
    }

    @Override
    public void initialize() { }

    @Override
    public void execute() {
        intake.setIntake(0.50);
        intake.setUpMotor(0.25);
    }

    @Override
    public boolean isFinished() { return false; }

    @Override
    public void end(boolean interrupted) {
        intake.setIntake(0);
        intake.setUpMotor(0);
    }
}
