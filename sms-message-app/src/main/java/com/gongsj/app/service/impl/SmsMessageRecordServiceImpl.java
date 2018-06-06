package com.gongsj.app.service.impl;


import com.gongsj.app.entity.SmsMessageRecord;
import com.gongsj.app.entity.SmsMessageUser;
import com.gongsj.app.repository.MessageRecordRepository;
import com.gongsj.app.service.SmsMessageRecordService;
import com.gongsj.app.service.SmsMessageUserService;

import com.gongsj.core.domain.MessageUser;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;


import java.util.*;


import static java.util.stream.Collectors.toSet;
import static org.springframework.util.Assert.*;

@Service
@Slf4j
@AllArgsConstructor
public class SmsMessageRecordServiceImpl implements SmsMessageRecordService {

    private final MessageRecordRepository messageRecordRepository;

    private final SmsMessageUserService messageUserService;

    private final MongoTemplate mongoTemplate;


    @Override
    public SmsMessageRecord get(String id) {
        hasText(id, "id is null or empty");
        SmsMessageRecord messageRecord = messageRecordRepository.findOne(id);
        MessageUser messageUser = messageRecord.getMessageUser();

        if (Objects.nonNull(messageUser)) {
            if (StringUtils.hasText(messageUser.getId())) {
                messageRecord.setMessageUser(messageUserService.get(messageUser.getId()));
            }
        }
        return messageRecord;
    }

    @Override
    public void remove(String id) {
        hasText(id, "id is null or empty");

        messageRecordRepository.delete(id);
    }

    @Override
    public void delete(Collection<String> ids) {
        notEmpty(ids, "collection('ids') is empty");
        Iterable<SmsMessageRecord> records = messageRecordRepository.findAll(ids);
        messageRecordRepository.delete(records);

    }

    @Override
    public Page<SmsMessageRecord> findByPage(Pageable pageRequest) {
        notNull(pageRequest, "pageRequest is null");

        Page<SmsMessageRecord> messageRecordPage = messageRecordRepository.findAll(pageRequest);
        List<SmsMessageRecord> messageRecords = messageRecordPage.getContent();

        List<SmsMessageUser> messageUsers = messageUserService.findAll(messageRecords.stream()
                .filter(m -> Objects.nonNull(m) && StringUtils.hasText(m.getId()))
                .map(messageRecord -> messageRecord.getMessageUser().getId())
                .collect(toSet()));

        messageRecords.stream()
                .filter(m -> Objects.nonNull(m) && StringUtils.hasText(m.getId())).forEach(messageRecord -> {
            String id = messageRecord.getMessageUser().getId();

            for (SmsMessageUser messageUser : messageUsers) {
                if (id.equals(messageUser.getId())) {
                    MessageUser temp = new MessageUser();
                    BeanUtils.copyProperties(messageUser, temp);
                    messageRecord.setMessageUser(temp);
                    break;
                }
            }
        });

        return messageRecordPage;
    }


    @Override
    public Page<SmsMessageRecord> findByPageAndSendTimeInterval(Date start, Date end, Pageable pageRequest) {
        notNull(pageRequest, "page is null");
        notNull(start, "the start date is null");
        notNull(end, "the end date is null");
        isTrue(end.after(start), "The start time can not be greater than the end time");

        return messageRecordRepository.findAllBySendTimeBetween(start, end, pageRequest);
    }

    @Override
    public List<Map> groupCountBySendTime(Date start, Date end, String groupBy) {
        notNull(start, "the start date is null");
        notNull(end, "the end date is null");
        isTrue(end.after(start), "The start time can not be greater than the end time");

        AggregationResults<Map> aggregationResults = mongoTemplate.aggregate(
                Aggregation.newAggregation(SmsMessageRecord.class, Aggregation.match(
                        Criteria.where("sendTime").gte(start).lt(end)
                ), Aggregation.group(groupBy).count().as("count")), SmsMessageRecord.class, Map.class);

        return aggregationResults.getMappedResults();
    }

   /* @Override
    public Map<String, Integer> countBySendTimeAndInterval(Date start, Date end, Integer number, ChronoUnit unit) {
        notNull(start, "the start date is null");
        notNull(end, "the end date is null");
        isTrue(end.after(start) || start.equals(end), "The start time can not be greater than the end time");

        if (unit != ChronoUnit.MONTHS && unit != ChronoUnit.DAYS) {
            throw new UnsupportedTemporalTypeException("Currently only supports months and days");
        }

        LocalDate startDate = dateToLocalDate(start);

        LocalDate endDate = dateToLocalDate(end);
        long difference = 1;

        if (unit == ChronoUnit.DAYS) {
            //两个时间的差值
            difference += DateUtils.computeDaysDifference(startDate, endDate);
     
        } else {
            //年份的差值
            difference += DateUtils.computeMonthsDifference(startDate, endDate);

        }

        int interval = (int) Math.floor(difference / number);

        Map<String, Integer> result = new LinkedHashMap<>();

        for (int i = 0; i < number; i++) {
            LocalDate startLocalDate = startDate.plus(i * interval, unit);
            //i + 1
            LocalDate endLocalDate = endDate.plus(-(number - i - 1) * interval, unit);

            result.put(
                    DateUtils.convertToString(startLocalDate, endLocalDate, unit),
                    messageRecordRepository.countBySendTimeBetween(
                            localDateToDate(startLocalDate),
                            //结算时间必须+1天
                            localDateToDate(endLocalDate.plusDays(1)))
            );

        }
        return result;
    }
*/

}
