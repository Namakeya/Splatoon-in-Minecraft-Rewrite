package jp.kotmw.splatoon.maingame;

import java.util.ArrayList;
import java.util.List;

import jp.kotmw.splatoon.util.MessageUtil;
import jp.kotmw.splatoon.util.SplatColor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import jp.kotmw.splatoon.gamedatas.DataStore;
import jp.kotmw.splatoon.gamedatas.DataStore.WeaponType;
import jp.kotmw.splatoon.gamedatas.PlayerData;
import jp.kotmw.splatoon.gamedatas.PlayerStatusData;
import jp.kotmw.splatoon.gamedatas.WeaponData;

public class InvMenu implements Listener {

	private String selectinv = ChatColor.BOLD.toString()+ChatColor.BLACK+"武器のカテゴリを選択してください";
	private String selectweapon = ChatColor.BOLD.toString()+ChatColor.BLACK+"武器を選択してください";

	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		//System.out.println("interact");
		if(!DataStore.hasPlayerData(e.getPlayer().getName()))
			return;
		//System.out.println("player data exists");
		Player player = e.getPlayer();
		ItemStack item = player.getInventory().getItemInMainHand();
		Action action = e.getAction();
		if(action == Action.LEFT_CLICK_AIR
				|| action == Action.LEFT_CLICK_BLOCK
				|| action == Action.PHYSICAL)
			return;
		if(item == null
				|| item.getType() == Material.AIR
				|| !item.getItemMeta().hasDisplayName()
				|| !item.getItemMeta().hasLore())
			return;
		String itemname = item.getItemMeta().getDisplayName();
		//System.out.println("itemname : "+itemname+" weaponselector : "+GameItems.weaponselector);
		if(itemname.equalsIgnoreCase(GameItems.weaponselector)) {
			//System.out.println("Opening Inventory");
			player.openInventory(openMenu());
		}
		else if(itemname.equalsIgnoreCase(GameItems.leave))
			MainGame.leave(player);
	}

	@EventHandler
	public void onInvClick(InventoryClickEvent e) {
		//System.out.println(e.getView().getTitle());
		if(!DataStore.hasPlayerData(e.getWhoClicked().getName()))
			return;
		PlayerData data = DataStore.getPlayerData(e.getWhoClicked().getName());
		e.setCancelled(true);
		if(e.getCurrentItem() == null
				|| e.getCurrentItem().getType() == Material.AIR
				|| !e.getCurrentItem().hasItemMeta())
			return;
		//System.out.println("inventoryname: "+e.getView().getTitle()+" selectinv: "+selectinv);
		if(e.getView().getTitle().equalsIgnoreCase(selectinv)) {
			if(!isType(ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName())))
				return;
			WeaponType type = WeaponType.valueOf(ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName()));

			//System.out.println("type : "+type.name());

			e.getWhoClicked().openInventory(getWeaponSelector(type, data.getName()));
		} else if(e.getView().getTitle().equalsIgnoreCase(selectweapon)) {
			//System.out.println("selectweapon");
			String weapon = e.getCurrentItem().getItemMeta().getDisplayName();
			if(!DataStore.hasWeaponData(weapon))
				return;
			data.setWeapon(weapon);
			MessageUtil.sendMessage(data,"You chose "+ ChatColor.AQUA+weapon+" !");
			e.getWhoClicked().closeInventory();
		}
	}

	public Inventory openMenu() {
		Inventory inv = Bukkit.createInventory(null, 9, selectinv);

		ItemStack shooter = new ItemStack(Material.WOODEN_HOE);
		ItemMeta shootermeta = shooter.getItemMeta();
		shootermeta.setDisplayName(ChatColor.GREEN+"Shooter");
		shootermeta.setLore(getDescriptionLore(WeaponType.Shooter));
		shooter.setItemMeta(shootermeta);

		ItemStack roller = new ItemStack(Material.STICK);
		ItemMeta rollermeta = roller.getItemMeta();
		rollermeta.setDisplayName(ChatColor.BLUE+"Roller");
		rollermeta.setLore(getDescriptionLore(WeaponType.Roller));
		roller.setItemMeta(rollermeta);

		ItemStack charger = new ItemStack(Material.DIAMOND_HOE);
		ItemMeta chargermeta = charger.getItemMeta();
		chargermeta.setDisplayName(ChatColor.YELLOW+"Charger");
		chargermeta.setLore(getDescriptionLore(WeaponType.Charger));
		charger.setItemMeta(chargermeta);

		ItemStack blaster = new ItemStack(Material.GOLDEN_HOE);
		ItemMeta blastermeta = blaster.getItemMeta();
		blastermeta.setDisplayName(ChatColor.RED+"Blaster");
		blastermeta.setLore(getDescriptionLore(WeaponType.Blaster));
		blaster.setItemMeta(blastermeta);

		inv.setItem(0, shooter);
		inv.setItem(2, roller);
		inv.setItem(4, charger);
		inv.setItem(6, blaster);

		return inv;
	}

	public Inventory getWeaponSelector(WeaponType type, String player) {
		Inventory inv = Bukkit.createInventory(null, 9, selectweapon);
		int i = 0;
		for(WeaponData weapon : getPlayerWeapons(player)) {
			//System.out.println(weapon.getName());
			if(!weapon.getType().equals(type))
				continue;
			//System.out.println(weapon.getName()+"is type");
			ItemStack item = new ItemStack(weapon.getItemtype());
			ItemMeta meta = item.getItemMeta();
			meta.setDisplayName(weapon.getName());
			List<String> lore = new ArrayList<String>();
			lore.add("サブウェポン: "+weapon.getSubWeapon());
			lore.add("スペシャル: "+weapon.getSpecialWeapon());
			meta.setLore(lore);
			item.setItemMeta(meta);
			inv.setItem(i, item);
			i++;
		}
		return inv;
	}

	private List<String> getDescriptionLore(WeaponType type) {
		List<String> lore = new ArrayList<String>();
		switch(type) {
		case Shooter:
			lore.add(ChatColor.GREEN+"シューター系武器のセレクターを開きます");
			break;
		case Roller:
			lore.add(ChatColor.BLUE+"ローラー系武器のセレクターを開きます");
			break;
		case Charger:
			lore.add(ChatColor.YELLOW+"チャージャー系武器のセレクターを開きます");
			break;
			case Blaster:
				lore.add(ChatColor.RED+"ブラスター系武器のセレクターを開きます");
				break;
		default:
			break;
		}
		return lore;
	}

	private List<WeaponData> getPlayerWeapons(String player) {
		PlayerStatusData data = DataStore.getStatusData(player);
		List<WeaponData> weapons = new ArrayList<WeaponData>();
		for(String weapon : data.getWeapons())
			weapons.add(DataStore.getWeapondata(weapon));
		return weapons;
	}

	public boolean isType(String typename) {
		for(WeaponType type : WeaponType.values()) {
			if(typename.equalsIgnoreCase(type.toString()))
				return true;
		}
		return false;
	}
}
