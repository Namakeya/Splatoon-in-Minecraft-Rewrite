package jp.kotmw.splatoon.commands;

import java.util.ArrayList;
import java.util.List;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.internal.annotation.Selection;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.world.World;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;


import com.sk89q.worldedit.bukkit.WorldEditPlugin;

import jp.kotmw.splatoon.filedatas.OtherFiles;
import jp.kotmw.splatoon.filedatas.StageFiles;
import jp.kotmw.splatoon.filedatas.WaitRoomFiles;
import jp.kotmw.splatoon.filedatas.WeaponFiles;
import jp.kotmw.splatoon.gamedatas.ArenaData;
import jp.kotmw.splatoon.gamedatas.DataStore;
import jp.kotmw.splatoon.gamedatas.DataStore.BattleType;
import jp.kotmw.splatoon.gamedatas.DataStore.GameStatusEnum;
import jp.kotmw.splatoon.gamedatas.PlayerData;
import jp.kotmw.splatoon.gamedatas.WaitRoomData;
import jp.kotmw.splatoon.maingame.GameSigns;
import jp.kotmw.splatoon.maingame.MainGame;
import jp.kotmw.splatoon.manager.Paint;
import jp.kotmw.splatoon.manager.SplatScoreBoard;


public class SettingCommands extends CommandLib {

	List<ArmorStand> stands = new ArrayList<ArmorStand>();

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(!(sender instanceof Player))
			return false;
		player = (Player) sender;
		if(args.length == 0) {
			sendMsg(MainGame.Prefix);
			sendMsgs("-----Setting Command List-----"
			,"/splatsetting setlobby"
					,"/splatsetting configreload"
					,"/splatsetting start"
					,"/splatsetting endbattle"
					,"/splatsetting allview"
					,"/splatsetting addweapon <weapon>"

			,"/splatsetting setroom <room>"
			,"/splatsetting removeroom <room>"
			,"/splatsetting addarena <room> <arena>"
			,"/splatsetting removearena <room> <arena>"
			,"/splatsetting setarena <arena>"
			,"/splatsetting setarea <arena>"
			,"/splatsetting finish <arena>"
			,"/splatsetting setspawn <arena> <1/2> <1/2/3/4>"
			,"/splatsetting editmode <arena>"
					,"/splatsetting rollback <arena>"
			,"------------------------------");
			return true;
		} else if(args.length == 1) {
			if("setlobby".equalsIgnoreCase(args[0])) {
				OtherFiles.createLobby(player.getLocation());
				sendPMsg(ChatColor.GREEN + "ロビーを設定しました");
				return true;
			} else if("configreload".equalsIgnoreCase(args[0])) {
				OtherFiles.ConfigReload();

				sendPMsg("Config.ymlを再読み込みしました");
				return true;
			} else if("start".equalsIgnoreCase(args[0])) {
				if(DataStore.hasPlayerData(player.getName())) {
					PlayerData playerdata = DataStore.getPlayerData(player.getName());
					MainGame.start(DataStore.getRoomData(playerdata.getRoom()));
					return true;
				}
				sendPMsg(ChatColor.RED+"部屋に参加してからコマンド実行をしてくださいな");
				return false;

			} else if("endbattle".equalsIgnoreCase(args[0])) {
				if(DataStore.hasPlayerData(player.getName())) {
					PlayerData playerdata = DataStore.getPlayerData(player.getName());
					ArenaData arena=DataStore.getArenaData(playerdata.getArena());
					if(arena.getGameStatus() == GameStatusEnum.INGAME && arena.getTask()!=null){
						arena.getTask().endNow=true;
					}
					return true;
				}
				sendPMsg(ChatColor.RED+"ゲームを行っていません");
				return false;
			} else if("allview".equalsIgnoreCase(args[0])) {
				if(DataStore.hasPlayerData(player.getName())) {
					PlayerData playerdata = DataStore.getPlayerData(player.getName());
					playerdata.setAllView(!playerdata.isAllView());
					return true;
				}
				return false;
			}
		} else if(args.length == 2) {
			String name = args[1];
			if("rollback".equalsIgnoreCase(args[0])) {
				if(!DataStore.hasArenaData(name)){
					sendPMsg(ChatColor.RED+"そのステージは存在しません");
					return false;
				}

				ArenaData data = DataStore.getArenaData(name);
				Paint.RollBack(data);
				return true;
			}

			if("setarena".equalsIgnoreCase(args[0])) {
				if(StageFiles.AlreadyCreate(name)) {
					sendPMsgs(ChatColor.RED+"そのステージは既に存在します"
					,ChatColor.GREEN+"ステージ範囲の再設定をしたい場合は "
					+ChatColor.YELLOW+"/splatsetting editmode "+name+" "+ChatColor.GREEN+"のコマンドを使用してステージを無効化してからsetarenaのコマンドを再実行してください");
					return false;
				}
				if(name.getBytes().length > 16) {
					sendPMsg(ChatColor.RED+"ステージ名は16バイト以下にしてください");
					return false;
				}
				WorldEditPlugin worldEdit = (WorldEditPlugin)Bukkit.getPluginManager().getPlugin("WorldEdit");
				Region selection = null;
				try {
					selection = worldEdit.getSession(player).getSelection(BukkitAdapter.adapt(player.getWorld()));
				} catch (IncompleteRegionException e) {
					selection=null;
				}
				if(selection == null)
					return false;
				if(!StageFiles.createArena(name, player.getWorld(),
						BukkitAdapter.adapt(player.getWorld(),selection.getMinimumPoint()), BukkitAdapter.adapt(player.getWorld(),selection.getMaximumPoint()))) {
					sendPMsg(ChatColor.RED+"着色可能ブロックを1つ以上設置してください");
					return false;
				}
				sendPMsgs(ChatColor.GREEN+"ステージの範囲設定が完了しました"
				,ChatColor.YELLOW+"以下のコマンドで設定を終えてから、finishコマンドを実行してください"
				,ChatColor.YELLOW+"/splatsetting setspawn "+name+" <1/2> <1/2/3/4>"
				,ChatColor.YELLOW+"/splatsetting setarea "+name+"");
				return true;
			} else if("setarea".equalsIgnoreCase(args[0])) {
				if(!StageFiles.AlreadyCreateFile(name)) {
					sendPMsg(ChatColor.RED+"そのステージは存在しません");
					return false;
				}
				if(StageFiles.AlreadyCreate(name) && DataStore.getArenaData(name).isStatus()) {
					sendPMsg(ChatColor.RED + "そのステージは既に有効化されています");
					sendPMsg(ChatColor.GREEN + "エリア範囲の再設定をしたい場合は "
					+ChatColor.YELLOW+"/splatsetting editmode "+name+" "+ChatColor.GREEN+"のコマンドを使用してステージを無効化してからsetarenaのコマンドを再実行してください");
					return false;
				}
				WorldEditPlugin worldEdit = (WorldEditPlugin)Bukkit.getPluginManager().getPlugin("WorldEdit");
				Region selection = null;
				try {
					selection = worldEdit.getSession(player).getSelection(BukkitAdapter.adapt(player.getWorld()));
				} catch (IncompleteRegionException e) {
					selection=null;
				}
				if(selection == null)
					return false;
				StageFiles.setArea(name, BukkitAdapter.adapt(player.getWorld(),selection.getMinimumPoint()), BukkitAdapter.adapt(player.getWorld(),selection.getMaximumPoint()));
				sendPMsg(ChatColor.GREEN+"エリア範囲を設定しました");
				return true;
			} else if("finish".equalsIgnoreCase(args[0])) {
				int total = 0;
				boolean update = false;
				if(DataStore.hasArenaData(name)) {
					if(DataStore.getArenaData(name).isStatus()) {
						sendPMsg(ChatColor.RED+"既に有効化済みです");
						return false;
					}
					total = DataStore.getArenaData(name).getTotalpaintblock();
					update = true;
				}
				ArenaData data = StageFiles.setArenaData(name, DataStore.hasArenaData(name));
				if(!StageFiles.isFinishSetup(data)) {
					sendPMsg(ChatColor.RED+"そのステージはセットアップが完了していません");
					DataStore.removeArenaData(name);
					return false;
				}
				if(update)
					sendPMsg(total+" -> "+data.getTotalpaintblock());
				if(data.getTotalpaintblock() <= 0) {
					sendPMsg(ChatColor.RED+"着色可能ブロックを1つ以上設置してください");
					return false;
				}
				data.setStatus(true);
				data.setGameStatus(GameStatusEnum.ENABLE);
				StageFiles.setEnable(name);
				StageFiles.AllStageReload();
				sendPMsg(ChatColor.GREEN+"設定完了を確認し、使用可能になりました！");
				return true;
			} else if("setroom".equalsIgnoreCase(args[0])) {
				if(name.getBytes().length > 16) {
					sendPMsg(ChatColor.RED+"待機部屋名は16バイト以下にしてください");
					return false;
				}
				if(DataStore.hasRoomData(name)) {
					sendPMsg(ChatColor.GREEN+"既に作成されていた待機部屋の座標と置き換えました");
					WaitRoomData data = DataStore.getRoomData(name);
					sendPMsg(ChatColor.RED+"以前に設定されていたデータはこちらです");
					sendMsgs("X: "+data.getX()
					,"Y: "+data.getY()
					,"Z: "+data.getZ()
					,"Yaw: "+data.getYaw()
					,"Pitch: "+data.getPitch()
					,"BattleType: "+data.getBattleType().toString());
				} else {
					sendPMsg(ChatColor.YELLOW+name+ChatColor.GREEN+" という待機部屋を作成しました");
				}
				WaitRoomFiles.creareWaitRoom(name, player.getLocation(), BattleType.Turf_War);
				return true;
			} else if("loadroom".equalsIgnoreCase(args[0])) {
				boolean already = DataStore.hasRoomData(name);
				if(!WaitRoomFiles.RoomLoad(name)) {
					sendPMsg(ChatColor.RED+"対象の待機部屋データファイルが存在しません");
					return false;
				}
				sendPMsg(ChatColor.GREEN+"対象の待機部屋データを"+(already ? "再" : "")+"読み込みました");
				GameSigns.UpdateJoinSign(name);
				return true;
			} else if("removeroom".equalsIgnoreCase(args[0])){
				if(!DataStore.hasRoomData(name)) {
					sendPMsg(ChatColor.RED+"その待機部屋は存在していません");
					return false;
				}
				if(!WaitRoomFiles.removeRoomFile(name)) {
					sendPMsg(ChatColor.RED+"ファイルの消去に失敗しました");
					return false;
				}
				DataStore.removeRoomData(name);
				GameSigns.disableJoinSign(name);
				return true;
			} else if("editmode".equalsIgnoreCase(args[0])) {
				if(!DataStore.hasArenaData(name)) {
					sendPMsg(ChatColor.RED+"セットアップが完了しているステージでのみ使用可能です");
					return false;
				}
				ArenaData data = DataStore.getArenaData(name);
				if(!data.isStatus()) {
					sendPMsg(ChatColor.RED+"再有効化の場合は /splatsetting finish "+name+" のコマンドを実行してください");
					return false;
				}
				data.setStatus(false);
				data.setGameStatus(GameStatusEnum.DISABLE);
				sendPMsg(ChatColor.GREEN+"ステージを無効化し、編集モードに切り替わりました");
				return true;
			} else if("start".equalsIgnoreCase(args[0])) {
				if (DataStore.hasPlayerData(player.getName())) {
					PlayerData playerdata = DataStore.getPlayerData(player.getName());
					WaitRoomData roomdata=DataStore.getRoomData(playerdata.getRoom());
					if(DataStore.hasArenaData(args[1])){
						MainGame.start(roomdata,DataStore.getArenaData(args[1]));
						return true;
					}else{
						sendPMsg(ChatColor.RED + args[1]+" というアリーナは存在しません");
						return false;
					}

				}
				sendPMsg(ChatColor.RED + "部屋に参加してからコマンド実行をしてくださいな");
				return false;
			}
		} else if(args.length == 3) {
			String name = args[1];
			if("addweapon".equalsIgnoreCase(args[0])) {
				String weaponname = args[2];
				Player target = getPlayer(name);
				if(target == null) {
					sendPMsg( ChatColor.RED+"そのプレイヤーは存在しません");
					return false;
				}
				if(!WeaponFiles.exists(weaponname)) {
					sendPMsg( ChatColor.RED+"そのブキは存在しません");
					return false;
				}
				if(DataStore.getStatusData(player.getName()).hasHaveWeapon(weaponname)) {
					sendPMsg( ChatColor.RED+"対象のプレイヤーはその武器を既に持っています");
					return false;
				}
				DataStore.getStatusData(player.getName()).addWeapon(weaponname);
				sendPMsg( ChatColor.YELLOW+target.getName()+ChatColor.WHITE+" に "+ChatColor.GREEN+weaponname+ChatColor.WHITE+" を追加しました");
				return true;
			} else if("setroom".equalsIgnoreCase(args[0])) {
				BattleType type = getType(args[2]);
				if(name.length() > 16) {
					sendPMsg(ChatColor.RED+"待機部屋名は16文字以下にしてください");
					return false;
				}
				if(type == null) {
					sendPMsg(ChatColor.RED+"そのバトルタイプはありません");
					return false;
				}
				if(DataStore.hasRoomData(name)) {
					sendPMsg( ChatColor.GREEN + "既に作成されていた待機部屋の座標と置き換えました");
					WaitRoomData data = DataStore.getRoomData(name);
					sendPMsg(ChatColor.RED + "以前に設定されていたデータはこちらです");
					sendMsgs("X: "+data.getX()
					,"Y: "+data.getY()
					,"Z: "+data.getZ()
					,"Yaw: "+data.getYaw()
					,"Pitch: "+data.getPitch()
					,"BattleType: "+data.getBattleType().toString());
				} else {
					sendPMsg( ChatColor.YELLOW + name + ChatColor.GREEN + " という待機部屋を作成しました");
				}
				WaitRoomFiles.creareWaitRoom(name, player.getLocation(), type);
				return true;
			} else if("addarena".equalsIgnoreCase(args[0])) {
				String arena = args[2];
				if(!DataStore.hasArenaData(arena)) {
					sendPMsg(ChatColor.RED+"そのステージは存在しません。一覧確認コマンドは"+ChatColor.GOLD+"/splat arenalist");
					return false;
				} else if(!DataStore.hasRoomData(name)) {
					sendPMsg(ChatColor.RED+"その待機部屋は存在しません。一覧確認コマンドは"+ChatColor.GOLD+"/splat roomlist");
					return false;
				} else if(DataStore.getRoomData(name).getSelectList().contains(arena)) {
					sendPMsg(ChatColor.RED+"そのステージは既に追加されています。");
					return false;
				}
				WaitRoomFiles.editSelectList(DataStore.getRoomData(name), arena, true);
				sendPMsg(ChatColor.AQUA+name+ChatColor.GREEN+" という待機部屋に "+ChatColor.YELLOW+arena+" を選択ステージとして追加しました");
				return true;
			} else if("removearena".equalsIgnoreCase(args[0])) {
				String arena = args[2];
				if(!DataStore.hasRoomData(name)) {
					sendPMsg(ChatColor.RED+"その待機部屋は存在しません。一覧確認コマンドは"+ChatColor.GOLD+"/splat roomlist");
					return false;
				} else if(!DataStore.getRoomData(name).getSelectList().contains(arena)) {
					sendPMsg(ChatColor.RED+"そのステージは追加されていません。");
					return false;
				}
				WaitRoomFiles.editSelectList(DataStore.getRoomData(name), arena, false);
				sendPMsg(ChatColor.AQUA+name+ChatColor.GREEN+" という待機部屋から "+ChatColor.YELLOW+arena+" を選択ステージから削除しました");
				return true;
			}
		} else if(args.length == 4) {
			String name = args[1];
			if("setspawn".equalsIgnoreCase(args[0])) {
				if(!StageFiles.AlreadyCreateFile(name)) {
					sendPMsg(ChatColor.RED + "そのステージは存在しません");
					return false;
				}
				if(StageFiles.AlreadyCreate(name) && DataStore.getArenaData(name).isStatus()) {
					sendPMsg(ChatColor.RED + "そのステージは既に有効化されています");
					sendPMsg(ChatColor.GREEN + "スポーン地点の再設定をしたい場合は "
					+ChatColor.YELLOW+"/settingfiles editmode "+name+" "+ChatColor.GREEN+"のコマンドを使用してステージを無効化してからsetarenaのコマンドを再実行してください");
					return false;
				}
				if(!NumberUtils.isNumber(args[2].replaceAll("@", ""))
						|| !NumberUtils.isNumber(args[3].replaceAll("@", ""))) {
					sendPMsg(ChatColor.RED + "両方とも数値を入れてください");
					return false;
				}
				boolean teamb = args[2].contains("@"), posb = args[3].contains("@");
				int team = Integer.parseInt(args[2].replaceAll("@", "")), pos = Integer.parseInt(args[3].replaceAll("@", ""));
				if(team == 0 || team > (teamb ? 8 : 2)) {
					sendPMsg(ChatColor.RED + "1か2にしてください");
					return false;
				}
				if(pos == 0 || pos > (posb ? 20 : 4)) {
					sendPMsg(ChatColor.RED + "1～4の範囲にしてください");
					return false;
				}
				StageFiles.setSpawnPos(name, player.getLocation(), team, pos);
				sendPMsg(ChatColor.GREEN + "チーム"+team+"、"+pos+"人目のスポーン地点を設定");
				return true;
			}
		}
		sendPMsg(ChatColor.RED+"そんなコマンド実装されていません(´・ω・｀)");
		return false;
	}

	public BattleType getType(String type) {
		BattleType bt = null;
		for(BattleType types : BattleType.values())
			if(types.toString().equalsIgnoreCase(type))
				bt = types;
		return bt;
	}
}
