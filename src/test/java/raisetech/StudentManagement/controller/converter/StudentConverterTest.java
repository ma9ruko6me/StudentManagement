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
import raisetech.StudentManagement.dto.result.SearchResult;

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
  void 全項目が正しく変換されること(){
    SearchResult searchResult = new SearchResult();
    searchResult.setStudentId("1");
    searchResult.setName("テスト四太郎");
    searchResult.setFurigana("てすとしたろう");
    searchResult.setNickname("テスト大好きマン");
    searchResult.setEmail("test@test.com");
    searchResult.setArea("テスト県");
    searchResult.setAge(33);
    searchResult.setGender("男性");
    searchResult.setRemark("テストが大好きです。");
    searchResult.setDeleted(false);

    Student actual = sut.convertStudent(searchResult);

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