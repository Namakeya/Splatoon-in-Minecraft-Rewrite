package jp.kotmw.splatoon.gamedatas;

import jp.kotmw.splatoon.gamedatas.DataStore.BombType;
import org.bukkit.Material;
import jp.kotmw.splatoon.gamedatas.DataStore.SpecialType;

public class SpecialWeaponData {
	private String name;
	private Material itemtype;
	private SpecialType type;

	public SpecialWeaponData(String name, Material material, SpecialType bombtype) {
		this.name = name;
		this.itemtype = material;
		this.type = bombtype;
	}

	public String getName() {return name;}
	public Material getItemtype() {return itemtype;}
	public SpecialType getType() {return type;}


	/*public void setName(String name) {this.name = name;}
	public void setItemtype(Material itemtype) {this.itemtype = itemtype;}
	public void setType(BombType type) {this.type = type;}
	public void setDamage(int damage) {this.damage = damage;}
	public void setCost(float cost) {this.cost = cost;}
	public void setCooltime(int cooltime) {this.cooltime = cooltime;}*/
}
