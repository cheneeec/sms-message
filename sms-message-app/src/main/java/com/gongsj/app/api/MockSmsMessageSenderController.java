package com.gongsj.app.api;

import com.gongsj.app.exception.UnSupportPlatformException;
import com.gongsj.core.SmsSimpleSendResponse;
import com.gongsj.core.exception.PlatArrearsException;
import com.gongsj.core.sender.MockSmsMessageSender;
import com.gongsj.core.sender.PersistentSaveRepository;
import com.gongsj.core.sender.SmsMessageSender;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/v1/api/sms/mock")
public class MockSmsMessageSenderController {

    private final Map<String, SmsMessageSender> messageSenders;

    public MockSmsMessageSenderController(PersistentSaveRepository persistentSaveRepository) {
        this.messageSenders = new HashMap<>(2);
        messageSenders.put("yd", new MockSmsMessageSender(persistentSaveRepository, "中国移动"));
        messageSenders.put("lt", new MockSmsMessageSender(persistentSaveRepository, "联通"));
    }

    @PostMapping("/{sysNo}")
    @CrossOrigin("*")
    public SmsSimpleSendResponse sendMessage(@PathVariable String sysNo,
                                             String templateId,
                                             String phoneNumber,
                                             String content,
                                             @RequestParam(defaultValue = "lt") String platform) throws PlatArrearsException {


        return Optional.ofNullable(messageSenders.get(platform))
                .orElseThrow(() -> new UnSupportPlatformException("nonsupport platform:" + platform))
                .sendMessage(sysNo, phoneNumber, content, templateId);
    }


}
