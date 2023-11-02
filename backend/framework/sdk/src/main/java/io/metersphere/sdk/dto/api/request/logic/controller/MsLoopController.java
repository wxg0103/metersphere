package io.metersphere.sdk.dto.api.request.logic.controller;

import io.metersphere.plugin.api.annotation.PluginSubType;
import io.metersphere.plugin.api.dto.TestElementDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@PluginSubType("MsLoopController")
public class MsLoopController extends TestElementDTO {
    private String clazzName = MsLoopController.class.getCanonicalName();
    private String loopType;
}