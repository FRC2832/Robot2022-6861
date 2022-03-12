package frc.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.TalonFX;
import com.ctre.phoenix.motorcontrol.can.TalonFXConfiguration;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class Shooter extends SubsystemBase 
{
    final double SENSOR_UNITS_TO_RPM = 3.414;
    final int HOOD_SENSOR_ACTIVE = 700;
    final int MAX_ANGLE_COUNTS = 400;
    final int MIN_ANGLE = 20;
    final int MAX_ANGLE = 70;

    TalonFX shooterFx;
    TalonSRX hoodMotor;
    boolean isHomed;    //report if hood has been homed
    boolean lastHomed;
    double hoodSensorOffset;
    Pi pi;
    private double distance;
    private double hoodAngle;
    private double targetRpm;

    //TODO: write home hood method
    //Distance to Target
    //Hood Angle
    //Turn robot to goal
    //Turret Angle?
    
    public Shooter(Pi pi) {
        this.pi = pi;
        isHomed = false;
        // Example usage of a TalonSRX motor controller
        shooterFx = new TalonFX(23); // creates a new TalonSRX with ID 0
        shooterFx.setNeutralMode(NeutralMode.Coast);
        shooterFx.setInverted(false);
        hoodMotor = new TalonSRX(25);
        hoodMotor.setNeutralMode(NeutralMode.Brake);

        TalonFXConfiguration config = new TalonFXConfiguration();
        //PID values from calibration on field, 6878 units/100ms = 32.3% power, 47.89% = 9892, 16.62%=3572 units=1046 rpm
        config.slot0.kP = 0.8;
        config.slot0.kI = 0.001;
        config.slot0.kD = 16;
        config.slot0.kF = 0.05205;
        config.slot0.integralZone = 65;
        config.closedloopRamp = 0.1;         //take 100ms to ramp to max power
        shooterFx.configAllSettings(config); // apply the config settings; this selects the quadrature encoder
    }

    @Override
    public void periodic() {
        SmartDashboard.putNumber("Shooter Output Velocity", getShooterVelocity());
        SmartDashboard.putNumber("Hood Angle Position", getHoodAngle());
        SmartDashboard.putNumber("Shot Distance", getShotDist());
        SmartDashboard.putNumber("Calc RPM", getTargetRpm());
        SmartDashboard.putNumber("Calc Hood Angle", getTargetHoodAngle());
        SmartDashboard.putBoolean("Hood Bottom", hoodBottom());
        SmartDashboard.putNumber("Hood Sensor", hoodMotor.getSensorCollection().getAnalogInRaw());
        SmartDashboard.putBoolean("IsHomed", isHomed);

        //when we exit the home position, save the zero position
        if (lastHomed == true && !hoodBottom()){
            hoodMotor.setSelectedSensorPosition(0);
            isHomed = true;
        }
        lastHomed = hoodBottom();
    }

    public void setShootPct(double percent) {
        shooterFx.set(ControlMode.PercentOutput, percent);
    }

    public void setShooterRpm(double rpm) {
        shooterFx.set(ControlMode.Velocity, rpm * SENSOR_UNITS_TO_RPM);
    }

    public boolean isHoodHomed() {
        return isHomed;
    }

    /**
     * Get shooter speed in RPM
     * @return RPM
     */
    public double getShooterVelocity() {
        return shooterFx.getSelectedSensorVelocity() / SENSOR_UNITS_TO_RPM;
    }

    public double getHoodAngle() {
        double sensor = hoodMotor.getSelectedSensorPosition();
        return (sensor/MAX_ANGLE_COUNTS)*(MAX_ANGLE-MIN_ANGLE) + MIN_ANGLE;
    }

    public void setHoodSpeedPct(double pct) { 
        //allow control if homed or only down if not homed
        if(hoodBottom()) {
            if(pct > 0.1) {
                //if driving down, stop at home
                hoodMotor.set(ControlMode.PercentOutput, 0);
            } else {
                //slowly drive out to get accurate home
                hoodMotor.set(ControlMode.PercentOutput, 0.18);
            }
        }
        else if(hoodMotor.getSelectedSensorPosition() > MAX_ANGLE_COUNTS && pct > 0){
            hoodMotor.set(ControlMode.PercentOutput, 0);
        }
        else {
            hoodMotor.set(ControlMode.PercentOutput, pct);
        }
    }

    public boolean hoodBottom() {
        return hoodMotor.getSensorCollection().getAnalogInRaw() > HOOD_SENSOR_ACTIVE;
    }

    public void setHoodAngle(double position) {
        //TODO: set hood angle
    }

    public void calcShot() {
        //first, calculate distance to target
        double centerY = pi.getCenterY();
        distance = Pi.LinearInterp(ShooterConstants.VISION_DIST_TABLE, centerY);

        hoodAngle = Pi.LinearInterp(ShooterConstants.DIST_HOOD_TABLE, distance);
        targetRpm = Pi.LinearInterp(ShooterConstants.DIST_RPM_TABLE, distance);
    }

    public double getTargetRpm() {
        return targetRpm;
    }

    public double getTargetHoodAngle() {
        return hoodAngle;
    }

    public double getShotDist() {
        return distance;
    }
}
