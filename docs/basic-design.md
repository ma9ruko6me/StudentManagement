# 受講生管理システム 基本設計書

## 改訂履歴

[改訂履歴はこちら](basic-design-changelog.md)

---

## 目次

- [1. 技術スタック](#1-技術スタック)
- [2. API設計](#2-api設計)
- [3. DB物理設計](#3-db物理設計)
- [4. ディレクトリ構成](#4-ディレクトリ構成)
- [5. 今後の課題](#5-今後の課題)

---

## 1. 技術スタック

### 1.1 バックエンド

| 項目 | 選定 | 理由 |
|------|------|------|
| 言語 | Java 21 | LTS版を使用 |
| フレームワーク | Spring Boot 3.3.5 | DI・自動設定によりREST APIを最小構成で構築できる |
| ORM | MyBatis | 複数テーブルを横断するJOINを含む検索条件を、SQLを直接書いて柔軟に組み立てるため選定(JPAのような自動生成に頼らず、SQL設計そのものを学習する狙い) |
| バリデーション | spring-boot-starter-validation | リクエストの入力チェックを宣言的に実装 |
| API仕様書 | springdoc-openapi(Swagger UI) | エンドポイントの仕様をブラウザから確認できるようにする |
| ボイラープレート削減 | Lombok | Getter/Setter等の定型コードを削減 |
| ユーティリティ | Apache Commons Lang3 | 文字列操作等の補助 |

### 1.2 フロントエンド

| 項目 | 選定 | 理由 |
|------|------|------|
| ライブラリ | React 19 | 姉妹プロジェクトTaskManagementと構成を揃え、複数ポートフォリオで再現性のある開発プロセスを示すため |
| ビルドツール | Vite 8 | TaskManagementと同一。高速な開発サーバーとビルドを利用できる |
| 言語 | TypeScript | 型安全性を確保し、バックエンドDTO(`StudentDetail`等)との対応関係を明示するため |
| データ取得 | TanStack Query(React Query) | サーバー状態(ローディング・エラー・キャッシュ)の管理を手動の`useState`+`useEffect`に代えて宣言的に扱うため |
| HTTPクライアント | axios | TaskManagementと同一 |
| スタイリング | Tailwind CSS v4(`@tailwindcss/vite`) | TaskManagementと同一。ユーティリティクラスで一貫した見た目を素早く構築するため |
| Lint/フォーマット | ESLint(flat config) + Prettier | TaskManagementと同一設定を踏襲 |
| テスト | Vitest + Testing Library | TaskManagementには無い要素として、今回新たに導入。手動確認だけに頼らず、今後の機能追加時のデグレを機械的に検知できるようにするため |

CORS設定はバックエンド側(`raisetech.StudentManagement.config.WebConfig`)で`http://localhost:5173`からのGETリクエストのみ許可している。

### 1.3 データベース・インフラ

| 項目 | 選定 | 理由 |
|------|------|------|
| データベース(本番) | MySQL(AWS RDS) | 指定 |
| データベース(テスト) | H2(インメモリ) | テスト実行のたびにクリーンな状態で高速に検証するため |
| 本番インフラ | AWS EC2 + RDS | [docs/infrastructure.md](infrastructure.md)を参照 |

## 2. API設計

ベースURL: (本番/ローカル環境に応じて変わる。ローカルは`http://localhost:8080`)

| メソッド | パス | 概要 |
|---------|------|------|
| GET | `/students` | 受講生一覧検索(全件、条件指定なし) |
| GET | `/students/{id}` | 受講生検索(IDに紐づく受講生詳細を取得) |
| POST | `/students/search` | 受講生条件検索(受講生・受講コース・申込状況を横断した条件で検索) |
| POST | `/students/register` | 受講生登録(受講コース・申込状況の初期登録を含む) |
| POST | `/students/{id}/courses/add` | 受講コース詳細追加 |
| PUT | `/students/update` | 受講生更新(論理削除によるキャンセルフラグ更新を含む) |
| PUT | `/courses/update` | 受講コース詳細更新(申込状況の状態遷移チェックを含む) |
| GET | `/testException` | (開発用)例外ハンドリングの動作確認用エンドポイント |

すべてのレスポンスは`StudentDetail`(受講生+受講コース詳細のリストをまとめたドメインオブジェクト)、または`CourseDetail`(受講コース情報+申込状況をまとめたドメインオブジェクト)を基本とする。

### エラーレスポンス

| 例外 | HTTPステータス |
|------|-----------------|
| `ResourceNotFoundException` | 404 Not Found |
| `InvalidStatusTransitionException` | 409 Conflict |
| `IllegalArgumentException`(バリデーションエラー等) | 400 Bad Request |
| `TestException`(動作確認用) | 400 Bad Request |

## 3. DB物理設計

要件定義書の[ER図](requirements.md#4-データ項目er図)を参照。

| テーブル | 主なカラム | 備考 |
|----------|------------|------|
| students | id(bigint, 自動採番), name, furigana, nickname, age, email, area, gender, remark, is_deleted | APIレイヤーでは`id`を`String`として扱っている(DBの`bigint`と型が一致していない点は[今後の課題](#5-今後の課題)を参照) |
| students_courses | id, student_id, course_name, course_start_at, course_end_at | 登録時、開始日=現在時刻、終了日=開始日+1年をアプリケーション側で自動設定 |
| course_applications | id, student_id, course_id, application_status | `application_status`は`ApplicationStatus` enumの値(TEMP/FORMAL/IN_PROGRESS/COMPLETED/CANCEL) |

## 4. ディレクトリ構成

```
backend/src/main/java/raisetech/StudentManagement/
├── config/                 # Spring設定(CORS設定等)
├── controller/            # REST APIのエンドポイント
│   └── converter/          # Entity(Student等)とDTO/ドメインオブジェクトの相互変換
├── data/                   # DBテーブルに対応するEntity(Student, StudentCourse, CourseApplication)
├── domain/                 # 複数Entityを組み合わせたドメインオブジェクト(StudentDetail, CourseDetail)
├── dto/
│   ├── request/             # リクエスト用DTO(検索条件など)
│   └── result/              # フラットな検索結果を受け取るDTO
├── enums/                  # 状態・区分を表すenum(ApplicationStatus, SearchType等)
├── exception/              # 業務例外・システム例外とグローバル例外ハンドラ
├── repository/             # MyBatisのMapperインターフェース
└── service/                # ビジネスロジック(検索・登録・更新・状態遷移チェック)
```

## 5. 今後の課題

未着手のまま残っている項目を記録する(着手・解決したら取り消し線または削除する)。

- [ ] `/testException`エンドポイントの扱いを検討する(本番公開のままでよいか)
- [ ] IDが`String`型・DBが`bigint`という型の整合性を確認する
- [ ] 結合テストを追加する
- [ ] Testcontainers導入(H2から本物のMySQLでの統合テストへ)を検討する
- [ ] Flywayでのスキーマバージョン管理導入を検討する
- [x] 自作コードとAI利用コードの区別方針(フロントエンドはディレクトリ分離、バックエンドはコミット/PR単位)をフロントエンド着手時に適用する → `frontend/`ディレクトリを新設することで対応(2026-09-02)
