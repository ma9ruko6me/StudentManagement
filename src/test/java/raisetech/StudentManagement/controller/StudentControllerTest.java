package raisetech.StudentManagement.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import raisetech.StudentManagement.data.Student;
import raisetech.StudentManagement.domain.StudentDetail;
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
  void 受講生詳細の一覧検索が実行できて空のリストが返ってくること () throws Exception {
    mockMvc.perform(get("/studentList"))
        .andExpect(status().isOk())
        .andExpect(content().json("[]"));

    verify(service, times(1)).searchStudentlList();
  }

  @Test
  void 受講生詳細の一覧検索が実行できてデータのあるリストが返ってくること () throws Exception {
    Student student = createStudent();
    StudentDetail studentDetail = new StudentDetail();
    studentDetail.setStudent(student);
    List<StudentDetail> studentDetailList = List.of(studentDetail);
    when(service.searchStudentlList()).thenReturn(studentDetailList);

    mockMvc.perform(get("/studentList"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(1))
        .andExpect(jsonPath("$[0].student.id").value(student.getId()));
  }

  @Test
  void 受講生詳細の検索が実行できてIDに対応したリストが返ってくること () throws Exception {
    Student student = createStudent();
    StudentDetail studentDetail = new StudentDetail(student,new ArrayList<>());
    String id = student.getId();
    when(service.searchStudent(id)).thenReturn(studentDetail);

    mockMvc.perform(get("/student/{id}", id))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.student.id").value(id))
        .andExpect(jsonPath("$.student.name").value(student.getName()));

    verify(service, times(1)).searchStudent(id);
  }

  @Test
  void 受講生詳細の検索で存在しないIDを検索したときにNotFoundが返ってくること()  throws Exception {
    String id = "999";
    when(service.searchStudent(id)).thenThrow(new RuntimeException("Student not found"));

    mockMvc.perform(get("/student/{id}", id))
        .andExpect(status().isNotFound());
  }

  @Test
  void 受講生詳細の登録が実行できて空で返ってくること () throws Exception {
    mockMvc.perform(post("/registerStudent").contentType(MediaType.APPLICATION_JSON).content(
            """
                {
                    "student": {
                        "name": "久保建英",
                        "hurigana": "くぼたけふさ",
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
                             "course": "デザインコース"
                           }
                         }
                    ]
                }
            """
        ))
        .andExpect(status().isOk());

    verify(service, times(1)).registerStudent(any());
  }

  @Test
  void 受講生詳細の更新が実行できて空で返ってくること () throws Exception {
    mockMvc.perform(put("/updateStudent").contentType(MediaType.APPLICATION_JSON).content(
            """
                    {
                       "student": {
                         "id": "2",
                         "name": "久保建英",
                         "hurigana": "くぼたけふさ",
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
                             "id": "6",
                             "studentId": "2",
                             "course": "デザインコース",
                             "startDate": "2025-10-07",
                             "endDate": "2026-06-23"
                           },
                           "courseApplication": {
                             "id": "6",
                             "studentId": "6",
                             "courseId": "6",
                             "status": "本申込"
                           }
                         }
                       ]
                     }
                """
        ))
        .andExpect(status().isOk())
        .andExpect(content().string("更新処理が成功しました。"));

    verify(service, times(1)).updateStudent(any());
  }

  @Test
  void 受講生詳細の例外APIが実行できてステータスが400で帰ってくること () throws Exception {
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
  void 受講生詳細の受講生でIDに数字が入力された時に入力チェックに異常が発生しないこと() throws Exception {
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
  void 受講生詳細の受講生でEmailに適切な入力された時に入力チェックに異常が発生しないこと () throws Exception {
    Student student = createStudent();

    Set<ConstraintViolation<Student>> violations = validator.validate(student);

    assertThat(violations.size()).isEqualTo(0);
  }

  @Test
  void 受講生詳細の受講生でEmailにメールアドレス以外が入力された時に入力チェックにかかること () throws Exception {
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
    student.setHurigana("てすとしたろう");
    student.setNickname("テスト大好きくん");
    student.setEmail("test@example.com");
    student.setArea("テスト県");
    student.setGender("男性");
    student.setDeleted(false);
    student.setRemark("テスト大好きです。");
    return student;
  }
}