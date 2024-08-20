package com.jenakahw.email;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailDetails {
private String sendTo;
private String msgBody;
private String subject;
private String attachment;
}
