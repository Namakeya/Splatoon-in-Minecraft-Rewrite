package jp.kotmw.splatoon.maingame;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jp.kotmw.splatoon.gamedatas.*;
import jp.kotmw.splatoon.specialweapon.SpecialWeapon;
import jp.kotmw.splatoon.util.SplatColor;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

import jp.kotmw.splatoon.Main;
import jp.kotmw.splatoon.event.BattleStartEvent;
import jp.kotmw.splatoon.event.PlayerGameJoinEvent;
import jp.kotmw.splatoon.event.PlayerGameLeaveEvent;
import jp.kotmw.splatoon.filedatas.PlayerFiles;
import jp.kotmw.splatoon.gamedatas.DataStore.BattleType;
import jp.kotmw.splatoon.gamedatas.DataStore.GameStatusEnum;
import jp.kotmw.splatoon.maingame.threads.AnimationRunnable;
import jp.kotmw.splatoon.maingame.threads.TransferRunnable;
import jp.kotmw.splatoon.manager.Paint;
import jp.kotmw.splatoon.util.MessageUtil;
import org.bukkit.util.BoundingBox;

public class MainGame extends MessageUtil {

	public static String Prefix = "[ "+ChatColor.GREEN+"Splatoon"+ChatColor.WHITE+" ] ";

	public static final int minPlayer=8;

	public static void join(Player player, WaitRoomData data) {
		if(DataStore.hasPlayerData(player.getName()))
			return;
		PlayerGameJoinEvent event = new PlayerGameJoinEvent(player, data);
		Bukkit.getPluginManager().callEvent(event);
		if(event.isCancelled()) {
			String reason = event.getCancelreason();
			if(reason == null)
				reason = "参加を拒否されました";
			player.sendMessage(MainGame.Prefix+reason);
			return;
		}
		PlayerFiles.checkPlayerData(player.getUniqueId().toString(), player.getName());
		if(data.getTask() == null) {
			BukkitRunnable task = new AnimationRunnable(data);
			task.runTaskTimer(Main.main, 0, 60);
			data.setTask(task);
		}
		PlayerData playerdata = new PlayerData(player.getName());
		playerdata.setRoom(data.getName());
		playerdata.setRollBackLocation(player.getLocation());
		playerdata.setAllCansel(true);
		List<ItemStack> items = new ArrayList<>();
		for(int i = 0 ; i <= 40 ; i++) {
			ItemStack item = player.getInventory().getItem(i);
			if(item == null)
				item = new ItemStack(Material.AIR);
			items.add(item);
		}
		playerdata.setRollBackItems(items);
		player.teleport(new Location(Bukkit.getWorld(data.getWorld()),
				data.getX(),
				data.getY(),
				data.getZ(),
				(float)data.getYaw(),
				(float)data.getPitch()));
		chooseWeapon(player);
		for(PotionEffect effect : player.getActivePotionEffects())
			player.removePotionEffect(effect.getType());
		DataStore.addPlayerData(player.getName(), playerdata);
		GameSigns.UpdateJoinSign(data.getName());

		player.sendMessage("Your Weapon : "+ChatColor.AQUA+playerdata.getWeapon());
		//if(DataStore.getRoomPlayersList(data.getName()).size() < minPlayer)
			//return;
		//start(data);
	}

	public static void chooseWeapon(Player player){
		player.getInventory().clear();
		player.getInventory().setItem(0, GameItems.getSelectItem());
		player.getInventory().setItem(8, GameItems.getLeaveItem());
	}

	public static PlayerData leave(Player player) {
		if(!DataStore.hasPlayerData(player.getName()))
			return null;
		PlayerGameLeaveEvent event = new PlayerGameLeaveEvent(DataStore.getPlayerData(player.getName()));
		Bukkit.getPluginManager().callEvent(event);
		PlayerData data = DataStore.removePlayerData(player.getName());
		if(data.getTask() != null) {
			data.getTask().cancel();
			data.setTask(null);
		}
		player.getInventory().clear();
		sendTitle(data, 0, 1, 0, " ", " ");
		sendActionBar(data, " ");
		int i = 0;
		for(ItemStack item : data.getRollbackItems()) {
			player.getInventory().setItem(i, item);
			i++;
		}
		player.teleport(data.getRollBackLocation());
		GameSigns.UpdateJoinSign(data.getRoom());
		return data;
	}

	public static void start(WaitRoomData roomdata) {
		ArenaData arenadata = getRandomArena(roomdata);
		if(arenadata == null) {
			DataStore.addPriority(roomdata.getName());
			for(PlayerData data : DataStore.getRoomPlayersList(roomdata.getName())) {
				sendMessage(data, ChatColor.GREEN+"全てのステージが使用中のため、転送がキャンセルされました");
				sendMessage(data, ChatColor.GREEN+"ステージが1つでも空いたら"+ChatColor.GOLD.toString()+ChatColor.BOLD+"この待機部屋が最優先で"+ChatColor.GREEN+"転送されるので今しばらくお待ちください");
			}
			return;
		}
		for(PlayerData data : DataStore.getRoomPlayersList(roomdata.getName()))
			sendActionBar(data, " ");
		arenadata.setGameStatus(GameStatusEnum.INGAME);
		BattleStartEvent event = new BattleStartEvent(roomdata, arenadata);
		Bukkit.getPluginManager().callEvent(event);
		new TransferRunnable(arenadata, roomdata.getName(), DataStore.getConfig().getTransfarCount(), roomdata.getBattleType()).runTaskTimer(Main.main, 0, 20);
	}

	public static void start(WaitRoomData roomdata, ArenaData arenadata) {
		if(roomdata == null || arenadata == null)
			return;
		roomdata.getTask().cancel();
		roomdata.setTask(null);
		for(PlayerData data : DataStore.getRoomPlayersList(roomdata.getName())) {
			sendActionBar(data, " ");
			//sendMessage(data, ChatColor.GOLD+"大変長らくお待たせいたしました、ただいまより転送いたします");
		}
		arenadata.setGameStatus(GameStatusEnum.INGAME);
		BattleStartEvent event = new BattleStartEvent(roomdata, arenadata);
		Bukkit.getPluginManager().callEvent(event);
		GameSigns.UpdateJoinSign(roomdata.getName());
		new TransferRunnable(arenadata, roomdata.getName(), DataStore.getConfig().getTransfarCount(), roomdata.getBattleType()).runTaskTimer(Main.main, 0, 20);
	}

	public static void setInv(PlayerData data) {
		Player player = Bukkit.getPlayer(data.getName());
		player.getInventory().clear();
		if(data.getWeapon() == null)
			data.setWeapon(DataStore.getStatusData(data.getName()).getWeapons().get(0));
		WeaponData weapon=DataStore.getWeapondata(data.getWeapon());
		ArenaData arena = DataStore.getArenaData(data.getArena());
		SplatColor color=arena.getSplatColor(data.getTeamid());
		player.getInventory().setItem(0, GameItems.getWeaponItem(weapon));
		//player.getInventory().setItem(1, GameItems.getSubWeaponItem(weapon));
		if(!SpecialWeapon.SPPENABLED || data.isCanUseSpecial()) {
			player.getInventory().setItem(2, GameItems.getSpecialWeaponItem(weapon));
		}
		player.getInventory().setItem(EquipmentSlot.HEAD,GameItems.getHelmetItem(weapon,color));
		/*for(int i=0;i<9;i++){
			if(player.getInventory().getItem(i) == null){
				player.getInventory().setItem(i,GameItems.getFillerItem(DataStore.getWeapondata(data.getWeapon())));
			}
		}*/
		if(weapon.isBowItem()){
			player.getInventory().setItem(9,new ItemStack(Material.ARROW,64));
		}
	}

	public static void setSquidInv(PlayerData data) {
		Player player = Bukkit.getPlayer(data.getName());
		player.getInventory().clear();
		if(data.getWeapon() == null)
			data.setWeapon(DataStore.getStatusData(data.getName()).getWeapons().get(0));
		WeaponData weapon=DataStore.getWeapondata(data.getWeapon());
		ArenaData arena = DataStore.getArenaData(data.getArena());
		SplatColor color=arena.getSplatColor(data.getTeamid());
		player.getInventory().setItem(EquipmentSlot.HEAD,null);
		for(int i=0;i<3;i++){
			if(player.getInventory().getItem(i) == null){
				player.getInventory().setItem(i,GameItems.getFillerItem(DataStore.getWeapondata(data.getWeapon())));
			}
		}
	}

	/**
	 *
	 * @param data 対象の待機部屋のデータ
	 *
	 * @return ランダムで選択されたステージ、すべてのステージが使用不可能な場合はnullを返す
	 *
	 */
	public static ArenaData getRandomArena(WaitRoomData data) {
		List<String> arenas = data.getSelectList();
		Collections.shuffle(arenas);
		for(String arena : arenas) {
			if(!DataStore.hasArenaData(arena))
				continue;
			ArenaData arenadata = DataStore.getArenaData(arena);
			if(data.getBattleType() == BattleType.Splat_Zones) {
					if(arenadata.getAreaPosition1().getX() == 0
							&& arenadata.getAreaPosition1().getY() == 0
							&& arenadata.getAreaPosition1().getZ() == 0)
						if((arenadata.getAreaPosition1().getX() == arenadata.getAreaPosition2().getX())
								&&(arenadata.getAreaPosition1().getY() == arenadata.getAreaPosition2().getY())
								&&(arenadata.getAreaPosition1().getZ() == arenadata.getAreaPosition2().getZ()))
						continue;
			}
			if(!data.isLimitBreak())
				if((arenadata.getMaximumTeamNum() > 2) || (arenadata.getMaximumPlayerNum() > 4))
					continue;
			if(arenadata.getGameStatus() == GameStatusEnum.ENABLE)
				return arenadata;
		}
		return null;
	}

	public static void end(ArenaData data, boolean tf) {
		Paint.RollBack(data);
		data.clearStatus();
		for(PlayerData datalist : DataStore.getArenaPlayersList(data.getName())) {
			Player player = Bukkit.getPlayer(datalist.getName());
			if(!tf) {
				PlayerStatusData statusData = datalist.getPlayerStatus();
				if(data.getWinTeam() == datalist.getTeamid()) statusData.updateWinnerScore();
				else statusData.updateLoserScore();
				if(statusData.updateScoreExp()) {
					player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.7f, 1);
					player.sendMessage(MainGame.Prefix+ChatColor.GREEN+"ランクが上がりました！");
				}
			}
			player.getInventory().clear();
			player.setFoodLevel(20);
			player.setGameMode(Bukkit.getDefaultGameMode());
			for(PotionEffect potion : player.getActivePotionEffects())
				player.removePotionEffect(potion.getType());
			sendTitle(datalist, 0, 1, 0, " ", " ");
			sendActionBar(datalist, " ");
			int i = 0;
			for(ItemStack item : datalist.getRollbackItems()) {
				player.getInventory().setItem(i, item);
				i++;
			}
			player.teleport(datalist.getRollBackLocation());
			DataStore.removePlayerData(datalist.getName());
		}
	}

	public static void Teleport(PlayerData data, Location loc) {
		Bukkit.getPlayer(data.getName()).teleport(loc);
	}


	/*public static void Damager(PlayerData data, Block block, int damage) {
		if(block == null)
			return;
		Damager(data, block.getX(), block.getY(), block.getZ(), 20);
	}*/
	
	public static void Damager(PlayerData data, Location location, int damage) {
		if(location == null)
			return;
		Damager(data, location.getBlockX(), location.getBlockY(), location.getBlockZ(), damage);
	}

	public static void Damager(PlayerData player, int x, int y, int z, int damage) {
		for(PlayerData data : DataStore.getArenaPlayersList(player.getArena())) {
			Player target = Bukkit.getPlayer(data.getName());
			Location loc = target.getLocation();
			int target_x = loc.getBlockX(),
					target_y = loc.getBlockY(),
					target_z = loc.getBlockZ();
			if(x == target_x && (y == target_y) && z == target_z) {
				damageTarget(player,target,damage);
			}
		}
		Player playere = Bukkit.getPlayer(player.getName());
		for(Entity e: playere.getWorld().getNearbyEntities(new BoundingBox(x,y,z,x+1,y+0.5,z+1))){
			if(e instanceof Creeper){
				Creeper target=((Creeper)e);
				damageTarget(player,target,damage);
			}
		}

	}

	/*public static void SphereDamager(PlayerData player, Location center, SubWeaponData subWeaponData, double radius) {
		for(PlayerData data : DataStore.getArenaPlayersList(player.getArena())) {
			if(data.getName() == player.getName())
				continue;
			if(player.getTeamid() == data.getTeamid())
				continue;
			Player target = Bukkit.getPlayer(data.getName());
			double distance = center.distance(target.getLocation());
			if(radius > distance) target.damage(subWeaponData.getMaxDamage());
		}
	}*/

	public static void SphereDamager(PlayerData player, Location center, SubWeaponData subWeaponData, double radius, boolean crit) {
		SphereDamager(player, center, subWeaponData.getCriticalDamage(), subWeaponData.getMaxDamage(),subWeaponData.getMinDamage(), radius, crit);
	}
	
	public static void SphereDamager(PlayerData player, Location center, double critDamage,double maxDamage,double minDamage, double radius, boolean crit) {
		for(PlayerData data : DataStore.getArenaPlayersList(player.getArena())) {

			Player target = Bukkit.getPlayer(data.getName());
			double distance = center.distance(target.getLocation());
			if(radius > distance) {
				if(crit && 0.5 > distance) {
					damageTarget(player,target,critDamage);
					continue;
				}
				//距離減衰式を入れる
				damageTarget(player,target,maxDamage);
			}else if(1.5*radius > distance && minDamage>0.1) {
				damageTarget(player,target,minDamage);
			}
		}
		Player playere = Bukkit.getPlayer(player.getName());
		for(Entity e: playere.getWorld().getNearbyEntities
				(new BoundingBox(center.getX()-radius, center.getY()-radius,center.getZ()-radius
						,center.getX()+radius, center.getY()+radius,center.getZ()+radius))){
			if(e instanceof Creeper){
				Creeper target=(Creeper) e;
				double distance = center.distance(target.getLocation());
				if(radius > distance) {
					if(crit && 0.5 > distance) {
						damageTarget(player,target,critDamage);
						continue;
					}
					//距離減衰式を入れる
					damageTarget(player,target,maxDamage);
				}else if(1.5*radius > distance && minDamage>0.1) {
					damageTarget(player,target,minDamage);
				}
			}
		}
	}

	/**Mainly for Debugging*/
	public static void damageTarget(PlayerData pd, LivingEntity target, double amount){
		if(canDamage(pd,target)) {
			System.out.println(pd.getName() + " dealt "+String.format("%.0f",amount)+" damage to " + target.getName());
			target.setMaximumNoDamageTicks(1);
			target.damage(amount);
			Player player=Bukkit.getPlayer(pd.getName());
			if(target.isDead() || ((target instanceof Player) && DataStore.getPlayerData(target.getName()).isDead())) {
				SplatColor color = DataStore.getArenaData(pd.getArena()).getSplatColor(pd.getTeamid());
				MessageUtil.sendMessageforArena(pd.getArena(), color.getChatColor() + pd.getWeapon() + " -> " + target.getName());

				player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PLAYER_SPLASH, 3f, 0.6f);
				MainGame.fireworkExplosion(target.getLocation(), color);
				Paint.SpherePaint(target.getLocation(), 4, pd);

			}
		}else{
			System.out.println(pd.getName() + " cannot damage " + target.getName());
		}
	}

	public static boolean canDamage(PlayerData pd, LivingEntity target){
		Player pe = Bukkit.getPlayer(pd.getName());
		return canDamage(pe,pd,target);
	}
/** @param attacker should be Player
 * @param defender should be LivingEntity*/
	public static boolean canDamage(Entity attacker,Entity defender){
		if(attacker instanceof Player && defender instanceof LivingEntity) {
			Player pe=(Player)attacker;
			LivingEntity target=(LivingEntity) defender;
			if (DataStore.hasPlayerData(pe.getName())) {
				PlayerData pd = DataStore.getPlayerData(pe.getName());

				return canDamage(pe, pd, target);
			}
		}
		return false;

	}

	private static boolean canDamage(Player pe,PlayerData pd,LivingEntity target){
		if(!target.isDead()) {
			if (target instanceof Creeper) {
				return true;
			} else if (target instanceof Player) {
				Player tpe = (Player) target;
				PlayerData tpd = DataStore.getPlayerData(tpe.getName());
				if (tpd != null) {
					if (pd.getTeamid() != tpd.getTeamid() && !tpd.isDead()) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public static void fireworkExplosion(Location location, SplatColor color){
		FireworkEffect effect=FireworkEffect.builder().withColor(color.getColor()).with(FireworkEffect.Type.BALL).build();

		Firework fw = (Firework) location.getWorld().spawnEntity(location, EntityType.FIREWORK);
		FireworkMeta fwm = fw.getFireworkMeta();

		fwm.addEffect(effect);
		fw.setSilent(true);

		fw.setFireworkMeta(fwm);
		fw.detonate();
	}
	

	/*DataStore.getArenaPlayersList(player.getArena()).stream()
	.filter(data -> data.getName() != player.getName())
	.filter(data -> player.getTeamid() == data.getTeamid())
	.filter(data -> (0.5 > center.distance(Bukkit.getPlayer(data.getName()).getLocation())))
	.forEach(data -> Bukkit.getPlayer(data.getName()).damage(critical));*/

	public static int getTime(BattleType type) {
		switch(type) {
		case Turf_War:
			return DataStore.getConfig().getTimeforTurfWar();
		case Splat_Zones:
			return DataStore.getConfig().getTimeforSplatZones();
		case Rain_Maker:
			break;
		}
		return 180;
	}
	
	public static void sync(Runnable runnable) {
		Bukkit.getScheduler().runTask(Main.main, runnable);
	}
}
