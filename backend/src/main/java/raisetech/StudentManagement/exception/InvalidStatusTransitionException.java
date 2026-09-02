package raisetech.StudentManagement.exception;

/**
 * 現在のステータスから遷移不可能なステータスへ変更しようとした場合にスローされる例外です。
 */
public class InvalidStatusTransitionException extends RuntimeException {

  public InvalidStatusTransitionException(String message) {
    super(message);
  }
}
