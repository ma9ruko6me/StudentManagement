package raisetech.StudentManagement.enums;

import lombok.Getter;

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
