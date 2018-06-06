package com.gongsj.core.parse;

import com.gongsj.core.SmsRemainingNumberResponse;
import com.gongsj.core.SmsSimpleSendResponse;
import com.gongsj.core.exception.PlatArrearsException;
import com.gongsj.core.property.SmsPlatformProperties;

import java.util.List;


public interface SmsHttpResponseParser {

    SmsSimpleSendResponse parse(String httpResponseStr, SmsPlatformProperties.SendResponseProperty SendResponseProperty) throws PlatArrearsException;

    SmsRemainingNumberResponse parse(String httpResponseStr, SmsPlatformProperties.RemainingNumberResponseProperty remainingNumberResponseProperty);


}
