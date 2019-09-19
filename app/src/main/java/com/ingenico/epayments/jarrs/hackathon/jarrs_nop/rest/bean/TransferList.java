package com.ingenico.epayments.jarrs.hackathon.jarrs_nop.rest.bean;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransferList {

    private List<Transfer> transferList;
}
