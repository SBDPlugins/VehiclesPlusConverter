package tech.sbdevelopment.vehiclesplusconverter;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import tech.sbdevelopment.vehiclesplusconverter.cmd.ConverterCMD;

public final class VehiclesPlusConverter extends JavaPlugin {
    private static VehiclesPlusConverter instance;

    public static VehiclesPlusConverter getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;

        if (!Bukkit.getPluginManager().isPluginEnabled("VehiclesPlusPro") || !Bukkit.getPluginManager().isPluginEnabled("VehiclesPlus")) {
            Bukkit.getLogger().severe("Make sure both the v2 and v3 fully load without any errors! Disabling the converter...");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        getCommand("vpconvert").setExecutor(new ConverterCMD());
    }

    @Override
    public void onDisable() {
        instance = null;
    }
}
