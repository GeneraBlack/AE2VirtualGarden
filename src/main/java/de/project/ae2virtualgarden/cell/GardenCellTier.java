package de.project.ae2virtualgarden.cell;

import de.project.ae2virtualgarden.config.VirtualGardenConfig;

public enum GardenCellTier {
    TIER_1K("1k", 1024, 8, 63, 0.5),
    TIER_4K("4k", 4096, 32, 63, 1.0),
    TIER_16K("16k", 16384, 128, 63, 2.0),
    TIER_64K("64k", 65536, 512, 63, 4.0),
    TIER_256K("256k", 262144, 2048, 63, 8.0);

    private final String name;
    private final int totalBytes;
    private final int bytesPerType;
    private final int totalTypes;
    private final double idleDrain;

    GardenCellTier(String name, int totalBytes, int bytesPerType, int totalTypes, double idleDrain) {
        this.name = name;
        this.totalBytes = totalBytes;
        this.bytesPerType = bytesPerType;
        this.totalTypes = totalTypes;
        this.idleDrain = idleDrain;
    }

    public String getTierName() {
        return name;
    }

    public int getTotalBytes() {
        return totalBytes;
    }

    public int getBytesPerType() {
        return bytesPerType;
    }

    public int getTotalTypes() {
        return totalTypes;
    }

    public double getIdleDrain() {
        return idleDrain;
    }

    public int getDropCount() {
        return switch (this) {
            case TIER_1K -> VirtualGardenConfig.TIER_1K_DROPS.get();
            case TIER_4K -> VirtualGardenConfig.TIER_4K_DROPS.get();
            case TIER_16K -> VirtualGardenConfig.TIER_16K_DROPS.get();
            case TIER_64K -> VirtualGardenConfig.TIER_64K_DROPS.get();
            case TIER_256K -> VirtualGardenConfig.TIER_256K_DROPS.get();
        };
    }
}
