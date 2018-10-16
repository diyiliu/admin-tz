package com.tiza.support.model;

import com.diyiliu.model.DiskInfo;
import com.diyiliu.model.MonitorInfo;
import com.diyiliu.server.support.IMsgObserver;
import com.tiza.web.devops.dto.DevNode;
import com.tiza.web.devops.facade.DevNodeJpa;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * Description: MonitorObserver
 * Author: DIYILIU
 * Update: 2018-10-15 15:02
 */

@Slf4j
@Component
public class MonitorObserver implements IMsgObserver {

    @Resource
    private DevNodeJpa devNodeJpa;

    @Override
    public void read(MonitorInfo monitorInfo) {
        String host = monitorInfo.getIp();
        log.info("[{}] 上报状态信息 ... ", host);

        DevNode node = devNodeJpa.findByHost(host);
        if (node.getOs() == null){

            String os = monitorInfo.getOs();
            int cpuCore = monitorInfo.getCpuCore();
            int memorySize = monitorInfo.getTotalMemory();
            int diskSize = 0;

            List<DiskInfo> diskInfoList = monitorInfo.getDiskInfos();
            for (DiskInfo diskInfo: diskInfoList){
                diskSize += diskInfo.getTotalSpace();
            }

            node.setOs(os);
            node.setCpuCore(cpuCore);
            node.setMemorySize(mb2Gb(memorySize));
            node.setDiskSize(mb2Gb(diskSize));
            devNodeJpa.save(node);
        }



    }

    private int mb2Gb(int size){

       return   new BigDecimal(size).divide(new BigDecimal(1024), 0, RoundingMode.UP).intValue();
    }
}
