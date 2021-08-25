package jp.kotmw.splatoon;

import jp.kotmw.splatoon.commands.*;
import jp.kotmw.splatoon.mainweapons.*;
import jp.kotmw.splatoon.specialweapon.MultiMissile;
import jp.kotmw.splatoon.subweapon.*;
import jp.kotmw.splatoon.superjump.SuperjumpListener;
import org.bukkit.Material;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import jp.kotmw.splatoon.filedatas.OtherFiles;
import jp.kotmw.splatoon.filedatas.PlayerFiles;
import jp.kotmw.splatoon.filedatas.StageFiles;
import jp.kotmw.splatoon.filedatas.WaitRoomFiles;
import jp.kotmw.splatoon.filedatas.WeaponFiles;
import jp.kotmw.splatoon.gamedatas.ArenaData;
import jp.kotmw.splatoon.gamedatas.DataStore;
import jp.kotmw.splatoon.maingame.GameSigns;
import jp.kotmw.splatoon.maingame.InvMenu;
import jp.kotmw.splatoon.maingame.Listeners;
import jp.kotmw.splatoon.maingame.SquidMode;
import jp.kotmw.splatoon.manager.Paint;

public class Main extends JavaPlugin{

	public static Main main;
	public static Material meaningless;

	@Override
	public void onEnable() {
		main = this;
		getCommand("splatsetting").setExecutor(new SettingCommands());
		getCommand("splatsetting").setTabCompleter(new SettingCompleter());
		getCommand("splatconsole").setExecutor(new ConsoleCommands());
		getCommand("splatoon").setExecutor(new PlayerCommands());
		getCommand("splatoon").setTabCompleter(new PlayerCompleter());
		PluginManager pm = this.getServer().getPluginManager();
		pm.registerEvents(new Listeners(), this);
		pm.registerEvents(new SquidMode(), this);
		pm.registerEvents(new GameSigns(), this);
		pm.registerEvents(new InvMenu(), this);
		pm.registerEvents(new Shooter(), this);
		pm.registerEvents(new Roller(), this);
		pm.registerEvents(new SuperjumpListener(), this);

		//pm.registerEvents(new Charger(), this);
		pm.registerEvents(new ArrowCharger(), this);
		pm.registerEvents(new Blaster(), this);
		//pm.registerEvents(new Bomb(), this);
		pm.registerEvents(new QuickBomb(), this);
		pm.registerEvents(new SplashBomb(), this);
		pm.registerEvents(new SuckerBomb(), this);
		pm.registerEvents(new Trap(), this);
		pm.registerEvents(new MultiMissile(), this);
		//pm.registerEvents(new Barrier(), this);
		OtherFiles.AllTemplateFileGenerator();
		PlayerFiles.AllPlayerFileReload();
		StageFiles.AllStageReload();
		WaitRoomFiles.AllRoomReload();
		OtherFiles.AllSignReload();
		WeaponFiles.AllWeaponReload();
		WeaponFiles.AllSubWeaponReload();
		WeaponFiles.AllSpecialWeaponReload();
		OtherFiles.ConfigReload();
		OtherFiles.RankFileReload();

	}

	@Override
	public void onDisable() {
		DataStore.datasAllClear();
		for(ArenaData data : DataStore.getArenaList()) {
			if(data.getTask()!=null) {
				data.getTask().cancel();
				Paint.RollBack(data);
			}
		}
	}
}
