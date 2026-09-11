import { useState } from 'react'
import { StudentTable } from './components/StudentTable'
import { RegisterStudentModal } from './components/RegisterStudentModal'

function App() {
  const [isRegisterModalOpen, setIsRegisterModalOpen] = useState(false)

  return (
    <div className="min-h-screen bg-bg text-text">
      <header className="bg-accent px-5 py-3 text-white">
        <h1 className="text-lg font-bold">受講生一覧</h1>
      </header>
      <div className="px-5 pt-5">
        <button
          type="button"
          onClick={() => setIsRegisterModalOpen(true)}
          className="rounded bg-accent px-4 py-2 text-sm font-bold text-white"
        >
          ＋ 新規登録
        </button>
      </div>
      <StudentTable />
      <RegisterStudentModal isOpen={isRegisterModalOpen} onClose={() => setIsRegisterModalOpen(false)} />
    </div>
  )
}

export default App
