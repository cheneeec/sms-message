package com.gongsj.core.parse;

import com.alibaba.fastjson.JSONObject;
import com.gongsj.core.SmsRemainingNumberResponse;
import com.gongsj.core.SmsSimpleSendResponse;
import com.gongsj.core.exception.PlatArrearsException;
import com.gongsj.core.property.SmsPlatformProperties;
import org.springframework.util.CollectionUtils;

import java.util.List;

public class JsonHttpResponseParser implements SmsHttpResponseParser {

    @Override
    public SmsSimpleSendResponse parse(String httpResponseStr, SmsPlatformProperties.SendResponseProperty sendResponseProperty) throws PlatArrearsException {

        JSONObject responseJsonObject = JSONObject.parseObject(httpResponseStr);
        //获得状态值
        String state = responseJsonObject.getString(sendResponseProperty.getStateProperty());
        List<String> stateArrearsValue = sendResponseProperty.getStateArrearsValue();
        if (CollectionUtils.isEmpty(stateArrearsValue) && stateArrearsValue.contains(state)) {
            throw new PlatArrearsException();
        }
        //获得消息
        String message = responseJsonObject.getString(sendResponseProperty.getMessageProperty());
        //判断是否成功
        boolean success = sendResponseProperty.getStateSuccessValue().equalsIgnoreCase(state);


        return new SmsSimpleSendResponse(success, success ? null : message);
    }

    @Override
    public SmsRemainingNumberResponse parse(String httpResponseStr, SmsPlatformProperties.RemainingNumberResponseProperty remainingNumberResponseProperty) {

        JSONObject responseJsonObject = JSONObject.parseObject(httpResponseStr);
        String stateValue = responseJsonObject.getString(remainingNumberResponseProperty.getStateProperty());

        SmsRemainingNumberResponse smsRemainingNumberResponse = new SmsRemainingNumberResponse();
        smsRemainingNumberResponse.setNumber(responseJsonObject.getLongValue(remainingNumberResponseProperty.getNumberProperty()));
        smsRemainingNumberResponse.setDescription(responseJsonObject.getString(remainingNumberResponseProperty.getDescriptionProperty()));
        smsRemainingNumberResponse.setSuccess(stateValue.equalsIgnoreCase(remainingNumberResponseProperty.getStateSuccessValue()));
        return smsRemainingNumberResponse;
    }
}
