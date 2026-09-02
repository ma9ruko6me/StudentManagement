package raisetech.StudentManagement.enums;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Schema(description = "ソート順（昇順・降順）")
public enum SortOrder {

  ASC("ASC"),
  DESC("DESC");

  @Getter
  private final String value;

  SortOrder(String value) {
    this.value = value;
  }
}
