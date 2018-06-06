package com.gongsj.core.parse;

import com.gongsj.core.SmsRemainingNumberResponse;
import com.gongsj.core.SmsSimpleSendResponse;
import com.gongsj.core.exception.PlatArrearsException;
import com.gongsj.core.property.SmsPlatformProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class ParamsHttpResponseParser implements SmsHttpResponseParser {

    @Override
    public SmsSimpleSendResponse parse(String httpResponseStr, SmsPlatformProperties.SendResponseProperty sendResponseProperty) throws PlatArrearsException {
        log.trace("get sendResponse string->{}", httpResponseStr);
        Map<String, String> tempMap = convertStringParamsToMap(httpResponseStr);

        String stateSuccessValue = sendResponseProperty.getStateSuccessValue();

        List<String> stateArrearsValue = sendResponseProperty.getStateArrearsValue();
        if (CollectionUtils.isEmpty(stateArrearsValue) && stateArrearsValue.contains(stateSuccessValue)) {
            throw new PlatArrearsException();
        }

        return new SmsSimpleSendResponse(stateSuccessValue.equalsIgnoreCase(tempMap.get(sendResponseProperty.getStateProperty())), tempMap.get(sendResponseProperty.getMessageProperty()));
    }

    private static Map<String, String> convertStringParamsToMap(String httpResponseStr) {
        String[] results = httpResponseStr.split("&");

        Map<String, String> tempMap = new HashMap<>(5);

        Arrays.stream(results)
                .flatMap(str -> Arrays.stream(str.split("&")))
                .map(s -> s.split("="))
                .filter(s -> s.length > 1)
                .forEach(strings -> tempMap.put(strings[0], strings[1]));
        return tempMap;
    }

    @Override
    public SmsRemainingNumberResponse parse(String httpResponseStr, SmsPlatformProperties.RemainingNumberResponseProperty remainingNumberResponseProperty) {
        SmsRemainingNumberResponse remainingNumberResponse = new SmsRemainingNumberResponse();
        log.info("get remainingNumberResponse:{}",httpResponseStr);
        Map<String, String> params = convertStringParamsToMap(httpResponseStr);
        String stateProperty = params.get(remainingNumberResponseProperty.getStateProperty());

        boolean responseSuccess = remainingNumberResponseProperty.getStateSuccessValue().equalsIgnoreCase(stateProperty);
        if(responseSuccess){
            remainingNumberResponse.setDescription(params.get(remainingNumberResponseProperty.getDescriptionProperty()));
            remainingNumberResponse.setNumber(Long.valueOf(params.get(remainingNumberResponseProperty.getNumberProperty())));
        }
        remainingNumberResponse.setSuccess(responseSuccess);
        return remainingNumberResponse;
    }
}
