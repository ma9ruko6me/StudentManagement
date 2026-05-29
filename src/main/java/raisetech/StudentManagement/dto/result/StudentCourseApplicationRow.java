package raisetech.StudentManagement.dto.result;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import raisetech.StudentManagement.enums.ApplicationStatus;

@Schema(description = "受講生・受講生コース情報・受講生コース申込状況を結合した検索結果（フラット構造）")
@Getter
@Setter
public class StudentCourseApplicationRow {

  private String studentId;
  private String name;
  private String furigana;
  private String nickname;
  private String email;
  private String area;
  private int age;
  private String gender;
  private String remark;
  private boolean isDeleted;

  private String courseId;
  private String courseName;
  private LocalDateTime courseStartAt;
  private LocalDateTime courseEndAt;

  private String applicationId;
  private ApplicationStatus applicationStatus;
}
