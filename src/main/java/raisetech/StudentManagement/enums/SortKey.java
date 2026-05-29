package raisetech.StudentManagement.enums;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Schema(description = "ソートキー（どの項目で並び替えるか指定）")
public enum SortKey {

  NAME("name"),
  AGE("age"),
  AREA("area"),
  GENDER("gender"),
  IS_DELETED("isDeleted"),
  COURSE_NAME("courseName"),
  APPLICATION_STATUS("applicationStatus");

  @Getter
  private final String column;

  SortKey(String column) {
    this.column = column;
  }
}
