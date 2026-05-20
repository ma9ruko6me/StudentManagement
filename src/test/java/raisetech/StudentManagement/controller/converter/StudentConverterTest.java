package raisetech.StudentManagement.controller.converter;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import raisetech.StudentManagement.data.Student;
import raisetech.StudentManagement.data.StudentCourse;
import raisetech.StudentManagement.domain.StudentDetail;

class StudentConverterTest {

  @Mock

  private StudentConverter sut;
  @BeforeEach
  void before () { sut = new StudentConverter();}

  @Test
  void 受講生のリストとそれに紐づく受講生コース情報のリストを渡して受講生詳細のリストが作成できること() {
    Student student = new Student();
    student.setId("1");
    student.setName("テスト四太郎");

    StudentCourse studentCourse = new StudentCourse();
    studentCourse.setId("1");
    studentCourse.setStudentId("1");
    studentCourse.setCourse("テストコース");

    List<Student> studentList = List.of(student);
    List<StudentCourse>  studentCourseList = List.of(studentCourse);

    List<StudentDetail> actual = sut.convertStudentDetails(studentList, studentCourseList);

    assertThat(actual.get(0).getStudent()).isEqualTo(student);
    assertThat(actual.get(0).getStudentCourseList()).isEqualTo(studentCourseList);
  }

  @Test
  void 受講生のリストとそれに紐づかない受講生コース情報のリストを渡して紐づかない受講生コース情報が除外された受講生詳細のリストができること() {
    Student student = new Student();
    student.setId("1");
    student.setName("テスト四太郎");

    StudentCourse studentCourse1 = new StudentCourse();
    studentCourse1.setId("1");
    studentCourse1.setStudentId("2");
    studentCourse1.setCourse("テストコース");

    List<Student> studentList = List.of(student);
    List<StudentCourse>  studentCourseList = List.of(studentCourse1);

    List<StudentDetail> actual = sut.convertStudentDetails(studentList, studentCourseList);

    assertThat(actual.get(0).getStudent()).isEqualTo(student);
    assertThat(actual.get(0).getStudentCourseList()).isEmpty();
  }

}