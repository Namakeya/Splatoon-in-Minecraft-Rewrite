package jp.kotmw.splatoon.maingame;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jp.kotmw.splatoon.gamedatas.SpecialWeaponData;
import jp.kotmw.splatoon.specialweapon.SpecialWeapon;
import jp.kotmw.splatoon.util.SplatColor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import jp.kotmw.splatoon.gamedatas.DataStore;
import jp.kotmw.splatoon.gamedatas.DataStore.WeaponType;
import jp.kotmw.splatoon.gamedatas.SubWeaponData;
import jp.kotmw.splatoon.gamedatas.WeaponData;
import org.bukkit.inventory.meta.LeatherArmorMeta;

public class GameItems {

	public static String weaponselector = ChatColor.GREEN
			+ ChatColor.BOLD.toString()
			+ "WeaponSelector"
			+ ChatColor.GRAY
			+ " [Right Click]";
	public static String leave = ChatColor.YELLOW
			+ ChatColor.BOLD.toString()
			+ "Leave"
			+ ChatColor.GRAY
			+ " [Right Click]";

	public static ItemStack getSelectItem() {
		ItemStack item = new ItemStack(Material.CHEST);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(weaponselector);
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.GOLD + "右クリックで武器選択の表示");
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}
	
	public static ItemStack getLeaveItem() {
		ItemStack item = new ItemStack(Material.IRON_DOOR);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(leave);
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.GREEN + "右クリックで退室します");
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}

	public static ItemStack getWeaponItem(WeaponData data) {
		ItemStack item = new ItemStack(data.getItemtype());
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(data.getDisplayname());
		List<String> lore = new ArrayList<String>();
		lore.add(ChatColor.GOLD+"SplatoonPluginItem");
		lore.add("----Status-----");
		lore.add("WeaponType: "+data.getType().toString());
		lore.add("Ink Cost: "+data.getCost());
		lore.add("Damage: "+data.getDamage());
		if(data.getType() == WeaponType.Shooter)
			lore.add("Fire speed: "+data.getFirespeed());
		else if(data.getType() == WeaponType.Roller)
			lore.add("SlowLevel: "+data.getSlowLevel());
		meta.setLore(lore);
		//meta.addAttributeModifier(Attribute.GENERIC_MOVEMENT_SPEED,
				//new AttributeModifier(UUID.randomUUID(),"speed",-0.2,AttributeModifier.Operation.MULTIPLY_SCALAR_1, EquipmentSlot.HAND));

		item.setItemMeta(meta);
		return item;
	}

	public static ItemStack getSubWeaponItem(WeaponData data) {
		SubWeaponData subweapon = DataStore.getSubWeaponData(data.getSubWeapon());
		ItemStack item = new ItemStack(subweapon.getItemtype());
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(subweapon.getType().toString());
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.GOLD+"SplatoonPluginItem");
		lore.add("-----Status-----");
		lore.add("Bomb");
		lore.add("Bomb!");
		lore.add("Bomb!!");
		lore.add("Bomb!!!");
		lore.add("Bomb!!!!");
		meta.setLore(lore);
		//meta.addAttributeModifier(Attribute.GENERIC_MOVEMENT_SPEED,
				//new AttributeModifier(UUID.randomUUID(),"speed",-0.2,AttributeModifier.Operation.MULTIPLY_SCALAR_1, EquipmentSlot.HAND));
		item.setItemMeta(meta);
		return item;
	}

	public static ItemStack getSpecialWeaponItem(WeaponData data) {
		//System.out.println("specialweaponname: "+data.getSpecialWeapon());
		SpecialWeaponData specialweapon = DataStore.getSpecialWeaponData(data.getSpecialWeapon());
		if(SpecialWeapon.SPECIALENABLED && specialweapon!=null) {
			ItemStack item = new ItemStack(specialweapon.getItemtype());
			ItemMeta meta = item.getItemMeta();
			meta.addEnchant(Enchantment.ARROW_DAMAGE,1,true);
			meta.setDisplayName(specialweapon.getType().toString());
			List<String> lore = new ArrayList<>();
			lore.add(ChatColor.GOLD + "SplatoonPluginItem");
			lore.add("-----Status-----");
			lore.add("Bomb");
			lore.add("Bomb!");
			lore.add("Bomb!!");
			lore.add("Bomb!!!");
			lore.add("Bomb!!!!");
			meta.setLore(lore);
			//meta.addAttributeModifier(Attribute.GENERIC_MOVEMENT_SPEED,
			//new AttributeModifier(UUID.randomUUID(),"speed",-0.2,AttributeModifier.Operation.MULTIPLY_SCALAR_1, EquipmentSlot.HAND));
			item.setItemMeta(meta);
			return item;
		}else{
			System.out.println("specialweapon is null ");
			return null;
		}
	}
	public static ItemStack getFillerItem(WeaponData data) {
		ItemStack item=new ItemStack(Material.STONE_BUTTON,64);
		ItemMeta meta = item.getItemMeta();
		//meta.addAttributeModifier(Attribute.GENERIC_MOVEMENT_SPEED,
				//new AttributeModifier(UUID.randomUUID(),"speed",-0.2,AttributeModifier.Operation.MULTIPLY_SCALAR_1, EquipmentSlot.HAND));
		item.setItemMeta(meta);
		return item;
	}

	public static ItemStack getHelmetItem(WeaponData data, SplatColor color) {
		ItemStack item=new ItemStack(Material.LEATHER_HELMET,1);
		LeatherArmorMeta meta = (LeatherArmorMeta)item.getItemMeta();
		meta.setColor(color.getColor());
		meta.addAttributeModifier(Attribute.GENERIC_MOVEMENT_SPEED,
		new AttributeModifier(UUID.randomUUID(),"speed",data.getManSpeed()-1,AttributeModifier.Operation.MULTIPLY_SCALAR_1, EquipmentSlot.HEAD));
		item.setItemMeta(meta);
		return item;
	}

	public static ItemStack getFeetItemSquid(WeaponData data) {
		ItemStack item=new ItemStack(Material.STONE_BUTTON,1);
		ItemMeta meta = item.getItemMeta();
		meta.addAttributeModifier(Attribute.GENERIC_MOVEMENT_SPEED,
				new AttributeModifier(UUID.randomUUID(),"speed",0.4,AttributeModifier.Operation.MULTIPLY_SCALAR_1, EquipmentSlot.FEET));
		item.setItemMeta(meta);
		return item;
	}
}
