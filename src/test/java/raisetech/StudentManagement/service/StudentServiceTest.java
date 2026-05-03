package raisetech.StudentManagement.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import raisetech.StudentManagement.controller.converter.StudentConverter;
import raisetech.StudentManagement.data.Student;
import raisetech.StudentManagement.data.StudentCourse;
import raisetech.StudentManagement.domain.StudentDetail;
import raisetech.StudentManagement.repository.StudentRepository;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

  @Mock
  private StudentRepository repository;

  @Mock
  private StudentConverter converter;

  private StudentService sut;
  @BeforeEach
  void before() {
    sut = new StudentService(repository, converter);
  }

  @Test
  void 受講生詳細の一覧検索_リポジトリとコンバーターの処理が適切に呼び出せていること () {
    List<Student> studentList = new ArrayList<>();
    List<StudentCourse> studentCourseList = new ArrayList<>();
    List<StudentDetail> expected = new ArrayList<>();
    when(repository.search()).thenReturn(studentList);
    when(repository.searchStudentCourseList()).thenReturn(studentCourseList);
    when(converter.convertStudentDetails(studentList,studentCourseList)).thenReturn(expected);

    List<StudentDetail> actual = sut.searchStudentList();

    verify(repository, times(1)).search();
    verify(repository, times(1)).searchStudentCourseList();
    verify(converter, times(1)).convertStudentDetails(studentList, studentCourseList);
    assertEquals(expected, actual);
  }

  @Test
  void 受講生詳細の単一検索_リポジトリの処理が適切に呼び出せていること () {
    int id = 999;
    Student student = new Student();
    student.setId(id);
    StudentCourse studentCourse = new StudentCourse();
    List<StudentCourse> studentCourseList = List.of(studentCourse);

    when(repository.searchStudent(id)).thenReturn(student);
    when(repository.searchStudentCourse(id)).thenReturn(studentCourseList);

    StudentDetail expected = new StudentDetail(student, studentCourseList);

    StudentDetail actual = sut.searchStudent(id);

    verify(repository, times(1)).searchStudent(id);
    verify(repository, times(1)).searchStudentCourse(id);

    assertEquals(expected.getStudent().getId(), actual.getStudent().getId());
  }

  @Test
  void 受講生詳細の単一検索_StudentとStudentDetailがNullでもStudentDetailを返すこと (){
    int id = 999;

    when(repository.searchStudent(id)).thenReturn(null);
    when(repository.searchStudentCourse(id)).thenReturn(null);

    StudentDetail actual = sut.searchStudent(id);

    assertNotNull(actual);
    assertNull(actual.getStudent());
    assertNull(actual.getStudentCourseList());
  }

  @Test
  void 受講生詳細の単一検索_StudentがNullでもStudentDetailを返すこと (){
    int id = 999;

    when(repository.searchStudent(id)).thenReturn(null);
    when(repository.searchStudentCourse(id)).thenReturn(new ArrayList<>());

    StudentDetail actual = sut.searchStudent(id);

    assertNotNull(actual);
    assertNull(actual.getStudent());
  }

  @Test
  void 受講生詳細の単一検索_StudentCourseListがNullでもStudentDetailを返すこと (){
    int id = 999;

    when(repository.searchStudent(id)).thenReturn(new Student());
    when(repository.searchStudentCourse(id)).thenReturn(null);

    StudentDetail actual = sut.searchStudent(id);

    assertNotNull(actual);
    assertNull(actual.getStudentCourseList());
  }

  @Test
  void 受講生詳細の単一検索_リポジトリが例外を投げた場合はそのまま伝播すること () {
    int id = 999;
    when(repository.searchStudent(id)).thenThrow(new RuntimeException());

    assertThrows(RuntimeException.class, () -> sut.searchStudent(id));
  }

  @Test
  void 受講生詳細の登録_リポジトリの処理が適切に呼び出せていること () {
    Student student = new Student();
    StudentCourse studentCourse = new StudentCourse();
    List<StudentCourse> studentCourseList = List.of(studentCourse);
    StudentDetail studentDetail = new StudentDetail(student, studentCourseList);

    sut.registerStudent(studentDetail);

    verify(repository,times(1)).registerStudent(student);
    verify(repository, times(1)).registerStudentCourse(studentCourse);
  }

  @Test
  void 受講生詳細の登録_正しい内容で保存されていること () {
    Student student = new Student();
    student.setId(999);
    student.setName("名無し");
    StudentCourse studentCourse = new StudentCourse();
    studentCourse.setId(999);
    studentCourse.setCourse("AAA");
    List<StudentCourse> studentCourseList = List.of(studentCourse);
    StudentDetail studentDetail = new StudentDetail(student, studentCourseList);

    ArgumentCaptor<Student> studentCaptor = ArgumentCaptor.forClass(Student.class);
    ArgumentCaptor<StudentCourse> studentCourseCaptor = ArgumentCaptor.forClass(StudentCourse.class);

    sut.registerStudent(studentDetail);

    verify(repository).registerStudent(studentCaptor.capture());
    verify(repository).registerStudentCourse(studentCourseCaptor.capture());

    Student actualStudent = studentCaptor.getValue();
    StudentCourse actualStudentCourse = studentCourseCaptor.getValue();

    assertEquals(999, actualStudent.getId());
    assertEquals("名無し", actualStudent.getName());
    assertEquals(999, actualStudentCourse.getId());
    assertEquals("AAA", actualStudentCourse.getCourse());
  }

  @Test
  void 受講生詳細の更新_リポジトリの処理が適切に呼び出せていること () {
    Student student = new Student();
    StudentCourse studentCourse = new StudentCourse();
    List<StudentCourse> studentCourseList = List.of(studentCourse);
    StudentDetail studentDetail = new StudentDetail(student, studentCourseList);

    sut.updateStudent(studentDetail);

    verify(repository,times(1)).updateStudent(student);
    verify(repository, times(1)).updateStudentCourse(studentCourse);
  }

  @Test
  void 受講生詳細の更新_正しい内容で保存されていること () {
    Student student = new Student();
    student.setId(999);
    student.setName("名無し");
    StudentCourse studentCourse = new StudentCourse();
    studentCourse.setId(999);
    studentCourse.setCourse("AAA");
    List<StudentCourse> studentCourseList = List.of(studentCourse);
    StudentDetail studentDetail = new StudentDetail(student, studentCourseList);

    ArgumentCaptor<Student> studentCaptor = ArgumentCaptor.forClass(Student.class);
    ArgumentCaptor<StudentCourse> studentCourseCaptor = ArgumentCaptor.forClass(StudentCourse.class);

    sut.updateStudent(studentDetail);

    verify(repository).updateStudent(studentCaptor.capture());
    verify(repository).updateStudentCourse(studentCourseCaptor.capture());

    Student actualStudent = studentCaptor.getValue();
    StudentCourse actualStudentCourse = studentCourseCaptor.getValue();

    assertEquals(999, actualStudent.getId());
    assertEquals("名無し", actualStudent.getName());
    assertEquals(999, actualStudentCourse.getId());
    assertEquals("AAA", actualStudentCourse.getCourse());
  }
}