package io.metersphere.api.dto;

import io.metersphere.plugin.api.dto.ParameterConfig;
import io.metersphere.plugin.api.spi.AbstractMsTestElement;
import io.metersphere.project.dto.environment.EnvironmentInfoDTO;
import io.metersphere.project.dto.environment.GlobalParams;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: jianxing
 * @CreateTime: 2023-10-27  17:30
 */
@Data
public class ApiParamConfig extends ParameterConfig {
    /**
     * 报告ID
     */
    private String reportId;
    /**
     * 使用全局cookie
     */
    private Boolean enableGlobalCookie = true;
    /**
     * 环境配置信息
     */
    private EnvironmentInfoDTO envConfig;
    /**
     * 全局参数
     */
    private GlobalParams globalParams;
    /**
     * AbstractMsTestElement 实现类与插件 ID 的映射
     * key 为 AbstractMsTestElement 实现类对象
     * value 为对应的插件 ID
     */
    private Map<Class<? extends AbstractMsTestElement>, String> testElementClassPluginIdMap = HashMap.newHashMap(0);
    /**
     * AbstractMsTestElement 实现类与接口协议的映射
     * key 为 AbstractMsTestElement 实现类对象
     * value 为对应的接口协议
     * 环境前后置忽略协议需要使用
     */
    private Map<Class<? extends AbstractMsTestElement>, String> testElementClassProtocalMap = HashMap.newHashMap(0);


    @Override
    public Map<String, Object> getProtocolEnvConfig(AbstractMsTestElement msTestElement) {
        return getProtocolEnvConfig(msTestElement, getEnvConfig());
    }

    /**
     * 获取当前插件对应的环境配置
     * @param msTestElement 当前的 AbstractMsTestElement 实现类
     * @param envConfig 当前的环境配置信息
     * @return
     */
    public Map<String, Object> getProtocolEnvConfig(AbstractMsTestElement msTestElement,
                                       EnvironmentInfoDTO envConfig) {
        if (envConfig == null
                || envConfig.getConfig() == null
                || envConfig.getConfig().getPluginConfigMap() == null
                || testElementClassPluginIdMap == null
                || testElementClassPluginIdMap.get(msTestElement.getClass()) == null) {
            return null;
        }
        Map<String, Map<String, Object>> pluginConfigMap = envConfig.getConfig().getPluginConfigMap();
        String pluginId = testElementClassPluginIdMap.get(msTestElement.getClass());
        return pluginConfigMap.get(pluginId);
    }


    /**
     * 获取当前环境配置
     * @param projectId
     * @return
     */
    public EnvironmentInfoDTO getEnvConfig(String projectId) {
        return getEnvConfig();
    }
}
