package com.tiza.support.model;

import com.diyiliu.model.DiskInfo;
import com.diyiliu.model.MonitorInfo;
import com.diyiliu.model.ProcessInfo;
import com.diyiliu.server.support.IMsgObserver;
import com.tiza.web.devops.dto.DevNode;
import com.tiza.web.devops.dto.NodeStatus;
import com.tiza.web.devops.facade.DevNodeJpa;
import com.tiza.web.devops.facade.NodeStatusJpa;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
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

    @Resource
    private NodeStatusJpa nodeStatusJpa;

    @Override
    public void read(MonitorInfo monitorInfo) {
        String host = monitorInfo.getIp();
        //log.info("[{}] 上报状态信息 ... ", host);

        DevNode node = devNodeJpa.findByHost(host);
        if (node.getOs() == null) {

            String os = monitorInfo.getOs();
            int cpuCore = monitorInfo.getCpuCore();
            int memorySize = monitorInfo.getTotalMemory();
            int diskSize = 0;

            List<DiskInfo> diskInfoList = monitorInfo.getDiskInfos();
            for (DiskInfo diskInfo : diskInfoList) {
                diskSize += diskInfo.getTotalSpace();
            }

            node.setOs(os);
            node.setCpuCore(cpuCore);
            node.setMemorySize(mb2Gb(memorySize));
            node.setDiskSize(mb2Gb(diskSize));
            devNodeJpa.save(node);
        }

        NodeStatus status = nodeStatusJpa.findByNodeHost(host);
        if (status != null) {

            status.setCpuUsage(new BigDecimal(monitorInfo.getCpuLoad() * 100).intValue());
            status.setMemoryUsage(new BigDecimal(monitorInfo.getMemUsage() * 100).intValue());

            if (CollectionUtils.isNotEmpty(monitorInfo.getProcessInfos())) {
                ProcessInfo processInfo = monitorInfo.getProcessInfos().get(0);

                status.setProcessInfo(processInfo.getName() + ": " + String.format("%.0f", processInfo.getMemUsage() * 100));
            }

            if (CollectionUtils.isNotEmpty(monitorInfo.getDiskInfos())) {
                DiskInfo diskInfo = monitorInfo.getDiskInfos().get(0);

                status.setDiskInfo(diskInfo.getName() + ": " + String.format("%.0f", diskInfo.getDiskUsage() * 100));
            }

            status.setStatus(1);
            status.setUpdateTime(new Date());
            nodeStatusJpa.save(status);
        }
    }

    private int mb2Gb(int size) {

        return new BigDecimal(size).divide(new BigDecimal(1024), 0, RoundingMode.UP).intValue();
    }
}
