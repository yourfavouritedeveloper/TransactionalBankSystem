package com.my.commandservice.dto.request;

import com.my.commandservice.entity.enumeration.Role;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateUserRequest {

    private String fullName;

    private String username;

    private String password;

    @Email
    private String email;

}
