package jp.kotmw.splatoon.gamedatas;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import jp.kotmw.splatoon.gamedatas.DataStore.WeaponType;

public class WeaponData {
	private String name;
	private String displayname;
	private Material itemtype;
	private WeaponType type;
	private int damage;
	private float cost;
	private String subweaponname;
	private double speed;

	private String soundId;
	private float soundVolume;
	private float soundPitch;

	private double airResistance;
	private double flyingPaintRadius;
	/**威力減衰(爆発)までの時間*/
	private int flyDecayTick;
	/**威力減衰の速さ*/
	private double flyDecayRatio;

	/**ヒト速*/
	private double ManSpeed;
	/**硬直時間*/
	private int RecoilTick;

	private boolean NoGravity;

	//Shooter
	private int Firespeed;
	private double radius;
	private int angle;

	//Roller
	private int InkSplash;
	private int InkSplashDamage;
	private int InkSplashAngle;
	private double InkSplashPaintRadius;
	private double InkSplashCost;
	private int SlowLevel;
	//Charger
	private int fullcharge;
	private double range;
	private boolean isBowItem;

	//Blaster
	private double ExplosionRadius;
	private int ExplosionDamage;

	public WeaponData(String name, FileConfiguration file) {
		this.name = name;
		this.displayname = ChatColor.translateAlternateColorCodes('&', file.getString("ItemInfo.Name"));
		this.itemtype = getItemType(file.getString("ItemInfo.Type"));
		this.type = getWeaponType(file.getString("WeaponInfo.Type"));
		this.damage = file.getInt("WeaponInfo.Damage");
		this.cost = (float) (file.getDouble("WeaponInfo.InkCost")/100);
		this.subweaponname = file.getString("WeaponInfo.SubWeapon");
		this.Firespeed = file.getInt("WeaponInfo.FireSpeed");
		this.speed = file.getDouble("WeaponInfo.Speed");
		this.radius = file.getDouble("WeaponInfo.Radius");
		this.angle = file.getInt("WeaponInfo.Angle");
		this.soundId=file.getString("WeaponInfo.SoundId");
		this.soundVolume = (float) file.getDouble("WeaponInfo.SoundVolume");
		this.soundPitch = (float) file.getDouble("WeaponInfo.SoundPitch");
		this.airResistance = file.getDouble("WeaponInfo.AirResistance");
		this.flyingPaintRadius=file.getDouble("WeaponInfo.FlyingPaintRadius");
		this.flyDecayRatio=file.getDouble("WeaponInfo.FlyDecayRatio");
		this.flyDecayTick=file.getInt("WeaponInfo.FlyDecayTick");

		this.ManSpeed=file.getDouble("WeaponInfo.ManSpeed",1.0);
		this.RecoilTick=file.getInt("WeaponInfo.RecoilTick");
		this.NoGravity=file.getBoolean("WeaponInfo.NoGravity");


		this.InkSplash = file.getInt("WeaponInfo.InkSplash");
		this.InkSplashDamage = file.getInt("WeaponInfo.InkSplashDamage");
		this.InkSplashAngle = file.getInt("WeaponInfo.InkSplashAngle");
		this.InkSplashPaintRadius = file.getDouble("WeaponInfo.InkSplashPaintRadius");
		this.InkSplashCost = file.getDouble("WeaponInfo.InkSplashCost")/100;
		this.SlowLevel = file.getInt("WeaponInfo.SlowLevel");
		this.fullcharge = file.getInt("WeaponInfo.FullCharge");
		this.range = file.getDouble("WeaponInfo.Range");
		this.isBowItem =file.getBoolean("WeaponInfo.isBowItem");

		this.ExplosionDamage=file.getInt("WeaponInfo.ExplosionDamage");
		this.ExplosionRadius=file.getDouble("WeaponInfo.ExplosionRadius");
	}
	public boolean NoGravity(){
		return NoGravity;
	}
	public boolean isBowItem(){
		return isBowItem;
	}
	public double getManSpeed() {
		return ManSpeed;
	}

	public int getRecoilTick() {
		return RecoilTick;
	}

	public double getExplosionRadius() {
		return ExplosionRadius;
	}

	public int getExplosionDamage() {
		return ExplosionDamage;
	}

	public double getRange() {return range;}
	public int getFlyDecayTick() {return flyDecayTick;}
	public double getFlyDecayRatio() {return flyDecayRatio;}

	public double getFlyingPaintRadius() {
		return flyingPaintRadius;
	}
	public double getAirResistance() {
		return airResistance;
	}
	public double getInkSplashCost() {
		return InkSplashCost;
	}
	public double getInkSplashPaintRadius() {
		return InkSplashPaintRadius;
	}


	public String getSoundId() {
		return soundId;
	}


	public float getSoundVolume() {
		return soundVolume;
	}

	public float getSoundPitch() {
		return soundPitch;
	}

	public String getName() {return name;}

	public String getDisplayname() {return displayname;}

	public Material getItemtype() {return itemtype;}

	public WeaponType getType() {return type;}

	public int getDamage() {return damage;}

	public int getFirespeed() {return Firespeed;}

	public double getSpeed(){return speed;}

	public double getRadius() {return radius;}

	public int getAngle() {return angle;}

	public float getCost() {return cost;}

	public int getInkSplash() {return InkSplash;}

	public int getInkSplashDamage() {return InkSplashDamage;}

	public int getInkSplashAngle() {return InkSplashAngle;}

	public int getSlowLevel() {return SlowLevel;}

	public int getFullcharge() {return fullcharge;}

	public String getSubWeapon() {return subweaponname;}

	private Material getItemType(String item) {
		for(Material type : Material.values())
			if(type.toString().equalsIgnoreCase(item))
				return type;
		return null;
	}

	private WeaponType getWeaponType(String weapon) {
		for(WeaponType type : WeaponType.values())
			if(type.toString().equalsIgnoreCase(weapon))
				return type;
		return null;
	}
}
