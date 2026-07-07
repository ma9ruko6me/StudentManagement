package raisetech.StudentManagement.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import raisetech.StudentManagement.data.Student;
import raisetech.StudentManagement.domain.CourseDetail;
import raisetech.StudentManagement.domain.StudentDetail;
import raisetech.StudentManagement.dto.request.SearchCondition;
import raisetech.StudentManagement.exception.TestException;
import raisetech.StudentManagement.service.StudentService;

/**
 * 受講生情報の検索や登録、更新などを行うREST APIとして実行されるControllerです。
 */
@RestController
@Validated
public class StudentController {

  private StudentService service;

  @Autowired
  public StudentController(StudentService service) {
    this.service = service;
  }

  /**
   * 受講生詳細の一覧検索です。 全件検索を行うので、条件指定は行いません。
   *
   * @return　受講生詳細一覧（全件）
   */
  @Operation(summary = "受講生一覧検索", description = "受講生の一覧を検索します。")
  @GetMapping("/students")
  public List<StudentDetail> getStudentList() {return service.searchStudentList();}

  /**
   * 受講生詳細の検索です。 IDに紐づく任意の受講生情報を取得します。
   *
   * @param id 受講生ID
   * @return　受講生詳細
   */
  @Operation(summary = "受講生検索", description = "受講生を検索します。")
  @GetMapping("/students/{id}")
  public ResponseEntity<StudentDetail> getStudent(@PathVariable String id) {
    return ResponseEntity.ok(service.searchStudent(id));
  }

  /**
   * 受講生詳細の条件検索です。受講生・受講生コース・受講生コース申込状況を横断した検索条件に一致して受講生情報を取得します。
   *
   * @param searchCondition 受講生・受講生コース・受講生コース申込状況を横断した検索条件
   * @return　検索条件に一致した受講生詳細一覧
   */
  @Operation(summary = "受講生条件検索", description = "条件に一致した受講生を検索します。")
  @PostMapping("/students/search")
  public ResponseEntity<List<StudentDetail>> getStudentListByCondition(@RequestBody @Valid SearchCondition searchCondition) {
    return ResponseEntity.ok(service.searchStudentListByCondition(searchCondition));
  }

  /**
   * 受講生詳細の登録を行います。
   *
   * @param studentDetail 受講生詳細
   * @return　実行結果
   */
  @Operation(summary = "受講生登録", description = "受講生を登録します。")
  @PostMapping("/students/register")
  public ResponseEntity<StudentDetail> registerStudent(@RequestBody @Valid StudentDetail studentDetail) {
    StudentDetail responseStudentDetail = service.registerStudent(studentDetail);
    return ResponseEntity.status(HttpStatus.CREATED).body(responseStudentDetail);
  }

  /**
   * 受講生コース詳細の追加を行います。
   *
   * @param id 受講生ID
   * @param courseDetail 受講生詳細
   * @return　実行結果
   */
  @Operation(summary = "受講生コース詳細追加", description = "受講生コース詳細を追加します。")
  @PostMapping("/students/{id}/courses/add")
  public ResponseEntity<StudentDetail> addCourseDetail(@PathVariable String id, @RequestBody @Valid CourseDetail courseDetail) {
    StudentDetail responseStudentDetail = service.addCourseDetail(id, courseDetail);
    return ResponseEntity.ok(responseStudentDetail);
  }

  /**
   * 受講生の更新を行います。 キャンセルフラグの更新もここで行います。（論理削除）
   *
   * @param student 受講生
   * @return　実行結果
   */
  @Operation(summary = "受講生更新", description = "受講生を更新します。")
  @PutMapping("/students/update")
  public ResponseEntity<String> updateStudent(@RequestBody @Valid Student student) {
    service.updateStudent(student);
    return ResponseEntity.ok("更新処理が成功しました。");
  }

  /**
   * 受講生コース詳細の更新を行います。
   *
   * @param courseDetail コース詳細
   * @return　実行結果
   */
  @Operation(summary = "コース詳細更新", description = "コース詳細を更新します。")
  @PutMapping("/courses/update")
  public ResponseEntity<String> updateCourseDetail(@RequestBody @Valid CourseDetail courseDetail) {
    service.updateCourseDetail(courseDetail);
    return ResponseEntity.ok("コース詳細を更新しました。");
  }

  /**
   * テスト用に例外を発生させるAPIです。
   * グローバル例外ハンドリングやエラーレスポンスの動作確認を目的としています。
   *
   * @throws TestException 常にスローされる例外
   */
  @Operation(summary = "例外発生テスト", description = "例外を意図的に発生させ、エラーハンドリングを確認します。")
  @GetMapping("/testException")
  public List<StudentDetail> testException() throws TestException {
    throw new TestException("これは例外を発生させるAPIです。");
  }
}
