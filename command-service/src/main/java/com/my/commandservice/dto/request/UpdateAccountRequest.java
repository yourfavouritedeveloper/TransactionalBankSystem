package com.my.commandservice.dto.request;

import com.my.commandservice.entity.enumeration.AccountStatus;
import com.my.commandservice.entity.enumeration.AccountType;
import com.my.commandservice.entity.enumeration.Currency;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateAccountRequest {

    private BigDecimal balance;

    private AccountStatus status;

    private AccountType type;

    private Currency currency;


}
