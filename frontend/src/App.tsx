import { StudentTable } from './components/StudentTable'

function App() {
  return (
    <div className="min-h-screen bg-bg text-text">
      <header className="bg-accent px-5 py-3 text-white">
        <h1 className="text-lg font-bold">受講生一覧</h1>
      </header>
      <StudentTable />
    </div>
  )
}

export default App
