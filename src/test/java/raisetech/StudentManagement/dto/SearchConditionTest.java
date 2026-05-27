package raisetech.StudentManagement.dto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class SearchConditionTest {

  @Test
  void 検索条件が1つでも入力されている場合trueを返すこと(){
    SearchCondition condition = new SearchCondition();
    StudentSearchCondition studentSearchCondition = new StudentSearchCondition();
    studentSearchCondition.setKeyword("テスト");
    condition.setStudentSearchCondition(studentSearchCondition);

    assertThat(condition.hasAnyCondition()).isTrue();
  }

  @Test
  void 検索条件が何も入力されていない場合falseを返すこと(){
    SearchCondition condition = new SearchCondition();

    assertThat(condition.hasAnyCondition()).isFalse();
  }

}