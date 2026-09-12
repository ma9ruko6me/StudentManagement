import { useState, type FormEvent } from 'react'
import { useUpdateStudent } from '../hooks/useUpdateStudent'
import type { Student } from '../types/student'

interface EditStudentModalProps {
  isOpen: boolean
  student: Student
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

function toFormState(student: Student): FormState {
  return {
    name: student.name,
    furigana: student.furigana,
    nickname: student.nickname,
    email: student.email,
    area: student.area,
    age: String(student.age),
    gender: student.gender,
    remark: student.remark,
  }
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

export function EditStudentModal({ isOpen, student, onClose }: EditStudentModalProps) {
  const [form, setForm] = useState<FormState>(() => toFormState(student))
  const [validationError, setValidationError] = useState<string | null>(null)
  const updateStudentMutation = useUpdateStudent()

  if (!isOpen) {
    return null
  }

  function handleClose() {
    setValidationError(null)
    updateStudentMutation.reset()
    onClose()
  }

  function handleChange<K extends keyof FormState>(key: K, value: FormState[K]) {
    setForm((prev) => ({ ...prev, [key]: value }))
  }

  function handleSubmit(event: FormEvent) {
    event.preventDefault()

    const errorMessage = validate(form)
    if (errorMessage) {
      setValidationError(errorMessage)
      return
    }
    setValidationError(null)

    const updatedStudent: Student = {
      ...student,
      name: form.name.trim(),
      furigana: form.furigana.trim(),
      nickname: form.nickname.trim(),
      email: form.email.trim(),
      area: form.area.trim(),
      age: Number(form.age),
      gender: form.gender,
      remark: form.remark.trim(),
    }

    updateStudentMutation.mutate(updatedStudent, {
      onSuccess: handleClose,
    })
  }

  return (
    <div
      className="fixed inset-0 flex items-center justify-center bg-black/40 p-5"
      onClick={(event) => {
        if (event.target === event.currentTarget) handleClose()
      }}
    >
      <div className="max-h-[90vh] w-full max-w-lg overflow-y-auto rounded-lg bg-surface p-5 shadow-lg">
        <div className="mb-3 flex items-center justify-between">
          <h2 className="text-base font-bold text-text">受講生 編集</h2>
          <button type="button" onClick={handleClose} className="text-text-muted hover:text-text">
            ✕
          </button>
        </div>

        <form onSubmit={handleSubmit} noValidate className="flex flex-col gap-4">
          <p className="text-xs text-text-muted">
            <span className="text-danger">＊</span> は必須項目です
          </p>
          <div className="grid grid-cols-2 gap-3">
            <label className="flex flex-col gap-1 text-xs text-text-muted">
              <span>名前<span className="text-danger">＊</span></span>
              <input
                type="text"
                value={form.name}
                onChange={(e) => handleChange('name', e.target.value)}
                className="rounded border border-border px-2 py-1.5 text-sm text-text"
                required
              />
            </label>
            <label className="flex flex-col gap-1 text-xs text-text-muted">
              <span>ふりがな<span className="text-danger">＊</span></span>
              <input
                type="text"
                value={form.furigana}
                onChange={(e) => handleChange('furigana', e.target.value)}
                className="rounded border border-border px-2 py-1.5 text-sm text-text"
                required
              />
            </label>
            <label className="flex flex-col gap-1 text-xs text-text-muted">
              <span>ニックネーム<span className="text-danger">＊</span></span>
              <input
                type="text"
                value={form.nickname}
                onChange={(e) => handleChange('nickname', e.target.value)}
                className="rounded border border-border px-2 py-1.5 text-sm text-text"
                required
              />
            </label>
            <label className="flex flex-col gap-1 text-xs text-text-muted">
              <span>メール<span className="text-danger">＊</span></span>
              <input
                type="email"
                value={form.email}
                onChange={(e) => handleChange('email', e.target.value)}
                className="rounded border border-border px-2 py-1.5 text-sm text-text"
                required
              />
            </label>
            <label className="flex flex-col gap-1 text-xs text-text-muted">
              <span>地域<span className="text-danger">＊</span></span>
              <input
                type="text"
                value={form.area}
                onChange={(e) => handleChange('area', e.target.value)}
                className="rounded border border-border px-2 py-1.5 text-sm text-text"
                required
              />
            </label>
            <label className="flex flex-col gap-1 text-xs text-text-muted">
              <span>年齢<span className="text-danger">＊</span></span>
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
              <span>性別<span className="text-danger">＊</span></span>
              <select
                value={form.gender}
                onChange={(e) => handleChange('gender', e.target.value)}
                className="rounded border border-border px-2 py-1.5 text-sm text-text"
                required
              >
                <option value="">選択してください</option>
                <option value="男性">男性</option>
                <option value="女性">女性</option>
                <option value="その他">その他</option>
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

          {validationError && <p className="text-xs text-danger">{validationError}</p>}
          {updateStudentMutation.isError && (
            <p className="text-xs text-danger">更新に失敗しました。時間をおいて再度お試しください。</p>
          )}

          <div className="flex gap-2">
            <button
              type="submit"
              disabled={updateStudentMutation.isPending}
              className="rounded bg-accent px-4 py-2 text-sm font-bold text-white disabled:opacity-60"
            >
              {updateStudentMutation.isPending ? '更新中...' : '更新する'}
            </button>
          </div>
        </form>
      </div>
    </div>
  )
}
