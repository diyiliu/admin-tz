package com.tiza.web.devops;

import com.tiza.support.util.RemoteUtil;
import com.tiza.web.devops.dto.DevNode;
import com.tiza.web.devops.facade.DevNodeJpa;
import org.springframework.core.env.Environment;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Description: ClusterController
 * Author: DIYILIU
 * Update: 2018-10-11 14:38
 */

@RestController
@RequestMapping("/cluster")
public class ClusterController {

    /**
     * linux 监控程序路径
     **/
    private final static String targetDir = "/opt/java";

    @Resource
    private Environment environment;

    @Resource
    private DevNodeJpa devNodeJpa;

    @PostMapping("/notes")
    public Map noteList(@RequestParam int pageNo, @RequestParam int pageSize) {
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, Sort.by("name"));
        Page<DevNode> nodePage = devNodeJpa.findAll(pageable);

        Map respMap = new HashMap();
        respMap.put("data", nodePage.getContent());
        respMap.put("total", nodePage.getTotalElements());

        return respMap;
    }

    @PostMapping("/node")
    public Integer node(DevNode devNode) throws Exception {
        if (devNode.getId() != null) {

            return modify(devNode);
        }

        devNode.setCreateTime(new Date());
        devNode = devNodeJpa.save(devNode);
        if (devNode == null) {

            return 0;
        }
        remoteCopy(devNode);

        return 1;
    }


    public Integer modify(DevNode devNode) throws Exception {
        DevNode temp = devNodeJpa.findById(devNode.getId()).get();
        temp.setName(devNode.getName());
        temp.setHost(devNode.getHost());
        temp.setPort(devNode.getPort());
        temp.setUser(devNode.getUser());
        temp.setPwd(devNode.getPwd());
        temp.setCheckOn(devNode.getCheckOn());

        temp = devNodeJpa.save(temp);
        if (temp == null) {

            return 0;
        }
        remoteCopy(devNode);

        return 1;
    }


    @DeleteMapping("/node/{id}")
    public Integer note(@PathVariable long id) {
        devNodeJpa.deleteById(id);

        return 1;
    }

    /**
     * 远程拷贝 监控目录
     *
     * @param devNode
     * @throws Exception
     */
    private void remoteCopy(DevNode devNode) throws Exception {
        org.springframework.core.io.Resource monitorRes =
                new UrlResource(environment.getProperty("upload.monitor"));

        Map config = new HashMap();
        config.put("localhost",devNode.getHost());

        if (monitorRes.exists()) {
            File file = monitorRes.getFile();
            RemoteUtil.copyFile(devNode, file, targetDir, config);
        }
    }
}
