package raisetech.StudentManagement.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import raisetech.StudentManagement.data.CourseApplication;
import raisetech.StudentManagement.data.Student;
import raisetech.StudentManagement.data.StudentCourse;
import raisetech.StudentManagement.enums.ApplicationStatus;

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
    assertThat(actual)
        .isPresent()
        .get()
        .satisfies(student -> {
          assertThat(student.getId()).isEqualTo(id);
          assertThat(student.getName()).isEqualTo("鈴木彩艶");
          assertThat(student.getAge()).isEqualTo(22);
        });
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

    assertThat(actual)
        .extracting(
            CourseApplication::getId,
            CourseApplication::getStudentId,
            CourseApplication::getCourseId,
            CourseApplication::getStatus
        )
        .contains(
            tuple("1","1","1",ApplicationStatus.IN_PROGRESS)
        );
  }

  @Test
  void 受講生IDに紐づく受講生コース申込状況の検索ができること() {
    String id = "1";
    List<CourseApplication> actual = sut.searchCourseApplicationByStudentId(id);

    assertThat(actual)
        .allSatisfy(courseApplication -> assertThat(courseApplication.getId()).isEqualTo(id));
  }

  @Test
  void 存在しない受講生IDに紐づく受講生コース申込状況の検索でnullが返ってくること() {
    String id = "999";
    List<CourseApplication> actual = sut.searchCourseApplicationByStudentId(id);
    assertThat(actual).isEmpty();
  }

  @Test
  void 受講生の登録が行えること () {
    Student student1 = new Student();
    student1.setName("テスト四太郎");
    student1.setHurigana("てすとしたろう");
    student1.setNickname("テスト大好きくん");
    student1.setEmail("test@example.com");
    student1.setArea("テスト県");
    student1.setAge(18);
    student1.setGender("男性");
    student1.setRemark("");
    student1.setDeleted(false);
    Student student = student1;

    sut.registerStudent(student);

    List<Student> actual =sut.search();
    assertThat(actual.size()).isEqualTo(6);
  }

  @Test
  void 受講生コース情報の登録ができること() {
    StudentCourse studentCourse1 = new StudentCourse();
    studentCourse1.setStudentId("1");
    studentCourse1.setCourse("テストコース");
    studentCourse1.setStartDate(LocalDate.parse("2026-02-16"));
    studentCourse1.setEndDate(LocalDate.parse("2027-02-16"));
    StudentCourse studentCourse = studentCourse1;

    sut.registerStudentCourse(studentCourse);

    List<StudentCourse> actual =sut.searchStudentCourseList();
    assertThat(actual.size()).isEqualTo(10);
  }

  @Test
  void 受講生コース申込状況の登録ができること() {
    CourseApplication courseApplication = new CourseApplication();
    courseApplication.setStudentId("10");
    courseApplication.setCourseId("10");
    courseApplication.setStatus(ApplicationStatus.TEMP);

    sut.registerCourseApplication(courseApplication);

    List<CourseApplication> actual =sut.searchCourseApplicationList();
    assertThat(actual.size()).isEqualTo(10);
    assertThat(actual)
        .extracting(
            CourseApplication::getStudentId,
            CourseApplication::getCourseId,
            CourseApplication::getStatus
        )
        .contains(tuple("10","10",ApplicationStatus.TEMP));
  }

  @Test
  void 同じ受講生コースIDを持つ受講生コース申込状況の登録をした時にエラーが返ってくること() {
    CourseApplication courseApplication = new CourseApplication();
    courseApplication.setStudentId("1");
    courseApplication.setCourseId("1");
    courseApplication.setStatus(ApplicationStatus.TEMP);

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
  void 受講生のnameの更新ができること() {
    String id = "1";
    Student before = sut.searchStudent(id).orElseThrow();

    Student expected = new Student();
    expected.setId(before.getId());
    expected.setName(before.getName());
    expected.setAge(before.getAge());

    expected.setName("テスト四太郎");

    sut.updateStudent(expected);

    Student after = sut.searchStudent(id).orElseThrow();

    assertThat(after)
        .satisfies(student -> {
          assertThat(student.getId()).isEqualTo(before.getId());

          assertThat(student.getName())
              .isEqualTo(expected.getName())
              .isNotEqualTo(before.getName());

          assertThat(student.getAge())
              .isEqualTo(before.getAge());
        });
  }

  @Test
  void 受講生の更新ができること() {
    String id = "1";
    Student before = sut.searchStudent(id).orElseThrow();

    Student expected = new Student();
    expected.setId(before.getId());
    expected.setName(before.getName());
    expected.setAge(before.getAge());

    expected.setName("テスト四太郎");

    sut.updateStudent(expected);

    Student after = sut.searchStudent(id).orElseThrow();

    assertThat(after)
        .satisfies(student -> {
          assertThat(student.getId()).isEqualTo(before.getId());

          assertThat(student.getName())
              .isEqualTo(expected.getName())
              .isNotEqualTo(before.getName());

          assertThat(student.getAge())
              .isEqualTo(before.getAge());
        });
  }

  @Test
  void 受講生コース情報の更新ができること() {
    String id = "1";

    StudentCourse before = sut.searchStudentCourse(id).get(0);

    StudentCourse expected = new StudentCourse();
    expected.setId(before.getId());
    expected.setStudentId(before.getStudentId());
    expected.setCourse("テストコース");
    expected.setStartDate(before.getStartDate());
    expected.setEndDate(before.getEndDate());

    sut.updateStudentCourse(expected);

    List<StudentCourse> actual = sut.searchStudentCourse(id);

    assertThat(actual)
        .anySatisfy(studentCourse -> {
          assertThat(studentCourse.getId())
              .isEqualTo(before.getId());

          assertThat(studentCourse.getStudentId())
              .isEqualTo(before.getStudentId());

          assertThat(studentCourse.getCourse())
              .isEqualTo(expected.getCourse())
              .isNotEqualTo(before.getCourse());
        });
  }

  @Test
  void 受講生コース申込状況の更新ができること() {
    String id = "1";
    CourseApplication before = sut.searchCourseApplicationByStudentId(id).get(0);
    CourseApplication expected = new CourseApplication();
    expected.setId(before.getId());
    expected.setStudentId(before.getStudentId());
    expected.setCourseId(before.getCourseId());
    expected.setStatus(ApplicationStatus.FORMAL);

    sut.updateCourseApplication(expected);

    List<CourseApplication> actual = sut.searchCourseApplicationByStudentId(id);

    assertThat(actual).anySatisfy(courseApplication -> {
      assertThat(courseApplication.getId()).isEqualTo(before.getId());
      assertThat(courseApplication.getStudentId()).isEqualTo(before.getStudentId());
      assertThat(courseApplication.getCourseId()).isEqualTo(before.getCourseId());

      assertThat(courseApplication.getStatus())
          .isEqualTo(expected.getStatus())
          .isNotEqualTo(before.getStatus());
    });
  }
}