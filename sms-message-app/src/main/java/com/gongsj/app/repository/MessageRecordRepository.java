package com.gongsj.app.repository;

import com.gongsj.app.entity.SmsMessageRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Date;

public interface MessageRecordRepository extends MongoRepository<SmsMessageRecord, String> {
    Page<SmsMessageRecord> findAllBySendTimeBetween(Date start, Date end, Pageable pageable);

    Integer countBySendTimeBetween(Date start, Date end);


}
