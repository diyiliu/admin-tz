package com.tiza.web.devops;

import com.tiza.support.model.ExecuteOut;
import com.tiza.support.util.RemoteUtil;
import com.tiza.web.devops.dto.Deploy;
import com.tiza.web.devops.facade.DeployJpa;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
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
    private DeployJpa deployJpa;


    @PostMapping("/list")
    public Map list(@RequestParam int pageNo, @RequestParam int pageSize) {
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
        Page<Deploy> deployPage = deployJpa.findAll(pageable);

        // 获取程序运行状态
        for (Deploy deploy : deployPage.getContent()) {
            ExecuteOut out = RemoteUtil.run(deploy, -1);
            if (out.getResult() == 0 &&
                    StringUtils.isNotEmpty(out.getOutStr())){

                deploy.setStatus(1);
            }else {
                if (StringUtils.isEmpty(out.getOutErr())){
                    deploy.setStatus(0);
                }else {
                    deploy.setStatus(-1);
                }
            }
        }

        Map respMap = new HashMap();
        respMap.put("data", deployPage.getContent());
        respMap.put("total", deployPage.getTotalElements());

        return respMap;
    }

    @PostMapping("/save")
    public Integer save(Deploy deploy) {
        deploy.setCreateTime(new Date());
        deploy = deployJpa.save(deploy);
        if (deploy == null) {

            return 0;
        }

        return 1;
    }

    @DeleteMapping("/del/{id}")
    public Integer delete(@PathVariable long id) {
        deployJpa.deleteById(id);

        return 1;
    }

    @PutMapping("/exec/{status}/{id}")
    public Map executeJar(@PathVariable int status, @PathVariable Long id) {
        Deploy deploy = deployJpa.findById(id).get();

        ExecuteOut out = RemoteUtil.run(deploy, status);
        Map respMap = new HashMap();
        if (out == null || out.getResult() != 0) {
            respMap.put("status", 0);
            if (out != null) {
                respMap.put("error", out.getOutErr());
            }
        } else {
            respMap.put("status", 1);
        }

        return respMap;
    }
}
