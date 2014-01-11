Android Dev Phone 1 をSIMカード無しでアクティベート
=====================================
ここでは、Android Dev Phone1 をSIMカード無しでアクティベートする方法を説明します。
いまさら感はありますが、最近届いたのでやってみました。
手順としては、**activate adp1 without sim card**　のままですので、リンク見たほうが早いかも
[http://www.gotontheinter.net/content/faq-unlockingactivating-g1-or-adp1-without-sim-card](http://www.gotontheinter.net/content/faq-unlockingactivating-g1-or-adp1-without-sim-card)


なぜか、携帯電話持ってないけど、Android Dev Phone1を買った人とかにお奨めします。
設定を一つ変えるだけなので問題はたぶん起きないと思います。
![](http://www3.akjava.com//img2/androiddev6.png)

###アクティベート
Wifi無線Lan接続環境が無いと無理です。当然ながらAndroid SDKのインストールも必要になります
####Androidの起動
電源入れます。たぶん、赤く?No Sim Cardなので先すすめません。
####USBドライバーのインストール
USBドライバーのインストール方法 を見てドライバーをインストールします。
####シェルに接続
MS-DOS(cmd.exe)を呼び出して
パスを通すか、次へ移動します。D:android-sdk-windows-1.1_r1tools 

そして以下のように入力します。
```
adb -d shell
```
すると 行の頭がドル マークになります。
ここで、error:device not found と出ると、USB接続に問題ありです。

そして、さらに、以下のようにコマンドを入れて、スーパーユーザーになります。
```
su
```
すると、行の頭が#になります。

####settingのdevice_provisionedを更新する
スーパーユーザーなので行の頭は#になっています。
次のコマンドで設定データーベースに接続します。（ここでタイプミスしても気づかないのでコピペ推奨)
```
sqlite3 /data/data/com.android.providers.settings/databases/settings.db
```
ここで、本作業とは関係ないのですが、select * from system; とかすると、現在の設定が見れます。

そして、以下のようにして device_provisionedの値を1とします。
```
INSERT INTO system (name, value) VALUES ('device_provisioned', 1);
```
ここでerrorとか出ると、データーベースの接続間違っているか、コマンドに不具合があります。あと**device_provisioned**のスペルも間違えないこと
うまくいくと何もでません。ここでもselect * from system;とか入れると、*:device_provisioned|1 とか出て確認できます。
うまくいったら、
.quitと、exit exit exitを繰り返して、Shellを終了します。
####Android端末の再起動
電源長押しと、shutdownの確認のクリックで再起動します。
再起動すると、No Sim Cardの色が変わっていると思います。
変わってなければ、sqlite3からdevice_provisioned|1 があるか確認してみてください。
####設定Activityの起動
たぶんこのままスクリーン解除しても、Wizardが始まるので
再び、シェルに接続して
```
adb shell
```
設定を起動します。
```
am start -a android.intent.action.MAIN -n com.android.settings/.Settings 
```
####Wifiの設定
設定の一番上のWiress Controlを開いて**Wi-fi**をチェックします。
これでしばらくすると、自動でWifiが有効になるかもしれません。
ならなければ、Wi-Fi Settingsから、お使いのWifi設定を追加します。
すると、Notificationバーに**Wifiアイコン**が出ます。

あとは、設定を終了すれば、Googleのアカウント追加画面とかになります。それで完了です。
###関連情報
Ubuntuユーザーとかはここの方がわかりやすいかも
[http://hanagurotanuki.blogspot.com/2008/12/android-dev-phone-1-sim.html](http://hanagurotanuki.blogspot.com/2008/12/android-dev-phone-1-sim.html)
