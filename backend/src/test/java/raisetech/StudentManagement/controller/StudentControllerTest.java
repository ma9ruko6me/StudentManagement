package raisetech.StudentManagement.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import jakarta.annotation.Nonnull;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import raisetech.StudentManagement.data.Student;
import raisetech.StudentManagement.domain.CourseDetail;
import raisetech.StudentManagement.domain.StudentDetail;
import raisetech.StudentManagement.enums.SearchType;
import raisetech.StudentManagement.enums.SortKey;
import raisetech.StudentManagement.enums.SortOrder;
import raisetech.StudentManagement.exception.InvalidStatusTransitionException;
import raisetech.StudentManagement.exception.ResourceNotFoundException;
import raisetech.StudentManagement.service.StudentService;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

@WebMvcTest(StudentController.class)
class StudentControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private StudentService service;

  private Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

  @Test
  void 受講生詳細の一覧検索が実行できて空のリストが返ってくること() throws Exception {
    mockMvc.perform(get("/students"))
        .andExpect(status().isOk())
        .andExpect(content().json("[]"));

    verify(service, times(1)).searchStudentList();
  }

  @Test
  void 受講生詳細の一覧検索が実行できてデータのあるリストが返ってくること() throws Exception {
    Student student = createStudent();
    StudentDetail studentDetail = new StudentDetail();
    studentDetail.setStudent(student);
    List<StudentDetail> studentDetailList = List.of(studentDetail);
    when(service.searchStudentList()).thenReturn(studentDetailList);

    mockMvc.perform(get("/students"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(1))
        .andExpect(jsonPath("$[0].student.id").value(student.getId()));
  }

  @Test
  void 受講生詳細の検索が実行できてIDに対応したリストが返ってくること() throws Exception {
    Student student = createStudent();
    StudentDetail studentDetail = new StudentDetail(student, new ArrayList<>());
    String id = student.getId();
    when(service.searchStudent(id)).thenReturn(studentDetail);

    mockMvc.perform(get("/students/{id}", id))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.student.id").value(id))
        .andExpect(jsonPath("$.student.name").value(student.getName()));

    verify(service, times(1)).searchStudent(id);
  }

  @Test
  void 受講生詳細の検索で存在しないIDを検索したときに404が返ってくること() throws Exception {
    String id = "999";
    when(service.searchStudent(id)).thenThrow(new ResourceNotFoundException("Student not found"));

    mockMvc.perform(get("/students/{id}", id))
        .andExpect(status().isNotFound());
  }

  @Test
  void 受講生の条件検索が実行できて検索条件が渡されること() throws Exception {
    mockMvc.perform(post("/students/search").contentType(MediaType.APPLICATION_JSON)
            .content("""
                  {
                    "studentSearchCondition":{"keyword":"テスト"},
                    "sortKey":"AGE",
                    "sortOrder":"DESC",
                    "searchType":"AND"
                  }
                """))
        .andExpect(status().isOk());

    verify(service,times(1)).searchStudentListByCondition(argThat(searchCondition ->
            searchCondition.getStudentSearchCondition() != null
            && "テスト".equals(searchCondition.getStudentSearchCondition().getKeyword())

            && searchCondition.getSortKey() == SortKey.AGE
            && searchCondition.getSortOrder() == SortOrder.DESC
            && searchCondition.getSearchType() == SearchType.AND

        ));
  }

  @Test
  void 受講生の条件検索で検索条件が一部未指定でも実行できること() throws Exception {
    mockMvc.perform(post("/students/search").contentType(MediaType.APPLICATION_JSON)
            .content("""
                  {
                    "studentSearchCondition":{"keyword":"テスト"},
                    "searchType":"OR"
                  }
                """))
        .andExpect(status().isOk());

    verify(service,times(1)).searchStudentListByCondition(argThat(searchCondition ->
        searchCondition.getStudentSearchCondition() != null
            && "テスト".equals(searchCondition.getStudentSearchCondition().getKeyword())

            && searchCondition.getSortKey() == null
            && searchCondition.getSortOrder() == null
            && searchCondition.getSearchType() == SearchType.OR

    ));
  }

  @Test
  void 受講生の条件検索で検索条件が設定されなかった時に400が返ってくる() throws Exception {
    doThrow(new IllegalArgumentException()).when(service).searchStudentListByCondition(any());

    mockMvc.perform(post("/students/search").contentType(MediaType.APPLICATION_JSON).content(""))
        .andExpect(status().isBadRequest());
  }

  @Test
  void 受講生詳細の登録が実行できること() throws Exception {
    when(service.registerStudent(any())).thenReturn(new StudentDetail());

    mockMvc.perform(post("/students/register").contentType(MediaType.APPLICATION_JSON).content(
            """
                    {
                        "student": {
                            "name": "久保建英",
                            "furigana": "くぼたけふさ",
                            "nickname": "タケ",
                            "email": "take.kubo@example.com",
                            "area": "神奈川県",
                            "age": 23,
                            "gender": "男性",
                            "remark": ""
                        },
                        "courseDetailList": [
                             {
                               "studentCourse": {
                                 "courseName": "デザインコース"
                               }
                             }
                        ]
                    }
                """
        ))
        .andExpect(status().isCreated());

    ArgumentCaptor<StudentDetail> studentDetailArgumentCaptor = ArgumentCaptor.forClass(StudentDetail.class);

    verify(service, times(1)).registerStudent(studentDetailArgumentCaptor.capture());

    assertThat(studentDetailArgumentCaptor.getValue().getStudent())
        .satisfies(student -> {
          assertThat(student.getName()).isEqualTo("久保建英");
          assertThat(student.getEmail()).isEqualTo("take.kubo@example.com");
          assertThat(student.getAge()).isEqualTo(23);
        });

    assertThat(studentDetailArgumentCaptor.getValue().getCourseDetailList())
        .hasSize(1)
        .first()
        .satisfies(courseDetail -> {
          assertThat(courseDetail.getStudentCourse().getCourseName()).isEqualTo("デザインコース");
        });
  }

  @Test
  void 受講生コース詳細の追加が実行できること() throws Exception {
    when(service.addCourseDetail(any(),any())).thenReturn(new StudentDetail());

    mockMvc.perform(post("/students/999/courses/add")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {"studentCourse": {"courseName": "デザインコース"}}
                """))
        .andExpect(status().isOk());

    ArgumentCaptor<String> idCaptor = ArgumentCaptor.forClass(String.class);
    ArgumentCaptor<CourseDetail> courseDetailCaptor = ArgumentCaptor.forClass(CourseDetail.class);

    verify(service, times(1)).addCourseDetail(idCaptor.capture(),courseDetailCaptor.capture());

    assertThat(idCaptor.getValue()).isEqualTo("999");
    assertThat(courseDetailCaptor.getValue().getStudentCourse().getCourseName()).isEqualTo("デザインコース");
  }

  @Test
  void 存在しない受講生IDに紐づく受講生コース詳細の追加で404が返ってくること() throws Exception {
    doThrow(new ResourceNotFoundException("Student not found"))
        .when(service).addCourseDetail(eq("999"),any(CourseDetail.class));

    mockMvc.perform(post("/students/999/courses/add").contentType(MediaType.APPLICATION_JSON)
            .content("""
                {"studentCourse": {"courseName": "デザインコース"}}
                """))
        .andExpect(status().isNotFound());
  }

  @Test
  void 受講生の更新が実行できること() throws Exception {
    mockMvc.perform(put("/students/update").contentType(MediaType.APPLICATION_JSON).content(
            """
                    {
                         "id": "2",
                         "name": "久保建英",
                         "furigana": "くぼたけふさ",
                         "nickname": "タケ",
                         "email": "take.kubo@example.com",
                         "area": "神奈川県",
                         "age": 23,
                         "gender": "男性",
                         "remark": ""
                     }
                """
        ))
        .andExpect(status().isOk())
        .andExpect(content().string("更新処理が成功しました。"));

    verify(service, times(1)).updateStudent(any());
  }

  @Test
  void 存在しない受講生の更新で404が返ってくること() throws Exception {
    doThrow(new ResourceNotFoundException("Student not found")).when(service).updateStudent(any());

    mockMvc.perform(put("/students/update").contentType(MediaType.APPLICATION_JSON).content("""
              {
                "id": "2",
                "name": "久保建英",
                "furigana": "くぼたけふさ",
                "nickname": "タケ",
                "email": "take.kubo@example.com",
                "area": "神奈川県",
                "age": 23,
                "gender": "男性",
                "remark": ""
              }
            """))
        .andExpect(status().isNotFound());
  }

  @Test
  void 受講生コース詳細の更新が実行できること() throws Exception {
    mockMvc.perform(put("/courses/update").contentType(MediaType.APPLICATION_JSON)
            .content("""
                  {"studentCourse": {
                  "id": "6",
                  "studentId": "2",
                  "courseName": "デザインコース"
                },
                "courseApplication": {
                  "id": "6",
                  "studentId": "2",
                  "courseId": "6",
                  "applicationStatus": "FORMAL"
                }}
                """))
        .andExpect(status().isOk())
        .andExpect(content().string("コース詳細を更新しました。"));

    verify(service, times(1)).updateCourseDetail(any());
  }

  @Test
  void 存在しない受講生コースの更新で404が返ってくること() throws Exception {
    doThrow(new ResourceNotFoundException("Course not found")).when(service).updateCourseDetail(any());

    mockMvc.perform(put("/courses/update").contentType(MediaType.APPLICATION_JSON)
            .content("""
              {
                 "studentCourse": {
                    "id": "6",
                    "courseName": "デザインコース"
                 },
                 "courseApplication": {
                    "id": "6",
                    "applicationStatus": "FORMAL"
                 }
              }
            """))
        .andExpect(status().isNotFound());
  }

  @Test
  void 不正なステータス遷移で409が返ってくること() throws Exception {
    doThrow(new InvalidStatusTransitionException("Invalid status transition")).when(service).updateCourseDetail(any());

    mockMvc.perform(put("/courses/update").contentType(MediaType.APPLICATION_JSON).content("""
              {
                 "studentCourse": {
                    "id": "6",
                    "courseName": "デザインコース"
                 },
                 "courseApplication": {
                    "id": "6",
                    "applicationStatus": "FORMAL"
                 }
              }
            """))
        .andDo(print())
        .andExpect(status().isConflict());
  }

  @Test
  void 受講生詳細の例外APIが実行できてステータスが400で帰ってくること() throws Exception {
    mockMvc.perform(get("/testException"))
        .andExpect(status().is4xxClientError())
        .andExpect(content().string("これは例外を発生させるAPIです。"));
  }

  @Test
  void 受講生詳細の受講生で名前が正しく入力された時に入力チェックに異常が発生しないこと() throws Exception {
    Student student = createStudent();

    Set<ConstraintViolation<Student>> violations = validator.validate(student);

    assertThat(violations.size()).isEqualTo(0);
  }

  @Test
  void 受講生詳細の受講生で名前が入力されなかった時に入力チェックにかかること() throws Exception {
    Student student = createStudent();
    student.setName("");

    Set<ConstraintViolation<Student>> violations = validator.validate(student);

    assertThat(violations.size()).isEqualTo(1);
  }

  @Test
  void 受講生詳細の受講生でIDに数字が入力された時に入力チェックに異常が発生しないこと()
      throws Exception {
    Student student = createStudent();

    Set<ConstraintViolation<Student>> violations = validator.validate(student);

    assertThat(violations.size()).isEqualTo(0);
  }

  @Test
  void 受講生詳細の受講生でIDに数字以外が入力された時に入力チェックにかかること() throws Exception {
    Student student = createStudent();
    student.setId("テスト");

    Set<ConstraintViolation<Student>> violations = validator.validate(student);

    assertThat(violations.size()).isEqualTo(1);
  }

  @Test
  void 受講生詳細の受講生でEmailに適切な入力された時に入力チェックに異常が発生しないこと()
      throws Exception {
    Student student = createStudent();

    Set<ConstraintViolation<Student>> violations = validator.validate(student);

    assertThat(violations.size()).isEqualTo(0);
  }

  @Test
  void 受講生詳細の受講生でEmailにメールアドレス以外が入力された時に入力チェックにかかること()
      throws Exception {
    Student student = createStudent();
    student.setEmail("testexample.com");

    Set<ConstraintViolation<Student>> violations = validator.validate(student);

    assertThat(violations.size()).isEqualTo(1);
    assertThat(violations).extracting("message")
        .containsOnly("正しいメールアドレスを入力してください。");
  }

  @Nonnull
  private static Student createStudent() {
    Student student = new Student();
    student.setId("1");
    student.setName("テスト四太郎");
    student.setFurigana("てすとしたろう");
    student.setNickname("テスト大好きくん");
    student.setEmail("test@example.com");
    student.setArea("テスト県");
    student.setGender("男性");
    student.setDeleted(false);
    student.setRemark("テスト大好きです。");
    return student;
  }
}