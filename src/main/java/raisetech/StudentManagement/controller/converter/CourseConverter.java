package raisetech.StudentManagement.controller.converter;


import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;
import raisetech.StudentManagement.data.StudentCourse;
import raisetech.StudentManagement.data.CourseApplication;
import raisetech.StudentManagement.domain.CourseDetail;
import raisetech.StudentManagement.dto.result.SearchResult;

/**
 * 受講生コース詳細を受講生コース情報や申込状況、もしくはその逆の変換を行うコンバーターです。
 */
@Component
public class CourseConverter {

  /**
   * 受講生コース情報に紐づく申込状況をマッピングする。
   *
   * @param studentCourseList 受講生コース情報
   * @param courseApplicationList       受講生一覧
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

  public CourseDetail convertCourseDetail (SearchResult searchResult){
    CourseDetail courseDetail = new CourseDetail();
    StudentCourse studentCourse = new StudentCourse();
    CourseApplication courseApplication = new CourseApplication();

    studentCourse.setId(searchResult.getCourseId());
    studentCourse.setStudentId(searchResult.getStudentId());
    studentCourse.setCourseName(searchResult.getCourseName());
    studentCourse.setCourseStartAt(searchResult.getCourseStartAt());
    studentCourse.setCourseEndAt(searchResult.getCourseEndAt());
    courseDetail.setStudentCourse(studentCourse);

    courseApplication.setId(searchResult.getApplicationId());
    courseApplication.setStudentId(searchResult.getStudentId());
    courseApplication.setCourseId(searchResult.getCourseId());
    courseApplication.setApplicationStatus(searchResult.getApplicationStatus());
    courseDetail.setCourseApplication(courseApplication);

    return courseDetail;
  }
}
