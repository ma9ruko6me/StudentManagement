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

}