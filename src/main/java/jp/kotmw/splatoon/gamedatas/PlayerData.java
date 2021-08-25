package jp.kotmw.splatoon.gamedatas;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class PlayerData {
	private String name; //プレイヤー名
	private String arena; //使用ステージ名
	private String room; //使用ルーム名
	private int teamid; //チームID
	private int posisionid; //チーム内ID
	private int killcount; //キル数
	private int deathcount; //デス数
	private LivingEntity squid; //プレイヤー専属のイカ
	private boolean Squidmode = false; //イカ状態かどうか
	private boolean climb = false; //壁上り状態かどうか
	private boolean move = true; //動ける状態かどうか
	private boolean allcancel = false;
	private boolean allview = false;//後々消す
	private Location loc; //プレイヤー参加前の座標
	private List<ItemStack> items; //プレイヤー参加前のインベントリデータ
	private int paintscore; //塗ったスコア
	private BukkitRunnable task; //プレイヤーの武器のRunnable
	private Thread thread;
	private BukkitRunnable squidtask;
	private BukkitRunnable healthtask;
	private int runnableTick; //武器のRunnableのtick
	private boolean paint = false; //ローラー使用の時の塗れる時間かどうか 現在使われておりません sesamugi
	private boolean invincible = false; //無敵状態かどうか
	private boolean dead = false;
	private int charge;//チャージャーで使う
	private int inkcooltime;
	private int superjumpStatus;// 0:何もない 1:待機中 2:上昇中 3:下降中

	private int subCooldown;//サブ武器のクールダウン期間

	private int subCount;//トラップのような設置個数に制限のあるサブをいくつ置いているか

	private int recoilTick;//硬直状態かどうか

	private boolean dropped;//アイテムドロップしたかどうか。onInteractで左クリックとアイテムドロップを見分けるために使用

	private int specialPoint;//スペシャルポイント

	private boolean usingSpecial;//スペシャル使用中かどうか

	private boolean canUseSpecial;//スペシャル使用可能かどうか

	private Location lastPos;// プレイヤーの前tickの位置

	private Vector motion=new Vector(0,0,0);//getVelocity()が動作しないのでこれを使う

	public Vector getMotion() {
		return motion;
	}

	public Location getLastPos() {
		return lastPos;
	}

	public void setLastPos(Location lastPos) {
		if(this.lastPos!=null) {
			this.motion = (lastPos.toVector().subtract(this.lastPos.toVector()));
		}
		this.lastPos = lastPos;
	}

	public boolean isCanUseSpecial() {
		return canUseSpecial;
	}

	public void setCanUseSpecial(boolean canUseSpecial) {
		this.canUseSpecial = canUseSpecial;
	}

	public boolean isUsingSpecial() {
		return usingSpecial;
	}

	public void setUsingSpecial(boolean usingSpecial) {
		this.usingSpecial = usingSpecial;
	}

	public int getSpecialPoint() {
		return specialPoint;
	}

	public void setSpecialPoint(int specialPoint) {
		this.specialPoint = specialPoint;
	}

	public boolean isDropped() {
		return dropped;
	}

	public void setDropped(boolean dropped) {
		this.dropped = dropped;
	}

	public int getSubCount() {
		return subCount;
	}

	public void setSubCount(int subCount) {
		this.subCount = subCount;
	}

	public int getSubCooldown() {
		return subCooldown;
	}

	public void setSubCooldown(int subCooldown) {
		this.subCooldown = subCooldown;
	}

	public PlayerData(String name) {this.name = name;}

	public int getSuperjumpStatus() {
		return superjumpStatus;
	}

	public void setSuperjumpStatus(int superjumpStatus) {
		this.superjumpStatus = superjumpStatus;
	}

	public String getName() {return name;}

	public String getArena() {return arena;}

	public String getRoom() {return room;}

	public int getTeamid() {return teamid;}
	
	public int getPosisionId() {return posisionid;}

	public int getKillcount() {return killcount;}

	public int getDeathcount() {return deathcount;}

	public LivingEntity getPlayerSquid() {return squid;}

	public boolean isSquidMode() {return Squidmode;}

	public boolean isClimb() {return climb;}

	public boolean isMove() {return move;}

	public boolean isAllCancel() {return allcancel;}
	
	public boolean isAllView() {return allview;}//後々消す

	public Location getRollBackLocation() {return loc;}

	public List<ItemStack> getRollbackItems() {return items;}

	public int getScore() {return paintscore;}

	public BukkitRunnable getTask() {return task;}
	
	public Thread getThread() {return thread;}/////////

	public BukkitRunnable getSquidTask() {return squidtask;}
	
	public BukkitRunnable getHealthTask() {return healthtask;}

	public int getTick() {return runnableTick;}

	public boolean isPaint() {return paint;}

	public boolean isInvincible() {return invincible;}

	public boolean isDead() {return dead;}

	public String getWeapon() {
		return this.getPlayerStatus().getCurrentWeapon();
	}

	public int getCharge() {return charge;}

	public int getInkCoolTime() {return inkcooltime;}

	@Deprecated
	public int getOpponentTeamid() {
		return teamid == 1 ? 2 : 1;
	}
	
	public PlayerStatusData getPlayerStatus() {
		return DataStore.getStatusData(name);
	}

	public int getRecoilTick() {
		return recoilTick;
	}

	public void setRecoilTick(int recoilTick) {
		this.recoilTick = recoilTick;
	}

	public void setName(String name) {this.name = name;}

	public void setArena(String arena) {this.arena = arena;}

	public void setRoom(String room) {this.room = room;}

	public void setTeamId(int teamid) {this.teamid = teamid;}
	
	public void setPosisionId(int posisionid) {this.posisionid = posisionid;}

	public void setKillcount(int killcount) {this.killcount = killcount;}

	public void setDeathcount(int deathcount) {this.deathcount = deathcount;}

	public void setPlayerSquid(LivingEntity squid) {this.squid = squid;}

	public void setSquidMode(boolean Squidmode) {this.Squidmode = Squidmode;}

	public void setClimb(boolean climb) {this.climb = climb;}

	public void setMove(boolean move) {this.move = move;}

	public void setAllCansel(boolean allcancel) {this.allcancel = allcancel;}
	
	public void setAllView(boolean allview) {this.allview = allview;}//後々消す

	public void setRollBackLocation(Location loc) {this.loc = loc;}

	public void setRollBackItems(List<ItemStack> items) {this.items = items;}

	public void setScore(int paintscore) {this.paintscore = paintscore;}

	public void setTask(BukkitRunnable task) {this.task = task;}
	
	public void setThread(Thread thread) {this.thread = thread;}//////////////

	public void setSquidTask(BukkitRunnable squidtask) {this.squidtask = squidtask;}
	
	public void setHealthTask(BukkitRunnable healthtask) {this.healthtask = healthtask;}

	public void setTick(int runnableTick) {this.runnableTick = runnableTick;}

	public void setPaint(boolean paint) {this.paint = paint;}

	public void setInvincible(boolean invincible) {this.invincible = invincible;}

	public void setDead(boolean dead) {this.dead = dead;}

	public void setWeapon(String weapon) {
		this.getPlayerStatus().setCurrentWeapon(weapon);
	}

	public void setCharge(int charge) {this.charge = charge;}

	public void setInkCoolTime(int inkcooltime) {this.inkcooltime = inkcooltime;}
}
