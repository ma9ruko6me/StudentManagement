package raisetech.StudentManagement.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class StudentExceptionHandler {

  //テスト用例外
  @ExceptionHandler(TestException.class)
  public ResponseEntity<String> handleTestException(TestException ex) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
  }

  //業務ルール違反(ステータス遷移など)
  @ExceptionHandler(InvalidStatusTransitionException.class)
  public ResponseEntity<String> handleInvalidStatusTransition(InvalidStatusTransitionException e) {
    return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
  }

  //リソース未存在
  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<String> handleResourceNotFound(ResourceNotFoundException e) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
  }

  //バリデーションエラーなどの入力不正
  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException e) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
  }
}
