package raisetech.StudentManagement.controller.converter;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StudentCourse {
  private int id;
  private int studentId;
  private String course;
  private LocalDateTime startDate;
  private LocalDateTime endDate;
}
