package com.gongsj.app.api;


import com.gongsj.core.SmsSimpleSendResponse;

import com.gongsj.core.exception.PlatArrearsException;
import com.gongsj.core.sender.manager.MessageSenderManager;
import lombok.AllArgsConstructor;
import lombok.Data;


import lombok.Getter;
import lombok.Setter;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/v1/api/sms")
@Data
@AllArgsConstructor
public class SmsMessageSenderController {

    private MessageSenderManager messageSenderManager;

    /**
     * 发送短信
     *
     * @param sysNo       调用方的唯一标识。
     * @param templateId  这里需要和相关平台的<code>templateId</code>保持一致。
     * @param phoneNumber 短信的接收方，多个号码使用<strong>英文逗号,</strong>分割。
     * @param content     需要发送的内容。
     * @return {@link SmsSimpleSendResponse} 对消息极简的封装，只包含成功与否和错误提示消息（如果有的话）。
     */
    @PostMapping("/{sysNo}")
    @CrossOrigin("*")
    public SmsSimpleSendResponse sendMessage(@PathVariable String sysNo,
                                             String templateId,
                                             String phoneNumber,
                                             String content) throws PlatArrearsException {
        return messageSenderManager.sendMessage(sysNo, phoneNumber, content, templateId);
    }

    @PostMapping()
    @CrossOrigin("*")
    public SmsSimpleSendResponse sendMessage(@RequestBody SendMessageObject sendMessageObject) throws PlatArrearsException {
        return messageSenderManager.sendMessage(sendMessageObject.getSysNo(), sendMessageObject.getPhoneNumber(), sendMessageObject.getContent(), sendMessageObject.getTemplateId());
    }


    @Getter
    @Setter
    public static class SendMessageObject {
        private String sysNo;
        private String content;
        private String phoneNumber;
        private String templateId;
    }

}
