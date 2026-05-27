package raisetech.StudentManagement.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

@Schema(description = "受講生検索条件")
@Getter
@Setter
public class StudentSearchCondition {

  private String name;

  @Min(0)
  private Integer ageFrom;

  @Min(0)
  private Integer ageTo;

  private  String area;
}
