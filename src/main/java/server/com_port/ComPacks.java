package server.com_port;

/**
 * Created by Пучков Константин on 12.03.2019.
 */
public enum ComPacks {
    START_TRANSMISSION(new byte[] {(byte) 0xff, (byte) 0xff, (byte) 0x04, (byte) 0x02}, true, true),
    STOPT_RANSMISSION(new byte[] {(byte) 0xff, (byte) 0xff, (byte) 0x04, (byte) 0x03}, false, true),
    REBOOT(new byte[] {(byte) 0xff, (byte) 0xff, (byte) 0x04, (byte) 0x01}, false,false);

    private byte[] data;
    private boolean DTR, RTS;

    ComPacks(byte[] data, boolean DTR, boolean RTS) {
        this.data = data;
        this.DTR = DTR;
        this.RTS = RTS;
    }

    public byte[] getData() {
        return data;
    }

    public boolean getDTR() {
        return DTR;
    }

    public boolean getRTS() {
        return RTS;
    }}
