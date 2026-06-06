package raisetech.StudentManagement.controller.converter;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import raisetech.StudentManagement.data.Student;
import raisetech.StudentManagement.data.StudentCourse;
import raisetech.StudentManagement.domain.CourseDetail;
import raisetech.StudentManagement.domain.StudentDetail;
import raisetech.StudentManagement.dto.result.StudentCourseApplicationRow;

class StudentConverterTest {

  @Mock

  private StudentConverter sut;
  @BeforeEach
  void before () { sut = new StudentConverter();}

  @Test
  void 受講生のリストとそれに紐づく受講生コース詳細のリストを渡して受講生詳細のリストが作成できること() {
    Student student = new Student();
    student.setId("1");
    student.setName("テスト四太郎");

    StudentCourse studentCourse = new StudentCourse();
    studentCourse.setId("1");
    studentCourse.setStudentId("1");
    studentCourse.setCourseName("テストコース");

    CourseDetail courseDetail = new CourseDetail();
    courseDetail.setStudentCourse(studentCourse);

    List<Student> studentList = List.of(student);
    List<CourseDetail> CourseDetailList = List.of(courseDetail);

    List<StudentDetail> actual = sut.convertStudentDetails(studentList, CourseDetailList);

    assertThat(actual.get(0).getStudent()).isEqualTo(student);
    assertThat(actual.get(0).getCourseDetailList()).isEqualTo(CourseDetailList);
  }

  @Test
  void 受講生のリストに紐づかない受講生コース詳細は結果に含まれないこと() {
    Student student = new Student();
    student.setId("1");
    student.setName("テスト四太郎");

    StudentCourse studentCourse1 = new StudentCourse();
    studentCourse1.setId("1");
    studentCourse1.setStudentId("2");
    studentCourse1.setCourseName("テストコース");

    CourseDetail courseDetail = new CourseDetail();
    courseDetail.setStudentCourse(studentCourse1);

    List<Student> studentList = List.of(student);
    List<CourseDetail> CourseDetailList = List.of(courseDetail);

    List<StudentDetail> actual = sut.convertStudentDetails(studentList, CourseDetailList);

    assertThat(actual.get(0).getStudent()).isEqualTo(student);
    assertThat(actual.get(0).getCourseDetailList()).isEmpty();
  }

  @Test
  void フラットな検索結果から受講生に正しく変換されること(){
    StudentCourseApplicationRow studentCourseApplicationRow = new StudentCourseApplicationRow();
    studentCourseApplicationRow.setStudentId("1");
    studentCourseApplicationRow.setName("テスト四太郎");
    studentCourseApplicationRow.setFurigana("てすとしたろう");
    studentCourseApplicationRow.setNickname("テスト大好きマン");
    studentCourseApplicationRow.setEmail("test@test.com");
    studentCourseApplicationRow.setArea("テスト県");
    studentCourseApplicationRow.setAge(33);
    studentCourseApplicationRow.setGender("男性");
    studentCourseApplicationRow.setRemark("テストが大好きです。");
    studentCourseApplicationRow.setDeleted(false);

    Student actual = sut.convertStudent(studentCourseApplicationRow);

    assertThat(actual.getId()).isEqualTo("1");
    assertThat(actual.getName()).isEqualTo("テスト四太郎");
    assertThat(actual.getFurigana()).isEqualTo("てすとしたろう");
    assertThat(actual.getNickname()).isEqualTo("テスト大好きマン");
    assertThat(actual.getEmail()).isEqualTo("test@test.com");
    assertThat(actual.getArea()).isEqualTo("テスト県");
    assertThat(actual.getAge()).isEqualTo(33);
    assertThat(actual.getGender()).isEqualTo("男性");
    assertThat(actual.getRemark()).isEqualTo("テストが大好きです。");
    assertThat(actual.isDeleted()).isEqualTo(false);
  }
}