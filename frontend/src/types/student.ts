export interface Student {
  id: string
  name: string
  furigana: string
  nickname: string
  email: string
  area: string
  age: number
  gender: string
  remark: string
  // バックエンドのLombok `isDeleted` フィールドはJacksonにより `deleted` というJSONキーでシリアライズされる
  deleted: boolean
}

export interface StudentCourse {
  id?: string
  studentId?: string
  courseName: string
  courseStartAt?: string
  courseEndAt?: string
}

export type ApplicationStatus = 'TEMP' | 'FORMAL' | 'IN_PROGRESS' | 'COMPLETED' | 'CANCEL'

export const APPLICATION_STATUS_LABELS: Record<ApplicationStatus, string> = {
  TEMP: '仮申込',
  FORMAL: '本申込',
  IN_PROGRESS: '受講中',
  COMPLETED: '受講終了',
  CANCEL: 'キャンセル',
}

export const APPLICATION_STATUS_TRANSITIONS: Record<ApplicationStatus, ApplicationStatus[]> = {
  TEMP: ['FORMAL', 'CANCEL'],
  FORMAL: ['TEMP', 'IN_PROGRESS', 'CANCEL'],
  IN_PROGRESS: ['FORMAL', 'COMPLETED', 'CANCEL'],
  COMPLETED: ['IN_PROGRESS'],
  CANCEL: [],
}

// 新規登録時はサーバ側で studentId/courseId/applicationStatus が強制的に設定されるため送信時は空オブジェクトでよい
export interface CourseApplication {
  id?: string
  studentId?: string
  courseId?: string
  applicationStatus?: ApplicationStatus
}

export interface CourseDetail {
  studentCourse: StudentCourse
  courseApplication: CourseApplication
}

export interface StudentDetail {
  student: Student
  courseDetailList: CourseDetail[]
}

export interface StudentRegistrationInput {
  name: string
  furigana: string
  nickname: string
  email: string
  area: string
  age: number
  gender: string
  remark: string
}

export interface RegisterStudentPayload {
  student: StudentRegistrationInput
  courseDetailList: CourseDetail[]
}
