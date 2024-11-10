package org.fintech.controllers.payload;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Schema(description = "User registration")
@NoArgsConstructor
@AllArgsConstructor
public class SignUpRequest {

    @Schema(example = "Jon")
    @Size(min = 3, max = 50)
    @NotBlank
    private String username;

    @Schema(example = "jondoe@gmail.com")
    @Size(min = 5, max = 255)
    @NotBlank
    @Email
    private String email;

    @Schema(example = "my_1secret1_password")
    @Size(max = 255)
    private String password;
}