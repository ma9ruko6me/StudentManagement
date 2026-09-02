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

export interface StudentDetail {
  student: Student
  courseDetailList: unknown[]
}
