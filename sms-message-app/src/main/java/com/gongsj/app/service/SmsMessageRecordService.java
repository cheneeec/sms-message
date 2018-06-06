package com.gongsj.app.service;


import com.gongsj.app.entity.SmsMessageRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface SmsMessageRecordService {

    SmsMessageRecord get(String id);

    void remove(String id);

    void delete(Collection<String> ids);

    Page<SmsMessageRecord> findByPage(Pageable pageRequest);


    Page<SmsMessageRecord> findByPageAndSendTimeInterval(Date start, Date end, Pageable pageRequest);

    List<Map> groupCountBySendTime(Date start, Date end, String groupBy);

    /**
     * 从<code>start</code>指定的时间到<code>end</code>的时间进行统计，且按照<code>numberProperty</code>将其分成指定的段数，精确到每天。
     *
     * @param start  统计的开始时间。
     * @param end    统计的结束时间。
     * @param number 将时间间隔分成指定段数进行统计。
     * @param unit 单位。单位：天，月，年等等。
     * @return 键：分割后的日期段。<br/>
     * 值：该时间段内发送的短信条数。
     */
//    Map<String, Integer> countBySendTimeAndInterval(Date start, Date end, Integer number,ChronoUnit unit);


}
