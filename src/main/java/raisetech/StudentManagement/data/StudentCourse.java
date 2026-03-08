package raisetech.StudentManagement.data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StudentCourse {

  private int id;

  private int studentId;

  @NotBlank
  private String course;

  @NotNull
  private LocalDate startDate;

  @NotNull
  private LocalDate endDate;
}
