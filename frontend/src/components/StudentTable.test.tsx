import { render, screen } from '@testing-library/react'
import { describe, expect, it, vi } from 'vitest'
import { StudentTable } from './StudentTable'
import { useStudents } from '../hooks/useStudents'

vi.mock('../hooks/useStudents')

const mockedUseStudents = vi.mocked(useStudents)

describe('StudentTable', () => {
  it('shows a loading message while fetching', () => {
    mockedUseStudents.mockReturnValue({ isLoading: true, isError: false, data: undefined } as never)

    render(<StudentTable />)

    expect(screen.getByText('読み込み中...')).toBeInTheDocument()
  })

  it('shows an error message when the request fails', () => {
    mockedUseStudents.mockReturnValue({ isLoading: false, isError: true, data: undefined } as never)

    render(<StudentTable />)

    expect(screen.getByText('受講生の取得に失敗しました。')).toBeInTheDocument()
  })

  it('renders students and hides logically deleted ones', () => {
    mockedUseStudents.mockReturnValue({
      isLoading: false,
      isError: false,
      data: [
        {
          student: {
            id: '1',
            name: '山田太郎',
            furigana: 'ヤマダタロウ',
            nickname: 'たろ',
            age: 20,
            email: 'yamada@example.com',
            area: '東京都',
            gender: '男性',
            remark: '',
            deleted: false,
          },
          courseDetailList: [],
        },
        {
          student: {
            id: '2',
            name: '削除済み花子',
            furigana: 'サクジョズミハナコ',
            nickname: '',
            age: 30,
            email: 'deleted@example.com',
            area: '大阪府',
            gender: '女性',
            remark: '',
            deleted: true,
          },
          courseDetailList: [],
        },
      ],
    } as never)

    render(<StudentTable />)

    expect(screen.getByText('山田太郎')).toBeInTheDocument()
    expect(screen.queryByText('削除済み花子')).not.toBeInTheDocument()
  })
})
