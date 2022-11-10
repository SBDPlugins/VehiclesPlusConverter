/*
 * This file is part of MapReflectionAPI.
 * Copyright (c) 2022 inventivetalent / SBDevelopment - All Rights Reserved
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package tech.sbdevelopment.vehiclesplusconverter.utils;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

public class YamlFile {
    private final JavaPlugin plugin;
    private final File file;
    private FileConfiguration fileConfiguration;

    public YamlFile(JavaPlugin plugin, File file) {
        this.plugin = plugin;
        this.file = file;
    }

    public void reload() {
        this.fileConfiguration = YamlConfiguration.loadConfiguration(this.file);
    }

    public FileConfiguration getFile() {
        if (this.fileConfiguration == null)
            reload();

        return this.fileConfiguration;
    }

    public void save() {
        if (this.fileConfiguration == null || this.file == null)
            return;

        try {
            this.fileConfiguration.save(this.file);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Couldn't save the file " + file.getName() + ".", e);
        }
    }
}