package com.brandon3055.draconicevolution.api.modules.properties;

import com.brandon3055.draconicevolution.api.TechLevel;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

import java.util.List;

/**
 * Created by covers1624 on 4/16/20.
 */
public class EnergyModuleProperties extends ModuleProperties<EnergyModuleProperties> {

    private final long capacity;

    public EnergyModuleProperties(TechLevel techLevel, long capacity, int width, int height) {
        super(techLevel, width, height);
        this.capacity = capacity;
    }

    public EnergyModuleProperties(TechLevel techLevel, long capacity) {
        this(techLevel, capacity, 1, 2);
    }

    public long getCapacity() {
        return capacity;
    }

    @Override
    public void addCombinedStats(List<EnergyModuleProperties> propertiesList, List<ITextComponent> combinedStats) {
        combinedStats.add(new StringTextComponent("//TODO Combined Energy stats"));
    }

    //    class Impl implements EnergyModuleProperties {
//
//        public int width = 2;
//        public int height = 4;
//        public long capacity;
//
//        public Impl(long capacity) {
//            this.capacity = capacity;
//        }
//
//        @Override
//        public EnergyModuleProperties setStorageSize(long capacity) {
//            this.capacity = capacity;
//            return this;
//        }
//
//        @Override
//        public EnergyModuleProperties setDimensions(int width, int height) {
//            this.width = width;
//            this.height = height;
//            return this;
//        }
//
//        public void merge(Impl other) {
//            capacity += other.capacity;
//        }
//
//    }
}
