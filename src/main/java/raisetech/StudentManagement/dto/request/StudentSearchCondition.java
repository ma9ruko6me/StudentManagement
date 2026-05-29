package raisetech.StudentManagement.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

@Schema(description = "受講生の検索条件")
@Getter
@Setter
public class StudentSearchCondition {

  private String keyword;

  @Min(0)
  private Integer ageFrom;

  @Min(0)
  private Integer ageTo;

  private  String area;

  private String gender;

  /**
   * 全ての検索条件が未指定かどうかを判定する。
   *
   * @return　全てnullまたは空文字の場合はtrue、それ以外はfalse
   */
  public boolean isEmpty() {
    return keyword == null && ageFrom == null && ageTo == null && area == null  && gender == null;
  }
}
