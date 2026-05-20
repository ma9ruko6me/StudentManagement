package raisetech.StudentManagement.data;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Schema(description = "受講生")
@Getter
@Setter
public class Student {

  @Pattern(regexp = "^[0-9]+$")
  private String id;

  @NotBlank
  private String name;

  @NotBlank
  private String hurigana;

  @NotBlank
  private String nickname;

  @NotBlank
  @Email(message = "正しいメールアドレスを入力してください。")
  private String email;

  @NotBlank
  private String area;

  private int age;

  @NotBlank
  private String gender;

  private String remark;
  private boolean isDeleted;
}


