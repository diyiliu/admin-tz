package com.tiza.web.deploy;

import com.tiza.support.model.ExecuteOut;
import com.tiza.support.util.DateUtil;
import com.tiza.support.util.RemoteUtil;
import com.tiza.web.deploy.dto.Deploy;
import com.tiza.web.deploy.dto.Schedule;
import com.tiza.web.deploy.facade.DeployJpa;
import com.tiza.web.deploy.facade.ScheduleJpa;
import com.tiza.web.devops.dto.DevNode;
import com.tiza.web.devops.facade.DevNodeJpa;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.env.Environment;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Description: DeployController
 * Author: DIYILIU
 * Update: 2018-09-10 09:49
 */

@RestController
@RequestMapping("/deploy")
public class DeployController {

    @Resource
    private Environment environment;

    @Resource
    private DeployJpa deployJpa;

    @Resource
    private DevNodeJpa devNodeJpa;

    @Resource
    private ScheduleJpa scheduleJpa;

    @PostMapping("/normal")
    public Integer saveNormal(Deploy deploy, MultipartFile[] files) throws Exception {
        deploy.setType(0);
        deploy.setCreateTime(new Date());
        deploy = deployJpa.save(deploy);
        if (deploy == null) {

            return 0;
        }

        DevNode node = devNodeJpa.findById(deploy.getNode().getId()).get();
        copyFiles(node, deploy.getDir(), files);

        return 1;
    }

    @PostMapping("/list/{type}")
    public Map list(@PathVariable int type, @RequestParam int pageNo, @RequestParam int pageSize) {
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, Sort.by(Sort.Direction.DESC, "createTime"));
        Page<Deploy> deployPage = deployJpa.findByType(type, pageable);

        if (type == 0) {
            // 获取程序运行状态
            for (Deploy deploy : deployPage.getContent()) {
                String path = deploy.getDir() + "/" + deploy.getJarFile();

                ExecuteOut out = RemoteUtil.run(deploy.getNode(), path, deploy.getArgs(), -1);
                if (out.getResult() == 0 &&
                        StringUtils.isNotEmpty(out.getOutStr())) {

                    deploy.setStatus(1);
                    long time = System.currentTimeMillis() - deploy.getUptime().getTime();
                    deploy.setUptimeStr(DateUtil.formatMilliseconds(time));
                } else {
                    if (StringUtils.isEmpty(out.getOutErr())) {
                        deploy.setStatus(0);
                    } else {
                        deploy.setStatus(-1);
                    }
                }

                deploy.setUpdateTime(new Date());
                deployJpa.save(deploy);
            }
        }

        Map respMap = new HashMap();
        respMap.put("data", deployPage.getContent());
        respMap.put("total", deployPage.getTotalElements());

        return respMap;
    }

    @DeleteMapping("/del/{id}")
    public Integer delete(@PathVariable long id) {
        deployJpa.deleteById(id);

        return 1;
    }

    @PutMapping("/exec/{status}/{id}")
    public Map executeJar(@PathVariable int status, @PathVariable Long id) {
        Deploy deploy = deployJpa.findById(id).get();
        String path = deploy.getDir() + "/" + deploy.getJarFile();

        ExecuteOut out = RemoteUtil.run(deploy.getNode(), path, deploy.getArgs(), status);
        Map respMap = new HashMap();
        if (out == null || out.getResult() != 0) {
            respMap.put("status", 0);
            if (out != null) {
                respMap.put("error", out.getOutErr());
            }
        } else {
            respMap.put("status", 1);

            // 更新启动时间
            deploy.setUptime(new Date());
            deployJpa.save(deploy);
        }

        return respMap;
    }

    /* ================================  计划任务  =================================== */
    @PostMapping("/cron")
    public Integer saveCron(Deploy deploy, MultipartFile[] files) throws Exception {
        deploy.setType(1);
        deploy.setCreateTime(new Date());
        deploy = deployJpa.save(deploy);
        if (deploy == null) {

            return 0;
        }

        Schedule schedule = deploy.getSchedule();
        schedule.setDeploy(deploy);
        schedule.setCreateTime(new Date());
        schedule = scheduleJpa.save(schedule);
        if (schedule == null) {

            return 0;
        }

        DevNode node = devNodeJpa.findById(deploy.getNode().getId()).get();
        copyFiles(node, deploy.getDir(), files);

        return 1;
    }


    @DeleteMapping("/cron/{id}")
    public Integer delCron(@PathVariable long id) {
        Schedule schedule = scheduleJpa.findById(id).get();
        scheduleJpa.delete(schedule);

        return 1;
    }

    /**
     * 远程拷贝文件
     *
     * @param node
     * @param target
     * @param files
     * @throws Exception
     */
    private void copyFiles(DevNode node, String target, MultipartFile[] files) throws Exception {
        org.springframework.core.io.Resource tempRes =
                new UrlResource(environment.getProperty("upload.temp"));

        for (MultipartFile file : files) {
            File temp = new File(tempRes.getFile().getAbsolutePath() + "/" + file.getOriginalFilename());
            file.transferTo(temp);
            RemoteUtil.copyFile(node, temp, target, null);

            temp.delete();
        }
    }
}
