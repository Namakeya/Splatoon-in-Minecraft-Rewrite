package jp.kotmw.splatoon.superjump;

import jp.kotmw.splatoon.Main;
import jp.kotmw.splatoon.gamedatas.ArenaData;
import jp.kotmw.splatoon.gamedatas.DataStore;
import jp.kotmw.splatoon.gamedatas.PlayerData;
import jp.kotmw.splatoon.maingame.SquidMode;
import jp.kotmw.splatoon.maingame.threads.SquidRunnable;
import jp.kotmw.splatoon.mainweapons.threads.ShooterBulletRunnable;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class Superjump{


    public static final String respawnItemName= ChatColor.GREEN+"Respawn";
    public static final String superjumpItemName= ChatColor.BLUE+"Superjump";

    public ArenaData arenaData;

    public Superjump(ArenaData arenaData){
        this.arenaData=arenaData;
    }
    public void superjumpTo(PlayerData player, Location location){
        //System.out.println(player.getName() +" to "+ location);
        BukkitRunnable task = new SuperjumpRunnable(arenaData,player,location.add(0,0.5,0),80);
        task.runTaskTimer(Main.main, 20, 1);
        Player pe= Bukkit.getPlayer(player.getName());
        SquidMode.toSquid(pe,player,false);
        player.setSuperjumpStatus(1);
        pe.playSound(pe.getLocation(), Sound.UI_BUTTON_CLICK,1,1);
    }

    public void setSuperjumpItems(){
        for(PlayerData pd:DataStore.getArenaPlayersList(arenaData.getName())) {
            int slot=18;
            
            Player pe= Bukkit.getPlayer(pd.getName());
            ItemStack respawnItem = new ItemStack(Material.RESPAWN_ANCHOR);
            ItemMeta respawnmeta = respawnItem.getItemMeta();
            Location respawnLoc = arenaData.getTeamPlayerPosision(pd.getTeamid(), pd.getPosisionId()).convertLocation();

            double distance=respawnLoc.distance(pe.getLocation());
            respawnmeta.setDisplayName(respawnItemName);
            List<String> lore = new ArrayList<String>();
            lore.add("Go Back to Respawn");
            lore.add("キョリ : "+String.format("%.0f", distance)+"m");
            respawnmeta.setLore(lore);
            respawnItem.setItemMeta(respawnmeta);
            pe.getInventory().setItem(slot, respawnItem);
        
            for(PlayerData pd2:DataStore.getArenaPlayersList(arenaData.getName())){
                
                if(pd2.getTeamid() == pd.getTeamid() && !pd2.getName().equals(pd.getName())){
                    slot++;
                    Player pe2= Bukkit.getPlayer(pd2.getName());
                    ItemStack playerHeadItem = new ItemStack(Material.PLAYER_HEAD);
                    SkullMeta playerHeadmeta = (SkullMeta) playerHeadItem.getItemMeta();

                    playerHeadmeta.setDisplayName(superjumpItemName);
                    if (playerHeadmeta == null)continue;

                    playerHeadmeta.setOwningPlayer(pe2);

                    Location playerHeadLoc = pe2.getLocation();

                    double dist=playerHeadLoc.distance(pe.getLocation());
                    //playerHeadmeta.setDisplayName(pd2.getName());
                    List<String> lore_ = new ArrayList<String>();
                    lore_.add("Jump to "+pd2.getName());
                    lore_.add("キョリ : "+String.format("%.0f", dist)+"m");
                    playerHeadmeta.setLore(lore_);
                    playerHeadItem.setItemMeta(playerHeadmeta);
                    pe.getInventory().setItem(slot, playerHeadItem);
                }
            }

        }
    }



}
