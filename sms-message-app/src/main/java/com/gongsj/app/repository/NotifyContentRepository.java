package com.gongsj.app.repository;

import com.gongsj.app.entity.NotifyContent;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface NotifyContentRepository extends MongoRepository<NotifyContent,String>{
   String NOTIFY_CONTENT_ID = "1";
}
