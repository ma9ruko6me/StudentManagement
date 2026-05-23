package raisetech.StudentManagement.enums;

public enum ApplicationStatus {
  TEMP ("仮申込"),
  FORMAL("本申込"),
  IN_PROGRESS("受講中"),
  COMPLETED("受講終了");

  private final String label;

  ApplicationStatus(String label) {
    this.label = label;
  }

  public String getLabel() {
    return label;
  }
}
