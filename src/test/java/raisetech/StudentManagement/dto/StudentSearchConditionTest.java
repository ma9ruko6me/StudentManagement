package raisetech.StudentManagement.dto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class StudentSearchConditionTest {

  @Test
  void 検索条件が何も入力されていない場合trueを返すこと(){
    StudentSearchCondition condition = new StudentSearchCondition();

    assertThat(condition.isEmpty()).isTrue();
  }

  @Test
  void 検索条件が1つでも入力されている場合falseを返すこと(){
    StudentSearchCondition condition = new StudentSearchCondition();
    condition.setKeyword("テスト");

    assertThat(condition.isEmpty()).isFalse();
  }
}