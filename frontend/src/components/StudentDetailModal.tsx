import { useState, type FormEvent } from 'react'
import { isAxiosError } from 'axios'
import { useStudent } from '../hooks/useStudent'
import { useAddCourse } from '../hooks/useAddCourse'
import { useUpdateCourseStatus } from '../hooks/useUpdateCourseStatus'
import { EditStudentModal } from './EditStudentModal'
import {
  APPLICATION_STATUS_LABELS,
  APPLICATION_STATUS_TRANSITIONS,
  type ApplicationStatus,
  type CourseDetail,
} from '../types/student'

interface StudentDetailModalProps {
  studentId: string | null
  onClose: () => void
}

function extractErrorMessage(error: unknown): string {
  if (isAxiosError(error) && typeof error.response?.data === 'string' && error.response.data) {
    return error.response.data
  }
  return '更新に失敗しました。時間をおいて再度お試しください。'
}

export function StudentDetailModal({ studentId, onClose }: StudentDetailModalProps) {
  const { data: studentDetail, isLoading, isError } = useStudent(studentId)
  const addCourseMutation = useAddCourse(studentId)
  const updateCourseStatusMutation = useUpdateCourseStatus(studentId)

  const [courseName, setCourseName] = useState('')
  const [statusSelections, setStatusSelections] = useState<Record<string, ApplicationStatus>>({})
  const [statusError, setStatusError] = useState<{ courseId: string; message: string } | null>(null)
  const [isEditOpen, setIsEditOpen] = useState(false)
  const [editKey, setEditKey] = useState(0)

  if (studentId === null) {
    return null
  }

  function handleClose() {
    setCourseName('')
    setStatusSelections({})
    setStatusError(null)
    addCourseMutation.reset()
    onClose()
  }

  function handleAddCourse(event: FormEvent) {
    event.preventDefault()

    const trimmed = courseName.trim()
    if (!trimmed) {
      return
    }

    addCourseMutation.mutate(trimmed, {
      onSuccess: () => setCourseName(''),
    })
  }

  function handleStatusChange(courseId: string, nextStatus: ApplicationStatus, courseDetail: CourseDetail) {
    setStatusSelections((prev) => ({ ...prev, [courseId]: nextStatus }))

    updateCourseStatusMutation.mutate(
      {
        studentCourse: courseDetail.studentCourse,
        courseApplication: { ...courseDetail.courseApplication, applicationStatus: nextStatus },
      },
      {
        onError: (error) => setStatusError({ courseId, message: extractErrorMessage(error) }),
        onSuccess: () => setStatusError(null),
      },
    )
  }

  return (
    <>
      <div
        className="fixed inset-0 flex items-center justify-center bg-black/40 p-5"
        onClick={(event) => {
          if (event.target === event.currentTarget) handleClose()
        }}
      >
        <div className="max-h-[90vh] w-full max-w-lg overflow-y-auto rounded-lg bg-surface p-5 shadow-lg">
          <div className="mb-3 flex items-center justify-between">
            <h2 className="text-base font-bold text-text">受講生詳細</h2>
            <button type="button" onClick={handleClose} className="text-text-muted hover:text-text">
              ✕
            </button>
          </div>

          {isLoading && <p className="text-sm text-text-muted">読み込み中...</p>}
          {isError && <p className="text-sm text-danger">受講生の取得に失敗しました。</p>}

          {studentDetail && (
            <>
              <div className="grid grid-cols-2 gap-3 text-sm">
                <div className="flex flex-col gap-1">
                  <span className="text-xs text-text-muted">名前</span>
                  <span className="text-text">{studentDetail.student.name}</span>
                </div>
                <div className="flex flex-col gap-1">
                  <span className="text-xs text-text-muted">ふりがな</span>
                  <span className="text-text">{studentDetail.student.furigana}</span>
                </div>
                <div className="flex flex-col gap-1">
                  <span className="text-xs text-text-muted">ニックネーム</span>
                  <span className="text-text">{studentDetail.student.nickname}</span>
                </div>
                <div className="flex flex-col gap-1">
                  <span className="text-xs text-text-muted">メール</span>
                  <span className="text-text">{studentDetail.student.email}</span>
                </div>
                <div className="flex flex-col gap-1">
                  <span className="text-xs text-text-muted">地域</span>
                  <span className="text-text">{studentDetail.student.area}</span>
                </div>
                <div className="flex flex-col gap-1">
                  <span className="text-xs text-text-muted">年齢</span>
                  <span className="text-text">{studentDetail.student.age}</span>
                </div>
                <div className="flex flex-col gap-1">
                  <span className="text-xs text-text-muted">性別</span>
                  <span className="text-text">{studentDetail.student.gender}</span>
                </div>
                <div className="col-span-2 flex flex-col gap-1">
                  <span className="text-xs text-text-muted">備考</span>
                  <span className="text-text">{studentDetail.student.remark || '(なし)'}</span>
                </div>
              </div>

              <div className="mt-4">
                <button
                  type="button"
                  onClick={() => {
                    setEditKey((key) => key + 1)
                    setIsEditOpen(true)
                  }}
                  className="rounded bg-accent px-4 py-2 text-sm font-bold text-white"
                >
                  編集
                </button>
              </div>

              <h3 className="mt-5 mb-2 text-sm font-bold text-text">コース一覧</h3>
              <table className="w-full text-sm">
                <thead>
                  <tr>
                    <th className="border-b border-border px-2 py-1.5 text-left text-xs font-bold text-text-muted">
                      コース名
                    </th>
                    <th className="border-b border-border px-2 py-1.5 text-left text-xs font-bold text-text-muted">
                      申込状況
                    </th>
                    <th className="border-b border-border px-2 py-1.5 text-left text-xs font-bold text-text-muted">
                      状況変更
                    </th>
                  </tr>
                </thead>
                <tbody>
                  {studentDetail.courseDetailList.map((courseDetail) => {
                    const courseId = courseDetail.courseApplication.courseId ?? courseDetail.studentCourse.id ?? ''
                    const currentStatus = courseDetail.courseApplication.applicationStatus
                    const nextStatuses = currentStatus ? APPLICATION_STATUS_TRANSITIONS[currentStatus] : []
                    const selected = statusSelections[courseId] ?? nextStatuses[0]

                    return (
                      <tr key={courseId}>
                        <td className="border-b border-border px-2 py-1.5 text-text">
                          {courseDetail.studentCourse.courseName}
                        </td>
                        <td className="border-b border-border px-2 py-1.5 text-text">
                          {currentStatus ? APPLICATION_STATUS_LABELS[currentStatus] : '-'}
                        </td>
                        <td className="border-b border-border px-2 py-1.5">
                          {nextStatuses.length > 0 ? (
                            <div className="flex items-center gap-2">
                              <select
                                value={selected}
                                onChange={(e) =>
                                  setStatusSelections((prev) => ({
                                    ...prev,
                                    [courseId]: e.target.value as ApplicationStatus,
                                  }))
                                }
                                className="rounded border border-border px-2 py-1 text-xs text-text"
                              >
                                {nextStatuses.map((status) => (
                                  <option key={status} value={status}>
                                    {APPLICATION_STATUS_LABELS[status]}
                                  </option>
                                ))}
                              </select>
                              <button
                                type="button"
                                onClick={() => handleStatusChange(courseId, selected, courseDetail)}
                                disabled={updateCourseStatusMutation.isPending}
                                className="rounded bg-accent px-2 py-1 text-xs font-bold text-white disabled:opacity-60"
                              >
                                変更
                              </button>
                            </div>
                          ) : (
                            <span className="text-xs text-text-muted">-</span>
                          )}
                          {statusError?.courseId === courseId && (
                            <p className="mt-1 text-xs text-danger">{statusError.message}</p>
                          )}
                        </td>
                      </tr>
                    )
                  })}
                </tbody>
              </table>

              <h3 className="mt-5 mb-2 text-sm font-bold text-text">コース追加</h3>
              <form onSubmit={handleAddCourse} className="flex items-center gap-2">
                <input
                  type="text"
                  value={courseName}
                  onChange={(e) => setCourseName(e.target.value)}
                  placeholder="コース名"
                  className="flex-1 rounded border border-border px-2 py-1.5 text-sm text-text"
                />
                <button
                  type="submit"
                  disabled={addCourseMutation.isPending}
                  className="rounded bg-accent px-4 py-2 text-sm font-bold text-white disabled:opacity-60"
                >
                  追加
                </button>
              </form>
              {addCourseMutation.isError && (
                <p className="mt-1 text-xs text-danger">{extractErrorMessage(addCourseMutation.error)}</p>
              )}
            </>
          )}
        </div>
      </div>

      {studentDetail && (
        <EditStudentModal
          key={editKey}
          isOpen={isEditOpen}
          student={studentDetail.student}
          onClose={() => setIsEditOpen(false)}
        />
      )}
    </>
  )
}
