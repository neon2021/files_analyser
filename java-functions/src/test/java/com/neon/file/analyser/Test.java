package com.neon.file.analyser;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import oshi.*;

import oshi.hardware.*;
import oshi.software.os.*;

public class Test {
    public static void main(String[] args) {
        System.out.println("hello");

        for (FileStore store : FileSystems.getDefault().getFileStores()) {
            outputInfo(store);
        }

        System.out.println("getting fileStore from disk path");
        for (Path rootDirectory : FileSystems.getDefault().getRootDirectories()) {
            try {
                System.out.println("rootDirectory: " + rootDirectory);
                outputInfo(Files.getFileStore(rootDirectory));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // get unique id of a driver
        SystemInfo systemInfo = new SystemInfo();
        OperatingSystem operatingSystem = systemInfo.getOperatingSystem();
        HardwareAbstractionLayer hardwareAbstractionLayer = systemInfo.getHardware();
        // CentralProcessor centralProcessor = hardwareAbstractionLayer.getProcessor();
        ComputerSystem computerSystem = hardwareAbstractionLayer.getComputerSystem();
        List<HWDiskStore> diskStores = hardwareAbstractionLayer.getDiskStores();
        for (HWDiskStore hwDiskStore : diskStores) {
//            System.out.println("hwDiskStore: " + ToStringBuilder.reflectionToString(hwDiskStore, ToStringStyle.MULTI_LINE_STYLE));
            System.out.println("hwDiskStore: " + ToStringBuilder.reflectionToString(hwDiskStore));
        }

        List<UsbDevice> usbDevices = hardwareAbstractionLayer.getUsbDevices(true);
        for (UsbDevice usbDevice : usbDevices) {
//            System.out.println("usbDeviceInfo: " + ToStringBuilder.reflectionToString(usbDevice, ToStringStyle.MULTI_LINE_STYLE));
            System.out.println("usbDeviceInfo: " + ToStringBuilder.reflectionToString(usbDevice));
        }

        System.out.println("hardwareAbstractionLayer.getLogicalVolumeGroups(): "+hardwareAbstractionLayer.getLogicalVolumeGroups());
        hardwareAbstractionLayer.getLogicalVolumeGroups().forEach(r->System.out.println("LogicalVolumeGroup: " + ToStringBuilder.reflectionToString(r)));
    }

    private static void outputInfo(FileStore store) {
        try {
            System.out.printf("name: %s, type: %s, TotalSpace: %s, UsableSpace:%s\n ", store.name(), store.type(), store.getTotalSpace(), store.getUsableSpace());
            System.out.println("ext info: " + store);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
