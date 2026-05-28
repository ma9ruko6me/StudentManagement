package raisetech.StudentManagement.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
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
import raisetech.StudentManagement.dto.SearchCondition;
import raisetech.StudentManagement.enums.ApplicationStatus;
import raisetech.StudentManagement.enums.SearchType;
import raisetech.StudentManagement.exception.InvalidStatusTransitionException;
import raisetech.StudentManagement.exception.ResourceNotFoundException;
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
  public List<StudentDetail> searchStudentList() {
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
    Student student = repository.searchStudent(id)
        .orElseThrow(() -> new ResourceNotFoundException("Student not found" + id));
    List<StudentCourse> studentCourseList = repository.searchStudentCourseByStudentId(id);
    List<CourseApplication> courseApplicationList = repository.searchCourseApplicationByStudentId(id);
    return new StudentDetail(student, courseConverter.convertCourseDetails(studentCourseList, courseApplicationList));
  }

  public List<StudentDetail> searchStudentListByCondition(SearchCondition condition) {
    if (condition == null || !condition.hasAnyCondition()) {
      throw new IllegalArgumentException("Condition is null");
    }

    if (condition.getSearchType() == null) {
      condition.setSearchType(SearchType.AND);
    }

    List<Student> studentList = repository.searchStudentByCondition(condition);
    List<StudentCourse> studentCourseList = repository.searchStudentCourseList();
    List<CourseApplication>  courseApplicationList = repository.searchCourseApplicationList();

    List<CourseDetail> courseDetailList = courseConverter.convertCourseDetails(studentCourseList, courseApplicationList);

    return studentConverter.convertStudentDetails(studentList, courseDetailList);
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
    if (studentDetail == null || studentDetail.getStudent() == null) {
      throw new IllegalArgumentException("invalid student detail");
    }

    Student student = studentDetail.getStudent();

    repository.registerStudent(student);
    studentDetail.getCourseDetailList().forEach(courseDetail -> {
      initStudentCourse(courseDetail,studentDetail.getStudent().getId());
      repository.registerStudentCourse(courseDetail.getStudentCourse());
      initCourseApplication(courseDetail,studentDetail.getStudent().getId());
      repository.registerCourseApplication(courseDetail.getCourseApplication());
    });
    return studentDetail;
  }

  /**
   * 受講生コース詳細の登録を行います。 受講生コース情報、受講生コース申込状況を個別に登録します。
   * 受講生コース情報には受講生情報を紐づける値やコース開始日、コース終了日を設定します。
   * 受講生コース申込状況には受講生情報と受講生コース情報を結びつける値と申込状況を設定します。
   *
   * @param id 受講生ID
   * @param courseDetail 受講生コース詳細
   * @return　新しく受講生コース詳細を追加した受講生詳細
   */
  @Transactional
  public StudentDetail addCourseDetail(String id, CourseDetail courseDetail) {
    Student student = repository.searchStudent(id)
        .orElseThrow(() -> new ResourceNotFoundException("Student not found" + id));

    initStudentCourse(courseDetail,id);
    repository.registerStudentCourse(courseDetail.getStudentCourse());
    initCourseApplication(courseDetail,id);
    repository.registerCourseApplication(courseDetail.getCourseApplication());

    List<StudentCourse> studentCourseList = repository.searchStudentCourseByStudentId(id);
    List<CourseApplication> courseApplicationList = repository.searchCourseApplicationByStudentId(id);

    return new StudentDetail(student, courseConverter.convertCourseDetails(studentCourseList, courseApplicationList));
  }

  /**
   * 受講生コース情報を登録する際の初期設定をする。
   *
   * @param courseDetail 受講生コース詳細
   * @param studentId 受講生ID
   */
  private void initStudentCourse(CourseDetail courseDetail,String studentId) {
    StudentCourse studentCourse = courseDetail.getStudentCourse();
    LocalDateTime now = LocalDateTime.now();

    studentCourse.setStudentId(studentId);
    studentCourse.setCourseStartAt(now);
    studentCourse.setCourseEndAt(now.plusYears(1));
  }

  /**
   * 受講生コース申込状況を登録する際の初期設定をする。
   *
   * @param courseDetail 受講生コース詳細
   * @param studentId 受講生ID
   */
  private void initCourseApplication(CourseDetail courseDetail,String studentId) {
    CourseApplication courseApplication = courseDetail.getCourseApplication();

    courseApplication.setStudentId(studentId);
    courseApplication.setCourseId(courseDetail.getStudentCourse().getId());
    courseApplication.setApplicationStatus(ApplicationStatus.TEMP);
  }

  /**
   * 受講生の更新を行います。
   *
   * @param student 受講生
   */
  @Transactional
  public void updateStudent(Student student) {
    repository.searchStudent(student.getId())
        .orElseThrow(() -> new ResourceNotFoundException("Student not found" + student.getId()));

    repository.updateStudent(student);
  }

  /**
   * 受講生コース詳細の更新を行います。受講生コース情報、受講生コース申込状況をそれぞれ更新します。
   *
   * @param courseDetail 受講生コース詳細
   */
  @Transactional
  public void updateCourseDetail(CourseDetail courseDetail) {
    String courseId = courseDetail.getStudentCourse().getId();
    Optional<StudentCourse> studentCourse = repository.searchStudentCourseByCourseId(
        courseId);
    if (studentCourse.isEmpty()) {
      throw new ResourceNotFoundException("Course not found" + courseId);
    }

    CourseApplication courseApplication = repository.searchCourseApplicationByCourseId(courseId)
        .orElseThrow(() -> new ResourceNotFoundException("Application not found" + courseId));

    ApplicationStatus currentStatus = courseApplication.getApplicationStatus();
    ApplicationStatus nextStatus = courseDetail.getCourseApplication().getApplicationStatus();

    if (!currentStatus.canTransitionTo(nextStatus)) {
      throw new InvalidStatusTransitionException("Invalid status transition");
    }

    repository.updateStudentCourse(courseDetail.getStudentCourse());
    repository.updateCourseApplication(courseDetail.getCourseApplication());
  }
}
