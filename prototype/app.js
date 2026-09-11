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

// ==== 新規登録モーダル ====
const registerModal = document.getElementById('registerModal');
const registerForm = document.getElementById('registerForm');
const registerFormError = document.getElementById('registerFormError');

function openRegisterModal() {
  registerForm.reset();
  registerFormError.hidden = true;
  registerModal.hidden = false;
}

function closeRegisterModal() {
  registerModal.hidden = true;
}

document.getElementById('openRegisterBtn').addEventListener('click', openRegisterModal);
document.getElementById('closeRegisterBtn').addEventListener('click', closeRegisterModal);
registerModal.addEventListener('click', (event) => {
  if (event.target === registerModal) closeRegisterModal();
});

function validateStudentInput(data) {
  if (!data.name || !data.furigana || !data.nickname || !data.email || !data.area || !data.gender) {
    return '必須項目が未入力です。';
  }
  if (!/^[^@\s]+@[^@\s]+\.[^@\s]+$/.test(data.email)) {
    return 'メールアドレスの形式が正しくありません。';
  }
  if (data.age === '' || Number.isNaN(Number(data.age)) || Number(data.age) < 0) {
    return '年齢は0以上の数値で入力してください。';
  }
  return null;
}

registerForm.addEventListener('submit', (event) => {
  event.preventDefault();

  const formData = new FormData(registerForm);
  const data = {
    name: formData.get('name').trim(),
    furigana: formData.get('furigana').trim(),
    nickname: formData.get('nickname').trim(),
    email: formData.get('email').trim(),
    area: formData.get('area').trim(),
    age: formData.get('age'),
    gender: formData.get('gender'),
  };

  const errorMessage = validateStudentInput(data);
  if (errorMessage) {
    registerFormError.textContent = errorMessage;
    registerFormError.hidden = false;
    return;
  }

  students.push({ ...data, age: Number(data.age) });
  registerFormError.hidden = true;
  renderStudentTable();
  closeRegisterModal();
});
