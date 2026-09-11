import { apiClient } from './client'
import type { RegisterStudentPayload, StudentDetail } from '../types/student'

export async function fetchStudents(): Promise<StudentDetail[]> {
  const response = await apiClient.get<StudentDetail[]>('/students')
  return response.data
}

export async function registerStudent(payload: RegisterStudentPayload): Promise<StudentDetail> {
  const response = await apiClient.post<StudentDetail>('/students/register', payload)
  return response.data
}
