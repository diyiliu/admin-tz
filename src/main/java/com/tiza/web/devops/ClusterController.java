package com.tiza.web.devops;

import com.tiza.support.util.RemoteUtil;
import com.tiza.web.devops.dto.Deploy;
import com.tiza.web.devops.dto.DevNode;
import com.tiza.web.devops.facade.DevNodeJpa;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
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
    public Integer node(DevNode devNode){
        if (devNode.getId() != null){

            return modify(devNode);
        }

        devNode.setCreateTime(new Date());
        devNode = devNodeJpa.save(devNode);
        if (devNode == null){

            return 0;
        }

        return 1;
    }


    public Integer modify(DevNode devNode){
        DevNode temp = devNodeJpa.findById(devNode.getId()).get();

        temp.setName(devNode.getName());
        temp.setHost(devNode.getHost());
        temp.setPort(devNode.getPort());
        temp.setUser(devNode.getUser());
        temp.setPwd(devNode.getPwd());
        temp = devNodeJpa.save(temp);
        if (temp == null){

            return 0;
        }

        return 1;
    }


    @DeleteMapping("/node/{id}")
    public Integer note(@PathVariable long id) {
        devNodeJpa.deleteById(id);

        return 1;
    }


}
