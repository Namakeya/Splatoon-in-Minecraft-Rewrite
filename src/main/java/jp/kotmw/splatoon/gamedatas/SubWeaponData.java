package jp.kotmw.splatoon.gamedatas;

import org.bukkit.Material;

import jp.kotmw.splatoon.gamedatas.DataStore.BombType;

public class SubWeaponData {
	private String name;
	private Material itemtype;
	private BombType type;
	private double maxdamage;
	private double mindamage;
	private double critical;
	private float cost;
	private int cooltime;

	public SubWeaponData(String name, Material material, BombType bombtype, double maxdamage, double mindamage, double critical, double ink, int inkcooltime) {
		this.name = name;
		this.itemtype = material;
		this.type = bombtype;
		this.maxdamage = maxdamage;
		this.mindamage = mindamage;
		this.critical = critical;
		this.cost = (float) (ink/100);
		this.cooltime = inkcooltime;
	}

	public String getName() {return name;}
	public Material getItemtype() {return itemtype;}
	public BombType getType() {return type;}
	public double getMaxDamage() {return maxdamage;}
	public double getMinDamage() {return mindamage;}
	public double getCriticalDamage() {return critical;}
	public float getCost() {return cost;}
	public int getCooltime() {return cooltime;}

	/*public void setName(String name) {this.name = name;}
	public void setItemtype(Material itemtype) {this.itemtype = itemtype;}
	public void setType(BombType type) {this.type = type;}
	public void setDamage(int damage) {this.damage = damage;}
	public void setCost(float cost) {this.cost = cost;}
	public void setCooltime(int cooltime) {this.cooltime = cooltime;}*/
}
