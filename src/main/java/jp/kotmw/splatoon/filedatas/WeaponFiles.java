package jp.kotmw.splatoon.filedatas;

import java.io.File;
import java.util.List;

import jp.kotmw.splatoon.gamedatas.SpecialWeaponData;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import jp.kotmw.splatoon.gamedatas.DataStore;
import jp.kotmw.splatoon.gamedatas.DataStore.BombType;
import jp.kotmw.splatoon.gamedatas.DataStore.WeaponType;
import jp.kotmw.splatoon.gamedatas.SubWeaponData;
import jp.kotmw.splatoon.gamedatas.WeaponData;

public class WeaponFiles extends PluginFiles {

	static String filedir = "Weapons";

	public static void createTemplateShooterFile() {
		if(!DirFile(filedir, "SplatShooter").exists()) {

			FileConfiguration file = new YamlConfiguration();
			file.set("ItemInfo.Name", "SplatShooter");
			file.set("ItemInfo.Type", Material.WOODEN_HOE.toString());
			file.set("WeaponInfo.Type", WeaponType.Shooter.toString());
			file.set("WeaponInfo.Damage", 4);
			file.set("WeaponInfo.FireSpeed", 4);
			file.set("WeaponInfo.Radius", 2);
			file.set("WeaponInfo.FlyingPaintRadius", 1);
			file.set("WeaponInfo.InkCost", 2.5);
			file.set("WeaponInfo.Angle", 6);
			file.set("WeaponInfo.Speed", 1.1);
			file.set("WeaponInfo.SoundId", "minecraft:entity.zombie.attack_wooden_door");
			file.set("WeaponInfo.SoundVolume", 0.5);
			file.set("WeaponInfo.SoundPitch", 1.5);
			file.set("WeaponInfo.AirResistance", 0.0);
			//file.set("WeaponInfo.ManSpeed", 0.8);
			file.set("WeaponInfo.SubWeapon", "QuickBomb");

			file.set("WeaponInfo.SpecialWeapon", "MultiMissile");
			file.set("WeaponInfo.SpecialPoint", 400);
			SettingFiles(file, DirFile(filedir, "SplatShooter"));
		}

		if(!DirFile(filedir, "WakabaShooter").exists()) {

			FileConfiguration file = new YamlConfiguration();
			file.set("ItemInfo.Name", "WakabaShooter");
			file.set("ItemInfo.Type", Material.OAK_SAPLING.toString());
			file.set("WeaponInfo.Type", WeaponType.Shooter.toString());
			file.set("WeaponInfo.Damage", 3);
			file.set("WeaponInfo.FireSpeed", 3);
			file.set("WeaponInfo.Radius", 1.8);
			file.set("WeaponInfo.FlyingPaintRadius", 1);
			file.set("WeaponInfo.InkCost", 1);
			file.set("WeaponInfo.Angle", 25);
			file.set("WeaponInfo.Speed", 0.9);
			file.set("WeaponInfo.SoundId", "minecraft:entity.snowball.throw");
			file.set("WeaponInfo.SoundVolume", 0.5);
			file.set("WeaponInfo.SoundPitch", 0.8);
			file.set("WeaponInfo.AirResistance", 0.05);
			file.set("WeaponInfo.FlyDecayTick", 30);
			file.set("WeaponInfo.FlyDecayRatio", 5.0);
			file.set("WeaponInfo.SubWeapon", "SplashBomb");
			file.set("WeaponInfo.SpecialWeapon", "Barrier");
			file.set("WeaponInfo.SpecialPoint", 350);
			SettingFiles(file, DirFile(filedir, "WakabaShooter"));
		}
	}

	public static void createTemplateRollerFile() {
		if(!DirFile(filedir, "SplatRoller").exists()) {
			FileConfiguration file = new YamlConfiguration();
			file.set("ItemInfo.Name", "SplatRoller");
			file.set("ItemInfo.Type", Material.STICK.toString());
			file.set("WeaponInfo.Type", WeaponType.Roller.toString());
			file.set("WeaponInfo.Damage", 20);
			file.set("WeaponInfo.Radius", 2);
			file.set("WeaponInfo.InkSplash", 5);
			file.set("WeaponInfo.FireSpeed", 12);
			file.set("WeaponInfo.InkSplashDamage", 10);
			file.set("WeaponInfo.InkSplashAngle", 75);
			file.set("WeaponInfo.InkSplashPaintRadius", 2);
			file.set("WeaponInfo.InkSplashCost", 10);
			file.set("WeaponInfo.SlowLevel", 0);
			file.set("WeaponInfo.InkCost", 0.2);
			file.set("WeaponInfo.AirResistance", 0.3);
			file.set("WeaponInfo.FlyDecayTick", 10);
			file.set("WeaponInfo.FlyDecayRatio", 5.0);
			file.set("WeaponInfo.RecoilTick", 10);

			file.set("WeaponInfo.Speed", 2.5);
			file.set("WeaponInfo.SoundId", "minecraft:entity.player.splash.high_speed");
			file.set("WeaponInfo.SoundVolume", 0.8);
			file.set("WeaponInfo.SoundPitch", 1.3);

			file.set("WeaponInfo.SubWeapon", "SuckerBomb");

			file.set("WeaponInfo.SpecialWeapon", "SplashBomb");
			file.set("WeaponInfo.SpecialPoint", 350);
			SettingFiles(file, DirFile(filedir, "SplatRoller"));
		}
	}

	public static void createTemplateChargerFile() {
		if(!DirFile(filedir, "3k-Scope").exists()) {

			FileConfiguration file2 = new YamlConfiguration();
		/*
		file.set("ItemInfo.Name", "SplatCharger");
		file.set("ItemInfo.Type", Material.DIAMOND_HOE.toString());
		file.set("WeaponInfo.Type", WeaponType.Charger.toString());
		file.set("WeaponInfo.Damage", 20);
		file.set("WeaponInfo.InkCost", 18);
		file.set("WeaponInfo.FullCharge", 20);
		file.set("WeaponInfo.Range", 20);

		file.set("WeaponInfo.SoundId", "minecraft:entity.zombie.break_wooden_door");
		file.set("WeaponInfo.SoundVolume", 0.8);
		file.set("WeaponInfo.SoundPitch", 1.3);

		file.set("WeaponInfo.SubWeapon", "SplashBomb");

		 */

			file2.set("ItemInfo.Name", "3k-Scope");
			file2.set("ItemInfo.Type", Material.SPYGLASS.toString());
			file2.set("WeaponInfo.Type", WeaponType.Charger.toString());
			file2.set("WeaponInfo.Damage", 22);
			file2.set("WeaponInfo.InkCost", 30);
			file2.set("WeaponInfo.FullCharge", 28);

			file2.set("WeaponInfo.Radius", 3);
			file2.set("WeaponInfo.FlyingPaintRadius", 2);

			file2.set("WeaponInfo.FlyDecayTick", 15);
			file2.set("WeaponInfo.FlyDecayRatio", 10.0);

			file2.set("WeaponInfo.Speed", 4);

			file2.set("WeaponInfo.SoundId", "minecraft:entity.zombie.break_wooden_door");
			file2.set("WeaponInfo.SoundVolume", 1);
			file2.set("WeaponInfo.SoundPitch", 1.2);

			file2.set("WeaponInfo.SpecialWeapon", "SuperSensor");
			file2.set("WeaponInfo.SpecialPoint", 350);

			file2.set("WeaponInfo.SubWeapon", "Trap");
			SettingFiles(file2, DirFile(filedir, "3k-Scope"));
		}
		//////////////////
		if(!DirFile(filedir, "SplatCharger").exists()) {
			FileConfiguration file = new YamlConfiguration();
			file.set("ItemInfo.Name", "SplatCharger");
			file.set("ItemInfo.Type", Material.BOW.toString());
			file.set("WeaponInfo.Type", WeaponType.Charger.toString());
			file.set("WeaponInfo.Damage", 22);
			file.set("WeaponInfo.InkCost", 18);
			file.set("WeaponInfo.FullCharge", 20);

			file.set("WeaponInfo.Radius", 3);
			file.set("WeaponInfo.FlyingPaintRadius", 2);

			file.set("WeaponInfo.FlyDecayTick", 8);
			file.set("WeaponInfo.FlyDecayRatio", 10.0);

			file.set("WeaponInfo.Speed", 3);

			file.set("WeaponInfo.isBowItem", true);

			file.set("WeaponInfo.SoundId", "minecraft:entity.zombie.break_wooden_door");
			file.set("WeaponInfo.SoundVolume", 1);
			file.set("WeaponInfo.SoundPitch", 1.2);

			file.set("WeaponInfo.SpecialWeapon", "MultiMissile");
			file.set("WeaponInfo.SpecialPoint", 400);

			file.set("WeaponInfo.SubWeapon", "SplashBomb");
			SettingFiles(file, DirFile(filedir, "SplatCharger"));
		}
	}

	public static void createTemplateBlasterFile() {
		if(!DirFile(filedir, "CrafBlaster").exists()) {
			FileConfiguration file = new YamlConfiguration();
			file.set("ItemInfo.Name", "CrafBlaster");
			file.set("ItemInfo.Type", Material.GOLDEN_HOE.toString());
			file.set("WeaponInfo.Type", WeaponType.Blaster.toString());
			file.set("WeaponInfo.Damage", 16);
			file.set("WeaponInfo.FireSpeed", 15);
			file.set("WeaponInfo.Radius", 2);
			file.set("WeaponInfo.FlyingPaintRadius", 1.5);
			file.set("WeaponInfo.InkCost", 6);
			file.set("WeaponInfo.Speed", 1);
			file.set("WeaponInfo.SoundId", "minecraft:entity.zombie.attack_wooden_door");
			file.set("WeaponInfo.SoundVolume", 0.5);
			file.set("WeaponInfo.SoundPitch", 1.7);
			file.set("WeaponInfo.AirResistance", 0.0);
			file.set("WeaponInfo.ManSpeed", 0.8);
			//file.set("WeaponInfo.RecoilTick", 0);
			file.set("WeaponInfo.NoGravity", true);

			file.set("WeaponInfo.FlyDecayTick", 11);
			file.set("WeaponInfo.ExplosionDamage", 7);
			file.set("WeaponInfo.ExplosionRadius", 3);
			file.set("WeaponInfo.SubWeapon", "Trap");

			file.set("WeaponInfo.SpecialWeapon", "QuickBomb");
			file.set("WeaponInfo.SpecialPoint", 350);
			SettingFiles(file, DirFile(filedir, "CrafBlaster"));
		}
	}

	public static List<String> getWeaponList() {
		return getFileList(new File(filepath + filedir));
	}

	public static File WeaponDir() {
		return new File(filepath + filedir);
	}

	public static boolean exists(String weaponname) {
		for(String weapon : getWeaponList())
			if(weapon.equals(weaponname))
				return true;
		return false;
	}

	public static void AllWeaponReload() {
		for(String weapon : getWeaponList()) {
			FileConfiguration file = YamlConfiguration.loadConfiguration(DirFile(filedir, weapon));
			WeaponData data = new WeaponData(weapon, file);
			DataStore.addWeaponData(weapon, data);
		}
	}

	public static void AllSubWeaponReload() {
		SubWeaponData quickbomb = new SubWeaponData("QuickBomb", Material.SLIME_BALL, BombType.QuickBomb, 12, 5, 12,2, 40, 6);//35, 25
		SubWeaponData splashbomb = new SubWeaponData("SplashBomb", Material.TNT, BombType.SplashBomb, 36, 6, 0,3, 70, 20);//180, 30
		SubWeaponData suckerbomb = new SubWeaponData("SuckerBomb", Material.BREWING_STAND, BombType.SuckerBomb, 36, 6,0, 4, 70, 20);//180, 30
		SubWeaponData trap = new SubWeaponData("Trap", Material.HEAVY_WEIGHTED_PRESSURE_PLATE, BombType.Trap, 9, 7,0, 3, 60, 0);//45,35
		DataStore.addSubWeaponData(quickbomb.getName(), quickbomb);
		DataStore.addSubWeaponData(splashbomb.getName(), splashbomb);
		DataStore.addSubWeaponData(suckerbomb.getName(), suckerbomb);
		DataStore.addSubWeaponData(trap.getName(), trap);
	}

	public static void AllSpecialWeaponReload() {
		SpecialWeaponData multimissile=new SpecialWeaponData("MultiMissile",Material.CARROT, DataStore.SpecialType.MultiMissile);
		SpecialWeaponData barrier=new SpecialWeaponData("Barrier",Material.WHITE_STAINED_GLASS, DataStore.SpecialType.Barrier);
		SpecialWeaponData splashPitcher=new SpecialWeaponData("SplashBomb",Material.TNT, DataStore.SpecialType.SplashBomb);
		SpecialWeaponData quickPitcher=new SpecialWeaponData("QuickBomb",Material.EXPERIENCE_BOTTLE, DataStore.SpecialType.QuickBomb);
		SpecialWeaponData suckerPitcher=new SpecialWeaponData("SuckerBomb",Material.STICKY_PISTON, DataStore.SpecialType.SuckerBomb);
		SpecialWeaponData superSensor=new SpecialWeaponData("SuperSensor",Material.GLOWSTONE, DataStore.SpecialType.SuperSensor);
		DataStore.addSpecialWeaponData(multimissile.getName(),multimissile);
		DataStore.addSpecialWeaponData(barrier.getName(),barrier);
		DataStore.addSpecialWeaponData(splashPitcher.getName(),splashPitcher);
		DataStore.addSpecialWeaponData(quickPitcher.getName(),quickPitcher);
		DataStore.addSpecialWeaponData(suckerPitcher.getName(),suckerPitcher);
		DataStore.addSpecialWeaponData(superSensor.getName(),superSensor);
	}
}
