// ==== ダミーデータ ====
// バックエンドAPIとは接続せず、静的なダミーデータのみを表示する
const students = [
  { name: '山田 太郎', furigana: 'ヤマダ タロウ', nickname: 'たろちゃん', age: 24, email: 'yamada.taro@example.com', area: '東京都', gender: '男性' },
  { name: '佐藤 花子', furigana: 'サトウ ハナコ', nickname: 'はなちゃん', age: 29, email: 'sato.hanako@example.com', area: '大阪府', gender: '女性' },
  { name: '鈴木 一郎', furigana: 'スズキ イチロウ', nickname: 'いっちゃん', age: 35, email: 'suzuki.ichiro@example.com', area: '愛知県', gender: '男性' },
  { name: '高橋 美咲', furigana: 'タカハシ ミサキ', nickname: 'みさき', age: 22, email: 'takahashi.misaki@example.com', area: '福岡県', gender: '女性' },
  { name: '田中 健', furigana: 'タナカ ケン', nickname: 'けんちゃん', age: 41, email: 'tanaka.ken@example.com', area: '北海道', gender: '男性' },
];

// ==== 一覧描画 ====
const tableBody = document.getElementById('studentTableBody');

function renderStudentTable() {
  tableBody.innerHTML = '';

  students.forEach(student => {
    const row = document.createElement('tr');

    [student.name, student.furigana, student.nickname, student.age, student.email, student.area, student.gender]
      .forEach(value => {
        const cell = document.createElement('td');
        cell.textContent = value;
        row.appendChild(cell);
      });

    tableBody.appendChild(row);
  });
}

renderStudentTable();
