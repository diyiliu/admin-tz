package com.tiza.web.devops;

import com.tiza.web.devops.dto.Manifest;
import com.tiza.web.devops.facade.ManifestJpa;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Description: ManifestController
 * Author: DIYILIU
 * Update: 2018-09-12 15:46
 */

@RestController
@RequestMapping("/manifest")
public class ManifestController {

    @Resource
    private ManifestJpa manifestJpa;

    @PostMapping("/list")
    public Map list(@RequestParam int pageNo, @RequestParam int pageSize) {
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
        Page<Manifest> mfPage = manifestJpa.findAll(pageable);

        Map respMap = new HashMap();
        respMap.put("data", mfPage.getContent());
        respMap.put("total", mfPage.getTotalElements());

        return respMap;
    }


    @PostMapping("/save")
    public Integer save(Manifest mf) {
        mf.setCreateTime(new Date());
        mf = manifestJpa.save(mf);
        if (mf == null) {

            return 0;
        }

        return 1;
    }

    @DeleteMapping("/del/{id}")
    public Integer delete(@PathVariable long id) {
        manifestJpa.deleteById(id);
        
        return 1;
    }
}
