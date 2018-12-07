package com.bonree.ants.manager.plugins.syshelper.impl;

import org.hyperic.sigar.*;

import java.util.*;

public class DiskHandler extends BaseGatherHandler {
    private Map<String, Long> preWriteMap = new HashMap<>();
    private Map<String, Long> preReadMap = new HashMap<>();
    private Sigar sigar = null;

    public static enum DISK {
        MOUNT_POINT("MOUNT_POINT"),
        DEVICE_NAME("DEVICE_NAME"),
        DISK_SIZE("DISK_SIZE"),
        DISK_USED_SIZE("DISK_USED_SIZE"),
        DISK_WRITE_IO("DISK_WRITE_IO"),
        DISK_READ_IO("DISK_READ_IO");
        private String str;

        DISK(String str) {
            this.str = str;
        }

        public String getStrValue() {
            return str;
        }

        public static Collection<String> getCollection() {
            DISK[] disks = DISK.values();
            List<String> list = new ArrayList<>();
            for (DISK disk : disks) {
                list.add(disk.getStrValue());
            }
            return list;
        }
    }

    public DiskHandler(List<String> type, Sigar sigar) {
        super(type);
        this.sigar = sigar;
    }

    @Override
    public Map<String, Object> gatherInfo() throws Exception {
        return gatherDiskInfo(type);
    }

    @Override
    public void close() {
        this.sigar.close();
    }

    public Map<String, Object> gatherDiskInfo(Collection<String> gatherInfos) throws SigarException {
        Map<String, Object> map = new HashMap<>();
        FileSystem[] fileSystems = sigar.getFileSystemList();
        int fileSystemType = -1;
        String mountPoint = null;
        String devName = null;
        FileSystemUsage usage = null;
        for (FileSystem fileSystem : fileSystems) {
            fileSystemType = fileSystem.getType();
            if (FileSystem.TYPE_CDROM == fileSystemType || FileSystem.TYPE_NONE == fileSystemType || FileSystem.TYPE_SWAP == fileSystemType || FileSystem.TYPE_UNKNOWN == fileSystemType) {
                continue;
            }
            if (gatherInfos.contains(DISK.DEVICE_NAME.getStrValue())) {
                devName = fileSystem.getDevName();
                if (!map.containsKey(DISK.DEVICE_NAME.getStrValue())) {
                    map.put(DISK.DEVICE_NAME.getStrValue(), new ArrayList<String>());
                }
                ((List<String>) map.get(DISK.DEVICE_NAME.getStrValue())).add(devName);
            }
            mountPoint = fileSystem.getDirName();
            if (gatherInfos.contains(DISK.MOUNT_POINT.getStrValue())) {
                if (!map.containsKey(DISK.MOUNT_POINT.getStrValue())) {
                    map.put(DISK.MOUNT_POINT.getStrValue(), new ArrayList<String>());
                }
                ((List<String>) map.get(DISK.MOUNT_POINT.getStrValue())).add(mountPoint);
            }
            usage = sigar.getFileSystemUsage(mountPoint);
            if (gatherInfos.contains(DISK.DISK_SIZE.getStrValue())) {
                long diskSize = usage.getTotal();
                if (!map.containsKey(DISK.DISK_SIZE.getStrValue())) {
                    map.put(DISK.DISK_SIZE.getStrValue(), new ArrayList<Long>());
                }
                ((List<Long>) map.get(DISK.DISK_SIZE.name())).add(diskSize);
            }
            if (gatherInfos.contains(DISK.DISK_USED_SIZE.getStrValue())) {
                long usageSize = usage.getUsed();
                if (!map.containsKey(DISK.DISK_USED_SIZE.getStrValue())) {
                    map.put(DISK.DISK_USED_SIZE.getStrValue(), new ArrayList<Long>());
                }
                ((List<Long>) map.get(DISK.DISK_USED_SIZE.getStrValue())).add(usageSize);
            }

            if (gatherInfos.contains(DISK.DISK_WRITE_IO.getStrValue())) {
                long usageSize = usage.getDiskWrites();
                if (!map.containsKey(DISK.DISK_WRITE_IO.getStrValue())) {
                    map.put(DISK.DISK_WRITE_IO.getStrValue(), new ArrayList<Long>());
                }
                List<Long> list = (List<Long>) map.get(DISK.DISK_WRITE_IO.getStrValue());
                if (this.preWriteMap.containsKey(mountPoint)) {
                    list.add(usageSize - preWriteMap.get(mountPoint));
                } else {
                    list.add(0L);
                }
                this.preWriteMap.put(mountPoint, usageSize);
            }
            if (gatherInfos.contains(DISK.DISK_READ_IO.getStrValue())) {
                long usageSize = usage.getDiskWrites();
                if (!map.containsKey(DISK.DISK_READ_IO.getStrValue())) {
                    map.put(DISK.DISK_READ_IO.getStrValue(), new ArrayList<Long>());
                }
                List<Long> list = (List<Long>) map.get(DISK.DISK_READ_IO.getStrValue());
                if (this.preReadMap.containsKey(mountPoint)) {
                    list.add(usageSize - preReadMap.get(mountPoint));
                } else {
                    list.add(0L);
                }
                this.preReadMap.put(mountPoint, usageSize);
            }
        }
        return map;
    }

}
