/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.survivaloreminer;

import com.mcmiddleearth.pluginutil.NumericUtil;
import java.util.LinkedHashMap;
import java.util.Map;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author Eriol_Eandur
 */
public class SurvialOreMinerPlugin extends JavaPlugin implements Listener{

    private Map<Integer, Material> oreProbs = new LinkedHashMap<>();
    
    private final String PROBABILITIES = "Probabilities";
    @Override
    public void onEnable() {
        Configuration config = this.getConfig();
        ConfigurationSection oreSection = config.getConfigurationSection(PROBABILITIES);
        if(oreSection==null) {
            config.createSection(PROBABILITIES);
            oreSection = config.getConfigurationSection(PROBABILITIES);
            oreSection.set(Material.COBBLESTONE.name(), 50);
            oreSection.set(Material.COAL.name(), 20);
            oreSection.set(Material.IRON_ORE.name(), 10);
            oreSection.set(Material.GOLD_ORE.name(), 6);
            oreSection.set(Material.REDSTONE.name(), 6);
            oreSection.set(Material.INK_SACK.name(), 6);
            oreSection.set(Material.DIAMOND.name(), 2);
            saveConfig();
        }  
        Map<String,Object> oreConfig = oreSection.getValues(false);
        int probSum = 0;
        for(String oreName: oreConfig.keySet()) {
                probSum += (int) oreConfig.get(oreName);
                oreProbs.put(probSum, Material.getMaterial(oreName));
        }
        getServer().getPluginManager().registerEvents(this, this);
        getLogger().info("Enabled!");
    }
    
    @EventHandler
    public void playerMinesOre(BlockBreakEvent event) {
        if(event.getPlayer()!=null 
                && event.getPlayer().getGameMode().equals(GameMode.SURVIVAL)
                && event.getBlock().getType().equals(Material.STONE)) {
            event.setCancelled(true);
            BlockState blockState = event.getBlock().getState();
            blockState.setType(Material.AIR);
            blockState.update(true, true);
            int rand = NumericUtil.getRandom(0, 100);
            for(Integer oreProb: oreProbs.keySet()) {
                if(rand<=oreProb) {
                    Inventory inventory = event.getPlayer().getInventory();
                    Material material = oreProbs.get(oreProb);
                    if(material.equals(Material.INK_SACK)) {
                        inventory.addItem(new ItemStack(material,1,(short) 1, (byte)4));
                    } else {
                        inventory.addItem(new ItemStack(oreProbs.get(oreProb),1));
                    }
                    break;
                }
            }
        }
    }
    
}
