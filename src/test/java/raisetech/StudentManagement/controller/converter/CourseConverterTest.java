package raisetech.StudentManagement.controller.converter;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import raisetech.StudentManagement.data.CourseApplication;
import raisetech.StudentManagement.data.StudentCourse;
import raisetech.StudentManagement.domain.CourseDetail;
import raisetech.StudentManagement.dto.result.StudentCourseApplicationRow;
import raisetech.StudentManagement.enums.ApplicationStatus;

class CourseConverterTest {

  @Mock

  private CourseConverter sut;
  @BeforeEach
  void before () { sut = new CourseConverter();}

  @Test
  void 受講生コース情報のリストとそれに紐づく受講生コース申込状況のリストを渡して受講生コース詳細のリストが作成できること () {
    StudentCourse studentCourse1 = new StudentCourse();
    studentCourse1.setId("1");
    studentCourse1.setStudentId("1");
    studentCourse1.setCourseName("テストコース");
    CourseApplication courseApplication1 = new CourseApplication();
    courseApplication1.setId("1");
    courseApplication1.setCourseId("1");
    courseApplication1.setStudentId("1");
    courseApplication1.setApplicationStatus(ApplicationStatus.FORMAL);

    StudentCourse studentCourse2 = new StudentCourse();
    studentCourse2.setId("2");
    studentCourse2.setStudentId("2");
    studentCourse2.setCourseName("test");
    CourseApplication courseApplication2 = new CourseApplication();
    courseApplication2.setId("2");
    courseApplication2.setCourseId("2");
    courseApplication2.setStudentId("2");
    courseApplication2.setApplicationStatus(ApplicationStatus.IN_PROGRESS);

    List<StudentCourse> studentCourseList = List.of(studentCourse1, studentCourse2);
    List<CourseApplication> courseApplicationList = List.of(courseApplication1, courseApplication2);

    List<CourseDetail> actual =  sut.convertCourseDetailList(studentCourseList, courseApplicationList);

    assertThat(actual.size()).isEqualTo(2);

    assertThat(actual).extracting(CourseDetail::getStudentCourse).contains(studentCourse1, studentCourse2);
    assertThat(actual).extracting(CourseDetail::getCourseApplication).contains(courseApplication1, courseApplication2);
  }

  @Test
  void 受講生コース情報のリストに紐づかない受講生コース申込状況のリストは結果に含まれないこと () {
    StudentCourse studentCourse = new StudentCourse();
    studentCourse.setId("1");
    studentCourse.setStudentId("1");
    studentCourse.setCourseName("テストコース");
    CourseApplication courseApplication = new CourseApplication();
    courseApplication.setId("1");
    courseApplication.setCourseId("2");
    courseApplication.setStudentId("2");
    courseApplication.setApplicationStatus(ApplicationStatus.FORMAL);

    List<StudentCourse> studentCourseList = List.of(studentCourse);
    List<CourseApplication> courseApplicationList = List.of(courseApplication);

    List<CourseDetail> actual =  sut.convertCourseDetailList(studentCourseList, courseApplicationList);

    assertThat(actual.get(0).getStudentCourse()).isEqualTo(studentCourse);
    assertThat(actual.get(0).getCourseApplication()).isNull();
  }

  @Test
  void フラットな検索結果から受講生コース詳細に正しく変換されること () {
    StudentCourseApplicationRow studentCourseApplicationRow = new StudentCourseApplicationRow();
    studentCourseApplicationRow.setStudentId("1");
    studentCourseApplicationRow.setCourseId("1");
    studentCourseApplicationRow.setCourseName("テストコース");
    studentCourseApplicationRow.setCourseStartAt(LocalDateTime.parse("2026-02-07T16:49:29"));
    studentCourseApplicationRow.setCourseEndAt(LocalDateTime.parse("2027-02-07T16:49:29"));

    studentCourseApplicationRow.setApplicationId("1");
    studentCourseApplicationRow.setApplicationStatus(ApplicationStatus.FORMAL);

    CourseDetail actual = sut.convertCourseDetail(studentCourseApplicationRow);

    assertThat(actual.getStudentCourse())
        .satisfies(studentCourse -> {
          assertThat(studentCourse.getId()).isEqualTo("1");
          assertThat(studentCourse.getStudentId()).isEqualTo("1");
          assertThat(studentCourse.getCourseName()).isEqualTo("テストコース");
          assertThat(studentCourse.getCourseStartAt()).isEqualTo(LocalDateTime.parse("2026-02-07T16:49:29"));
          assertThat(studentCourse.getCourseEndAt()).isEqualTo(LocalDateTime.parse("2027-02-07T16:49:29"));
        });

    assertThat(actual.getCourseApplication())
        .satisfies(courseApplication -> {
          assertThat(courseApplication.getId()).isEqualTo("1");
          assertThat(courseApplication.getStudentId()).isEqualTo("1");
          assertThat(courseApplication.getCourseId()).isEqualTo("1");
          assertThat(courseApplication.getApplicationStatus()).isEqualTo(ApplicationStatus.FORMAL);
        });
  }
}