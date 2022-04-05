import org.junit.Test;

import frc.robot.LightDrive.LightDriveSerial;

public class LightDriveSerialTest {
    @Test
    public void testLightDriveSerial() {
        LightDriveSerial ldrive = new LightDriveSerial();

        ldrive.SetLevel(1, (byte)0xFF);
        ldrive.SetLevel(5, (byte)0xFF);
        ldrive.SetLevel(9, (byte)0xFF);
        ldrive.SetLevel(10, (byte)0xC8);
        ldrive.SetLevel(11, (byte)0xFF);
        ldrive.Update();
    }
}
