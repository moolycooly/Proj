package org.fintech.controllers.payload;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Schema(description = "Request to reset password")
@AllArgsConstructor
@NoArgsConstructor
public class ResetPasswordRequest {


    @Schema(example = "my_1secret1_password")
    @Size(min = 8, max = 255)
    @NotBlank
    private String password;

    @Schema(description = "Confirmation code", example = "0000")
    @Size(min = 4, max = 4)
    private String confirmationCode;
}
