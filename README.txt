Circular Framework

$Name:  $

構成

.settings/
	Eclipseのcircularプロジェクトに対する個別設定を保管しています。

document/
	Circular Frameworkに関する資料等が入っています。

document/CircularFrameworkOverview.xls
	Circular Framework概要設計書です。

lib/
	コンパイル・実行に必要なjarファイルが入っています。各jarファイルのバージョン番号等の情報はjarファイル内のMANIFEST.MFを参照してください。

lib-src/
	lib内のjarファイルでソースコードが提供されているものを、Eclipseでソースを参照できるように、jarに圧縮して入っています。

src/
	Circularのソースコードが入っています。

src/bootstrap
	Circularを起動させるクラスのソースコードが入っています。

src/framework
	Circular Frameworkのソースコードが入っています。

src/ui
	Circular UIのソースコードが入っています。

src/log4j.xml
	開発時に使用されるlog4j設定ファイルです。コンソールに出力されます。

.classpath
	Eclipseプロジェクト用設定ファイルです。

.project
	Eclipseプロジェクト用設定ファイルです。

CircularFrameworkConfigure.properties
	Circularを稼動する際の設定ファイルです。

AlertMail.properties
	Circularで発生した障害を報告するメールの設定ファイルです。

build.xml
	Ant用ビルドファイルです。

boot.bat
	ビルド後の起動コマンドです。
	Javaの起動オプションを変更する場合は修正してください。

start.bat
	ビルド後のCycle開始コマンドです。
	Javaの起動オプションを変更する場合は修正してください。

stop.bat
	ビルド後のCycle終了コマンドです。
	Javaの起動オプションを変更する場合は修正してください。

log4j.xml
	本番運用時に使用されるlog4j設定ファイルです。ログファイルに出力されます。

package-list
	javadoc標準APIへのリンク用パッケージ一覧です。

README.txt
	このファイルです。

merge.log
	Circular Frameworkソースのマージ状況を記録しています。
