package raisetech.StudentManagement.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.annotation.Nonnull;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import raisetech.StudentManagement.controller.converter.CourseConverter;
import raisetech.StudentManagement.controller.converter.StudentConverter;
import raisetech.StudentManagement.data.CourseApplication;
import raisetech.StudentManagement.data.Student;
import raisetech.StudentManagement.data.StudentCourse;
import raisetech.StudentManagement.domain.CourseDetail;
import raisetech.StudentManagement.domain.StudentDetail;
import raisetech.StudentManagement.enums.ApplicationStatus;
import raisetech.StudentManagement.exception.InvalidStatusTransitionException;
import raisetech.StudentManagement.exception.ResourceNotFoundException;
import raisetech.StudentManagement.repository.StudentRepository;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

  @Mock
  private StudentRepository repository;

  @Mock
  private StudentConverter studentConverter;

  @Mock
  private CourseConverter courseConverter;

  private StudentService sut;
  @BeforeEach
  void before() {
    sut = new StudentService(repository, studentConverter, courseConverter);
  }

  @Test
  void 受講生詳細の一覧検索_リポジトリとコンバーターの処理が適切に呼び出せていること () {
    Student student = createStudent();
    StudentCourse studentCourse = createStudentCourse(student.getId());
    CourseApplication courseApplication = createCourseApplication(student.getId(), studentCourse.getId());

    List<Student> studentList = List.of(student);
    List<StudentCourse> studentCourseList = List.of(studentCourse);
    List<CourseApplication> courseApplicationList = List.of(courseApplication);

    CourseDetail courseDetail = new CourseDetail();
    courseDetail.setStudentCourse(studentCourse);
    courseDetail.setCourseApplication(courseApplication);

    List<CourseDetail> courseDetailList = List.of(courseDetail);

    StudentDetail studentDetail = new StudentDetail();
    studentDetail.setStudent(student);
    studentDetail.setCourseDetailList(courseDetailList);

    List<StudentDetail> expected = List.of(studentDetail);

    when(repository.search()).thenReturn(studentList);
    when(repository.searchStudentCourseList()).thenReturn(studentCourseList);
    when(repository.searchCourseApplicationList()).thenReturn(courseApplicationList);

    when(courseConverter.convertCourseDetails(studentCourseList,courseApplicationList)).thenReturn(courseDetailList);
    when(studentConverter.convertStudentDetails(studentList,courseDetailList)).thenReturn(expected);

    List<StudentDetail> actual = sut.searchStudentList();

    verify(repository, times(1)).search();
    verify(repository, times(1)).searchStudentCourseList();
    verify(repository, times(1)).searchCourseApplicationList();

    verify(courseConverter, times(1)).convertCourseDetails(studentCourseList, courseApplicationList);
    verify(studentConverter, times(1)).convertStudentDetails(studentList,courseDetailList);

    assertThat(actual).isEqualTo(expected);
  }

  @Test
  void 受講生詳細の一覧検索_全リポジトリが空でも受講生詳細が生成されること () {
    List<Student> studentList = new ArrayList<>();
    List<StudentCourse> studentCourseList = new ArrayList<>();
    List<CourseApplication> courseApplicationList = new ArrayList<>();

    List<CourseDetail> courseDetailList = new ArrayList<>();
    List<StudentDetail> expected = new ArrayList<>();

    when(repository.search()).thenReturn(studentList);
    when(repository.searchStudentCourseList()).thenReturn(studentCourseList);
    when(repository.searchCourseApplicationList()).thenReturn(courseApplicationList);

    when(courseConverter.convertCourseDetails(studentCourseList,courseApplicationList)).thenReturn(courseDetailList);
    when(studentConverter.convertStudentDetails(studentList,courseDetailList)).thenReturn(expected);

    List<StudentDetail> actual = sut.searchStudentList();

    verify(repository, times(1)).search();
    verify(repository, times(1)).searchStudentCourseList();
    verify(repository, times(1)).searchCourseApplicationList();

    verify(courseConverter, times(1)).convertCourseDetails(studentCourseList, courseApplicationList);
    verify(studentConverter, times(1)).convertStudentDetails(studentList,courseDetailList);

    assertThat(actual).isEmpty();
  }

  @Test
  void 受講生詳細の単一検索_リポジトリの処理が適切に呼び出せていること () {
    Student student = createStudent();
    String id = student.getId();
    StudentCourse studentCourse = createStudentCourse(student.getId());
    CourseApplication courseApplication = createCourseApplication(student.getId(), studentCourse.getId());

    List<StudentCourse> studentCourseList = List.of(studentCourse);
    List<CourseApplication> courseApplicationList = List.of(courseApplication);

    CourseDetail courseDetail = new CourseDetail();
    courseDetail.setStudentCourse(studentCourse);
    courseDetail.setCourseApplication(courseApplication);

    List<CourseDetail> courseDetailList = List.of(courseDetail);

    StudentDetail expected = new StudentDetail();
    expected.setStudent(student);
    expected.setCourseDetailList(courseDetailList);

    when(repository.searchStudent(id)).thenReturn(Optional.of(student));
    when(repository.searchStudentCourseByStudentId(id)).thenReturn(studentCourseList);
    when(repository.searchCourseApplicationByStudentId(id)).thenReturn(courseApplicationList);
    when(courseConverter.convertCourseDetails(studentCourseList,courseApplicationList)).thenReturn(courseDetailList);

    StudentDetail actual = sut.searchStudent(id);

    verify(repository, times(1)).searchStudent(id);
    verify(repository, times(1)).searchStudentCourseByStudentId(id);
    verify(repository, times(1)).searchCourseApplicationByStudentId(id);
    verify(courseConverter, times(1)).convertCourseDetails(studentCourseList,courseApplicationList);

    assertThat(actual.getStudent()).isEqualTo(expected.getStudent());
    assertThat(actual.getCourseDetailList()).isEqualTo(expected.getCourseDetailList());
  }

  @Test
  void 受講生詳細の単一検索_存在しないIDで検索すると例外になること (){
    String id = "999";

    when(repository.searchStudent(id)).thenReturn(Optional.empty());

    assertThatThrownBy(()->sut.searchStudent(id))
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessageContaining(id);
  }

  @Test
  void 受講生詳細の単一検索_全リポジトリが空を返してもStudentDetailを返すこと (){
    String id = "999";
    when(repository.searchStudent(id)).thenReturn(Optional.of(new Student()));
    when(repository.searchStudentCourseByStudentId(id)).thenReturn(new ArrayList<>());
    when(repository.searchCourseApplicationByStudentId(id)).thenReturn(new ArrayList<>());

    StudentDetail actual = sut.searchStudent(id);

    assertThat(actual).isNotNull();
    assertThat(actual.getStudent()).isNotNull();
    assertThat(actual.getCourseDetailList()).isEmpty();
  }

  @Test
  void 受講生詳細の登録_リポジトリの処理が適切に呼び出せていること () {
    Student student = new Student();
    StudentCourse studentCourse = new StudentCourse();
    CourseApplication courseApplication = new CourseApplication();
    CourseDetail courseDetail = new CourseDetail(studentCourse, courseApplication);
    List<CourseDetail> courseDetailList = List.of(courseDetail);
    StudentDetail studentDetail = new StudentDetail(student, courseDetailList);

    sut.registerStudent(studentDetail);

    verify(repository,times(1)).registerStudent(student);
    verify(repository,times(1)).registerStudentCourse(studentCourse);
    verify(repository,times(1)).registerCourseApplication(courseApplication);
  }

  @Test
  void 受講生詳細の登録_正しい内容で保存されていること () {
    Student student = createStudent();
    student.setId(null);
    StudentCourse studentCourse = new StudentCourse();
    studentCourse.setCourse("テストコース");
    CourseApplication courseApplication = new CourseApplication();
    CourseDetail courseDetail = new CourseDetail(studentCourse, courseApplication);
    List<CourseDetail> courseDetailList = List.of(courseDetail);
    StudentDetail studentDetail = new StudentDetail(student, courseDetailList);

    ArgumentCaptor<Student> studentCaptor = ArgumentCaptor.forClass(Student.class);
    ArgumentCaptor<StudentCourse> studentCourseCaptor = ArgumentCaptor.forClass(StudentCourse.class);
    ArgumentCaptor<CourseApplication> courseApplicationCaptor = ArgumentCaptor.forClass(CourseApplication.class);

    sut.registerStudent(studentDetail);

    verify(repository,times(1)).registerStudent(studentCaptor.capture());
    verify(repository,times(1)).registerStudentCourse(studentCourseCaptor.capture());
    verify(repository,times(1)).registerCourseApplication(courseApplicationCaptor.capture());

    assertThat(studentCaptor.getValue().getId()).isNull();
    assertThat(studentCaptor.getValue().getName()).isEqualTo(student.getName());
    assertThat(studentCourseCaptor.getValue().getCourse()).isEqualTo(studentCourse.getCourse());
    assertThat(courseApplicationCaptor.getValue().getStatus()).isEqualTo(courseApplication.getStatus());
  }

  @Test
  void 受講生詳細の登録_初期値として設定される値が正しく設定されていること (){
    Student student = createStudent();
    student.setId(null);
    StudentCourse studentCourse = new StudentCourse();
    CourseApplication courseApplication = new CourseApplication();
    CourseDetail courseDetail = new CourseDetail(studentCourse, courseApplication);
    List<CourseDetail> courseDetailList = List.of(courseDetail);
    StudentDetail studentDetail = new StudentDetail(student, courseDetailList);

    ArgumentCaptor<StudentCourse> studentCourseCaptor = ArgumentCaptor.forClass(StudentCourse.class);
    ArgumentCaptor<CourseApplication> courseApplicationCaptor = ArgumentCaptor.forClass(CourseApplication.class);

    sut.registerStudent(studentDetail);

    verify(repository,times(1)).registerStudentCourse(studentCourseCaptor.capture());
    verify(repository,times(1)).registerCourseApplication(courseApplicationCaptor.capture());

    assertThat(studentCourseCaptor.getValue().getStudentId()).isEqualTo(student.getId());
    assertThat(studentCourseCaptor.getValue().getStartDate()).isNotNull();
    assertThat(studentCourseCaptor.getValue().getEndDate()).isNotNull();

    assertThat(courseApplicationCaptor.getValue().getStudentId()).isEqualTo(student.getId());
    assertThat(courseApplicationCaptor.getValue().getCourseId()).isEqualTo(studentCourse.getId());
    assertThat(courseApplicationCaptor.getValue().getStatus()).isEqualTo(ApplicationStatus.TEMP);
  }

  @Test
  void 受講生詳細の登録_StudentDetailがnullの場合例外が発生すること (){
    assertThatThrownBy(()->sut.registerStudent(null)).isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void 受講生詳細の登録_Studentがnullの場合例外が発生すること(){
    StudentDetail studentDetail = new StudentDetail(null,List.of());

    assertThatThrownBy(()->sut.registerStudent(studentDetail)).isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void 受講生コース詳細の追加_リポジトリの処理が適切に呼び出せていること (){
    String id = "999";
    Student student = new Student();
    StudentCourse studentCourse = new StudentCourse();
    CourseApplication courseApplication = new CourseApplication();
    CourseDetail courseDetail = new CourseDetail(studentCourse, courseApplication);

    when(repository.searchStudent(id)).thenReturn(Optional.of(student));

    sut.addCourseDetail(id, courseDetail);

    verify(repository,times(1)).searchStudent(id);
    verify(repository,times(1)).registerStudentCourse(studentCourse);
    verify(repository,times(1)).registerCourseApplication(courseApplication);
  }

  @Test
  void 受講生コース詳細の追加_正しい内容で保存されること(){
    String id = "999";
    Student student = new Student();
    student.setId(id);
    StudentCourse studentCourse = new StudentCourse();
    studentCourse.setCourse("テストコース");
    CourseApplication courseApplication = new CourseApplication();
    CourseDetail courseDetail = new CourseDetail(studentCourse, courseApplication);

    when(repository.searchStudent(id)).thenReturn(Optional.of(student));

    ArgumentCaptor<StudentCourse> studentCourseCaptor = ArgumentCaptor.forClass(StudentCourse.class);
    ArgumentCaptor<CourseApplication> courseApplicationCaptor = ArgumentCaptor.forClass(CourseApplication.class);

    sut.addCourseDetail(id,courseDetail);

    verify(repository,times(1)).searchStudent(id);

    verify(repository,times(1)).registerStudentCourse(studentCourseCaptor.capture());
    verify(repository,times(1)).registerCourseApplication(courseApplicationCaptor.capture());

    assertThat(studentCourseCaptor.getValue().getStudentId()).isEqualTo(id);
    assertThat(studentCourseCaptor.getValue().getCourse()).isEqualTo(studentCourse.getCourse());
    assertThat(studentCourseCaptor.getValue().getStartDate()).isNotNull();
    assertThat(studentCourseCaptor.getValue().getEndDate()).isNotNull();

    assertThat(courseApplicationCaptor.getValue().getStudentId()).isEqualTo(student.getId());
    assertThat(courseApplicationCaptor.getValue().getCourseId()).isEqualTo(studentCourseCaptor.getValue().getId());
    assertThat(courseApplicationCaptor.getValue().getStatus()).isEqualTo(ApplicationStatus.TEMP);
  }

  @Test
  void 受講生コース詳細の追加_存在しない受講生IDでのコース追加で例外が発生すること(){
    String id = "999";
    when(repository.searchStudent(id)).thenReturn(Optional.empty());

    assertThatThrownBy(()->sut.addCourseDetail(id, new CourseDetail()))
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessageContaining(id);
  }

  @Test
  void 受講生の更新_リポジトリの処理が適切に呼び出せていること () {
    Student student = new Student();
    student.setId("999");

    when(repository.searchStudent(student.getId())).thenReturn(Optional.of(student));

    sut.updateStudent(student);

    verify(repository,times(1)).searchStudent(student.getId());
    verify(repository, times(1)).updateStudent(student);
  }

  @Test
  void 受講生の更新_正しい内容で更新されていること () {
    Student expected = createStudent();

    when(repository.searchStudent(expected.getId())).thenReturn(Optional.of(expected));

    ArgumentCaptor<Student> studentCaptor = ArgumentCaptor.forClass(Student.class);

    sut.updateStudent(expected);

    verify(repository,times(1)).updateStudent(studentCaptor.capture());

    assertThat(studentCaptor.getValue().getId()).isEqualTo(expected.getId());
    assertThat(studentCaptor.getValue().getName()).isEqualTo(expected.getName());
  }

  @Test
  void 受講生の更新_存在しない受講生の更新で例外が発生すること(){
    String id = "999";
    when(repository.searchStudent(id)).thenReturn(Optional.empty());

    assertThatThrownBy(()->sut.addCourseDetail(id, new CourseDetail()))
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessageContaining(id);
  }

  @Test
  void 受講生コース詳細の更新_リポジトリの処理が適切に呼び出せていること(){
    String id = "999";
    StudentCourse studentCourse = createStudentCourse(id);
    CourseApplication courseApplication = createCourseApplication(id,studentCourse.getId());

    CourseApplication nextCourseApplication = new CourseApplication();
    nextCourseApplication.setId(courseApplication.getId());
    nextCourseApplication.setStudentId(courseApplication.getStudentId());
    nextCourseApplication.setCourseId(courseApplication.getCourseId());
    nextCourseApplication.setStatus(ApplicationStatus.FORMAL);

    CourseDetail courseDetail = new CourseDetail(studentCourse, nextCourseApplication);


    when(repository.searchStudentCourseByCourseId(studentCourse.getId())).thenReturn(Optional.of(studentCourse));
    when(repository.searchCourseApplicationByCourseId(studentCourse.getId())).thenReturn(Optional.of(courseApplication));

    sut.updateCourseDetail(courseDetail);

    verify(repository,times(1)).searchStudentCourseByCourseId(studentCourse.getId());
    verify(repository,times(1)).searchCourseApplicationByCourseId(studentCourse.getId());
    verify(repository,times(1)).updateStudentCourse(studentCourse);
    verify(repository,times(1)).updateCourseApplication(nextCourseApplication);
  }

  @Test
  void 受講生コース詳細の更新_正しい内容で更新されていること(){
    String id = "999";
    StudentCourse studentCourse = createStudentCourse(id);
    CourseApplication courseApplication = createCourseApplication(id,studentCourse.getId());

    StudentCourse nextStudentCourse = new StudentCourse();
    nextStudentCourse.setId(studentCourse.getId());
    nextStudentCourse.setStudentId(studentCourse.getStudentId());
    nextStudentCourse.setCourse("テスト大変");
    nextStudentCourse.setStartDate(studentCourse.getStartDate());
    nextStudentCourse.setEndDate(studentCourse.getEndDate());

    CourseApplication nextCourseApplication = new CourseApplication();
    nextCourseApplication.setId(courseApplication.getId());
    nextCourseApplication.setStudentId(courseApplication.getStudentId());
    nextCourseApplication.setCourseId(courseApplication.getCourseId());
    nextCourseApplication.setStatus(ApplicationStatus.FORMAL);

    CourseDetail courseDetail = new CourseDetail(nextStudentCourse, nextCourseApplication);

    ArgumentCaptor<StudentCourse> studentCourseCaptor = ArgumentCaptor.forClass(StudentCourse.class);
    ArgumentCaptor<CourseApplication> courseApplicationCaptor = ArgumentCaptor.forClass(CourseApplication.class);

    when(repository.searchStudentCourseByCourseId(studentCourse.getId())).thenReturn(Optional.of(studentCourse));
    when(repository.searchCourseApplicationByCourseId(studentCourse.getId())).thenReturn(Optional.of(courseApplication));

    sut.updateCourseDetail(courseDetail);

    verify(repository,times(1)).updateStudentCourse(studentCourseCaptor.capture());
    verify(repository,times(1)).updateCourseApplication(courseApplicationCaptor.capture());

    assertThat(studentCourseCaptor.getValue()).satisfies(course ->  {
      assertThat(course.getId()).isEqualTo(nextStudentCourse.getId());
      assertThat(course.getCourse()).isNotEqualTo(studentCourse.getCourse());
      assertThat(course.getCourse()).isEqualTo(nextStudentCourse.getCourse());
    });

    assertThat(courseApplicationCaptor.getValue()).satisfies(application ->  {
      assertThat(application).isNotSameAs(nextStudentCourse);
      assertThat(application.getId()).isEqualTo(nextCourseApplication.getId());
      assertThat(application.getStatus()).isNotEqualTo(courseApplication.getStatus());
      assertThat(application.getStatus()).isEqualTo(nextCourseApplication.getStatus());
    });
  }

  @Test
  void 受講生コース詳細の更新_存在しない受講生コースの更新で例外が発生すること(){
    String id = "999";

    StudentCourse studentCourse = new StudentCourse();
    studentCourse.setId(id);

    CourseDetail courseDetail = new CourseDetail();
    courseDetail.setStudentCourse(studentCourse);

    when(repository.searchStudentCourseByCourseId(id)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> sut.updateCourseDetail(courseDetail));
  }

  @Test
  void 受講生コース詳細の更新_存在しない受講生コース申込状況の更新で例外が発生すること(){
    String id = "999";

    StudentCourse studentCourse = new StudentCourse();
    studentCourse.setId(id);

    CourseApplication courseApplication = new CourseApplication();
    courseApplication.setCourseId(id);

    CourseDetail courseDetail = new CourseDetail(studentCourse, courseApplication);

    when(repository.searchStudentCourseByCourseId(id)).thenReturn(Optional.of(studentCourse));
    when(repository.searchCourseApplicationByCourseId(id)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> sut.updateCourseDetail(courseDetail));
  }

  @Test
  void 受講生コース詳細の更新_不正なStatusの遷移で例外が発生すること(){
    String id = "999";
    StudentCourse studentCourse = createStudentCourse(id);
    CourseApplication courseApplication = createCourseApplication(id,studentCourse.getId());

    CourseApplication nextCourseApplication = new CourseApplication();
    nextCourseApplication.setId(courseApplication.getId());
    nextCourseApplication.setStudentId(courseApplication.getStudentId());
    nextCourseApplication.setCourseId(courseApplication.getCourseId());
    nextCourseApplication.setStatus(ApplicationStatus.COMPLETED);

    CourseDetail courseDetail = new CourseDetail(studentCourse, nextCourseApplication);

    when(repository.searchStudentCourseByCourseId(studentCourse.getId())).thenReturn(Optional.of(studentCourse));
    when(repository.searchCourseApplicationByCourseId(studentCourse.getId())).thenReturn(Optional.of(courseApplication));

    assertThrows(InvalidStatusTransitionException.class, () -> sut.updateCourseDetail(courseDetail));
  }

  @Nonnull
  private static Student createStudent() {
    Student student = new Student();
    student.setId("1");
    student.setName("テスト四太郎");
    student.setHurigana("てすとしたろう");
    student.setNickname("テスト大好きくん");
    student.setEmail("test@example.com");
    student.setArea("テスト県");
    student.setGender("男性");
    student.setDeleted(false);
    student.setRemark("テスト大好きです。");
    return student;
  }

  @Nonnull
  private static StudentCourse createStudentCourse(String studentId) {
    StudentCourse studentCourse = new StudentCourse();
    studentCourse.setId("1");
    studentCourse.setStudentId(studentId);
    studentCourse.setCourse("テストコース");
    studentCourse.setStartDate(LocalDate.parse("2026-02-16"));
    studentCourse.setEndDate(LocalDate.parse("2027-02-16"));
    return studentCourse;
  }

  @Nonnull
  private static CourseApplication createCourseApplication(String studentId,
      String studentCourseId) {
    CourseApplication courseApplication = new CourseApplication();
    courseApplication.setId("1");
    courseApplication.setStudentId(studentId);
    courseApplication.setCourseId(studentCourseId);
    courseApplication.setStatus(ApplicationStatus.TEMP);
    return courseApplication;
  }
}