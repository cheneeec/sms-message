package com.gongsj.core;

import lombok.Data;

@Data
public class SmsRemainingNumberResponse {
    private boolean success;// 是否成功
    private String description;//描述
    private long number;//短信数

}
