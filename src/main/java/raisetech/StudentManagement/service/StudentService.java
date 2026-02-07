package raisetech.StudentManagement.service;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import raisetech.StudentManagement.data.Student;
import raisetech.StudentManagement.data.StudentCourse;
import raisetech.StudentManagement.domain.StudentDetail;
import raisetech.StudentManagement.repository.StudentRepository;

@Service
public class StudentService  {

  private StudentRepository repository;

  @Autowired
  public StudentService(StudentRepository repository) {
    this.repository = repository;
  }

  public List<Student> searchStudentList() {
    return repository.search();
  }

  public List<StudentCourse> searchStudentCourseList() {
    return repository.searchCourses();
  }

  @Transactional
  public Student registerStudent (StudentDetail studentDetail) {
    Student student = studentDetail.getStudent();
    repository.registerStudent(student);

    List<StudentCourse> courses = studentDetail.getStudentCourses();
    for (StudentCourse course : courses) {
      course.setStudentId(student.getId());
      course.setStartDate(LocalDateTime.now());
      course.setEndDate(LocalDateTime.now().plusYears(1));
      repository.registerStudentCourse(course);
    }
    return student;
  }

}
