package com.greenfirework.pfaamod;

import java.io.File;

import net.minecraftforge.common.config.Configuration;

public class Config {


    public static int reactorCoolantBoilTemperature = 127315; // 100 °C coolant before hotCoolant is produced    
    public static int reactorCoolantExportHeat = 100; // Bad name.  How much delta T is withdrawn from the reactor coolant per unit of hotCoolant produced.
    public static int reactorCoolantSpecificHeat = 100; // Bad name.  How much delta T is withdrawn from the reactor coolant per unit of hotCoolant produced.
    public static int reactorCoolantPerBlock = 4000; // Coolant stored per block in the reactor, in mB
    public static int reactorHeatXferCoeff = 256; // Reactor heat transfer coefficient
    public static int reactorAssemblySpecificHeat = 256; // Reactor heat transfer coefficient
    
    public static int fuelRodMaxDurability = 20000;
    public static int fuelRodFluxDurability = 20;
    
    public static int fuelRodFluxHeat = 10;
    public static int fuelRodFlux = 10;
    
    
    public static final String REACTOR = "reactor";
    
    public static void synchronizeConfiguration(File configFile) {
        Configuration configuration = new Configuration(configFile);
       

        reactorCoolantBoilTemperature = configuration.getInt("boilTemperature",  REACTOR,  127315,  0,  90000,  "Threshold temperature in hundreds Kelvin before converting coolant to hot coolant");
        reactorCoolantExportHeat = configuration.getInt("latentHeat",  REACTOR,  100,  1,  10000,  "Approximate thermal energy consumed to convert coolant to hot coolant.  Unspecified units.");
        reactorCoolantPerBlock = configuration.getInt("blockCapacity",  REACTOR,  4000,  1000,  32000,  "Coolant capacity, per block of space in the fission reactor pressure vessel");
        reactorHeatXferCoeff = configuration.getInt("xferCoefficient",  REACTOR,  40,  1,  256,  "Thermal transfer coefficient for delta-T heat exchange");
        reactorAssemblySpecificHeat = configuration.getInt("assemblySpecificHeat",  REACTOR,  16,  1,  102400,  "Specific Heat for reactor channel assemblies");
        reactorCoolantSpecificHeat = configuration.getInt("coolantSpecificHeat",  REACTOR,  16,  1,  102400,  "Specific Heat for reactor coolant");
        fuelRodMaxDurability = configuration.getInt("fuelRodDurability",  REACTOR,  20000,  1,  100000,  "Fuel rod durability in seconds");
        fuelRodFluxHeat = configuration.getInt("fuelRodFluxHeat",  REACTOR,  10,  1,  1000,  "Thermal energy emitted by fuel rod per unit flux absorbed");
        fuelRodFlux = configuration.getInt("fuelRodFlux",  REACTOR,  10,  1,  1000,  "Units neutron flux emitted by a fuel rod assembly, at maximum durability");
        fuelRodFluxDurability = configuration.getInt("fuelRodFluxDurability",  REACTOR,  20,  1,  1000,  "Units neutron flux absorbed per unit durability lost");
        
        if (configuration.hasChanged()) {
            configuration.save();
        }
    }
}
