package raisetech.StudentManagement.data;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Schema(description = "受講コースの申し込み状況")
@Getter
@Setter
public class CourseApplication {

  @NotBlank
  @Pattern(regexp = "^[0-9]+$")
  private String id;

  @NotBlank
  @Pattern(regexp = "^[0-9]+$")
  private String studentId;

  @NotBlank
  @Pattern(regexp = "^[0-9]+$")
  private String courseId;

  @NotBlank
  private String status;

}
