package com.gongsj.core.property;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 封装各个平台发送消息所需要的基本信息。
 */
@Data
@NoArgsConstructor
public class SmsPlatformProperties {
    private String sendApiAddress;//发送消息的API地址
    private String remainingNumberApiAddress;//获得剩余短信数的API地址
    private Map<String, String> properties = new HashMap<>(); //提交的可变参数（封装用户名，密码，企业等信息）
    private String platform = "中国移动";  //平台 在SmsMessageSender#getPlatform()直接获得该值
    private String httpHeaders = "default"; //提交时的请求头（默认为空）
    private String mobileProperty = "mobile"; //手机号的提交属性
    private String contentProperty = "content";//内容的提交属性
    private String templateIdProperty = "template";//模板Id的提交属性（目前都不支持，暂做保留）
    private SendResponseProperty sendResponse = new SendResponseProperty();//发送短信的响应数据
    private RemainingNumberResponseProperty remainingNumberResponse = new RemainingNumberResponseProperty();


    public SmsPlatformProperties(String platform) {
        this.platform = platform;
    }

    @Data
    public static class SendResponseProperty {
        private String stateProperty = "stat";//状态码的属性字段
        private String stateSuccessValue = "100";//状态码的字段的成功值
        private List<String> stateArrearsValue;//状态码的字段的欠费值
        private String messageProperty = "message";//消息字段
        private String acceptCharset = "ISO8859-1";//从短信平台接收到的内容的字符编码集
        private String targetCharset = "GBK";//需要转化的字符编码集
        private String type = "json";//响应结果的类型
    }

    @Data
    public static class RemainingNumberResponseProperty {
        private String stateProperty;//状态属性值
        private String stateSuccessValue;//状态成功值
        private String descriptionProperty;//描述
        private String numberProperty;//剩余条数
    }

}
