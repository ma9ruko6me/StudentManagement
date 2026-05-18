package raisetech.StudentManagement.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import jakarta.annotation.Nonnull;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import raisetech.StudentManagement.data.Student;
import raisetech.StudentManagement.data.StudentCourse;

@MybatisTest
class StudentRepositoryTest {

  @Autowired
  private StudentRepository sut;

  @Test
  void 受講生の全件検索ができること() {
    List<Student> actual =sut.search();
    assertThat(actual.size()).isEqualTo(5);
  }

  @Test
  void 受講生の単一検索ができること() {
    int id = 1;
    Student actual = sut.searchStudent(id);
    assertThat(actual.getId()).isEqualTo(id);
    assertThat(actual.getName()).isEqualTo("鈴木彩艶");
    assertThat(actual.getAge()).isEqualTo(22);
  }

  @Test
  void 受講生の単一検索で存在しないIDで検索した時にNULLが返ってくること() {
    int id = 999;
    Student actual = sut.searchStudent(id);
    assertThat(actual).isNull();
  }

  @Test
  void 受講生コース情報の全件検索ができること() {
    List<StudentCourse> actual =sut.searchStudentCourseList();
    assertThat(actual.size()).isEqualTo(9);
  }

  @Test
  void 受講生IDに紐づく受講生コース情報の検索ができること() {
    int id = 3;
    List<StudentCourse> actual = sut.searchStudentCourse(id);
    assertThat(actual.size()).isEqualTo(3);
  }

  @Test
  void 存在しない受講生IDに紐づく受講生コース情報の検索で空のリストが返ってくること() {
    int id = 999;
    List<StudentCourse> actual = sut.searchStudentCourse(id);
    assertThat(actual).isEmpty();
  }

  @Test
  void 受講生の登録が行えること () {
    Student student = createStudent();

    sut.registerStudent(student);

    List<Student> actual =sut.search();
    assertThat(actual.size()).isEqualTo(6);
  }

  @Test
  void 受講生コース情報の登録ができること() {
    StudentCourse studentCourse = createStudentCourse();

    sut.registerStudentCourse(studentCourse);

    List<StudentCourse> actual =sut.searchStudentCourseList();
    assertThat(actual.size()).isEqualTo(10);
  }

  @Test
  void 受講生の更新ができること() {
    int id = 1;
    Student expected = createStudent();
    expected.setId(id);
    sut.updateStudent(expected);

    Student actual = sut.searchStudent(id);
    assertThat(actual.getAge()).isEqualTo(expected.getAge());
    assertThat(actual.getName()).isEqualTo(expected.getName());
  }

  @Test
  void 受講生コース情報の更新ができること() {
    int id = 1;
    StudentCourse expected = createStudentCourse();
    expected.setId(id);
    sut.updateStudentCourse(expected);

    List<StudentCourse> actual = sut.searchStudentCourse(id);
    assertThat(actual.get(0).getStudentId()).isEqualTo(expected.getStudentId());
    assertThat(actual.get(0).getCourse()).isEqualTo(expected.getCourse());
  }

  @Nonnull
  private static Student createStudent() {
    Student student = new Student();
    student.setName("テスト四太郎");
    student.setHurigana("てすとしたろう");
    student.setNickname("テスト大好きくん");
    student.setEmail("test@example.com");
    student.setArea("テスト県");
    student.setAge(18);
    student.setGender("男性");
    student.setRemark("");
    student.setDeleted(false);
    return student;
  }

  @Nonnull
  private static StudentCourse createStudentCourse() {
    StudentCourse studentCourse = new StudentCourse();
    studentCourse.setStudentId(1);
    studentCourse.setCourse("テストコース");
    studentCourse.setStartDate(LocalDate.parse("2026-02-16"));
    studentCourse.setEndDate(LocalDate.parse("2027-02-16"));
    return studentCourse;
  }
}