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
  courseName: string
}

// 新規登録時はサーバ側で studentId/courseId/applicationStatus が強制的に設定されるため空オブジェクトで送る
export type CourseApplication = Record<string, never>

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
