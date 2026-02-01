package raisetech.StudentManagement;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StudentCourse {
  private String id;
  private String studentId;
  private String course;
  private String startDate;
  private String endDate;
}
