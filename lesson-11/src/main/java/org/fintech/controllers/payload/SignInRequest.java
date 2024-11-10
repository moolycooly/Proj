package org.fintech.controllers.payload;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Schema(description = "Auth request")
@NoArgsConstructor
@AllArgsConstructor
public class SignInRequest {

    @Schema(example = "Jon")
    @Size(min = 3, max = 50)
    @NotBlank
    private String username;

    @Schema(example = "my_1secret1_password")
    @Size(min = 8, max = 255)
    @NotBlank
    private String password;

    @Schema(example = "true")
    private Boolean rememberMe = false;
}