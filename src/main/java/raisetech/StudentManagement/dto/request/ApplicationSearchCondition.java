package raisetech.StudentManagement.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import raisetech.StudentManagement.enums.ApplicationStatus;

@Schema(description = "受講生コース申込状況検索条件")
@Getter
@Setter
public class ApplicationSearchCondition {

  private ApplicationStatus applicationStatus;

  public boolean isEmpty() {
    return applicationStatus == null;
  }
}
