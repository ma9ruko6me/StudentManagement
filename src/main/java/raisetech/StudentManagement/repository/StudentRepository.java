package raisetech.StudentManagement.repository;

import java.util.List;
import java.util.Optional;
import org.apache.ibatis.annotations.Mapper;
import raisetech.StudentManagement.data.CourseApplication;
import raisetech.StudentManagement.data.Student;
import raisetech.StudentManagement.data.StudentCourse;
import raisetech.StudentManagement.dto.request.SearchCondition;
import raisetech.StudentManagement.dto.result.StudentCourseApplicationRow;

/**
 * 受講生テーブル、受講生コース情報テーブル、受講生コース申込状況テーブルと紐づくRepositoryです。
 */
@Mapper
public interface StudentRepository {

  /**
   * 受講生の全件検索を行います。
   *
   * @return　受講生一覧（全件）
   */
  List<Student> search();

  /**
   * 受講生の検索を行います。
   *
   * @param id 受講生ID
   * @return　受講生
   */
  Optional<Student> searchStudent(String id);

  /**
   * 受講生のコース情報の全件検索を行います。
   *
   * @return　受講生のコース情報（全件）
   */
  List<StudentCourse> searchStudentCourseList();

  /**
   * 受講生IDに紐づく受講生コース情報を検索します。
   *
   * @param studentId 受講生ID
   * @return　受講生IDに紐づく受講生コース情報
   */
  List<StudentCourse> searchStudentCourseByStudentId(String studentId);

  /**
   * 受講生コースIDに紐づく受講生コース情報を検索します。
   *
   * @param courseId 受講生コースID
   * @return　受講生コースIDに紐づく受講生コース情報
   */
  Optional<StudentCourse>searchStudentCourseByCourseId(String courseId);

  /**
   * 受講生コース申込状況の全件検索を行います。
   *
   * @return　受講生コース申込状況（全件）
   */
  List<CourseApplication> searchCourseApplicationList();

  /**
   * 受講生IDに紐づく受講生コース申込状況を検索します。
   *
   * @param studentId 受講生ID
   * @return　受講生IDに紐づく受講生コース申込状況
   */
  List<CourseApplication> searchCourseApplicationByStudentId(String studentId);

  /**
   * 受講生IDに紐づく受講生コース申込状況を検索します。
   *
   * @param courseId 受講生コースID
   * @return　受講生コースIDに紐づく受講生コース申込状況
   */
  Optional<CourseApplication> searchCourseApplicationByCourseId(String courseId);

  /**
   * 受講生詳細の条件検索を行います。
   *
   * @param condition 受講生・受講生コース・受講生コース申込状況を横断した検索条件
   * @return　フラットな検索結果（受講生・受講生コース情報・受講生コース申込状況を含む1レコード）のリスト
   */
  List<StudentCourseApplicationRow> searchStudentRows(SearchCondition condition);

  /**
   * 受講生を新規登録します。 IDに関しては自動採番を行う。
   *
   * @param student 受講生
   */
  void registerStudent(Student student);

  /**
   * 受講生コース情報を新規登録します。 ID関しては自動採番を行います。
   *
   * @param studentCourse 受講生コース情報
   */
  void registerStudentCourse(StudentCourse studentCourse);

  /**
   * 受講生コース申込状況を新規登録します。 ID関しては自動採番を行います。
   *
   * @param courseApplication 受講生コース申込状況
   */
  void registerCourseApplication(CourseApplication courseApplication);

  /**
   * 受講生を更新します。
   *
   * @param student 受講生
   */
  void updateStudent(Student student);

  /**
   * 受講生コース情報を更新します。
   *
   * @param studentCourse
   */
  void updateStudentCourse(StudentCourse studentCourse);

  /**
   * 受講生コース申込状況を更新します。
   *
   * @param courseApplication 受講生コース申込状況
   */
  void updateCourseApplication(CourseApplication courseApplication);
}
