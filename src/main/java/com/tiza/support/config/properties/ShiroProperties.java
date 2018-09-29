package com.tiza.support.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

/**
 * Description: ShiroProperties
 * Author: DIYILIU
 * Update: 2018-03-27 10:55
 */

@Data
@ConfigurationProperties("shiro")
public class ShiroProperties {
    private  String hashAlgorithmName = "md5";
    private  int hashIterations = 2;

    private String loginUrl;
    private String successUrl;

    private String usernameParam;
    private String passwordParam;
    private String rememberMeParam;

    /** Filter chain */
    private Map<String, String> filterChain;
}
