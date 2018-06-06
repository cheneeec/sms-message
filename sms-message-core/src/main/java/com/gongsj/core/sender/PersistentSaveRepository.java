package com.gongsj.core.sender;


import com.gongsj.core.domain.MessageRecord;

@FunctionalInterface
public interface PersistentSaveRepository  {
    void save(MessageRecord messageRecord);
}
