package jp.kotmw.splatoon.mainweapons;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import jp.kotmw.splatoon.Main;
import jp.kotmw.splatoon.gamedatas.DataStore;
import jp.kotmw.splatoon.gamedatas.DataStore.WeaponType;
import jp.kotmw.splatoon.gamedatas.PlayerData;
import jp.kotmw.splatoon.gamedatas.WeaponData;
import jp.kotmw.splatoon.maingame.MainGame;
import jp.kotmw.splatoon.mainweapons.threads.ChargerRunnable;

/**UNUSED*/
public class Charger implements Listener {

	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		if(!DataStore.hasPlayerData(e.getPlayer().getName()))
			return;
		if(DataStore.getPlayerData(e.getPlayer().getName()).getArena() == null)
			return;
		Action action = e.getAction();
		if(action == Action.LEFT_CLICK_AIR
				|| action == Action.LEFT_CLICK_BLOCK
				|| action == Action.PHYSICAL)
			return;
		Player p = e.getPlayer();
		ItemStack item = p.getInventory().getItemInMainHand();
		PlayerData data = DataStore.getPlayerData(p.getName());
		if(DataStore.getWeapondata(data.getWeapon()).getType() != WeaponType.Charger)
			return;
		if(data.isAllCancel()
				|| item == null
				|| item.getType() == Material.AIR
				|| !item.hasItemMeta()
				|| item.getItemMeta().getLore().size() < 5
				|| !item.getItemMeta().getDisplayName().equalsIgnoreCase(data.getWeapon()))
			return;
		WeaponData weapon = DataStore.getWeapondata(data.getWeapon());
		if(p.getExp() < weapon.getCost() && data.getCharge() <= 0) {
			MainGame.sendActionBar(data, ChatColor.RED+"インクがありません!");
			return;
		}
		if(data.getCharge() <= 0)
			p.setExp((float) (p.getExp()-weapon.getCost()));
		if(data.getTask() == null|| data.getTask().isCancelled()) {
			BukkitRunnable task = new ChargerRunnable(p.getName(),weapon.getFullcharge()/4);
			task.runTaskTimer(Main.main, 0, weapon.getFullcharge()/4);
			data.setTask(task);
		}
		data.setTick(1);
	}
}
