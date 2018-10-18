package com.tiza.support.model;

import ch.ethz.ssh2.Connection;
import com.diyiliu.model.DiskInfo;
import com.diyiliu.model.MonitorInfo;
import com.diyiliu.model.ProcessInfo;
import com.diyiliu.server.support.IMsgObserver;
import com.tiza.support.util.RemoteUtil;
import com.tiza.web.devops.dto.DevNode;
import com.tiza.web.devops.dto.NodeStatus;
import com.tiza.web.devops.facade.DevNodeJpa;
import com.tiza.web.devops.facade.NodeStatusJpa;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import oshi.util.FormatUtil;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
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
        log.info("[{}] 上报状态信息 ... ", host);

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

        NodeStatus status = nodeStatusJpa.findByNodeId(node.getId());
        if (status != null) {

            status.setCpuUsage(new BigDecimal(monitorInfo.getCpuLoad() * 100).intValue());
            status.setMemoryUsage(new BigDecimal(monitorInfo.getMemUsage() * 100).intValue());

            // 线程信息
            List<ProcessInfo> processInfoList = monitorInfo.getProcessInfos();
            if (node.getOs() != null && node.getOs().toUpperCase().indexOf("windows") < 0) {
                processInfoList = linuxProcess(node);
            }
            if (CollectionUtils.isNotEmpty(processInfoList)) {

                String info = "";
                for (int i = 0; i < processInfoList.size(); i++) {
                    if (i > 2) {
                        break;
                    }
                    ProcessInfo processInfo = processInfoList.get(i);
                    info += processInfo.getName() + ": " + String.format("%.1f%%", processInfo.getMemUsage() * 100) + ",";
                }

                status.setProcessInfo(info.substring(0, info.length() - 1));
            }

            // 磁盘信息
            if (CollectionUtils.isNotEmpty(monitorInfo.getDiskInfos())) {
                List<DiskInfo> diskInfoList = monitorInfo.getDiskInfos();
                String info = "";
                for (int i = 0; i < diskInfoList.size(); i++) {
                    if (i > 2) {
                        break;
                    }
                    DiskInfo diskInfo = diskInfoList.get(i);
                    info += diskInfo.getName() + ": " + String.format("%.0f%%", diskInfo.getDiskUsage() * 100) + ",";
                }
                status.setDiskInfo(info.substring(0, info.length() - 1));
            }

            status.setStatus(1);
            status.setUpdateTime(new Date());
            nodeStatusJpa.save(status);
        }
    }

    private int mb2Gb(int size) {

        return new BigDecimal(size).divide(new BigDecimal(1024), 0, RoundingMode.UP).intValue();
    }

    public List<ProcessInfo> linuxProcess(DevNode devNode) {
        List<ProcessInfo> processInfos = new ArrayList();

        try {
            String cmd = "ps aux|head -1;ps aux|sort -k4nr|head -5";
            Connection connection = new Connection(devNode.getHost(), devNode.getPort());
            connection.connect();
            boolean isAuth = connection.authenticateWithPassword(devNode.getUser(), devNode.getPwd());
            if (isAuth) {
                ExecuteOut out = RemoteUtil.execCommand(connection, cmd);

                if (out.isOk() && StringUtils.isNotEmpty(out.getOutStr())) {
                    String str = out.getOutStr();
                    String[] array = str.split("\n");

                    String header = array[0];
                    int index = header.lastIndexOf(" ");
                    for (int i = 1; i < array.length; i++) {
                        String info = array[i];
                        String content = info.substring(0, index).replaceAll("\\s+", " ");
                        String name = info.substring(index);

                        String[] items = content.split(" ");
                        String pid = items[1];
                        String cpu = items[2];
                        String mem = items[3];
                        String vsz = items[4];
                        String rss = items[5];

                        ProcessInfo process = new ProcessInfo();
                        process.setPid(Integer.parseInt(pid));
                        process.setName(name);
                        process.setMemUsage(Double.parseDouble(mem) * 0.01);
                        process.setVsz(FormatUtil.formatBytes(Long.parseLong(vsz)));
                        process.setRss(FormatUtil.formatBytes(Long.parseLong(rss)));
                        processInfos.add(process);
                    }
                }
            }
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return processInfos;
    }
}
