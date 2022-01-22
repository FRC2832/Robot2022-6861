package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Intake;

public class IntakeBall extends CommandBase {
    private Intake intake;

    public IntakeBall(Intake intake) {
        this.intake = intake;
        addRequirements(intake);
    }

    @Override
    public void initialize() { }

    @Override
    public void execute() {
        intake.setIntake(0.50);
    }

    @Override
    public boolean isFinished() { return false; }

    @Override
    public void end(boolean interrupted) {
        intake.setIntake(0);
    }
}
