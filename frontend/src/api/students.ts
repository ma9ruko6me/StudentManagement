import { apiClient } from './client'
import type { CourseDetail, RegisterStudentPayload, Student, StudentDetail } from '../types/student'

export async function fetchStudents(): Promise<StudentDetail[]> {
  const response = await apiClient.get<StudentDetail[]>('/students')
  return response.data
}

export async function fetchStudent(id: string): Promise<StudentDetail> {
  const response = await apiClient.get<StudentDetail>(`/students/${id}`)
  return response.data
}

export async function registerStudent(payload: RegisterStudentPayload): Promise<StudentDetail> {
  const response = await apiClient.post<StudentDetail>('/students/register', payload)
  return response.data
}

export async function updateStudent(student: Student): Promise<void> {
  await apiClient.put('/students/update', student)
}

export async function addCourse(studentId: string, courseName: string): Promise<StudentDetail> {
  const payload: CourseDetail = {
    studentCourse: { courseName },
    courseApplication: {},
  }
  const response = await apiClient.post<StudentDetail>(`/students/${studentId}/courses/add`, payload)
  return response.data
}

export async function updateCourseStatus(courseDetail: CourseDetail): Promise<void> {
  await apiClient.put('/courses/update', courseDetail)
}
