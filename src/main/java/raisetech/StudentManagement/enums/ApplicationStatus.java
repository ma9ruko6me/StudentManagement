package raisetech.StudentManagement.enums;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Getter;

@Schema(description = "申込状況")
public enum ApplicationStatus {
  TEMP ("仮申込"),
  FORMAL("本申込"),
  IN_PROGRESS("受講中"),
  COMPLETED("受講終了"),
  CANCEL("キャンセル");

  @Getter
  private final String label;
  private List<ApplicationStatus> nextStatuses;

  ApplicationStatus(String label) {
    this.label = label;
  }

  static {
    TEMP.nextStatuses = List.of(FORMAL,CANCEL);
    FORMAL.nextStatuses = List.of(TEMP,IN_PROGRESS,CANCEL);
    IN_PROGRESS.nextStatuses = List.of(FORMAL,COMPLETED,CANCEL);
    COMPLETED.nextStatuses = List.of(IN_PROGRESS);
    CANCEL.nextStatuses = List.of();
  }

  public boolean canTransitionTo(ApplicationStatus next) {
    return nextStatuses.contains(next);
  }
}
