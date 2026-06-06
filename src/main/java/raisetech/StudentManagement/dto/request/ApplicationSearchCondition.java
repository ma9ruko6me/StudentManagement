package raisetech.StudentManagement.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import raisetech.StudentManagement.enums.ApplicationStatus;

@Schema(description = "受講生コース申込状況の検索条件")
@Getter
@Setter
public class ApplicationSearchCondition {

  private ApplicationStatus applicationStatus;

  /**
   * 全ての検索条件が未指定かどうかを判定する。
   *
   * @return　全てnullまたは空文字の場合はtrue、それ以外はfalse
   */
  public boolean isEmpty() {
    return applicationStatus == null;
  }
}
