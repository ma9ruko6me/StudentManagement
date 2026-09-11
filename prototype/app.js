// ==== ダミーデータ ====
// バックエンドAPIとは接続せず、静的なダミーデータのみを表示する
let students = [
  {
    id: '1', name: '山田 太郎', furigana: 'ヤマダ タロウ', nickname: 'たろちゃん',
    age: 24, email: 'yamada.taro@example.com', area: '東京都', gender: '男性',
    remark: '', isDeleted: false,
    courses: [
      { courseName: 'Javaフルコース', courseStartAt: '2026-04-01', courseEndAt: '2026-09-30' },
    ],
  },
  {
    id: '2', name: '佐藤 花子', furigana: 'サトウ ハナコ', nickname: 'はなちゃん',
    age: 29, email: 'sato.hanako@example.com', area: '大阪府', gender: '女性',
    remark: '', isDeleted: false,
    courses: [
      { courseName: 'フロントエンドコース', courseStartAt: '2026-02-01', courseEndAt: '2026-06-30' },
    ],
  },
  {
    id: '3', name: '鈴木 一郎', furigana: 'スズキ イチロウ', nickname: 'いっちゃん',
    age: 35, email: 'suzuki.ichiro@example.com', area: '愛知県', gender: '男性',
    remark: '過去に受講経験あり', isDeleted: false,
    courses: [],
  },
  {
    id: '4', name: '高橋 美咲', furigana: 'タカハシ ミサキ', nickname: 'みさき',
    age: 22, email: 'takahashi.misaki@example.com', area: '福岡県', gender: '女性',
    remark: '', isDeleted: false,
    courses: [
      { courseName: 'デザインコース', courseStartAt: '2026-05-01', courseEndAt: '2026-10-31' },
    ],
  },
  {
    id: '5', name: '田中 健', furigana: 'タナカ ケン', nickname: 'けんちゃん',
    age: 41, email: 'tanaka.ken@example.com', area: '北海道', gender: '男性',
    remark: '', isDeleted: true,
    courses: [
      { courseName: 'インフラコース', courseStartAt: '2025-10-01', courseEndAt: '2026-03-31' },
    ],
  },
];

let nextStudentId = students.length + 1;
let currentDetailId = null;

// ==== 画面切り替え ====
function showScreen(screenId) {
  document.querySelectorAll('.screen').forEach((el) => {
    el.hidden = el.id !== screenId;
  });
}

document.querySelectorAll('.back-link').forEach((btn) => {
  btn.addEventListener('click', () => showScreen(btn.dataset.target));
});

// ==== 一覧画面：検索条件の選択肢を初期化 ====
function populateAreaOptions() {
  const areaSelect = document.getElementById('searchArea');
  const currentValue = areaSelect.value;
  areaSelect.innerHTML = '<option value="">すべて</option>';
  const areas = [...new Set(students.map((s) => s.area))].sort();
  areas.forEach((area) => {
    const option = document.createElement('option');
    option.value = area;
    option.textContent = area;
    areaSelect.appendChild(option);
  });
  areaSelect.value = currentValue;
}

// ==== 一覧画面：検索・絞り込み ====
function getSearchConditions() {
  return {
    keyword: document.getElementById('searchKeyword').value.trim(),
    area: document.getElementById('searchArea').value,
    gender: document.getElementById('searchGender').value,
    ageFrom: document.getElementById('searchAgeFrom').value,
    ageTo: document.getElementById('searchAgeTo').value,
  };
}

function getFilteredStudents() {
  const { keyword, area, gender, ageFrom, ageTo } = getSearchConditions();

  return students.filter((student) => {
    if (student.isDeleted) return false;
    if (keyword && !student.name.includes(keyword) && !student.furigana.includes(keyword)) return false;
    if (area && student.area !== area) return false;
    if (gender && student.gender !== gender) return false;
    if (ageFrom && student.age < Number(ageFrom)) return false;
    if (ageTo && student.age > Number(ageTo)) return false;
    return true;
  });
}

function renderStudentTable() {
  const tableBody = document.getElementById('studentTableBody');
  const emptyMessage = document.getElementById('listEmptyMessage');
  const filtered = getFilteredStudents();

  tableBody.innerHTML = '';
  filtered.forEach((student) => {
    const row = document.createElement('tr');
    row.classList.add('clickable-row');
    row.addEventListener('click', () => openDetail(student.id));

    [student.name, student.furigana, student.nickname, student.age, student.email, student.area, student.gender]
      .forEach((value) => {
        const cell = document.createElement('td');
        cell.textContent = value;
        row.appendChild(cell);
      });

    tableBody.appendChild(row);
  });

  emptyMessage.hidden = filtered.length > 0;
}

document.getElementById('searchForm').addEventListener('input', renderStudentTable);
document.getElementById('resetSearchBtn').addEventListener('click', () => {
  document.getElementById('searchForm').reset();
  renderStudentTable();
});

// ==== 論理削除済み一覧画面 ====
function renderDeletedTable() {
  const tableBody = document.getElementById('deletedTableBody');
  const emptyMessage = document.getElementById('deletedEmptyMessage');
  const deleted = students.filter((s) => s.isDeleted);

  tableBody.innerHTML = '';
  deleted.forEach((student) => {
    const row = document.createElement('tr');
    [student.name, student.furigana, student.nickname, student.age, student.email, student.area, student.gender]
      .forEach((value) => {
        const cell = document.createElement('td');
        cell.textContent = value;
        row.appendChild(cell);
      });
    tableBody.appendChild(row);
  });

  emptyMessage.hidden = deleted.length > 0;
}

document.getElementById('openDeletedBtn').addEventListener('click', () => {
  renderDeletedTable();
  showScreen('deletedScreen');
});

// ==== 詳細・編集画面 ====
const detailForm = document.getElementById('detailForm');
const editToggleBtn = document.getElementById('editToggleBtn');
const saveDetailBtn = document.getElementById('saveDetailBtn');
const detailFormError = document.getElementById('detailFormError');

function findStudentById(id) {
  return students.find((s) => s.id === id);
}

function fillDetailForm(student) {
  Object.entries(student).forEach(([key, value]) => {
    const field = detailForm.elements.namedItem(key);
    if (field) field.value = value;
  });
}

function setDetailFormDisabled(disabled) {
  Array.from(detailForm.elements).forEach((field) => {
    if (field.type !== 'submit' && field.type !== 'button') field.disabled = disabled;
  });
}

function renderCourseTable(student) {
  const tableBody = document.getElementById('courseTableBody');
  const emptyMessage = document.getElementById('courseEmptyMessage');

  tableBody.innerHTML = '';
  student.courses.forEach((course) => {
    const row = document.createElement('tr');
    [course.courseName, course.courseStartAt, course.courseEndAt].forEach((value) => {
      const cell = document.createElement('td');
      cell.textContent = value;
      row.appendChild(cell);
    });
    tableBody.appendChild(row);
  });

  emptyMessage.hidden = student.courses.length > 0;
}

function openDetail(id) {
  const student = findStudentById(id);
  if (!student) return;

  currentDetailId = id;
  fillDetailForm(student);
  setDetailFormDisabled(true);
  editToggleBtn.hidden = false;
  saveDetailBtn.hidden = true;
  detailFormError.hidden = true;
  renderCourseTable(student);
  showScreen('detailScreen');
}

editToggleBtn.addEventListener('click', () => {
  setDetailFormDisabled(false);
  editToggleBtn.hidden = true;
  saveDetailBtn.hidden = false;
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

function readFormData(form) {
  const formData = new FormData(form);
  return {
    name: formData.get('name')?.trim() ?? '',
    furigana: formData.get('furigana')?.trim() ?? '',
    nickname: formData.get('nickname')?.trim() ?? '',
    email: formData.get('email')?.trim() ?? '',
    area: formData.get('area')?.trim() ?? '',
    age: formData.get('age') ?? '',
    gender: formData.get('gender') ?? '',
    remark: formData.get('remark')?.trim() ?? '',
  };
}

detailForm.addEventListener('submit', (event) => {
  event.preventDefault();
  const student = findStudentById(currentDetailId);
  if (!student) return;

  const data = readFormData(detailForm);
  const errorMessage = validateStudentInput(data);
  if (errorMessage) {
    detailFormError.textContent = errorMessage;
    detailFormError.hidden = false;
    return;
  }

  Object.assign(student, data, { age: Number(data.age) });
  detailFormError.hidden = true;
  setDetailFormDisabled(true);
  editToggleBtn.hidden = false;
  saveDetailBtn.hidden = true;
  populateAreaOptions();
  renderStudentTable();
  showScreen('listScreen');
});

document.getElementById('deleteStudentBtn').addEventListener('click', () => {
  const student = findStudentById(currentDetailId);
  if (!student) return;
  if (!confirm(`${student.name} さんを論理削除します。よろしいですか？`)) return;

  student.isDeleted = true;
  renderStudentTable();
  showScreen('listScreen');
});

// ==== コース追加 ====
const courseForm = document.getElementById('courseForm');
const courseFormError = document.getElementById('courseFormError');

courseForm.addEventListener('submit', (event) => {
  event.preventDefault();
  const student = findStudentById(currentDetailId);
  if (!student) return;

  const courseName = document.getElementById('newCourseName').value.trim();
  const courseStartAt = document.getElementById('newCourseStart').value;
  const courseEndAt = document.getElementById('newCourseEnd').value;

  if (!courseName || !courseStartAt || !courseEndAt) {
    courseFormError.textContent = 'コース名・開始日・終了日をすべて入力してください。';
    courseFormError.hidden = false;
    return;
  }
  if (courseEndAt < courseStartAt) {
    courseFormError.textContent = '終了日は開始日より後の日付にしてください。';
    courseFormError.hidden = false;
    return;
  }

  student.courses.push({ courseName, courseStartAt, courseEndAt });
  courseFormError.hidden = true;
  courseForm.reset();
  renderCourseTable(student);
});

// ==== 新規登録画面 ====
const registerForm = document.getElementById('registerForm');
const registerFormError = document.getElementById('registerFormError');

document.getElementById('openRegisterBtn').addEventListener('click', () => {
  registerForm.reset();
  registerFormError.hidden = true;
  showScreen('registerScreen');
});

registerForm.addEventListener('submit', (event) => {
  event.preventDefault();
  const data = readFormData(registerForm);
  const errorMessage = validateStudentInput(data);
  if (errorMessage) {
    registerFormError.textContent = errorMessage;
    registerFormError.hidden = false;
    return;
  }

  students.push({
    id: String(nextStudentId++),
    ...data,
    age: Number(data.age),
    isDeleted: false,
    courses: [],
  });

  registerFormError.hidden = true;
  populateAreaOptions();
  renderStudentTable();
  showScreen('listScreen');
});

// ==== 初期描画 ====
populateAreaOptions();
renderStudentTable();
