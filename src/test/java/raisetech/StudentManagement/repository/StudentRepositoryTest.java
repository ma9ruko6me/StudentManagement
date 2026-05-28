package raisetech.StudentManagement.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import raisetech.StudentManagement.data.CourseApplication;
import raisetech.StudentManagement.data.Student;
import raisetech.StudentManagement.data.StudentCourse;
import raisetech.StudentManagement.dto.SearchCondition;
import raisetech.StudentManagement.dto.StudentSearchCondition;
import raisetech.StudentManagement.enums.ApplicationStatus;
import raisetech.StudentManagement.enums.SortKey;
import raisetech.StudentManagement.enums.SortOrder;

@MybatisTest
class StudentRepositoryTest {

  @Autowired
  private StudentRepository sut;

  @Test
  void 受講生の全件検索ができること() {
    List<Student> actual =sut.search();
    assertThat(actual.size()).isEqualTo(6);
  }

  @Test
  void 受講生の検索ができること() {
    String id = "1";
    Optional<Student> actual = sut.searchStudent(id);
    assertThat(actual)
        .isPresent()
        .get()
        .satisfies(student -> {
          assertThat(student.getId()).isEqualTo(id);
          assertThat(student.getName()).isEqualTo("鈴木彩艶");
          assertThat(student.getAge()).isEqualTo(22);
        });
  }

  @Test
  void 受講生の検索で存在しないIDの場合はOptionalが空で返ってくること() {
    String id = "999";
    Optional<Student> actual = sut.searchStudent(id);
    assertThat(actual).isEmpty();
  }

  @Test
  void 条件検索_キーワード_name(){
    StudentSearchCondition studentSearchCondition = new StudentSearchCondition();
    studentSearchCondition.setKeyword("鈴木");
    SearchCondition searchCondition = new SearchCondition();
    searchCondition.setStudentSearchCondition(studentSearchCondition);

    List<Student> actual = sut.searchStudentByCondition(searchCondition);
    assertThat(actual.size()).isEqualTo(1);
  }

  @Test
  void 条件検索_キーワード_furigana(){
    StudentSearchCondition studentSearchCondition = new StudentSearchCondition();
    studentSearchCondition.setKeyword("さわ");
    SearchCondition searchCondition = new SearchCondition();
    searchCondition.setStudentSearchCondition(studentSearchCondition);

    List<Student> actual = sut.searchStudentByCondition(searchCondition);
    assertThat(actual.size()).isEqualTo(1);
  }

  @Test
  void 条件検索_キーワード_nameとfurigana(){
    StudentSearchCondition studentSearchCondition = new StudentSearchCondition();
    studentSearchCondition.setKeyword("ゆい");
    SearchCondition searchCondition = new SearchCondition();
    searchCondition.setStudentSearchCondition(studentSearchCondition);

    List<Student> actual = sut.searchStudentByCondition(searchCondition);
    assertThat(actual.size()).isEqualTo(2);
  }

  @Test
  void 条件検索_ageFrom(){
    StudentSearchCondition studentSearchCondition = new StudentSearchCondition();
    studentSearchCondition.setAgeFrom(30);
    SearchCondition searchCondition = new SearchCondition();
    searchCondition.setStudentSearchCondition(studentSearchCondition);

    List<Student> actual = sut.searchStudentByCondition(searchCondition);
    assertThat(actual.size()).isEqualTo(3);
  }

  @Test
  void 条件検索_ageTo(){
    StudentSearchCondition studentSearchCondition = new StudentSearchCondition();
    studentSearchCondition.setAgeTo(30);
    SearchCondition searchCondition = new SearchCondition();
    searchCondition.setStudentSearchCondition(studentSearchCondition);

    List<Student> actual = sut.searchStudentByCondition(searchCondition);
    assertThat(actual.size()).isEqualTo(3);
  }

  @Test
  void 条件検索_名前とageFrom(){
    StudentSearchCondition studentSearchCondition = new StudentSearchCondition();
    studentSearchCondition.setKeyword("森保");
    studentSearchCondition.setAgeFrom(30);
    SearchCondition searchCondition = new SearchCondition();
    searchCondition.setStudentSearchCondition(studentSearchCondition);

    List<Student> actual = sut.searchStudentByCondition(searchCondition);
    assertThat(actual.size()).isEqualTo(1);
  }

  @Test
  void 条件検索_出身(){
    StudentSearchCondition studentSearchCondition = new StudentSearchCondition();
    studentSearchCondition.setArea("埼玉県");
    SearchCondition searchCondition = new SearchCondition();
    searchCondition.setStudentSearchCondition(studentSearchCondition);

    List<Student> actual = sut.searchStudentByCondition(searchCondition);
    assertThat(actual.size()).isEqualTo(2);
  }

  @Test
  void 条件検索_性別(){
    StudentSearchCondition studentSearchCondition = new StudentSearchCondition();
    studentSearchCondition.setGender("その他");
    SearchCondition searchCondition = new SearchCondition();
    searchCondition.setStudentSearchCondition(studentSearchCondition);

    List<Student> actual = sut.searchStudentByCondition(searchCondition);
    assertThat(actual.size()).isEqualTo(1);
  }

  @Test
  void 条件検索_ageFromとageTo(){
    StudentSearchCondition studentSearchCondition = new StudentSearchCondition();
    studentSearchCondition.setAgeFrom(20);
    studentSearchCondition.setAgeTo(30);
    SearchCondition searchCondition = new SearchCondition();
    searchCondition.setStudentSearchCondition(studentSearchCondition);

    List<Student> actual = sut.searchStudentByCondition(searchCondition);
    assertThat(actual.size()).isEqualTo(3);
  }

  @Test
  void 条件検索_名前と出身(){
    StudentSearchCondition studentSearchCondition = new StudentSearchCondition();
    studentSearchCondition.setKeyword("鈴木");
    studentSearchCondition.setArea("埼玉県");
    SearchCondition searchCondition = new SearchCondition();
    searchCondition.setStudentSearchCondition(studentSearchCondition);

    List<Student> actual = sut.searchStudentByCondition(searchCondition);
    assertThat(actual.size()).isEqualTo(1);
  }

  @Test
  void 条件検索_条件なし(){
    StudentSearchCondition studentSearchCondition = new StudentSearchCondition();
    SearchCondition searchCondition = new SearchCondition();
    searchCondition.setStudentSearchCondition(studentSearchCondition);

    List<Student> actual = sut.searchStudentByCondition(searchCondition);
    assertThat(actual.size()).isEqualTo(6);
    assertThat(actual).extracting(Student::getId).isSortedAccordingTo(Comparator.naturalOrder());
  }

  @Test
  void 条件検索_nameで降順(){
    StudentSearchCondition studentSearchCondition = new StudentSearchCondition();
    SearchCondition searchCondition = new SearchCondition();
    searchCondition.setStudentSearchCondition(studentSearchCondition);
    searchCondition.setSortKey(SortKey.NAME);
    searchCondition.setSortOrder(SortOrder.DESC);

    List<Student> actual = sut.searchStudentByCondition(searchCondition);
    assertThat(actual).extracting(Student::getName).isSortedAccordingTo(Comparator.reverseOrder());
  }

  @Test
  void 条件検索_ageで昇順(){
    StudentSearchCondition studentSearchCondition = new StudentSearchCondition();
    SearchCondition searchCondition = new SearchCondition();
    searchCondition.setStudentSearchCondition(studentSearchCondition);
    searchCondition.setSortKey(SortKey.AGE);
    searchCondition.setSortOrder(SortOrder.ASC);

    List<Student> actual = sut.searchStudentByCondition(searchCondition);
    assertThat(actual).extracting(Student::getAge).isSortedAccordingTo(Comparator.naturalOrder());
  }

  @Test
  void 受講生コース情報の全件検索ができること() {
    List<StudentCourse> actual =sut.searchStudentCourseList();
    assertThat(actual.size()).isEqualTo(10);
  }

  @Test
  void 受講生IDに紐づく受講生コース情報の検索ができること() {
    String id = "3";
    List<StudentCourse> actual = sut.searchStudentCourseByStudentId(id);
    assertThat(actual.size()).isEqualTo(3);
  }

  @Test
  void 存在しない受講生IDに紐づく受講生コース情報の検索で空のリストが返ってくること() {
    String id = "999";
    List<StudentCourse> actual = sut.searchStudentCourseByStudentId(id);
    assertThat(actual).isEmpty();
  }

  @Test
  void 受講生コースIDに紐づく受講生コース情報の検索ができること(){
    String id = "1";
    Optional<StudentCourse> actual = sut.searchStudentCourseByCourseId(id);
    assertThat(actual)
        .isPresent()
        .get()
        .satisfies(studentCourse -> {
          assertThat(studentCourse.getId()).isEqualTo(id);
          assertThat(studentCourse.getStudentId()).isEqualTo("1");
          assertThat(studentCourse.getCourseName()).isEqualTo("Javaコース");
        });
  }

  @Test
  void 存在しない受講生コースIDに紐づく受講生コース情報の検索でOptionalが空で返ってくること() {
    String id = "999";
    Optional<StudentCourse> actual = sut.searchStudentCourseByCourseId(id);
    assertThat(actual).isEmpty();
  }

  @Test
  void 受講生コース申込状況の全件検索ができること() {
    List<CourseApplication> actual =sut.searchCourseApplicationList();
    assertThat(actual.size()).isEqualTo(10);
  }

  @Test
  void 受講生コース申込状況の全件検索で中身が正しく取得できること() {
    List<CourseApplication> actual =sut.searchCourseApplicationList();

    assertThat(actual)
        .extracting(
            CourseApplication::getId,
            CourseApplication::getStudentId,
            CourseApplication::getCourseId,
            CourseApplication::getApplicationStatus
        )
        .contains(
            tuple("1","1","1",ApplicationStatus.IN_PROGRESS)
        );
  }

  @Test
  void 受講生IDに紐づく受講生コース申込状況の検索ができること() {
    String id = "1";
    List<CourseApplication> actual = sut.searchCourseApplicationByStudentId(id);

    assertThat(actual)
        .allSatisfy(courseApplication -> assertThat(courseApplication.getId()).isEqualTo(id));
  }

  @Test
  void 存在しない受講生IDに紐づく受講生コース申込状況の検索で空のリストが返ってくること() {
    String id = "999";
    List<CourseApplication> actual = sut.searchCourseApplicationByStudentId(id);
    assertThat(actual).isEmpty();
  }

  @Test
  void 受講生コースIDに紐づく受講生コース申込状況の検索ができること(){
    String id = "1";
    Optional<CourseApplication> actual = sut.searchCourseApplicationByCourseId(id);
    assertThat(actual)
        .isPresent()
        .get()
        .satisfies(courseApplication -> {
          assertThat(courseApplication.getId()).isEqualTo(id);
          assertThat(courseApplication.getStudentId()).isEqualTo("1");
          assertThat(courseApplication.getCourseId()).isEqualTo("1");
          assertThat(courseApplication.getApplicationStatus()).isEqualTo(ApplicationStatus.IN_PROGRESS);
        });
  }

  @Test
  void 存在しない受講生コースIDに紐づく受講生コース申込状況の検索でOptionalが空で返ってくること() {
    String id = "999";
    Optional<CourseApplication> actual = sut.searchCourseApplicationByCourseId(id);
    assertThat(actual).isEmpty();
  }

  @Test
  void 受講生の登録が行えること () {
    Student student = new Student();
    student.setName("テスト四太郎");
    student.setFurigana("てすとしたろう");
    student.setNickname("テスト大好きくん");
    student.setEmail("test@example.com");
    student.setArea("テスト県");
    student.setAge(18);
    student.setGender("男性");
    student.setRemark("");
    student.setDeleted(false);

    sut.registerStudent(student);

    List<Student> actual =sut.search();
    assertThat(actual.size()).isEqualTo(7);
    assertThat(student.getId()).isNotNull();
  }

  @Test
  void 受講生コース情報の登録ができること() {
    StudentCourse studentCourse = new StudentCourse();
    studentCourse.setStudentId("1");
    studentCourse.setCourseName("テストコース");
    studentCourse.setCourseStartAt(LocalDateTime.parse("2026-02-07T16:49:29"));
    studentCourse.setCourseEndAt(LocalDateTime.parse("2027-02-07T16:49:29"));

    sut.registerStudentCourse(studentCourse);

    List<StudentCourse> actual =sut.searchStudentCourseList();
    assertThat(actual.size()).isEqualTo(11);
    assertThat(studentCourse.getId()).isNotNull();
  }

  @Test
  void 受講生コース申込状況の登録ができること() {
    CourseApplication courseApplication = new CourseApplication();
    courseApplication.setStudentId("99");
    courseApplication.setCourseId("99");
    courseApplication.setApplicationStatus(ApplicationStatus.TEMP);

    sut.registerCourseApplication(courseApplication);

    List<CourseApplication> actual =sut.searchCourseApplicationList();
    assertThat(actual.size()).isEqualTo(11);
    assertThat(actual)
        .extracting(
            CourseApplication::getStudentId,
            CourseApplication::getCourseId,
            CourseApplication::getApplicationStatus
        )
        .contains(tuple("99","99",ApplicationStatus.TEMP));
    assertThat(courseApplication.getId()).isNotNull();
  }

  @Test
  void 受講生のnameが更新できること() {
    String id = "1";
    Student before = sut.searchStudent(id).orElseThrow();

    Student expected = new Student();
    expected.setId(before.getId());
    expected.setName(before.getName());
    expected.setAge(before.getAge());

    expected.setName("テスト四太郎");

    sut.updateStudent(expected);

    Student after = sut.searchStudent(id).orElseThrow();

    assertThat(after)
        .satisfies(student -> {
          assertThat(student.getId()).isEqualTo(before.getId());

          assertThat(student.getName())
              .isEqualTo(expected.getName())
              .isNotEqualTo(before.getName());

          assertThat(student.getAge())
              .isEqualTo(before.getAge());
        });
  }

  @Test
  void 受講生コース情報のcourseが更新できること() {
    String id = "1";

    StudentCourse before = sut.searchStudentCourseByStudentId(id).get(0);

    StudentCourse expected = new StudentCourse();
    expected.setId(before.getId());
    expected.setStudentId(before.getStudentId());
    expected.setCourseName("テストコース");
    expected.setCourseStartAt(before.getCourseStartAt());
    expected.setCourseEndAt(before.getCourseEndAt());

    sut.updateStudentCourse(expected);

    List<StudentCourse> actual = sut.searchStudentCourseByStudentId(id);

    assertThat(actual)
        .anySatisfy(studentCourse -> {
          assertThat(studentCourse.getId())
              .isEqualTo(before.getId());

          assertThat(studentCourse.getStudentId())
              .isEqualTo(before.getStudentId());

          assertThat(studentCourse.getCourseName())
              .isEqualTo(expected.getCourseName())
              .isNotEqualTo(before.getCourseName());
        });
  }

  @Test
  void 受講生コース申込状況のstatusが更新できること() {
    String id = "1";
    CourseApplication before = sut.searchCourseApplicationByStudentId(id).get(0);
    CourseApplication expected = new CourseApplication();
    expected.setId(before.getId());
    expected.setStudentId(before.getStudentId());
    expected.setCourseId(before.getCourseId());
    expected.setApplicationStatus(ApplicationStatus.FORMAL);

    sut.updateCourseApplication(expected);

    List<CourseApplication> actual = sut.searchCourseApplicationByStudentId(id);

    assertThat(actual).anySatisfy(courseApplication -> {
      assertThat(courseApplication.getId()).isEqualTo(before.getId());
      assertThat(courseApplication.getStudentId()).isEqualTo(before.getStudentId());
      assertThat(courseApplication.getCourseId()).isEqualTo(before.getCourseId());

      assertThat(courseApplication.getApplicationStatus())
          .isEqualTo(expected.getApplicationStatus())
          .isNotEqualTo(before.getApplicationStatus());
    });
  }
}