package tech.sbdevelopment.vehiclesplusconverter;

import me.legofreak107.vehiclesplus.VehiclesPlus;
import me.legofreak107.vehiclesplus.vehicles.api.VehiclesPlusAPI;
import me.legofreak107.vehiclesplus.vehicles.fuel.FuelType;
import me.legofreak107.vehiclesplus.vehicles.rims.RimDesign;
import me.legofreak107.vehiclesplus.vehicles.vehicles.objects.BaseVehicle;
import me.legofreak107.vehiclesplus.vehicles.vehicles.objects.StorageVehicle;
import me.legofreak107.vehiclesplus.vehicles.vehicles.objects.addons.Part;
import me.legofreak107.vehiclesplus.vehicles.vehicles.objects.addons.Wheel;
import me.legofreak107.vehiclesplus.vehicles.vehicles.objects.addons.seats.BikeSeat;
import me.legofreak107.vehiclesplus.vehicles.vehicles.objects.addons.seats.Seat;
import me.legofreak107.vehiclesplus.vehicles.vehicles.objects.addons.seats.TurretSeat;
import me.legofreak107.vehiclesplus.vehicles.vehicles.objects.addons.skins.BikeSkin;
import me.legofreak107.vehiclesplus.vehicles.vehicles.objects.addons.skins.Rotor;
import me.legofreak107.vehiclesplus.vehicles.vehicles.objects.addons.skins.Skin;
import me.legofreak107.vehiclesplus.vehicles.vehicles.objects.addons.skins.Turret;
import nl.sbdeveloper.vehiclesplus.api.vehicles.VehicleModel;
import nl.sbdeveloper.vehiclesplus.api.vehicles.settings.UpgradableSetting;
import nl.sbdeveloper.vehiclesplus.api.vehicles.settings.impl.*;
import nl.sbdeveloper.vehiclesplus.storage.file.JSONFile;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

import static tech.sbdevelopment.vehiclesplusconverter.utils.MainUtil.__;

public class Converter {
    private Converter() {
    }

    private static final Sounds defaultSounds = Sounds.builder()
            .idle(new Sounds.Sound("car.idle", 6))
            .start(new Sounds.Sound("car.start", 2))
            .accelerate(new Sounds.Sound("car.accelerate", 2))
            .driving(new Sounds.Sound("car.driving", 2))
            .slowingDown(new Sounds.Sound("car.slowingdown", 2))
            .build();

    public static void convert(CommandSender sender) {
        sender.sendMessage(__("&7[&3&lVehiclesPlusConverter&7] &fStarting v2 to v3 conversion..."));

        sender.sendMessage(__("&7[&3&lVehiclesPlusConverter&7] &fConverting rim designs..."));
        convertRims();

        sender.sendMessage(__("&7[&3&lVehiclesPlusConverter&7] &fConverting fuel types..."));
        convertFuels();

        sender.sendMessage(__("&7[&3&lVehiclesPlusConverter&7] &fConverting vehicle models..."));
        convertVehicleModels();

        sender.sendMessage(__("&7[&3&lVehiclesPlusConverter&7] &fConverting vehicles..."));
        convertVehicles();

        Bukkit.getScheduler().runTaskLater(VehiclesPlusConverter.getInstance(), () -> Bukkit.getServer().shutdown(), 20L * 15);
        new BukkitRunnable() {
            int counter = 15;

            @Override
            public void run() {
                sender.sendMessage(__("&7[&3&lVehiclesPlusConverter&7] &f" + (counter == 15 ? "Conversion finished! " : "") + "&bRebooting in &3" + counter + " &bseconds..."));
                counter--;
            }
        }.runTaskTimer(VehiclesPlusConverter.getInstance(), 20L, 20L);
    }

    private static void convertRims() {
        for (Map.Entry<String, RimDesign> entry : VehiclesPlus.getVehicleManager().getRimDesignHashMap().entrySet()) {
            nl.sbdeveloper.vehiclesplus.api.vehicles.rims.RimDesign rd = nl.sbdeveloper.vehiclesplus.api.vehicles.rims.RimDesign.builder()
                    .name(entry.getValue().getName())
                    .price(entry.getValue().getPrice())
                    .skin(entry.getValue().getSkin())
                    .build();

            nl.sbdeveloper.vehiclesplus.api.VehiclesPlusAPI.getRimDesigns().put(entry.getKey(), rd);
        }
    }

    private static void convertFuels() {
        for (Map.Entry<String, FuelType> entry : VehiclesPlus.getVehicleManager().getFuelTypeHashMap().entrySet()) {
            nl.sbdeveloper.vehiclesplus.api.vehicles.fuel.FuelType ft = nl.sbdeveloper.vehiclesplus.api.vehicles.fuel.FuelType.builder()
                    .name(entry.getValue().getName())
                    .item(entry.getValue().getFuelItem())
                    .pricePerLiter(entry.getValue().getPricePerLiter())
                    .build();

            nl.sbdeveloper.vehiclesplus.api.VehiclesPlusAPI.getFuelTypes().put(entry.getKey(), ft);
        }
    }

    private static void convertVehicleModels() {
        for (BaseVehicle baseVehicle : VehiclesPlusAPI.getVehicleManager().getBaseVehicleMap().values()) {
            try {
                VehicleModel.VehicleModelBuilder vehicleModelBuilder = VehicleModel.builder()
                        .id(baseVehicle.getName())
                        .displayName(baseVehicle.getName())
                        .typeId(getTypeIdByClass(baseVehicle.getName(), getClassByFullName(baseVehicle.getVehicleType())))
                        .availableColors(baseVehicle.getBaseColorList());

                for (Part part : baseVehicle.getPartList()) {
                    if (part instanceof BikeSeat) {
                        BikeSeat bikeSeat = (BikeSeat) part;
                        vehicleModelBuilder = vehicleModelBuilder.part(new nl.sbdeveloper.vehiclesplus.api.vehicles.parts.impl.seat.BikeSeat(
                                bikeSeat.getXOffset(),
                                bikeSeat.getYOffset(),
                                bikeSeat.getZOffset(),
                                bikeSeat.getSteer()
                        ));
                    } else if (part instanceof TurretSeat) {
                        TurretSeat turretSeat = (TurretSeat) part;
                        vehicleModelBuilder = vehicleModelBuilder.part(new nl.sbdeveloper.vehiclesplus.api.vehicles.parts.impl.seat.TurretSeat(
                                turretSeat.getXOffset(),
                                turretSeat.getYOffset(),
                                turretSeat.getZOffset(),
                                baseVehicle.getPartList().stream().filter(Turret.class::isInstance).findFirst().orElseThrow(() -> new ConversionException("No Turret found while loading TurretSeat in file", baseVehicle.getName())).getUID()
                        ));
                    } else if (part instanceof Seat) {
                        Seat seat = (Seat) part;
                        vehicleModelBuilder = vehicleModelBuilder.part(new nl.sbdeveloper.vehiclesplus.api.vehicles.parts.impl.seat.Seat(
                                seat.getXOffset(),
                                seat.getYOffset(),
                                seat.getZOffset(),
                                seat.getSteer()
                        ));
                    } else if (part instanceof BikeSkin) {
                        BikeSkin bikeSkin = (BikeSkin) part;
                        vehicleModelBuilder = vehicleModelBuilder.part(new nl.sbdeveloper.vehiclesplus.api.vehicles.parts.impl.skin.BikeSkin(
                                bikeSkin.getXOffset(),
                                bikeSkin.getYOffset(),
                                bikeSkin.getZOffset(),
                                bikeSkin.getSkinColored()
                        ));
                    } else if (part instanceof Rotor) {
                        Rotor rotor = (Rotor) part;
                        vehicleModelBuilder = vehicleModelBuilder.part(new nl.sbdeveloper.vehiclesplus.api.vehicles.parts.impl.skin.Rotor(
                                rotor.getXOffset(),
                                rotor.getYOffset(),
                                rotor.getZOffset(),
                                rotor.getSkinColored()
                        ));
                    } else if (part instanceof Turret) {
                        Turret turret = (Turret) part;
                        vehicleModelBuilder = vehicleModelBuilder.part(new nl.sbdeveloper.vehiclesplus.api.vehicles.parts.impl.skin.Turret(
                                turret.getXOffset(),
                                turret.getYOffset(),
                                turret.getZOffset(),
                                turret.getSkin(),
                                turret.getExplosionSize(),
                                turret.getAmmo()
                        ));
                    } else if (part instanceof Skin) {
                        Skin skin = (Skin) part;
                        vehicleModelBuilder = vehicleModelBuilder.part(new nl.sbdeveloper.vehiclesplus.api.vehicles.parts.impl.skin.Skin(
                                skin.getXOffset(),
                                skin.getYOffset(),
                                skin.getZOffset(),
                                skin.getSkinColored()
                        ));
                    } else if (part instanceof Wheel) {
                        Wheel wheel = (Wheel) part;
                        vehicleModelBuilder = vehicleModelBuilder.part(new nl.sbdeveloper.vehiclesplus.api.vehicles.parts.impl.Wheel(
                                wheel.getXOffset(),
                                wheel.getYOffset(),
                                wheel.getZOffset(),
                                VehiclesPlus.getVehicleManager().getRimDesignHashMap().values().stream().findFirst().orElseThrow(() -> new ConversionException("No RimDesign found while loading Wheel in file", baseVehicle.getName())).getName(),
                                wheel.getColor(),
                                wheel.getSteering(),
                                wheel.getRotationOffset()
                        ));
                    }
                }

                VehicleModel model = vehicleModelBuilder
                        .maxSpeed(new UpgradableSetting(
                                baseVehicle.getSpeedSettings().getBase(),
                                baseVehicle.getSpeedSettings().getMax(),
                                baseVehicle.getSpeedSettings().getStep(),
                                baseVehicle.getSpeedSettings().getUpgradeCost()
                        ))
                        .fuelTank(new UpgradableSetting(
                                baseVehicle.getFuelTankSettings().getBase(),
                                baseVehicle.getFuelTankSettings().getMax(),
                                baseVehicle.getFuelTankSettings().getStep(),
                                baseVehicle.getFuelTankSettings().getUpgradeCost()
                        ))
                        .turningRadius(new UpgradableSetting(
                                baseVehicle.getTurningRadiusSettings().getBase(),
                                baseVehicle.getTurningRadiusSettings().getMax(),
                                baseVehicle.getTurningRadiusSettings().getStep(),
                                baseVehicle.getTurningRadiusSettings().getUpgradeCost()
                        ))
                        .acceleration(new UpgradableSetting(
                                baseVehicle.getAccelerationSettings().getBase(),
                                baseVehicle.getAccelerationSettings().getMax(),
                                baseVehicle.getAccelerationSettings().getStep(),
                                baseVehicle.getAccelerationSettings().getUpgradeCost()
                        ))
                        .horn(new Horn(
                                baseVehicle.getHornSettings().getEnabled(),
                                baseVehicle.getHornSettings().getSound().name()
                        ))
                        .drift(baseVehicle.getDrift())
                        .exhaust(new Exhaust(
                                baseVehicle.getExhaustSettings().getEnabled(),
                                baseVehicle.getExhaustSettings().getXOffset(),
                                baseVehicle.getExhaustSettings().getYOffset(),
                                baseVehicle.getExhaustSettings().getZOffset(),
                                Particle.valueOf(baseVehicle.getExhaustSettings().getParticleName())
                        ))
                        .exitWhileMoving(baseVehicle.getCanExitWhileMoving())
                        .price(baseVehicle.getPrice())
                        .fuel(new Fuel(
                                baseVehicle.getFuelSettings().getType(),
                                baseVehicle.getFuelSettings().getUsage()
                        ))
                        .health(baseVehicle.getHealth())
                        .trunkSize(baseVehicle.getTrunkSize())
                        .hitbox(new Hitbox(
                                baseVehicle.getHitbox().getLength(),
                                baseVehicle.getHitbox().getWidth(),
                                baseVehicle.getHitbox().getHeight()
                        ))
                        .realisticSteering(baseVehicle.getSteeringType())
                        .permissions(Permissions.builder()
                                .buy(baseVehicle.getPermissions().getBuyPermission())
                                .ride(baseVehicle.getPermissions().getRidePermission())
                                .sitWithoutRidePermission(baseVehicle.getPermissions().getEnterWithoutRidePermission())
                                .adjust("vp.adjust." + baseVehicle.getName())
                                .spawn("vp.spawn." + baseVehicle.getName())
                                .build())
                        .sounds(defaultSounds)
                        .build();

                save(model, "vehicles/" + model.getTypeId(), model.getId());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static void convertVehicles() {
        for (Map.Entry<UUID, List<StorageVehicle>> set : VehiclesPlusAPI.getVehicleManager().getPlayerVehicleHashMap().entrySet()) {
            UUID ownerUUID = set.getKey();
            for (StorageVehicle vehicle : set.getValue()) {
                try {
                    nl.sbdeveloper.vehiclesplus.api.vehicles.impl.StorageVehicle newVehicle = new nl.sbdeveloper.vehiclesplus.api.vehicles.impl.StorageVehicle(
                            nl.sbdeveloper.vehiclesplus.api.VehiclesPlusAPI.getVehicleModels().values().stream().filter(v -> v.getId().equalsIgnoreCase(vehicle.getBaseVehicle())).findFirst().orElseThrow(() -> new ConversionException("No VehicleModel found for", vehicle.getUuid())),
                            ownerUUID
                    );

                    newVehicle.save();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static String getClassByFullName(String name) {
        String[] split = name.split("\\.");
        if (split.length == 0) return name;
        return split[split.length - 1]; //Last position
    }

    private static String getTypeIdByClass(String baseVehicle, String type) throws ConversionException {
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

    private static void save(Object data, String subFolder, String fileName) {
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
