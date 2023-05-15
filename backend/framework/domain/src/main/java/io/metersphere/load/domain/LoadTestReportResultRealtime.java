package io.metersphere.load.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.metersphere.validation.groups.Created;
import io.metersphere.validation.groups.Updated;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.io.Serializable;

@ApiModel(value = "即时报告结果")
@TableName("load_test_report_result_realtime")
@Data
public class LoadTestReportResultRealtime implements Serializable {
    private static final long serialVersionUID = 1L;
    @TableId
    @NotBlank(message = "{load_test_report_result_realtime.report_id.not_blank}", groups = {Updated.class})
    @ApiModelProperty(name = "报告ID", required = true, allowableValues="range[1, 50]")
    private String reportId;
    
    @TableId
    @NotBlank(message = "{load_test_report_result_realtime.report_key.not_blank}", groups = {Updated.class})
    @ApiModelProperty(name = "报告项目key", required = true, allowableValues="range[1, 64]")
    private String reportKey;
    
    @TableId
    @NotBlank(message = "{load_test_report_result_realtime.resource_index.not_blank}", groups = {Updated.class})
    @ApiModelProperty(name = "资源节点索引号", required = true, dataType = "Integer")
    private Integer resourceIndex;
    
    @TableId
    @NotBlank(message = "{load_test_report_result_realtime.sort.not_blank}", groups = {Updated.class})
    @ApiModelProperty(name = "报告项目内容排序", required = true, dataType = "Integer")
    private Integer sort;
    
    
    @ApiModelProperty(name = "报告项目内容", required = false, allowableValues="range[1, ]")
    private byte[] reportValue;
    

}