// ==== ダミーデータ ====
// バックエンドAPIとは接続せず、静的なダミーデータのみを表示する
const students = [
  {
    name: '山田 太郎', furigana: 'ヤマダ タロウ', nickname: 'たろちゃん', age: 24,
    email: 'yamada.taro@example.com', area: '東京都', gender: '男性', remark: '',
    courses: [
      { id: 1, courseName: 'Javaフルコース', applicationStatus: 'IN_PROGRESS' },
      { id: 2, courseName: 'AWSコース', applicationStatus: 'TEMP' },
    ],
  },
  {
    name: '佐藤 花子', furigana: 'サトウ ハナコ', nickname: 'はなちゃん', age: 29,
    email: 'sato.hanako@example.com', area: '大阪府', gender: '女性', remark: '転職希望',
    courses: [
      { id: 3, courseName: 'デザインコース', applicationStatus: 'FORMAL' },
    ],
  },
  {
    name: '鈴木 一郎', furigana: 'スズキ イチロウ', nickname: 'いっちゃん', age: 35,
    email: 'suzuki.ichiro@example.com', area: '愛知県', gender: '男性', remark: '',
    courses: [
      { id: 4, courseName: 'Javaフルコース', applicationStatus: 'COMPLETED' },
      { id: 5, courseName: 'AWSコース', applicationStatus: 'IN_PROGRESS' },
    ],
  },
  {
    name: '高橋 美咲', furigana: 'タカハシ ミサキ', nickname: 'みさき', age: 22,
    email: 'takahashi.misaki@example.com', area: '福岡県', gender: '女性', remark: '',
    courses: [
      { id: 6, courseName: 'デザインコース', applicationStatus: 'CANCEL' },
    ],
  },
  {
    name: '田中 健', furigana: 'タナカ ケン', nickname: 'けんちゃん', age: 41,
    email: 'tanaka.ken@example.com', area: '北海道', gender: '男性', remark: '',
    courses: [
      { id: 7, courseName: 'Javaフルコース', applicationStatus: 'TEMP' },
    ],
  },
];

let nextCourseId = 8;
let currentStudentIndex = null;

// ==== 申込状況(本番 ApplicationStatus と同じルール) ====
const STATUS_LABELS = {
  TEMP: '仮申込',
  FORMAL: '本申込',
  IN_PROGRESS: '受講中',
  COMPLETED: '受講終了',
  CANCEL: 'キャンセル',
};

const STATUS_TRANSITIONS = {
  TEMP: ['FORMAL', 'CANCEL'],
  FORMAL: ['TEMP', 'IN_PROGRESS', 'CANCEL'],
  IN_PROGRESS: ['FORMAL', 'COMPLETED', 'CANCEL'],
  COMPLETED: ['IN_PROGRESS'],
  CANCEL: [],
};

// ==== 一覧描画 ====
const tableBody = document.getElementById('studentTableBody');

function renderStudentTable() {
  tableBody.innerHTML = '';

  students.forEach((student, index) => {
    const row = document.createElement('tr');
    row.dataset.studentIndex = String(index);
    row.classList.add('clickable-row');

    [student.name, student.furigana, student.nickname, student.age, student.email, student.area, student.gender]
      .forEach(value => {
        const cell = document.createElement('td');
        cell.textContent = value;
        row.appendChild(cell);
      });

    row.addEventListener('click', () => openDetailModal(index));
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

  students.push({ ...data, age: Number(data.age), remark: '', courses: [] });
  registerFormError.hidden = true;
  renderStudentTable();
  closeRegisterModal();
});

// ==== 受講生詳細モーダル ====
const detailModal = document.getElementById('detailModal');

function openDetailModal(index) {
  currentStudentIndex = index;
  renderDetailModal();
  detailModal.hidden = false;
}

function closeDetailModal() {
  detailModal.hidden = true;
}

function renderDetailModal() {
  const student = students[currentStudentIndex];

  document.getElementById('detailName').textContent = student.name;
  document.getElementById('detailFurigana').textContent = student.furigana;
  document.getElementById('detailNickname').textContent = student.nickname;
  document.getElementById('detailEmail').textContent = student.email;
  document.getElementById('detailArea').textContent = student.area;
  document.getElementById('detailAge').textContent = student.age;
  document.getElementById('detailGender').textContent = student.gender;
  document.getElementById('detailRemark').textContent = student.remark || '(なし)';

  renderCourseList(student);

  document.getElementById('addCourseForm').reset();
  document.getElementById('addCourseFormError').hidden = true;
}

function renderCourseList(student) {
  const courseTableBody = document.getElementById('courseTableBody');
  courseTableBody.innerHTML = '';

  student.courses.forEach(course => {
    const row = document.createElement('tr');

    const nameCell = document.createElement('td');
    nameCell.textContent = course.courseName;
    row.appendChild(nameCell);

    const statusCell = document.createElement('td');
    const badge = document.createElement('span');
    badge.classList.add('status-badge', `status-${course.applicationStatus}`);
    badge.textContent = STATUS_LABELS[course.applicationStatus];
    statusCell.appendChild(badge);
    row.appendChild(statusCell);

    const actionCell = document.createElement('td');
    const nextStatuses = STATUS_TRANSITIONS[course.applicationStatus];

    if (nextStatuses.length > 0) {
      const select = document.createElement('select');
      nextStatuses.forEach(status => {
        const option = document.createElement('option');
        option.value = status;
        option.textContent = STATUS_LABELS[status];
        select.appendChild(option);
      });

      const changeBtn = document.createElement('button');
      changeBtn.type = 'button';
      changeBtn.classList.add('btn', 'btn-primary');
      changeBtn.textContent = '変更';
      changeBtn.addEventListener('click', () => {
        changeCourseStatus(course.id, select.value);
      });

      actionCell.appendChild(select);
      actionCell.appendChild(changeBtn);
    } else {
      actionCell.textContent = '-';
    }

    row.appendChild(actionCell);
    courseTableBody.appendChild(row);
  });
}

function changeCourseStatus(courseId, nextStatus) {
  const student = students[currentStudentIndex];
  const course = student.courses.find(c => c.id === courseId);
  if (!course) return;

  const allowed = STATUS_TRANSITIONS[course.applicationStatus];
  if (!allowed.includes(nextStatus)) return;

  course.applicationStatus = nextStatus;
  renderCourseList(student);
}

document.getElementById('closeDetailBtn').addEventListener('click', closeDetailModal);
detailModal.addEventListener('click', (event) => {
  if (event.target === detailModal) closeDetailModal();
});

// ==== コース追加 ====
const addCourseForm = document.getElementById('addCourseForm');
const addCourseFormError = document.getElementById('addCourseFormError');

addCourseForm.addEventListener('submit', (event) => {
  event.preventDefault();

  const formData = new FormData(addCourseForm);
  const courseName = formData.get('courseName').trim();

  if (!courseName) {
    addCourseFormError.textContent = 'コース名を入力してください。';
    addCourseFormError.hidden = false;
    return;
  }

  const student = students[currentStudentIndex];
  student.courses.push({ id: nextCourseId++, courseName, applicationStatus: 'TEMP' });

  addCourseFormError.hidden = true;
  addCourseForm.reset();
  renderCourseList(student);
});

// ==== 受講生編集モーダル ====
const editModal = document.getElementById('editModal');
const editForm = document.getElementById('editForm');
const editFormError = document.getElementById('editFormError');

function openEditModal() {
  const student = students[currentStudentIndex];
  editForm.reset();
  editFormError.hidden = true;

  editForm.elements.name.value = student.name;
  editForm.elements.furigana.value = student.furigana;
  editForm.elements.nickname.value = student.nickname;
  editForm.elements.email.value = student.email;
  editForm.elements.area.value = student.area;
  editForm.elements.age.value = student.age;
  editForm.elements.gender.value = student.gender;
  editForm.elements.remark.value = student.remark || '';

  editModal.hidden = false;
}

function closeEditModal() {
  editModal.hidden = true;
}

document.getElementById('openEditBtn').addEventListener('click', openEditModal);
document.getElementById('closeEditBtn').addEventListener('click', closeEditModal);
editModal.addEventListener('click', (event) => {
  if (event.target === editModal) closeEditModal();
});

editForm.addEventListener('submit', (event) => {
  event.preventDefault();

  const formData = new FormData(editForm);
  const data = {
    name: formData.get('name').trim(),
    furigana: formData.get('furigana').trim(),
    nickname: formData.get('nickname').trim(),
    email: formData.get('email').trim(),
    area: formData.get('area').trim(),
    age: formData.get('age'),
    gender: formData.get('gender'),
    remark: formData.get('remark').trim(),
  };

  const errorMessage = validateStudentInput(data);
  if (errorMessage) {
    editFormError.textContent = errorMessage;
    editFormError.hidden = false;
    return;
  }

  const student = students[currentStudentIndex];
  Object.assign(student, { ...data, age: Number(data.age) });

  editFormError.hidden = true;
  renderStudentTable();
  renderDetailModal();
  closeEditModal();
});
