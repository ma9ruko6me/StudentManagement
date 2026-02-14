package raisetech.StudentManagement.repository;

import java.util.List;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import raisetech.StudentManagement.data.Student;
import raisetech.StudentManagement.data.StudentCourse;

/**
 * 受講生テーブルと受講生コース情報テーブルと紐づくRepositoryです。
 */
@Mapper
public interface StudentRepository {

  /**
   * 受講生の全件検索を行います。
   *
   * @return　受講生一覧（全件）
   */
  @Select("SELECT * FROM students")
  List<Student> search();

  /**
   * 受講生の検索を行います。
   *
   * @param id　受講生ID
   * @return　受講生
   */
  @Select("SELECT * FROM students WHERE id = #{id}")
  Student searchStudent(int id);

  /**
   * 受講生のコース情報の全件検索を行います。
   *
   * @return　受講生のコース情報（全件）
   */
  @Select("SELECT * FROM students_courses")
  List<StudentCourse> searchCourses();

  /**
   * 受講生IDに紐づく受講生コーズ情報を検索します。
   * @param id　受講生ID
   * @return　受講生IDに紐づく受講生コース情報
   */
  @Select("SELECT * FROM students_courses WHERE student_id = #{id}")
  List<StudentCourse> searchStudentCourses(int id);

  @Insert("INSERT INTO students (name,hurigana,nickname,age,email,area,gender,remark,is_deleted) VALUES (#{name},#{hurigana},#{nickname},#{age},#{email},#{area},#{gender},#{remark},0)")
  @Options(useGeneratedKeys = true, keyProperty = "id")
  void registerStudent(Student student);

  @Insert("INSERT INTO students_courses (student_id,course,start_date,end_date) VALUES (#{studentId},#{course},#{startDate},#{endDate})")
  @Options(useGeneratedKeys = true, keyProperty = "id")
  void registerStudentCourse(StudentCourse studentCourse);

  @Update("UPDATE students SET  name = #{name},hurigana = #{hurigana},nickname = #{nickname},age = #{age},email = #{email},area = #{area},gender = #{gender},remark = #{remark},is_deleted = #{isDeleted} WHERE id = #{id}")
  void updateStudent(Student student);

  @Update("UPDATE students_courses SET course = #{course},start_date = #{startDate},end_date = #{endDate} WHERE id = #{id}")
  void updateStudentCourses(StudentCourse studentCourse);
}
