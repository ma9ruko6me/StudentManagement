package raisetech.StudentManagement.controller.converter;


import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;
import raisetech.StudentManagement.data.StudentCourse;
import raisetech.StudentManagement.data.CourseApplication;
import raisetech.StudentManagement.domain.CourseDetail;
import raisetech.StudentManagement.dto.result.StudentCourseApplicationRow;

/**
 * 受講生コース情報と受講生コース申込状況を相互に変換し、受講生コース詳細を生成するコンバーターです。
 */
@Component
public class CourseConverter {

  /**
   * 受講生コース情報と受講生コース申込状況を紐付けて受講生コース詳細に変換する。
   *
   * @param studentCourseList 受講生コース情報のリスト
   * @param courseApplicationList 受講生コース申込状況のリスト
   * @return　受講生コース詳細情報のリスト
   */
  public List<CourseDetail> convertCourseDetailList(List<StudentCourse> studentCourseList,List<CourseApplication> courseApplicationList){
    List<CourseDetail> courseDetailList = new ArrayList<>();

    for (StudentCourse studentCourse : studentCourseList) {
      CourseDetail courseDetail = new CourseDetail();
      courseDetail.setStudentCourse(studentCourse);

      CourseApplication courseApplication = null;

      for (CourseApplication application : courseApplicationList) {
        if (studentCourse.getId().equals(application.getCourseId())) {
          courseApplication = application;
          break;
        }
      }

      courseDetail.setCourseApplication(courseApplication);
      courseDetailList.add(courseDetail);
    }
    return courseDetailList;
  }

  /**
   * フラットな検索結果からコース詳細へ変換する。
   *
   * @param studentCourseApplicationRow フラットな検索結果（受講生・受講生コース情報・受講生コース申込状況を含む1レコード）
   * @return 受講生コース詳細
   */
  public CourseDetail convertCourseDetail (StudentCourseApplicationRow studentCourseApplicationRow){
    CourseDetail courseDetail = new CourseDetail();
    StudentCourse studentCourse = new StudentCourse();
    CourseApplication courseApplication = new CourseApplication();

    studentCourse.setId(studentCourseApplicationRow.getCourseId());
    studentCourse.setStudentId(studentCourseApplicationRow.getStudentId());
    studentCourse.setCourseName(studentCourseApplicationRow.getCourseName());
    studentCourse.setCourseStartAt(studentCourseApplicationRow.getCourseStartAt());
    studentCourse.setCourseEndAt(studentCourseApplicationRow.getCourseEndAt());
    courseDetail.setStudentCourse(studentCourse);

    courseApplication.setId(studentCourseApplicationRow.getApplicationId());
    courseApplication.setStudentId(studentCourseApplicationRow.getStudentId());
    courseApplication.setCourseId(studentCourseApplicationRow.getCourseId());
    courseApplication.setApplicationStatus(studentCourseApplicationRow.getApplicationStatus());
    courseDetail.setCourseApplication(courseApplication);

    return courseDetail;
  }
}
