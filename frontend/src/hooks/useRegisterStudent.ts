import { useMutation, useQueryClient } from '@tanstack/react-query'
import { registerStudent } from '../api/students'
import type { RegisterStudentPayload } from '../types/student'

export function useRegisterStudent() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: (payload: RegisterStudentPayload) => registerStudent(payload),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['students'] })
    },
  })
}
