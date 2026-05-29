package raisetech.StudentManagement.exception;

/**
 * テスト用に意図的にスローさせる例外です。
 */
public class TestException extends Exception {

  public TestException() {
    super();
  }

  public TestException(String message) {
    super(message);
  }

  public TestException(String message, Throwable cause) {
    super(message, cause);
  }

  public TestException(Throwable cause) {
    super(cause);
  }
}
