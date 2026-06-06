package raisetech.StudentManagement.controller.converter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import raisetech.StudentManagement.data.Student;
import raisetech.StudentManagement.domain.CourseDetail;
import raisetech.StudentManagement.domain.StudentDetail;
import raisetech.StudentManagement.dto.result.StudentCourseApplicationRow;

/**
 * 受講生と受講生コース詳細を相互に変換し、受講生詳細を生成するコンバーターです。
 */
@Component
public class StudentConverter {

  /**
   * 受講生と受講生コース詳細を紐付けて受講生詳細に変換する。
   *
   * @param studentList 受講生コース情報のリスト
   * @param courseDetailList 受講生コース申込状況のリスト
   * @return　受講生詳細情報のリスト
   */
  public List<StudentDetail> convertStudentDetails(List<Student> studentList,
      List<CourseDetail> courseDetailList) {
    List<StudentDetail> studentDetails = new ArrayList<>();
    studentList.forEach(student -> {
      StudentDetail studentDetail = new StudentDetail();
      studentDetail.setStudent(student);

      List<CourseDetail> convertCourseDetailList = courseDetailList.stream()
          .filter(courseDetail -> student.getId().equals(courseDetail.getStudentCourse().getStudentId()))
          .collect(Collectors.toList());

      studentDetail.setCourseDetailList(convertCourseDetailList);
      studentDetails.add(studentDetail);
    });
    return studentDetails;
  }

  /**
   * フラットな検索結果から受講生へ変換する。
   *
   * @param studentCourseApplicationRow フラットな検索結果（受講生・受講生コース情報・受講生コース申込状況を含む1レコード）
   * @return 受講生
   */
  public Student convertStudent(StudentCourseApplicationRow studentCourseApplicationRow) {
    Student student = new Student();
    student.setId(studentCourseApplicationRow.getStudentId());
    student.setName(studentCourseApplicationRow.getName());
    student.setFurigana(studentCourseApplicationRow.getFurigana());
    student.setNickname(studentCourseApplicationRow.getNickname());
    student.setEmail(studentCourseApplicationRow.getEmail());
    student.setArea(studentCourseApplicationRow.getArea());
    student.setAge(studentCourseApplicationRow.getAge());
    student.setGender(studentCourseApplicationRow.getGender());
    student.setRemark(studentCourseApplicationRow.getRemark());
    student.setDeleted(studentCourseApplicationRow.isDeleted());
    return student;
  }
}
