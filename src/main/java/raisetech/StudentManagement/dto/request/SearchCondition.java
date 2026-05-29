package raisetech.StudentManagement.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import raisetech.StudentManagement.enums.SearchType;
import raisetech.StudentManagement.enums.SortKey;
import raisetech.StudentManagement.enums.SortOrder;

@Schema(description = "受講生・受講生コース・受講生コース申込状況を横断した検索条件")
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

  /**
   * いずれかの検索条件がしてされているかどうかを判定する。
   *
   * @return　少なくとも1つ条件が指定されていればtrue、全て未指定ならfalse
   */
  public boolean hasAnyCondition() {
    return (studentSearchCondition != null && !studentSearchCondition.isEmpty())
        ||(courseSearchCondition != null && !courseSearchCondition.isEmpty())
        ||(applicationSearchCondition != null && !applicationSearchCondition.isEmpty());
  }

  private SortKey sortKey;

  private SortOrder sortOrder;

  private SearchType searchType;
}
