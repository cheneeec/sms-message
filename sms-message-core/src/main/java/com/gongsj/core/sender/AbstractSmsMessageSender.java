package com.gongsj.core.sender;

import com.gongsj.core.annotation.SmsPlatform;
import com.gongsj.core.property.SmsPlatformProperties;
import lombok.extern.slf4j.Slf4j;


/**
 * 继承于{@link AbstractBasicSmsMessageSender}方法，该类为抽象类，需要根据子类的类名或者{@link SmsPlatform}注解，来决定注入某个平台的属性。
 * @see SmsPlatform
 */
@Slf4j
public abstract class AbstractSmsMessageSender extends AbstractBasicSmsMessageSender {

    public AbstractSmsMessageSender(PersistentSaveRepository persistentSaveRepository, SmsPlatformProperties platformProperties) {
        super(persistentSaveRepository, platformProperties);
    }

    /* private static final String PLATFORM_SUFFIX = "SmsPlatformProperties";

        private static final String CHILD_CLASS_PLATFORM_JUDGMENT_MARK = "SmsMessage";

        protected final String platformSpringBeanName;

        protected  SmsPlatformProperties platformProperties;

        public AbstractSmsMessageSender(SmsPlatformProperties platformProperties) {
            super(persistentSaveRepository, platformProperties);
            this.platformSpringBeanName = generatePlatformPropertyName();
        }

        private String generatePlatformPropertyName() {
            SmsPlatform platform = this.getClass().getAnnotation(SmsPlatform.class);
            if (Objects.isNull(platform)) {
                return generatePlatformPropertyBySimpleClassName();
            }
            String platformName = platform.value();
            if (StringUtils.hasText(platformName)) {
                return platformName + PLATFORM_SUFFIX;
            } else {
                return generatePlatformPropertyBySimpleClassName();
            }
        }


        private String generatePlatformPropertyBySimpleClassName() {
            String simpleName = this.getClass().getSimpleName();
            StringBuilder platformPrefix = new StringBuilder(simpleName.substring(0, simpleName.indexOf(CHILD_CLASS_PLATFORM_JUDGMENT_MARK)));
            String firstStr = platformPrefix.substring(0, 1).toLowerCase();
            return platformPrefix.deleteCharAt(0).insert(0, firstStr).toString() + PLATFORM_SUFFIX;
        }

    */
   /* @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.platformProperties = applicationContext.getBean(this.platformSpringBeanName, SmsPlatformProperties.class);
        log.debug("Get the contents of the  platform successfully: {}", JSONObject.toJSONString(platformProperties));
    }*/
}
