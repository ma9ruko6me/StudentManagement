package raisetech.StudentManagement.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import raisetech.StudentManagement.data.CourseApplication;
import raisetech.StudentManagement.data.StudentCourse;

@Schema(description = "受講コース詳細")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CourseDetail {

  @Valid
  private StudentCourse studentCourse;

  @Valid
  private CourseApplication courseApplication;

}
