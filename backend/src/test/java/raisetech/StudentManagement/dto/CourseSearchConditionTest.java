package raisetech.StudentManagement.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import raisetech.StudentManagement.dto.request.CourseSearchCondition;

class CourseSearchConditionTest {

  @Test
  void 検索条件が何も入力されていない場合trueを返すこと(){
    CourseSearchCondition condition = new CourseSearchCondition();

    assertThat(condition.isEmpty()).isTrue();
  }

  @Test
  void 検索条件が1つでも入力されている場合falseを返すこと(){
    CourseSearchCondition condition = new CourseSearchCondition();
    condition.setCourseName("Javaコース");

    assertThat(condition.isEmpty()).isFalse();
  }
}