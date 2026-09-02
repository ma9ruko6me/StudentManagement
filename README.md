# 受講生管理システム

Spring Bootを用いた受講生管理システムです。

受講生情報の登録・更新・検索機能を実装しています。
RaiseTech Javaコースの課題として開発しました。

就職活動用ポートフォリオとして、[CLAUDE.md](CLAUDE.md)に定めた開発ルールに沿ってIssue駆動・PRベースで継続的に改善しています。

## ドキュメント

| ドキュメント | 内容 |
|--------------|------|
| [docs/requirements.md](docs/requirements.md) | 要件定義書。作成背景、想定利用者、機能要件、データ項目・ER図 |
| [docs/basic-design.md](docs/basic-design.md) | 基本設計書。技術スタック、API設計、DB物理設計、ディレクトリ構成 |
| [docs/infrastructure.md](docs/infrastructure.md) | インフラ構成書。AWS構成、デプロイの仕組み、運用手順 |

## 使用技術

| 分類 | 技術 | 用途 |
|------|------|------|
| Language | Java 21 | バックエンド開発 |
| Framework | Spring Boot | REST API開発 |
| ORM | MyBatis | DBアクセス |
| Database | MySQL | データ管理 |
| Test | JUnit5 | 単体テスト |
| Test | Mockito | モックを用いたテスト |
| Test | H2 Database | テスト用DB |
| Library | Lombok | ボイラープレート削減 |
| API Docs | Swagger / OpenAPI | API仕様の可視化 |
| Cloud | AWS EC2 | アプリケーション実行環境 |
| Database | AWS RDS(MySQL) | データベース環境 |
| CI/CD | GitHub Actions | 自動ビルド・自動デプロイ(現在は無効化中。[docs/infrastructure.md](docs/infrastructure.md)を参照) |
| Build Tool | Gradle | ビルド管理 |
| VCS | Git / GitHub | ソースコード管理 |

技術選定の理由は[docs/basic-design.md](docs/basic-design.md)を参照してください。

## ローカル環境構築

1. MySQLをローカルに用意し、`StudentManagement`という名前のデータベースを作成する
2. DB接続情報を環境変数として設定する(`DB_USERNAME`・`DB_PASSWORD`。値は各自のMySQL設定に合わせる)
   - IntelliJ IDEAの場合: 実行構成(Run Configuration)の「環境変数」欄に設定する
   - コマンドラインの場合: `export DB_USERNAME=root DB_PASSWORD=<自分のパスワード>`
   - 設定する項目は[backend/.env.example](backend/.env.example)を参照(このファイル自体は値を埋めても`.env`にリネームしない限りコミットされない)
3. `cd backend && ./gradlew bootRun`でアプリケーションを起動する

DB接続情報の秘密情報としての扱い方は[docs/infrastructure.md](docs/infrastructure.md#6-秘密情報の取り扱い)を参照してください。

## ディレクトリ構成

| ディレクトリ | 内容 |
|--------------|------|
| `backend/` | Spring Bootによるバックエンド実装一式(`build.gradle`・`src/`・`gradlew`など) |
| `docs/` | 要件定義・基本設計・インフラ構成などのドキュメント |
| `prototype/` | 画面確認用の静的HTML/CSS/JSプロトタイプ(本実装とは別) |

## API仕様

エンドポイント一覧は[docs/basic-design.md](docs/basic-design.md#2-api設計)を参照してください。API仕様はSwagger UIでも確認できます。

http://localhost:8080/swagger-ui/index.html

## 工夫した点

### 条件検索機能の実装

受講生・コース・申込状況を対象とした条件検索機能を実装しました。

当初は複数回のデータ取得を行い、アプリケーション側で組み立てる方法を想定していましたが、
効率性や実装の複雑さを考慮し、SQLのJOINを用いて一度で取得する設計に変更しました。

また、検索結果に合わせてDTOを新たに設計し、
必要なデータのみを扱う形に整理しました。

その後、ソートや検索条件の切り替えにも対応できるよう拡張しました。

---

### 申込状況機能の実装

受講生とコースの関係に対して申込状況を管理するため、
申込状況テーブルを新規に設計・追加しました。

リポジトリ層では全件検索・条件検索・登録・更新機能を実装し、
検索用途に応じた取得ができるようにしました。

また、既存の受講生詳細との整合性を考慮しながら、
DTOやConverterを用いてデータ構造を整理しました。

さらに、申込状況の状態管理を明確にするためにEnum化を行い、
あわせてコース追加APIの実装など設計の改善も行いました。

---

### 例外処理の実装

業務例外とシステム例外を分けることを意識しながら実装しました。
エラーの種類を整理することで、原因の切り分けがしやすい構造にしました。

---

### テストの実装

各レイヤーごとに単体テストを実装しました。
JUnitを用いて各層の動作確認を行い、
Mockitoを使用して依存関係をモック化することで、
テストの独立性を確保しました。

## 苦労した点

### 条件検索機能の設計

複数テーブルにまたがる条件検索を実装する際、
当初は各テーブルごとに必要なデータを個別に取得し、
アプリケーション側で結合する方法を想定していました。

しかしこの方法ではDBアクセスが複数回発生し、
実装も複雑になると感じたため設計を見直しました。

その後、JOINを用いてSQL側で結合する方法に変更しましたが、
複数テーブルの結合方法や結果の扱い方の理解に苦労しました。

さらに既存の実装と合わせるために、
DTOの設計を見直し、フラットな検索結果を扱えるよう調整しました。

---

### 申込状況機能の設計・実装

受講生とコースの関係に対して申込状況を管理するため、
申込状況テーブルを新規に追加しました。

当初はリポジトリ単位での実装を進めていましたが、
既存の受講生・コース管理機能との整合性を取る過程で、
データ構造と処理フローの見直しが必要になりました。

特に、受講生詳細情報の構造と申込状況の追加により、
既存のデータ構造との間に不整合が発生し、
CourseDetailの設計変更やDTOの再設計を行う必要がありました。

また、サービス・コントローラ層のテストにおいても、
依存関係を整理しながらテスト対象の単位を決める点に苦労しました。

---

### AWS環境構築とデプロイ

AWS環境へのデプロイでは、
EC2・RDS・GitHub Actionsを連携させる必要があり、
各サービスの役割や接続設定の理解に苦労しました。

また、レビューを通して機密情報の管理や
SSHホスト認証の重要性を学び、
GitHub Secretsやknown_hostsを利用した
安全なデプロイ方法へ改善しました。

さらに、サービス起動失敗時の原因特定が難しく、
systemdやjournalctlを利用したログ確認の仕組みを整備しました。

## 今後の改善点

各ドキュメントの「今後の課題」セクションにまとめています。

- [docs/requirements.md 今後の課題](docs/requirements.md#6-今後の課題)
- [docs/basic-design.md 今後の課題](docs/basic-design.md#5-今後の課題)
- [docs/infrastructure.md 今後の課題](docs/infrastructure.md#7-今後の課題)
