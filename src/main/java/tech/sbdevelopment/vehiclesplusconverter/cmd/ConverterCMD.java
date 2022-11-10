package tech.sbdevelopment.vehiclesplusconverter.cmd;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import tech.sbdevelopment.vehiclesplusconverter.handlers.Converter;

import static tech.sbdevelopment.vehiclesplusconverter.utils.MainUtil.__;

public class ConverterCMD implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (label.equalsIgnoreCase("vpconvert")) {
            if (!sender.hasPermission("vpconvert.convert")) {
                sender.sendMessage(__("&7[&3&lVehiclesPlusConverter&7] &fYou do not have the right permissions to use this command."));
                return false;
            }

            if (args.length >= 1 && args[0].equalsIgnoreCase("confirm")) {
                Converter.convert(sender);
            } else {
                sender.sendMessage(__("&7[&3&lVehiclesPlusConverter&7] &fPlease use &b/vpconvert confirm &fto start the conversion!"));
                sender.sendMessage(__("&7[&3&lVehiclesPlusConverter&7] &4&lPLEASE NOTE: &cExisting v3 vehicles may be overwritten! &c&lCreate a backup before confirming."));
            }
        }
        return true;
    }
}
