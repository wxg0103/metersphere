package io.metersphere.api.dto.request;

import io.metersphere.api.dto.request.assertion.MsAssertionConfig;
import io.metersphere.api.dto.request.processors.MsProcessorConfig;
import io.metersphere.plugin.api.spi.AbstractMsTestElement;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 协议插件中通用的配置
 * 添加到对应元素的 children 属性中
 * @Author: jianxing
 * @CreateTime: 2023-12-25  10:50
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MsCommonElement extends AbstractMsTestElement {
    /**
     * 前置处理器配置
     */
    private MsProcessorConfig preProcessorConfig;
    /**
     * 后置处理器配置
     */
    private MsProcessorConfig postProcessorConfig;
    /**
     * 断言配置
     */
    private MsAssertionConfig assertionConfig;
}
