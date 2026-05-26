package raisetech.StudentManagement.exception;

public class InvalidStatusTransitionException extends RuntimeException {

  public InvalidStatusTransitionException(String message) {
    super(message);
  }
}
