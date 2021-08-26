# Splatoon-in-Minecraft-Rewrite  

[旧Splatoon-in-Minecraft](https://github.com/kotmw0701/Splatoon-in-Minecraft)からの色々書き直しバージョン

## 概要

MinecraftでWiiUのスプラトゥーンもどきがプレイ出来るようになるプラグインです。  

## 操作

| 操作 | 説明 |
|-----|-----|
| WASD | 移動 |
| 右クリック | メイン武器使用 |
| 左クリック | サブ武器使用 |
| ダッシュ | イカ\<-\>ヒト切り替え |
| 物を捨てる | イカ\<-\>ヒト切り替え |
| Space | ジャンプ(ヒト状態) |
| | 壁のぼり(イカ状態) |
| Shift | 壁くだり(イカ状態) |

## コマンドリスト
**<>**:必要な引数 **[]**:無くても問題は無い  
**/**で区切られてる物は、その中のどれかを入れてください  
**[player]**の引数には**@a**,**@p**,**@r**も使用可能です
##### 管理者用コマンド

| Command | 説明 |
|-----|-----|
|/splatsetting setlobby|帰還ロビーを設定します(あまり要らない)|
|/splatsetting configreload|Config.ymlを再読み込みします|
|/splatsetting \<待機部屋名\> setroom|現在地の座標を待機部屋として登録します|
|/splatsetting \<待機部屋名\> removeroom|指定した待機部屋を消去します|
|/splatsetting \<ステージ名\> setarena|WorldEditで指定した範囲をステージとして登録します|
|/splatsetting \<ステージ名\> setarea|WorldEditで指定した範囲をガチエリアとして設定します(1つのみ)|
|/splatsetting \<ステージ名\> setspawn \<1/2\> \<1/2/3/4\>|現在地を対象のステージの転送位置として設定します|
|/splatsetting \<ステージ名\> finish|設定が全部終わったらこのコマンドを実行して、使用可能にします|
|/splatsetting \<ステージ名\> editmode|ステージの設定、範囲内ブロックを編集することが出来ます|
|/splatsetting \<待機部屋名\> addarena \<arena\>|対象の待機部屋の選択ステージを追加します|
|/splatsetting \<待機部屋名\> removearena \<arena\>|対象の待機部屋の選択ステージを消去します|

##### プレイヤー用コマンド

| Command | 説明 |
|-----|-----|
|/splat join \<待機部屋名\> [player]|対象の待機部屋に参加します|
|/splat leave|待機部屋に居る時に退出します|
|/splat roomlist|存在する待機部屋の一覧を表示します|
|/splat arenalist|存在するステージの一覧を表示します|
|/splat rank \<win/lose/rank/rate\>|ランキングを表示します|

##### 看板
splat
join
\<待機部屋名\>
対象の待機部屋に参加します

splat
shop
\<武器名\>
対象の武器を入手します(次の試合に持っていくわけではありません)

splat
choose
次の試合に持っていく武器を選択します

splat
arena
\<ステージ名\>
指定したステージで試合を開始します