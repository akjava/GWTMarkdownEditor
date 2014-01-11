Application Componentsについて
==========================
他のComponentとの連携が得意
普通に作っても再利用できるような各パーツになる？
探せば、ほとんどの目的のコンポーネンツが見つかりそうな気がする

Activities・Services・Broadcast receivers・Content providers
からなる

Activitiesは見た目そして、操作などほとんどの部分をしめる


Servicesはバックグラウンドで処理することすべて
通常Activitiesでの処理は、別のActivitiesに切り替わると止まる
音楽再生しながら、別のことするときなどに使う。
またはActiviesなしでも、定期的に何かを実行するときにも使うと思う。

Broadcast receiversは特別なイベントをキャッチする、
バッテリーが切れそうだとか、ネットワークがつながったとか、たぶんメールが来たとかも
たぶん、サービスと組み合わせて、目的地が近づいたら、broadcastするとかもできそう

Content providers　ようはデーターベース
アドレス帳などが代表例、他のアプリとデーターをやり取りする仕組み
ファイルフォーマットみたいなもの？


###コンポーネントの起動
通常、intentを経由して行われる。
intentとintentfilterは便利だけどややこしい

###コンポーネントの停止
broadcastとproviderは呼ばれて処理終われば勝手に終了となる
残りのactivityは自分で終了したり、他から終了させる仕組みがある。


###The manifest file
アプリに必ずある設定ファイル、ここに各コンポーネントを登録したりする

###IntentFilters
manifestファイルで設定する項目。
アクティビティーに対して、設定する。
起動時に呼ばれるメインアクティビティーとして指定したり、各種ファイルに対応したりできる
ファイルの関連付けみたいなもの？

こういう一覧もある
[http://www.openintents.org/en/intentstable](http://www.openintents.org/en/intentstable)

###Activities and Tasks
いろいろ細かい設定がる、でもあまり使わない？
####Launch modes
４つある
"standard" (the default mode) 
"singleTop" 
"singleTask" 
"singleInstance"
####Clearing the stack
デフォルトでは長時間放置すると、起動したActivity以外自動で閉じる
また、設定で他のActivity起動時に、閉じたり、終了したりできる
####Starting tasks
"singleTask" 
"singleInstance"はMAIN以外で使うべきでない
###Processes and Threads
パフォーマンス上げるのにいるのだろうな

###Component Lifecycles
onCreate(Bundle savedInstanceState) が重要だろう。
途中でアプリ再起動される作りにしないといけない。
これらの処理は、ある意味テンプレート化するだろうから、サンプルコードを参考にしよう