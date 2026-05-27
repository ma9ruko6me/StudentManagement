package raisetech.StudentManagement.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Schema(description = "受講生コース検索条件")
@Getter
@Setter
public class CourseSearchCondition {

  private String course;
}
