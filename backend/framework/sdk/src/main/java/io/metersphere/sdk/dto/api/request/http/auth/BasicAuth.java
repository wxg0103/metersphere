package io.metersphere.sdk.dto.api.request.http.auth;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Data;

/**
 * @Author: jianxing
 * @CreateTime: 2023-11-07  11:00
 */
@Data
@JsonTypeName("BASIC")
public class BasicAuth extends HTTPAuth {
    private String userName;
    private String password;
}