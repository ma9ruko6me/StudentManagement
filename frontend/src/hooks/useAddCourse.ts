import { useMutation, useQueryClient } from '@tanstack/react-query'
import { addCourse } from '../api/students'

export function useAddCourse(studentId: string | null) {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: (courseName: string) => addCourse(studentId as string, courseName),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['student', studentId] })
      queryClient.invalidateQueries({ queryKey: ['students'] })
    },
  })
}
