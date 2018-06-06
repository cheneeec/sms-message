package com.gongsj.app.controller;


import com.gongsj.app.entity.SmsMessageRecord;
import com.gongsj.app.service.SmsMessageRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/records")
public class SmsMessageRecordController {

    @Autowired
    private SmsMessageRecordService messageRecordService;

    @GetMapping("/{id:[a-z|A-Z|0-9]+}")
    public SmsMessageRecord findById(@PathVariable String id) {


        return messageRecordService.get(id);
    }

    @DeleteMapping("/{id:[a-z|A-Z|0-9]+}")
    public void deleteById(@PathVariable String id) {
        messageRecordService.remove(id);

    }

    @DeleteMapping
    public void deleteByIds(List<String> ids) {
        messageRecordService.delete(ids);
    }

    @GetMapping
    public Page<SmsMessageRecord> findByPage(Pageable pageRequest) {

        return messageRecordService.findByPage(pageRequest);
    }


    @GetMapping("/sendTime/{start:\\d+}/to/{end:\\d+}")
    public Page<SmsMessageRecord> findByPageAndSendTimeBetween(@PathVariable Date start,
                                                               @PathVariable Date end,
                                                               Pageable pageRequest) {

        return messageRecordService.findByPageAndSendTimeInterval(start, end, pageRequest);
    }


    @GetMapping("/group/count/sendTime/{start:\\d+}/to/{end:\\d+}")
    public List<Map> countBySendTimeBetween(@PathVariable Date start, @PathVariable Date end,
                                            @RequestParam(defaultValue = "systemName") String groupBy) {

        return messageRecordService.groupCountBySendTime(start, end, groupBy);
    }

   /* @GetMapping("/count/sendTime/{start:\\d+}/to/{end:\\d+}")
    public Map<String, Integer> countBySendTimeAndInterval(@PathVariable Date start,
                                                           @PathVariable Date end,
                                                           @RequestParam(defaultValue = "3") int number, ChronoUnit unit) {
        return messageRecordService.countBySendTimeAndInterval(start, end, number, unit);
    }*/

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(Date.class,
                new CustomDateEditor(new SimpleDateFormat("yyyyMMdd"), true));
    }
}
