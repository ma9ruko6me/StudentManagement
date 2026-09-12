import { fireEvent, render, screen } from '@testing-library/react'
import { describe, expect, it, vi } from 'vitest'
import { StudentDetailModal } from './StudentDetailModal'
import { useStudent } from '../hooks/useStudent'
import { useAddCourse } from '../hooks/useAddCourse'
import { useUpdateCourseStatus } from '../hooks/useUpdateCourseStatus'
import { useUpdateStudent } from '../hooks/useUpdateStudent'
import type { StudentDetail } from '../types/student'

vi.mock('../hooks/useStudent')
vi.mock('../hooks/useAddCourse')
vi.mock('../hooks/useUpdateCourseStatus')
vi.mock('../hooks/useUpdateStudent')

const mockedUseStudent = vi.mocked(useStudent)
const mockedUseAddCourse = vi.mocked(useAddCourse)
const mockedUseUpdateCourseStatus = vi.mocked(useUpdateCourseStatus)
const mockedUseUpdateStudent = vi.mocked(useUpdateStudent)

const studentDetail: StudentDetail = {
  student: {
    id: '1',
    name: '山田太郎',
    furigana: 'ヤマダタロウ',
    nickname: 'たろ',
    email: 'taro@example.com',
    area: '東京都',
    age: 20,
    gender: '男性',
    remark: '',
    deleted: false,
  },
  courseDetailList: [
    {
      studentCourse: { id: '10', studentId: '1', courseName: 'Javaコース' },
      courseApplication: { id: '20', studentId: '1', courseId: '10', applicationStatus: 'TEMP' },
    },
  ],
}

function setup(overrides?: { data?: StudentDetail; isLoading?: boolean; isError?: boolean }) {
  const addCourseMutate = vi.fn()
  const updateStatusMutate = vi.fn()

  mockedUseStudent.mockReturnValue({
    data: overrides?.data ?? studentDetail,
    isLoading: overrides?.isLoading ?? false,
    isError: overrides?.isError ?? false,
  } as never)

  mockedUseAddCourse.mockReturnValue({
    mutate: addCourseMutate,
    reset: vi.fn(),
    isPending: false,
    isError: false,
    error: null,
  } as never)

  mockedUseUpdateCourseStatus.mockReturnValue({
    mutate: updateStatusMutate,
    reset: vi.fn(),
    isPending: false,
    isError: false,
  } as never)

  mockedUseUpdateStudent.mockReturnValue({
    mutate: vi.fn(),
    reset: vi.fn(),
    isPending: false,
    isError: false,
  } as never)

  return { addCourseMutate, updateStatusMutate }
}

describe('StudentDetailModal', () => {
  it('renders nothing when no student is selected', () => {
    setup()
    const { container } = render(<StudentDetailModal studentId={null} onClose={vi.fn()} />)
    expect(container).toBeEmptyDOMElement()
  })

  it('shows the fetched student information and course list', () => {
    setup()
    render(<StudentDetailModal studentId="1" onClose={vi.fn()} />)

    expect(screen.getByText('山田太郎')).toBeInTheDocument()
    expect(screen.getByText('taro@example.com')).toBeInTheDocument()
    expect(screen.getByText('Javaコース')).toBeInTheDocument()
    expect(screen.getByText('仮申込')).toBeInTheDocument()
  })

  it('only offers valid transition options for the current status', () => {
    setup()
    render(<StudentDetailModal studentId="1" onClose={vi.fn()} />)

    const select = screen.getByRole('combobox')
    const options = Array.from(select.querySelectorAll('option')).map((option) => option.textContent)
    expect(options).toEqual(['本申込', 'キャンセル'])
  })

  it('submits a new course name via the add-course form', () => {
    const { addCourseMutate } = setup()
    render(<StudentDetailModal studentId="1" onClose={vi.fn()} />)

    fireEvent.change(screen.getByPlaceholderText('コース名'), { target: { value: 'AWSコース' } })
    fireEvent.click(screen.getByRole('button', { name: '追加' }))

    expect(addCourseMutate).toHaveBeenCalledWith('AWSコース', expect.anything())
  })
})
