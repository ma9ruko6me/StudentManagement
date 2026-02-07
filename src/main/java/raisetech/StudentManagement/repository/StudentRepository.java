package raisetech.StudentManagement.repository;

import java.util.List;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import raisetech.StudentManagement.data.Student;
import raisetech.StudentManagement.controller.converter.StudentCourse;

@Mapper
public interface StudentRepository {

  @Select("SELECT * FROM students")
  List<Student> search();

  @Select("SELECT * FROM students_courses")
  List<StudentCourse> searchCourses();

  @Insert("INSERT INTO students (name,hurigana,nickname,age,email,area,gender,remark,is_deleted) VALUES (#{name},#{hurigana},#{nickname},#{age},#{email},#{area},#{gender},#{remark},0)")
  @Options(useGeneratedKeys = true,keyProperty = "id")
  void registerStudent(Student student);
}
