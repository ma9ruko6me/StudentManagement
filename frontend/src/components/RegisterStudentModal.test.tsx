import { fireEvent, render, screen } from '@testing-library/react'
import { describe, expect, it, vi } from 'vitest'
import { RegisterStudentModal } from './RegisterStudentModal'
import { useRegisterStudent } from '../hooks/useRegisterStudent'

vi.mock('../hooks/useRegisterStudent')

const mockedUseRegisterStudent = vi.mocked(useRegisterStudent)

function setup() {
  const mutate = vi.fn()
  const reset = vi.fn()
  mockedUseRegisterStudent.mockReturnValue({
    mutate,
    reset,
    isPending: false,
    isError: false,
  } as never)
  return { mutate, reset }
}

function fillRequiredFields() {
  fireEvent.change(screen.getByLabelText(/^名前/), { target: { value: '山田太郎' } })
  fireEvent.change(screen.getByLabelText(/^ふりがな/), { target: { value: 'ヤマダタロウ' } })
  fireEvent.change(screen.getByLabelText(/^ニックネーム/), { target: { value: 'たろ' } })
  fireEvent.change(screen.getByLabelText(/^メール/), { target: { value: 'taro@example.com' } })
  fireEvent.change(screen.getByLabelText(/^地域/), { target: { value: '東京都' } })
  fireEvent.change(screen.getByLabelText(/^年齢/), { target: { value: '20' } })
  fireEvent.change(screen.getByLabelText(/^性別/), { target: { value: '男性' } })
}

describe('RegisterStudentModal', () => {
  it('renders nothing when closed', () => {
    setup()
    const { container } = render(<RegisterStudentModal isOpen={false} onClose={vi.fn()} />)
    expect(container).toBeEmptyDOMElement()
  })

  it('shows a validation error when required fields are missing', () => {
    setup()
    render(<RegisterStudentModal isOpen onClose={vi.fn()} />)

    fireEvent.click(screen.getByRole('button', { name: '登録する' }))

    expect(screen.getByText('必須項目が未入力です。')).toBeInTheDocument()
  })

  it('shows a validation error for an invalid email', () => {
    setup()
    render(<RegisterStudentModal isOpen onClose={vi.fn()} />)

    fillRequiredFields()
    fireEvent.change(screen.getByLabelText(/^メール/), { target: { value: 'invalid-email' } })
    fireEvent.click(screen.getByRole('button', { name: '登録する' }))

    expect(screen.getByText('メールアドレスの形式が正しくありません。')).toBeInTheDocument()
  })

  it('calls the registration mutation with course rows filtered when valid', () => {
    const { mutate } = setup()
    render(<RegisterStudentModal isOpen onClose={vi.fn()} />)

    fillRequiredFields()
    fireEvent.change(screen.getByPlaceholderText('コース名'), { target: { value: 'Javaコース' } })
    fireEvent.click(screen.getByRole('button', { name: '登録する' }))

    expect(mutate).toHaveBeenCalledWith(
      expect.objectContaining({
        student: expect.objectContaining({ name: '山田太郎', age: 20 }),
        courseDetailList: [{ studentCourse: { courseName: 'Javaコース' }, courseApplication: {} }],
      }),
      expect.anything(),
    )
  })
})
