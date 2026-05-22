package raisetech.StudentManagement.service;

import java.time.LocalDate;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import raisetech.StudentManagement.controller.converter.CourseConverter;
import raisetech.StudentManagement.controller.converter.StudentConverter;
import raisetech.StudentManagement.data.CourseApplication;
import raisetech.StudentManagement.data.Student;
import raisetech.StudentManagement.data.StudentCourse;
import raisetech.StudentManagement.domain.CourseDetail;
import raisetech.StudentManagement.domain.StudentDetail;
import raisetech.StudentManagement.repository.StudentRepository;

/**
 * 受講生情報を取り扱うサービスです。 受講生の検索や登録、更新処理を行います。
 */
@Service
public class StudentService {

  private StudentRepository repository;
  private StudentConverter studentConverter;
  private CourseConverter courseConverter;

  @Autowired
  public StudentService(StudentRepository repository, StudentConverter studentConverter, CourseConverter courseConverter) {
    this.repository = repository;
    this.studentConverter = studentConverter;
    this.courseConverter = courseConverter;
  }

  /**
   * 受講生詳細の一覧検索です。 全件検索を行うので、条件指定は行いません。
   *
   * @return　受講生詳細一覧（全件）
   */
  public List<StudentDetail> searchStudentlList() {
    List<Student> studentList = repository.search();
    List<StudentCourse> studentCourseList = repository.searchStudentCourseList();
    List<CourseApplication>  courseApplicationList = repository.searchCourseApplicationList();

    List<CourseDetail> courseDetailList = courseConverter.convertCourseDetails(studentCourseList, courseApplicationList);

    return studentConverter.convertStudentDetails(studentList, courseDetailList);
  }

  /**
   * 受講生詳細の検索です。 IDに紐づく受験生情報を取得したあと、その受講生に紐づく受講生コース情報と受講生コース申込状況を取得して設定します。
   *
   * @param id 受講生ID
   * @return　受講生詳細
   */
  public StudentDetail searchStudent(String id) {
    Student student = repository.searchStudent(id);
    List<StudentCourse> studentCourseList = repository.searchStudentCourse(id);
    List<CourseApplication> courseApplicationList = repository.searchCourseApplicationByStudentId(id);
    return new StudentDetail(student, courseConverter.convertCourseDetails(studentCourseList, courseApplicationList));
  }

  /**
   * 受講生詳細の登録を行います。 受講生、受講生コース情報、受講生コース申込状況を個別に登録します。
   * 受講生コース情報には受講生情報を紐づける値やコース開始日、コース終了日を設定します。
   * 受講生コース申込状況には受講生情報と受講生コース情報を結びつける値と申込状況を設定します。
   *
   * @param studentDetail 受講生詳細
   * @return　登録情報を付与した受講生詳細
   */
  @Transactional
  public StudentDetail registerStudent(StudentDetail studentDetail) {
    Student student = studentDetail.getStudent();

    repository.registerStudent(student);
    studentDetail.getCourseDetailList().forEach(courseDetail -> {
      initCourseDetail(studentDetail);
      repository.registerStudentCourse(studentDetail.getCourseDetailList().getFirst().getStudentCourse());
      repository.registerCourseApplication(studentDetail.getCourseDetailList().getFirst()
          .getCourseApplication());
    });
    return studentDetail;
  }

  /**
   * 受講生コース情報と受講生コース申込状況を登録する際の初期設定をする。
   *
   * @param studentDetail 受講生
   */
  private void initCourseDetail(StudentDetail studentDetail) {
    StudentCourse studentCourse = studentDetail.getCourseDetailList().getFirst().getStudentCourse();
    CourseApplication courseApplication = studentDetail.getCourseDetailList().getFirst().getCourseApplication();
    LocalDate now = LocalDate.now();

    studentCourse.setStudentId(studentDetail.getStudent().getId());
    studentCourse.setStartDate(now);
    studentCourse.setEndDate(now.plusYears(1));

    courseApplication.setStudentId(studentDetail.getStudent().getId());
    courseApplication.setCourseId(studentCourse.getId());
    courseApplication.setStatus("仮申込");
  }

  /**
   * 受講生詳細の更新を行います。 受講生、受講生コース情報、受講生コース申込状況をそれぞれ更新します。
   *
   * @param studentDetail 受講生詳細
   */
  @Transactional
  public void updateStudent(StudentDetail studentDetail) {
    repository.updateStudent(studentDetail.getStudent());
    studentDetail.getCourseDetailList()
        .forEach(courseDetail -> {
          repository.updateStudentCourse(courseDetail.getStudentCourse());
          repository.updateCourseApplication(courseDetail.getCourseApplication());
        });
  }

}
