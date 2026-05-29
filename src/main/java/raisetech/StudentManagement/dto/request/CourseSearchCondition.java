package raisetech.StudentManagement.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Schema(description = "受講生コースの検索条件")
@Getter
@Setter
public class CourseSearchCondition {

  private String courseName;

  /**
   * 全ての検索条件が未指定かどうかを判定する。
   *
   * @return　全てnullまたは空文字の場合はtrue、それ以外はfalse
   */
  public boolean isEmpty() {
    return courseName == null;
  }
}
