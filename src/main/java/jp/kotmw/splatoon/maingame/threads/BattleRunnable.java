package jp.kotmw.splatoon.maingame.threads;

import jp.kotmw.splatoon.gamedatas.WeaponData;
import jp.kotmw.splatoon.maingame.GameItems;
import jp.kotmw.splatoon.maingame.Turf_War;
import jp.kotmw.splatoon.mainweapons.MainWeapon;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import jp.kotmw.splatoon.Main;
import jp.kotmw.splatoon.gamedatas.ArenaData;
import jp.kotmw.splatoon.gamedatas.DataStore;
import jp.kotmw.splatoon.gamedatas.DataStore.BattleType;
import jp.kotmw.splatoon.gamedatas.DataStore.GameStatusEnum;
import jp.kotmw.splatoon.gamedatas.PlayerData;
import jp.kotmw.splatoon.maingame.MainGame;
import jp.kotmw.splatoon.maingame.SplatZones;

public class BattleRunnable extends BukkitRunnable {

	private ArenaData arenaData;
	private int tick;
	private int second;
	private BattleType type;
	private String main, sub;
	public boolean endNow=false;

	public BattleRunnable(ArenaData data, int second, BattleType type) {
		this.arenaData = data;
		this.tick = (second+10)*20;
		this.second = second;
		this.type = type;
		this.main = "ナワバリバトル";
		this.sub = "たくさんナワバリを確保しろ！";
		switch(type) {
		case Splat_Zones:
			this.main = "ガチエリア";
			this.sub = "ガチエリアを確保して守りぬけ！";
			break;
		case Rain_Maker:
			break;
		default:
			break;
		}
	}

	/*
	 * スタート前のカウントダウン           10*20tick
	 * ゲーム自体の時間                     180*20tick(可変)
	 */

	@Override
	public void run() {
		//System.out.println("call");
		if((tick > 0 || arenaData.getBattleClass().doExtra(tick)) && !endNow) {
			for(PlayerData data : DataStore.getArenaPlayersList(this.arenaData.getName())) {
				if (data.getSubCooldown() > 0) {
					data.setSubCooldown(data.getSubCooldown() - 1);
				}
				WeaponData weaponData=MainWeapon.getWeaponData(data);
				if(!data.isCanUseSpecial() && data.getSpecialPoint()>= weaponData.getSpecialPoint()){
					MainGame.sendMessage(data,ChatColor.RED+"||| SPECIAL AVAILABLE!! |||");
					Player player=Bukkit.getPlayer(data.getName());
					player.getInventory().setItem(2, GameItems.getSpecialWeaponItem(weaponData));
					data.setCanUseSpecial(true);
				}
			}
			this.arenaData.getBossBar().updateBar();
			if(tick%20 == 0) {
				if(tick > (second+5)*20) {//転送後のTitle表示
					for(PlayerData data : DataStore.getArenaPlayersList(this.arenaData.getName())) {
						MainGame.sendTitle(data, 0, 2, 0, main, sub);
						Player player=Bukkit.getPlayer(data.getName());
						player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP,1,2f);
					}

				} else if(tick <= (second+5)*20&&tick > second*20) {//カウントダウン
					int count = tick/20-second;
					for(PlayerData data : DataStore.getArenaPlayersList(this.arenaData.getName())) {
						MainGame.sendTitle(data,
								0,
								2,
								0,
								ChatColor.WHITE+"["+count+"]   >>> Ready? <<<   ["+count+"]",
								this.arenaData.getSplatColor(data.getTeamid()).getChatColor()+"[味方カラー]");
						MainGame.sendActionBarforTeam(this.arenaData, getColorText(data.getTeamid()), data.getTeamid());
						Player player=Bukkit.getPlayer(data.getName());
						player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING,1,1.5f);
					}

				} else if(tick == second*20) {//戦闘開始
					switch(type) {
						case Splat_Zones:
							//TransferRunnable内で動かすとなぜかアーマースタンドが二重に生成されたのでここに移動
							((SplatZones)arenaData.getBattleClass()).showZone(0);
							break;
					}
					for(PlayerData data : DataStore.getArenaPlayersList(this.arenaData.getName())) {
						data.setMove(true);
						data.setAllCansel(false);
						MainGame.sendTitle(data, 0, 2, 0, ChatColor.WHITE.toString()+ChatColor.BOLD+"GO!", " ");
						Player player=Bukkit.getPlayer(data.getName());
						player.playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_BLAST,1,1f);
						if(data.getSquidTask() == null) {
							BukkitRunnable task = new SquidRunnable(data.getName());
							task.runTaskTimer(Main.main, 0, 1);
							data.setSquidTask(task);
						}
						if(data.getHealthTask() == null) {
							BukkitRunnable task = new DamageHealthRunnable(data.getName());
							task.runTaskTimer(Main.main, 0, 10);
							data.setHealthTask(task);
						}
					}
					/*if(type.equals(BattleType.Turf_War))
						data.setTotalpaintblock(data.getBattleClass().getTotalArea());*/
				}
				//ここでスパジャンチャットを送る
				if(tick <= second*20) {
					arenaData.getScoreboard().changeTime(tick);
					arenaData.getSuperjump().setSuperjumpItems();
					if((tick/20)%60 == 0)
						MainGame.sendMessageforArena(arenaData.getName(), ChatColor.YELLOW+"残り時間 "+ChatColor.AQUA.toString()+ChatColor.BOLD+(tick/20)/60+ChatColor.YELLOW+" 分");
					if(tick/20 <= 10 && 0<tick) {
						for(PlayerData data : DataStore.getArenaPlayersList(this.arenaData.getName()))
							MainGame.sendTitle(data, 0, 2, 0, ChatColor.GRAY.toString()+ChatColor.BOLD+tick/20, " ");
					}
				}
			}
			if(type == BattleType.Splat_Zones) {
				if(tick%10 == 0 && tick < second*20 )
					((SplatZones) arenaData.getBattleClass()).checkArea();
				for(int i=1;i<=arenaData.getMaximumTeamNum();i++){
					if(arenaData.getCount(i).getcount()==0) {
						tick = 0;
						return;
					}
				}


					/*ResetPlayerData();
					for(PlayerData data : DataStore.getArenaPlayersList(this.arenaData.getName())) {
						MainGame.sendTitle(data, 0, 2, 0, ChatColor.GRAY.toString()+ChatColor.BOLD+"Finish!", " ");
						MainGame.sendMessage(data, ChatColor.RED+"開発途中のため、リザルトはスキップします");
					}
					for(ArmorStand stand : arenaData.getAreastands())
						stand.remove();
					MainGame.end(arenaData, true);
					this.cancel();
					return;*/


			}
		} else {
			for(PlayerData data : DataStore.getArenaPlayersList(this.arenaData.getName())) {
				Player player=Bukkit.getPlayer(data.getName());
				player.playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_TWINKLE,1,1f);
				MainGame.sendTitle(data, 1, 2, 1, ChatColor.GREEN.toString() + "～現在集計中～", ChatColor.YELLOW + "しばらくお待ち下さい|･ω･)ﾉ");
			}
			ResetPlayerData();
			arenaData.setGameStatus(GameStatusEnum.RESULT);
			if(type == BattleType.Turf_War)
				arenaData.getBattleClass().resultBattle();// TODO ここ 直しました sesamugi
			else if(type == BattleType.Splat_Zones) {
				SplatZones.clearAreaStand(arenaData);
				//for(ArmorStand stand : arenaData.getAreastands())
					//stand.remove();
				arenaData.getBattleClass().resultBattle();
				//MainGame.end(arenaData, false);
			}
			this.cancel();
		}
		tick--;
	}
	
	private String getColorText(int myteam) {
		String text = "   ";
		for(int team = 1; team <= arenaData.getMaximumTeamNum(); team++) {
			if(team == myteam)
				continue;
			text += arenaData.getSplatColor(team).getChatColor()+"█チーム"+team+"█"+"   "+ChatColor.RESET;
		}
		return text;
	}

	private void ResetPlayerData() {
		for(PlayerData pdata : DataStore.getArenaPlayersList(arenaData.getName())) {
			pdata.setSquidMode(false);
			if(pdata.getPlayerSquid() != null)
				pdata.getPlayerSquid().remove();
			pdata.setPlayerSquid(null);
			pdata.setDead(false);
			pdata.setClimb(false);
			pdata.setAllCansel(true);
			if(pdata.getTask() != null)
				pdata.getTask().cancel();
			if(pdata.getSquidTask() != null)
				pdata.getSquidTask().cancel();
			if(pdata.getHealthTask() != null) {
				pdata.getHealthTask().cancel();
			}
			pdata.setTask(null);
			pdata.setSquidTask(null);
			/*
			pdata.setSubCount(0);
			pdata.setDropped(false);
			pdata.setSpecialPoint(0);
			pdata.setCanUseSpecial(false);
*/



			arenaData.getScoreboard().hideBoard(pdata);
			Player player = Bukkit.getPlayer(pdata.getName());
			player.getInventory().clear();
			for(PotionEffectType p:PotionEffectType.values()){
				player.removePotionEffect(p);
			}

			player.setGameMode(GameMode.SPECTATOR);
			player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
			player.setFoodLevel(20);
		}
	}

	public int getTick() {
		return tick;
	}

	public void setTick(int tick) {
		this.tick = tick;
	}
}
