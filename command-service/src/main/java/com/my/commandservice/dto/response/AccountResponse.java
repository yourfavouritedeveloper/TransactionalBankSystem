package com.my.commandservice.dto.response;

import com.my.commandservice.entity.enumeration.AccountStatus;
import com.my.commandservice.entity.enumeration.AccountType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountResponse {


    private UUID id;

    private String iban;

    private BigDecimal balance;

    private AccountStatus status;

    private AccountType type;

    private UserResponse user;
}
