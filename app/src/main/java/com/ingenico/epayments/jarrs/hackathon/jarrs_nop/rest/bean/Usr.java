package com.ingenico.epayments.jarrs.hackathon.jarrs_nop.rest.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Usr {

    private String userId;
    private String firstName;
    private String lastName;
    private String email;
    private String balance;
    private String currency;
}
