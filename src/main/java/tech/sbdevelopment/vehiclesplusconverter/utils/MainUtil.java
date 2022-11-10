package tech.sbdevelopment.vehiclesplusconverter.utils;

import net.md_5.bungee.api.ChatColor;
import nl.sbdeveloper.vehiclesplus.storage.file.JSONFile;
import tech.sbdevelopment.vehiclesplusconverter.VehiclesPlusConverter;
import tech.sbdevelopment.vehiclesplusconverter.api.ConversionException;
import tech.sbdevelopment.vehiclesplusconverter.api.InvalidConversionException;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

public class MainUtil {
    private MainUtil() {
    }

    public static String __(String in) {
        return ChatColor.translateAlternateColorCodes('&', in);
    }

    public static String getClassByFullName(String name) {
        String[] split = name.split("\\.");
        if (split.length == 0) return name;
        return split[split.length - 1]; //Last position
    }

    public static String getTypeIdByClass(String baseVehicle, String type) throws ConversionException {
        switch (type) {
            case "BikeType":
                return "bike";
            case "BoatType":
                return "boat";
            case "CarType":
                return "car";
            case "HelicopterType":
                return "helicopter";
            case "HovercraftType":
                return "hovercraft";
            case "PlaneType":
                return "plane";
            default:
                throw new InvalidConversionException("vehicleType", baseVehicle);
        }
    }

    public static void saveToVehiclesPlus(Object data, String subFolder, String fileName) {
        File parentFolders = new File(nl.sbdeveloper.vehiclesplus.VehiclesPlus.getInstance().getDataFolder(), subFolder);
        if (!parentFolders.exists() && !parentFolders.mkdirs()) return;

        JSONFile jsonFile = new JSONFile(nl.sbdeveloper.vehiclesplus.VehiclesPlus.getInstance(), subFolder + "/" + fileName);
        try {
            jsonFile.write(data);
        } catch (IOException e) {
            VehiclesPlusConverter.getInstance().getLogger().log(Level.SEVERE, "Couldn't save to the file " + fileName, e);
        }
    }
}
