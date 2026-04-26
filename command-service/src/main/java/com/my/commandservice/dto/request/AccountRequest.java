package com.my.commandservice.dto.request;

import com.my.commandservice.entity.enumeration.AccountType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountRequest {

    @NotNull(message = "Account Type is required")
    private AccountType type;

    @NotNull(message = "User id is required")
    private UUID userId;

}
