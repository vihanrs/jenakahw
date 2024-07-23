package com.jenakahw.domain;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReportPurchaseOrder {
private String supplierFirstName;
private String company;
private String total;
private String count;
}
