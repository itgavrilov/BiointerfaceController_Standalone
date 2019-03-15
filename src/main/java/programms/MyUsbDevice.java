package programms;

import java.util.ArrayList;
import java.util.List;
import javax.usb.*;

public class MyUsbDevice {

    public static final int VENDOR_ID = 0x483;
    public static final int PRODUCT_ID = 0x1;

    private UsbServices services;
    private UsbHub usbHub;
    public List<javax.usb.UsbDevice> usbDeviceList;

    public MyUsbDevice() throws UsbException {
        services = UsbHostManager.getUsbServices();
        usbHub = services.getRootUsbHub();

        usbDeviceList = findUsbDevices(usbHub);
    }

    public List<javax.usb.UsbDevice> findUsbDevices(UsbHub hub) {
        List<javax.usb.UsbDevice> usbDeviceList = new ArrayList();
        if (hub != null) {
            for (javax.usb.UsbDevice device : (List<javax.usb.UsbDevice>) hub.getAttachedUsbDevices()) {
                UsbDeviceDescriptor desc = device.getUsbDeviceDescriptor();
                if (desc.idVendor() == VENDOR_ID && desc.idProduct() == PRODUCT_ID){
                    System.out.println("Found device!");
                    usbDeviceList.add(device);
                }
                if (device.isUsbHub()) {
                    usbDeviceList.addAll(findUsbDevices((UsbHub) device));
                }
            }
        }
        return usbDeviceList;
    }
}
