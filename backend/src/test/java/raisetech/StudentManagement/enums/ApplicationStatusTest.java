package raisetech.StudentManagement.enums;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class ApplicationStatusTest {

  @Test
  void 正しいステータス遷移の場合trueを返すこと(){
    ApplicationStatus status = ApplicationStatus.FORMAL;

    assertThat(status.canTransitionTo(ApplicationStatus.IN_PROGRESS)).isTrue();
  }

  @Test
  void 不正なステータス遷移の場合falseを返すこと(){
    ApplicationStatus status = ApplicationStatus.FORMAL;

    assertThat(status.canTransitionTo(ApplicationStatus.COMPLETED)).isFalse();
  }
}