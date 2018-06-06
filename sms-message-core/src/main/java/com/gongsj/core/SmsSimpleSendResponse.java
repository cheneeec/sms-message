package com.gongsj.core;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SmsSimpleSendResponse {
    private Boolean success;
    private String message;
}
