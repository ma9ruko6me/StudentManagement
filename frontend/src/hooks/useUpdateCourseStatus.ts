import { useMutation, useQueryClient } from '@tanstack/react-query'
import { updateCourseStatus } from '../api/students'
import type { CourseDetail } from '../types/student'

export function useUpdateCourseStatus(studentId: string | null) {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: (courseDetail: CourseDetail) => updateCourseStatus(courseDetail),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['student', studentId] })
      queryClient.invalidateQueries({ queryKey: ['students'] })
    },
  })
}
