package raisetech.StudentManagement.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import jakarta.annotation.Nonnull;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import raisetech.StudentManagement.data.CourseApplication;
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
    String id = "1";
    Optional<Student> actual = sut.searchStudent(id);
    assertThat(actual).isPresent();
    assertThat(actual.get().getId()).isEqualTo(id);
    assertThat(actual.get().getName()).isEqualTo("鈴木彩艶");
    assertThat(actual.get().getAge()).isEqualTo(22);
  }

  @Test
  void 受講生の単一検索で存在しないIDの場合はOptionalが空で返ってくること() {
    String id = "999";
    Optional<Student> actual = sut.searchStudent(id);
    assertThat(actual).isEmpty();
  }

  @Test
  void 受講生コース情報の全件検索ができること() {
    List<StudentCourse> actual =sut.searchStudentCourseList();
    assertThat(actual.size()).isEqualTo(9);
  }

  @Test
  void 受講生IDに紐づく受講生コース情報の検索ができること() {
    String id = "3";
    List<StudentCourse> actual = sut.searchStudentCourse(id);
    assertThat(actual.size()).isEqualTo(3);
  }

  @Test
  void 存在しない受講生IDに紐づく受講生コース情報の検索で空のリストが返ってくること() {
    String id = "999";
    List<StudentCourse> actual = sut.searchStudentCourse(id);
    assertThat(actual).isEmpty();
  }

  @Test
  void 受講生コース申込状況の全件検索ができること() {
    List<CourseApplication> actual =sut.searchCourseApplicationList();
    assertThat(actual.size()).isEqualTo(9);
  }

  @Test
  void 受講生コース申込状況の全件検索で中身が正しく取得できること() {
    List<CourseApplication> actual =sut.searchCourseApplicationList();

    assertThat(actual.get(0).getId()).isEqualTo("1");
    assertThat(actual.get(0).getStudentId()).isEqualTo("1");
    assertThat(actual.get(0).getCourseId()).isEqualTo("1");
    assertThat(actual.get(0).getStatus()).isEqualTo("受講中");
  }

  @Test
  void 受講生IDに紐づく受講生コース申込状況の検索ができること() {
    String id = "1";
    List<CourseApplication> actual = sut.searchCourseApplicationByStudentId(id);
    assertThat(actual.get(0).getId()).isEqualTo(id);
    assertThat(actual.get(0).getStudentId()).isEqualTo("1");
    assertThat(actual.get(0).getCourseId()).isEqualTo("1");
    assertThat(actual.get(0).getStatus()).isEqualTo("受講中");
  }

  @Test
  void 存在しない受講生IDに紐づく受講生コース申込状況の検索でnullが返ってくること() {
    String id = "999";
    List<CourseApplication> actual = sut.searchCourseApplicationByStudentId(id);
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
  void 受講生コース申込状況の登録ができること() {
    CourseApplication courseApplication = new CourseApplication();
    courseApplication.setStudentId("10");
    courseApplication.setCourseId("10");
    courseApplication.setStatus("仮申込");

    sut.registerCourseApplication(courseApplication);

    List<CourseApplication> actual =sut.searchCourseApplicationList();
    assertThat(actual.size()).isEqualTo(10);
    assertThat(actual.get(9).getStudentId()).isEqualTo(courseApplication.getStudentId());
    assertThat(actual.get(9).getCourseId()).isEqualTo(courseApplication.getCourseId());
    assertThat(actual.get(9).getStatus()).isEqualTo(courseApplication.getStatus());
  }

  @Test
  void 同じ受講生コースIDを持つ受講生コース申込状況の登録をした時にエラーが返ってくること() {
    CourseApplication courseApplication = new CourseApplication();
    courseApplication.setStudentId("1");
    courseApplication.setCourseId("1");
    courseApplication.setStatus("仮申込");

    assertThatThrownBy(() -> sut.registerCourseApplication(courseApplication)).isInstanceOf(Exception.class);
  }

  @Test
  void 受講生IDのない受講生コース申込状況の登録をしたときにエラーが返ってくること() {
    CourseApplication courseApplication = new CourseApplication();
    courseApplication.setCourseId("20");

    assertThatThrownBy(() -> sut.registerCourseApplication(courseApplication))
        .isInstanceOf(Exception.class);
  }

  @Test
  void 受講生コースIDのない受講生コース申込状況の登録をしたときにエラーが返ってくること() {
    CourseApplication courseApplication = new CourseApplication();
    courseApplication.setStudentId("20");

    assertThatThrownBy(() -> sut.registerCourseApplication(courseApplication))
        .isInstanceOf(Exception.class);
  }

  @Test
  void 受講生の更新ができること() {
    String id = "1";
    Student expected = createStudent();
    expected.setId(id);
    sut.updateStudent(expected);

    Optional<Student> actual = sut.searchStudent(id);
    assertThat(actual.get().getAge()).isEqualTo(expected.getAge());
    assertThat(actual.get().getName()).isEqualTo(expected.getName());
  }

  @Test
  void 受講生コース情報の更新ができること() {
    String id = "1";
    StudentCourse expected = createStudentCourse();
    expected.setId(id);
    sut.updateStudentCourse(expected);

    List<StudentCourse> actual = sut.searchStudentCourse(id);
    assertThat(actual.get(0).getStudentId()).isEqualTo(expected.getStudentId());
    assertThat(actual.get(0).getCourse()).isEqualTo(expected.getCourse());
  }

  @Test
  void 受講生コース申込状況の更新ができること() {
    String id = "1";
    CourseApplication expected = new CourseApplication();
    expected.setId(id);
    expected.setStatus("本申込");

    sut.updateCourseApplication(expected);

    List<CourseApplication> actual = sut.searchCourseApplicationByStudentId(id);
    assertThat(actual.get(0).getStatus()).isEqualTo(expected.getStatus());
  }

  @Test
  void 受講生コース申込状況のstutus以外は更新がされないこと() {
    String id = "1";

    List<CourseApplication> expected = sut.searchCourseApplicationByStudentId(id);

    CourseApplication courseApplication = new CourseApplication();
    courseApplication.setId(id);
    courseApplication.setStatus("本申込");

    sut.updateCourseApplication(courseApplication);

    List<CourseApplication> actual = sut.searchCourseApplicationByStudentId(id);

    assertThat(actual.get(0).getStudentId()).isEqualTo(expected.get(0).getStudentId());
    assertThat(actual.get(0).getCourseId()).isEqualTo(expected.get(0).getCourseId());
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
    studentCourse.setStudentId("1");
    studentCourse.setCourse("テストコース");
    studentCourse.setStartDate(LocalDate.parse("2026-02-16"));
    studentCourse.setEndDate(LocalDate.parse("2027-02-16"));
    return studentCourse;
  }
}