package jp.kotmw.splatoon.superjump;

import jp.kotmw.splatoon.gamedatas.ArenaData;
import jp.kotmw.splatoon.gamedatas.DataStore;
import jp.kotmw.splatoon.gamedatas.PlayerData;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

public class SuperjumpListener   implements Listener {

    @EventHandler
    public void onInvClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player))
            return;
        if (DataStore.hasPlayerData(e.getWhoClicked().getName())
                &&e.getInventory().getType() == InventoryType.CRAFTING) {

            if (e.getCurrentItem() != null && e.getCurrentItem().getItemMeta() != null && e.getCurrentItem().getItemMeta().getDisplayName()!=null){
                PlayerData player = DataStore.getPlayerData(e.getWhoClicked().getName());
                ItemMeta meta=e.getCurrentItem().getItemMeta();
                String itemname=meta.getDisplayName();
                ArenaData data = DataStore.getArenaData(player.getArena());
                if(data!=null && data.getGameStatus() == DataStore.GameStatusEnum.INGAME && player.getSuperjumpStatus()==0) {
                    if (itemname.equalsIgnoreCase(Superjump.respawnItemName)) {

                        Location respawn = data.getTeamPlayerPosision(player.getTeamid(), player.getPosisionId()).convertLocation();
                        data.getSuperjump().superjumpTo(player, respawn);

                    }else if (itemname.equalsIgnoreCase(Superjump.superjumpItemName) && meta instanceof SkullMeta) {
                        SkullMeta skullMeta=(SkullMeta) meta;
                        OfflinePlayer op=skullMeta.getOwningPlayer();
                        PlayerData pd=DataStore.getPlayerData(op.getName());

                        //System.out.println("call");
                        //System.out.println(op.getPlayer());
                        //System.out.println(pd.getTeamid());
                        //System.out.println(pd.getSuperjumpStatus());
                        if(op.getPlayer()!=null && pd.getTeamid() == player.getTeamid() &&  pd.getSuperjumpStatus()==0) {
                            Location pl = op.getPlayer().getLocation();
                            data.getSuperjump().superjumpTo(player, pl);
                        }

                    }
                    e.getWhoClicked().closeInventory();
                    e.setCancelled(true);
                }
            }

        }
    }
}
