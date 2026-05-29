package raisetech.StudentManagement.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "ソート順（昇順・降順）")
public enum SortOrder {

  ASC("ASC"),
  DESC("DESC");

  private final String value;

  SortOrder(String value) {
    this.value = value;
  }
}
