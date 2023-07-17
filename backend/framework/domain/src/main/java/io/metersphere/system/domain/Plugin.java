package io.metersphere.system.domain;

import io.metersphere.validation.groups.*;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import lombok.Data;

@Data
public class Plugin implements Serializable {
    @Schema(title = "ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "{plugin.id.not_blank}", groups = {Updated.class})
    @Size(min = 1, max = 50, message = "{plugin.id.length_range}", groups = {Created.class, Updated.class})
    private String id;

    @Schema(title = "插件名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "{plugin.name.not_blank}", groups = {Created.class})
    @Size(min = 1, max = 255, message = "{plugin.name.length_range}", groups = {Created.class, Updated.class})
    private String name;

    @Schema(title = "插件ID（名称加版本号）", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "{plugin.plugin_id.not_blank}", groups = {Created.class})
    @Size(min = 1, max = 300, message = "{plugin.plugin_id.length_range}", groups = {Created.class, Updated.class})
    private String pluginId;

    @Schema(title = "文件名", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "{plugin.file_name.not_blank}", groups = {Created.class})
    @Size(min = 1, max = 300, message = "{plugin.file_name.length_range}", groups = {Created.class, Updated.class})
    private String fileName;

    @Schema(title = "创建时间")
    private Long createTime;

    @Schema(title = "更新时间")
    private Long updateTime;

    @Schema(title = "创建人")
    private String createUser;

    @Schema(title = "是否启用插件")
    private Boolean enable;

    @Schema(title = "是否是全局插件")
    private Boolean global;

    @Schema(title = "是否是企业版插件")
    private Boolean xpack;

    @Schema(title = "插件描述")
    private String description;

    @Schema(title = "插件使用场景PAI/PLATFORM", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "{plugin.scenario.not_blank}", groups = {Created.class})
    @Size(min = 1, max = 50, message = "{plugin.scenario.length_range}", groups = {Created.class, Updated.class})
    private String scenario;

    private static final long serialVersionUID = 1L;
}