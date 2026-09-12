import { useMutation, useQueryClient } from '@tanstack/react-query'
import { updateStudent } from '../api/students'
import type { Student } from '../types/student'

export function useUpdateStudent() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: (student: Student) => updateStudent(student),
    onSuccess: (_data, student) => {
      queryClient.invalidateQueries({ queryKey: ['student', student.id] })
      queryClient.invalidateQueries({ queryKey: ['students'] })
    },
  })
}
