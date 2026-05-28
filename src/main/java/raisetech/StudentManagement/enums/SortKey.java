package raisetech.StudentManagement.enums;

import lombok.Getter;

public enum SortKey {

  NAME("name"),
  AGE("age"),
  AREA("area"),
  GENDER("gender");

  @Getter
  private final String column;

  SortKey(String column) {
    this.column = column;
  }
}
