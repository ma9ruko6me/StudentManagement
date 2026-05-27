package raisetech.StudentManagement.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(description = "受講生詳細検索条件")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SearchCondition {

  @Valid
  private StudentSearchCondition studentSearchCondition;

  @Valid
  private CourseSearchCondition courseSearchCondition;

  @Valid
  private ApplicationSearchCondition applicationSearchCondition;
}
