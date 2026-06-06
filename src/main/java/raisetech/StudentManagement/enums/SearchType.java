package raisetech.StudentManagement.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "検索タイプ（複数条件の組み合わせ方法）")
public enum SearchType {

  AND,
  OR

}
