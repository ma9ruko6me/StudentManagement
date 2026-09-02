---
name: quality-review
description: バックエンド(Spring Boot)・フロントエンド(Vite/React)の品質チェックと、実装とドキュメント(要件定義書・基本設計書・インフラ構成書)の整合性確認を行う。lint/静的解析の実行方法と、ドキュメント突き合わせの観点をまとめる。
---

# 品質レビュー手順

コード品質チェックとドキュメント整合性チェックを、機械的に検出できるものと設計判断が絡むものに分けて実施する。

## 方針

- lint/静的解析ツールで検出できる違反は、import順序や空白・命名規則などの機械的に安全なものに限りその場で修正してよい
- テストコード(Vitest/JUnit)の設計判断が絡む指摘は、自動修正せず一覧化してユーザーに報告する
- 実装とドキュメントが食い違う場合は、実装を正としてドキュメント側を修正する(逆に実装をドキュメントへ合わせる判断が必要な場合はユーザーに確認する)

## フロントエンド品質チェック(`frontend/`)

```bash
cd /Users/ogawahiroki/IdeaProjects/StudentManagement/frontend
npm run lint          # ESLint (typescript-eslint / react-hooks / react-refresh)
npx tsc -b             # 型チェック
npm run test           # Vitest
npx prettier --check . # フォーマット確認。eslint-plugin-prettierは未導入のため npm run lint では検知できない点に注意
```

確認観点:
- ESLintはエラー0件が前提。warningもできる限り解消する
- `tsconfig.app.json` は現状strictモード未設定。`any`の混入がないか個別に確認する
- Prettierの`--check`が失敗する場合、`--write`での一括整形は差分が大きくなるためユーザーに確認してから実施する
- コンポーネントの新規追加・変更時は対応するVitestのテスト(`*.test.tsx`)があるか確認する

## バックエンド品質チェック(`backend/`)

Checkstyle等の静的解析ツールは本プロジェクトでは未導入(2026-09-02時点)。テスト実行を必須のチェックとする。

```bash
cd /Users/ogawahiroki/IdeaProjects/StudentManagement/backend
./gradlew test
```

確認観点:
- 複数DB操作を伴うメソッドに`@Transactional`が付与されているか
- 例外ハンドラ(`StudentExceptionHandler`)が構造化されたレスポンスを返しているか
- Entity(`Student`, `StudentCourse`, `CourseApplication`)のフィールドが不用意に外部から変更可能になっていないか(setter乱用)
- 新規ロジックに対応するテスト(Service層はRepositoryをモック化した単体テスト)があるか

## ドキュメント整合性チェック

`docs/requirements.md`(要件定義書)・`docs/basic-design.md`(基本設計書)・`docs/infrastructure.md`(インフラ構成書)を、以下の観点で実装と突き合わせる。

| 観点 | ドキュメント側 | 実装側 |
|------|----------------|--------|
| データ項目・カラム | 要件定義書のER図、基本設計書3章のDB物理設計 | `backend/src/main/java/.../data/*.java`、`backend/src/main/resources/mapper/studentRepository.xml` |
| API仕様 | 基本設計書2章のエンドポイント一覧 | `StudentController.java`、各DTO/ドメインオブジェクト |
| 画面構成 | 要件定義書5章の画面要件 | `frontend/src/App.tsx`、`frontend/src/components/*.tsx` |
| 技術スタック(フロントエンド) | 基本設計書1.2節 | `frontend/package.json` |

相違を見つけたら、該当するドキュメント箇所を実装に合わせて修正する。ドキュメント内の目次アンカー(`#見出し`)が実際の見出しと一致しているかも合わせて確認する。
