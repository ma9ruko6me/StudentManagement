import { useState, type FormEvent } from 'react'
import { useRegisterStudent } from '../hooks/useRegisterStudent'
import type { CourseDetail, RegisterStudentPayload } from '../types/student'

interface RegisterStudentModalProps {
  isOpen: boolean
  onClose: () => void
}

interface FormState {
  name: string
  furigana: string
  nickname: string
  email: string
  area: string
  age: string
  gender: string
  remark: string
}

const INITIAL_FORM_STATE: FormState = {
  name: '',
  furigana: '',
  nickname: '',
  email: '',
  area: '',
  age: '',
  gender: '',
  remark: '',
}

interface CourseRow {
  key: string
  courseName: string
}

function createEmptyCourseRow(key: string): CourseRow {
  return { key, courseName: '' }
}

function validate(form: FormState): string | null {
  if (!form.name || !form.furigana || !form.nickname || !form.email || !form.area || !form.gender) {
    return '必須項目が未入力です。'
  }
  if (!/^[^@\s]+@[^@\s]+\.[^@\s]+$/.test(form.email)) {
    return 'メールアドレスの形式が正しくありません。'
  }
  if (form.age === '' || Number.isNaN(Number(form.age)) || Number(form.age) < 0) {
    return '年齢は0以上の数値で入力してください。'
  }
  return null
}

function buildPayload(form: FormState, courseRows: CourseRow[]): RegisterStudentPayload {
  const courseDetailList: CourseDetail[] = courseRows
    .filter((row) => row.courseName.trim() !== '')
    .map((row) => ({
      studentCourse: { courseName: row.courseName.trim() },
      courseApplication: {},
    }))

  return {
    student: {
      name: form.name.trim(),
      furigana: form.furigana.trim(),
      nickname: form.nickname.trim(),
      email: form.email.trim(),
      area: form.area.trim(),
      age: Number(form.age),
      gender: form.gender,
      remark: form.remark.trim(),
    },
    courseDetailList,
  }
}

export function RegisterStudentModal({ isOpen, onClose }: RegisterStudentModalProps) {
  const [form, setForm] = useState<FormState>(INITIAL_FORM_STATE)
  const [courseRows, setCourseRows] = useState<CourseRow[]>([createEmptyCourseRow('course-0')])
  const [validationError, setValidationError] = useState<string | null>(null)
  const registerStudentMutation = useRegisterStudent()

  if (!isOpen) {
    return null
  }

  function resetAndClose() {
    setForm(INITIAL_FORM_STATE)
    setCourseRows([createEmptyCourseRow('course-0')])
    setValidationError(null)
    registerStudentMutation.reset()
    onClose()
  }

  function handleChange<K extends keyof FormState>(key: K, value: FormState[K]) {
    setForm((prev) => ({ ...prev, [key]: value }))
  }

  function handleCourseNameChange(key: string, value: string) {
    setCourseRows((prev) => prev.map((row) => (row.key === key ? { ...row, courseName: value } : row)))
  }

  function addCourseRow() {
    setCourseRows((prev) => [...prev, createEmptyCourseRow(`course-${prev.length}-${Date.now()}`)])
  }

  function removeCourseRow(key: string) {
    setCourseRows((prev) => (prev.length === 1 ? prev : prev.filter((row) => row.key !== key)))
  }

  function handleSubmit(event: FormEvent) {
    event.preventDefault()

    const errorMessage = validate(form)
    if (errorMessage) {
      setValidationError(errorMessage)
      return
    }
    setValidationError(null)

    registerStudentMutation.mutate(buildPayload(form, courseRows), {
      onSuccess: resetAndClose,
    })
  }

  return (
    <div
      className="fixed inset-0 flex items-center justify-center bg-black/40 p-5"
      onClick={(event) => {
        if (event.target === event.currentTarget) resetAndClose()
      }}
    >
      <div className="max-h-[90vh] w-full max-w-lg overflow-y-auto rounded-lg bg-surface p-5 shadow-lg">
        <div className="mb-3 flex items-center justify-between">
          <h2 className="text-base font-bold text-text">受講生 新規登録</h2>
          <button type="button" onClick={resetAndClose} className="text-text-muted hover:text-text">
            ✕
          </button>
        </div>

        <form onSubmit={handleSubmit} noValidate className="flex flex-col gap-4">
          <div className="grid grid-cols-2 gap-3">
            <label className="flex flex-col gap-1 text-xs text-text-muted">
              名前
              <input
                type="text"
                value={form.name}
                onChange={(e) => handleChange('name', e.target.value)}
                className="rounded border border-border px-2 py-1.5 text-sm text-text"
                required
              />
            </label>
            <label className="flex flex-col gap-1 text-xs text-text-muted">
              ふりがな
              <input
                type="text"
                value={form.furigana}
                onChange={(e) => handleChange('furigana', e.target.value)}
                className="rounded border border-border px-2 py-1.5 text-sm text-text"
                required
              />
            </label>
            <label className="flex flex-col gap-1 text-xs text-text-muted">
              ニックネーム
              <input
                type="text"
                value={form.nickname}
                onChange={(e) => handleChange('nickname', e.target.value)}
                className="rounded border border-border px-2 py-1.5 text-sm text-text"
                required
              />
            </label>
            <label className="flex flex-col gap-1 text-xs text-text-muted">
              メール
              <input
                type="email"
                value={form.email}
                onChange={(e) => handleChange('email', e.target.value)}
                className="rounded border border-border px-2 py-1.5 text-sm text-text"
                required
              />
            </label>
            <label className="flex flex-col gap-1 text-xs text-text-muted">
              地域
              <input
                type="text"
                value={form.area}
                onChange={(e) => handleChange('area', e.target.value)}
                className="rounded border border-border px-2 py-1.5 text-sm text-text"
                required
              />
            </label>
            <label className="flex flex-col gap-1 text-xs text-text-muted">
              年齢
              <input
                type="number"
                min={0}
                value={form.age}
                onChange={(e) => handleChange('age', e.target.value)}
                className="rounded border border-border px-2 py-1.5 text-sm text-text"
                required
              />
            </label>
            <label className="flex flex-col gap-1 text-xs text-text-muted">
              性別
              <select
                value={form.gender}
                onChange={(e) => handleChange('gender', e.target.value)}
                className="rounded border border-border px-2 py-1.5 text-sm text-text"
                required
              >
                <option value="">選択してください</option>
                <option value="男性">男性</option>
                <option value="女性">女性</option>
              </select>
            </label>
            <label className="col-span-2 flex flex-col gap-1 text-xs text-text-muted">
              備考
              <textarea
                value={form.remark}
                onChange={(e) => handleChange('remark', e.target.value)}
                className="min-h-16 resize-y rounded border border-border px-2 py-1.5 text-sm text-text"
              />
            </label>
          </div>

          <div className="flex flex-col gap-2">
            <span className="text-xs text-text-muted">受講コース</span>
            {courseRows.map((row) => (
              <div key={row.key} className="flex items-center gap-2">
                <input
                  type="text"
                  placeholder="コース名"
                  value={row.courseName}
                  onChange={(e) => handleCourseNameChange(row.key, e.target.value)}
                  className="flex-1 rounded border border-border px-2 py-1.5 text-sm text-text"
                />
                <button
                  type="button"
                  onClick={() => removeCourseRow(row.key)}
                  disabled={courseRows.length === 1}
                  className="rounded px-2 py-1 text-xs text-text-muted hover:text-danger disabled:opacity-40"
                >
                  削除
                </button>
              </div>
            ))}
            <button
              type="button"
              onClick={addCourseRow}
              className="self-start rounded bg-surface-subtle px-3 py-1 text-xs text-text hover:bg-surface-hover"
            >
              + コースを追加
            </button>
          </div>

          {validationError && <p className="text-xs text-danger">{validationError}</p>}
          {registerStudentMutation.isError && (
            <p className="text-xs text-danger">登録に失敗しました。時間をおいて再度お試しください。</p>
          )}

          <div className="flex gap-2">
            <button
              type="submit"
              disabled={registerStudentMutation.isPending}
              className="rounded bg-accent px-4 py-2 text-sm font-bold text-white disabled:opacity-60"
            >
              {registerStudentMutation.isPending ? '登録中...' : '登録する'}
            </button>
          </div>
        </form>
      </div>
    </div>
  )
}
