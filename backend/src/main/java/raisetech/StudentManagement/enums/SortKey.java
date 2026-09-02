package raisetech.StudentManagement.enums;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Schema(description = "ソートキー（どの項目で並び替えるか指定）")
public enum SortKey {

  NAME("s.name"),
  AGE("s.age"),
  AREA("s.area"),
  GENDER("s.gender"),
  IS_DELETED("s.is_deleted"),
  COURSE_NAME("sc.course_name"),
  APPLICATION_STATUS("ca.application_status");

  @Getter
  private final String column;

  SortKey(String column) {
    this.column = column;
  }
}
