package raisetech.StudentManagement.controller.converter;


import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import raisetech.StudentManagement.data.Student;
import raisetech.StudentManagement.data.StudentCourse;
import raisetech.StudentManagement.data.CourseApplication;
import raisetech.StudentManagement.domain.CourseDetail;
import raisetech.StudentManagement.domain.StudentDetail;

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
  public List<CourseDetail> convertCourseDetails (List<StudentCourse> studentCourseList,List<CourseApplication> courseApplicationList){
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
}
