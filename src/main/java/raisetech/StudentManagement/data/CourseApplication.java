package raisetech.StudentManagement.data;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Schema(description = "受講生コース申込状況")
@Getter
@Setter
public class CourseApplication {

  @Pattern(regexp = "^[0-9]+$")
  private String id;

  @Pattern(regexp = "^[0-9]+$")
  private String studentId;

  @Pattern(regexp = "^[0-9]+$")
  private String courseId;

  @NotBlank
  private String status;

}
