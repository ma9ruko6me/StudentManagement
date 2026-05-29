package raisetech.StudentManagement.controller.converter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import raisetech.StudentManagement.data.Student;
import raisetech.StudentManagement.domain.CourseDetail;
import raisetech.StudentManagement.domain.StudentDetail;
import raisetech.StudentManagement.dto.result.SearchResult;

/**
 * 受講生詳細を受講生や受講生コース情報、もしくはその逆の変換を行うコンバーターです。
 */
@Component
public class StudentConverter {

  /**
   * 受講生に紐づく受講生コース情報をマッピングする。 受講生コース情報は受講生に対して複数存在するのでループを回して受講生詳細情報を組み立てる。
   *
   * @param studentList       受講生一覧
   * @param courseDetailList 受講生コース情報のリスト
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

  public Student convertStudent(SearchResult searchResult) {
    Student student = new Student();
    student.setId(searchResult.getStudentId());
    student.setName(searchResult.getName());
    student.setFurigana(searchResult.getFurigana());
    student.setNickname(searchResult.getNickname());
    student.setEmail(searchResult.getEmail());
    student.setArea(searchResult.getArea());
    student.setAge(searchResult.getAge());
    student.setGender(searchResult.getGender());
    student.setRemark(searchResult.getRemark());
    student.setDeleted(searchResult.isDeleted());
    return student;
  }
}
