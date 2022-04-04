package frc.robot.LightDrive;

import edu.wpi.first.wpilibj.SerialPort;
import edu.wpi.first.wpilibj.util.Color;

/* Original file lost to time...  Recreated by Team 6861 */
public class LightDriveSerial {
    SerialPort port;
    private byte[] m_tx;
    private RxPacket m_rx;

    public LightDriveSerial() {
        port = new SerialPort(115200, SerialPort.Port.kOnboard);
        port.setReadBufferSize(8);
        this.m_tx = new byte[14];
        this.m_rx = new RxPacket();
    }

    public void Update() {
        //pack the structure
        this.m_tx[0] = (byte)0xAA;
        
        for(int i=0; i<this.m_tx.length-1; i++) {

        }
        port.write(this.m_tx, this.m_tx.length);
        this.m_rx.SetBytes(port.read(8));
    }

    public void SetColor(int ch, final Color color) {
        if (ch < 1 || ch > 4) {
            return;
        }

        ch = --ch * 3;
        this.m_tx[ch + 1] = (byte)color.green;
        this.m_tx[ch + 2] = (byte)color.red;
        this.m_tx[ch + 3] = (byte)color.blue;
    }

    public void SetColor(int ch, final Color color, final double brightness) {
        if (ch < 1 || ch > 4) {
            return;
        }
        byte red = (byte)(color.red * brightness);
        byte green = (byte)(color.green * brightness);
        byte blue = (byte)(color.blue * brightness);

        ch = --ch * 3;
        this.m_tx[ch + 1] = (byte)green;
        this.m_tx[ch + 2] = (byte)red;
        this.m_tx[ch + 3] = (byte)blue;
    }

    public void SetLevel(final int ch, final byte level) {
        if (ch < 1 || ch > 12 || level < 0 || level > 255) {
            return;
        }
        this.m_tx[ch + 1] = level;
    }
    
    //from here down, identical to CAN version
    public float GetCurrent(final int ch) {
        float current = 0.0f;
        switch (ch) {
            case 1: {
                current = this.m_rx.I1;
                break;
            }
            case 2: {
                current = this.m_rx.I2;
                break;
            }
            case 3: {
                current = this.m_rx.I3;
                break;
            }
            case 4: {
                current = this.m_rx.I4;
                break;
            }
            default: {
                current = -10.0f;
                break;
            }
        }
        return current / 10.0f;
    }

    public float GetTotalCurrent() {
        return (this.m_rx.I1 + this.m_rx.I2 + this.m_rx.I3 + this.m_rx.I4) / 10.0f;
    }

    public float GetVoltage() {
        return this.m_rx.VIN / 10.0f;
    }

    public int GetFWVersion() {
        return this.m_rx.FW;
    }

    public Status GetStatus() {
        return this.m_rx.status;
    }

    public int GetPWMs(final int ch) {
        if (ch > 2 || ch < 1) {
            return -1;
        }
        return (ch > 1) ? (this.m_rx.PWMVals >> 8) : (this.m_rx.PWMVals & 0xFF);
    }
}
