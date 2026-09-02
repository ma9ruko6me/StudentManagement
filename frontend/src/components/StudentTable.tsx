import { useStudents } from '../hooks/useStudents'
import type { Student } from '../types/student'

const COLUMNS: { key: keyof Student; label: string }[] = [
  { key: 'name', label: '名前' },
  { key: 'furigana', label: 'ふりがな' },
  { key: 'nickname', label: 'ニックネーム' },
  { key: 'age', label: '年齢' },
  { key: 'email', label: 'メール' },
  { key: 'area', label: '地域' },
  { key: 'gender', label: '性別' },
]

export function StudentTable() {
  const { data: studentDetails, isLoading, isError } = useStudents()

  if (isLoading) {
    return <p className="p-6 text-sm text-text-muted">読み込み中...</p>
  }

  if (isError || !studentDetails) {
    return <p className="p-6 text-sm text-danger">受講生の取得に失敗しました。</p>
  }

  const students = studentDetails
    .map((detail) => detail.student)
    .filter((student) => !student.deleted)

  return (
    <div className="p-5">
      <table className="w-full overflow-hidden rounded-lg bg-surface text-sm shadow-sm">
        <thead>
          <tr>
            {COLUMNS.map((column) => (
              <th
                key={column.key}
                className="border-b border-border bg-surface-subtle px-3 py-2.5 text-left text-xs font-bold text-text-muted"
              >
                {column.label}
              </th>
            ))}
          </tr>
        </thead>
        <tbody className="divide-y divide-border">
          {students.map((student) => (
            <tr key={student.id} className="hover:bg-surface-hover">
              {COLUMNS.map((column) => (
                <td key={column.key} className="px-3 py-2.5 text-text">
                  {student[column.key]}
                </td>
              ))}
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  )
}
