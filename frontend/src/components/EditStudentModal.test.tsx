import { fireEvent, render, screen } from '@testing-library/react'
import { describe, expect, it, vi } from 'vitest'
import { EditStudentModal } from './EditStudentModal'
import { useUpdateStudent } from '../hooks/useUpdateStudent'
import type { Student } from '../types/student'

vi.mock('../hooks/useUpdateStudent')

const mockedUseUpdateStudent = vi.mocked(useUpdateStudent)

const student: Student = {
  id: '1',
  name: '山田太郎',
  furigana: 'ヤマダタロウ',
  nickname: 'たろ',
  email: 'taro@example.com',
  area: '東京都',
  age: 20,
  gender: '男性',
  remark: '既存の備考',
  deleted: false,
}

function setup() {
  const mutate = vi.fn()
  const reset = vi.fn()
  mockedUseUpdateStudent.mockReturnValue({
    mutate,
    reset,
    isPending: false,
    isError: false,
  } as never)
  return { mutate, reset }
}

describe('EditStudentModal', () => {
  it('renders nothing when closed', () => {
    setup()
    const { container } = render(<EditStudentModal isOpen={false} student={student} onClose={vi.fn()} />)
    expect(container).toBeEmptyDOMElement()
  })

  it('prefills the form with the given student', () => {
    setup()
    render(<EditStudentModal isOpen student={student} onClose={vi.fn()} />)

    expect(screen.getByLabelText(/^名前/)).toHaveValue('山田太郎')
    expect(screen.getByLabelText(/^メール/)).toHaveValue('taro@example.com')
    expect(screen.getByLabelText(/^備考/)).toHaveValue('既存の備考')
  })

  it('shows a validation error when a required field is cleared', () => {
    setup()
    render(<EditStudentModal isOpen student={student} onClose={vi.fn()} />)

    fireEvent.change(screen.getByLabelText(/^名前/), { target: { value: '' } })
    fireEvent.click(screen.getByRole('button', { name: '更新する' }))

    expect(screen.getByText('必須項目が未入力です。')).toBeInTheDocument()
  })

  it('calls the update mutation with the edited student when valid', () => {
    const { mutate } = setup()
    render(<EditStudentModal isOpen student={student} onClose={vi.fn()} />)

    fireEvent.change(screen.getByLabelText(/^名前/), { target: { value: '山田次郎' } })
    fireEvent.click(screen.getByRole('button', { name: '更新する' }))

    expect(mutate).toHaveBeenCalledWith(
      expect.objectContaining({ id: '1', name: '山田次郎' }),
      expect.anything(),
    )
  })
})
