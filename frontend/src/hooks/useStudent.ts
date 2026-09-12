import { useQuery } from '@tanstack/react-query'
import { fetchStudent } from '../api/students'

export function useStudent(id: string | null) {
  return useQuery({
    queryKey: ['student', id],
    queryFn: () => fetchStudent(id as string),
    enabled: id !== null,
  })
}
