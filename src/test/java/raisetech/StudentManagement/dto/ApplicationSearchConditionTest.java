package raisetech.StudentManagement.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import raisetech.StudentManagement.dto.request.ApplicationSearchCondition;
import raisetech.StudentManagement.enums.ApplicationStatus;

class ApplicationSearchConditionTest {

  @Test
  void 検索条件が何も入力されていない場合trueを返すこと(){
    ApplicationSearchCondition condition = new ApplicationSearchCondition();

    assertThat(condition.isEmpty()).isTrue();
  }

  @Test
  void 検索条件が1つでも入力されている場合falseを返すこと(){
    ApplicationSearchCondition condition = new ApplicationSearchCondition();
    condition.setApplicationStatus(ApplicationStatus.FORMAL);

    assertThat(condition.isEmpty()).isFalse();
  }
}