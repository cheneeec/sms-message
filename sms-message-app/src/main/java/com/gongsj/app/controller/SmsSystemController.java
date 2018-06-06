package com.gongsj.app.controller;

import com.gongsj.app.entity.NotifyContent;
import com.gongsj.app.repository.NotifyContentRepository;
import com.gongsj.app.sender.YdSmsMessageSender;
import com.gongsj.core.SmsRemainingNumberResponse;
import com.gongsj.core.sender.manager.MessageSenderManager;
import lombok.AllArgsConstructor;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.web.bind.annotation.*;


import static com.gongsj.app.repository.NotifyContentRepository.NOTIFY_CONTENT_ID;

@RestController
@RequestMapping("/api/system")
@AllArgsConstructor
public class SmsSystemController {

    private final MessageSenderManager messageSenderManager;

    private final NotifyContentRepository notifyContentRepository;


    @GetMapping("/current/platform")
    public String getCurrentPlatform() {
        return messageSenderManager.getPlatform();
    }

    //PUT会报错
    @PostMapping("/platform/refresh")
    public void refresh() {
        messageSenderManager.refresh();
    }


    @GetMapping("/remaining/number")
    public SmsRemainingNumberResponse getRemainingNumber(String platform) {
        return messageSenderManager.getRemainingNumber(platform);
    }


    @PutMapping("/current/sender")
    public boolean switchPlatform(boolean invalidCurrentPlatform) {
        return messageSenderManager.switchPlatform(invalidCurrentPlatform);
    }

    @PostMapping("/yd/remaining/{number}")
    public void setYdPlatformRemaining(@PathVariable Long number) {
        (((YdSmsMessageSender) messageSenderManager.getPlatform("yd"))).setRemaining(number);
    }

    //PUT会报错
    @PostMapping("/notify")
    public void setNotifyContent(@NotBlank @RequestBody String content) {
        notifyContentRepository.save(new NotifyContent(NOTIFY_CONTENT_ID, content));
    }

    @GetMapping("/notify")
    public String getNotifyContent() {
        return notifyContentRepository.findOne(NOTIFY_CONTENT_ID).getContent();
    }
}
