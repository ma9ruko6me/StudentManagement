import { apiClient } from './client'
import type { StudentDetail } from '../types/student'

export async function fetchStudents(): Promise<StudentDetail[]> {
  const response = await apiClient.get<StudentDetail[]>('/students')
  return response.data
}
