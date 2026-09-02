package raisetech.StudentManagement.exception;

/**
 * 指定したリソースが存在しない場合にスローされる例外です。
 */
public class ResourceNotFoundException extends RuntimeException {

  public ResourceNotFoundException(String message) {
    super(message);
  }
}
