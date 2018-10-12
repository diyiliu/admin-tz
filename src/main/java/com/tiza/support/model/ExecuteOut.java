package com.tiza.support.model;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 * Description: ExecuteOut
 * Author: DIYILIU
 * Update: 2018-09-11 14:55
 */

@Data
public class ExecuteOut {

    private Integer result;

    private String outStr;

    private String outErr;

    public boolean isOk(){
        if (result == 0 && StringUtils.isEmpty(outErr)){

            return true;
        }

        return false;
    }
}
