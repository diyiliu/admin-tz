package com.tiza.web.sys.dto;

import lombok.Data;

import javax.persistence.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description: SysAsset
 * Author: DIYILIU
 * Update: 2018-04-28 14:15
 */

@Data
@Entity
@Table(name = "sys_asset")
public class SysAsset {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String code;

    private Long pid;

    private String pids;
    
    private String type;

    /** 控制器(路径) */
    private String controller;

    /** 视图 */
    private String view;

    private String iconCss;

    /** 是否为菜单资源 0: 否; 1: 是*/
    private Integer isMenu;

    private Integer sort;

    @Transient
    private List<SysAsset> children;

    /**
     * 转为树形结构
     *
     * @param parentCode
     * @param ownList
     * @return
     */
    public Map toTreeItem(String parentCode, List<Long> ownList){
        Map map = new HashMap();
        map.put("id", id);
        map.put("text", name);

        // 携带数据
        Map data = new HashMap();
        data.put("access", type);
        data.put("permission", parentCode + ":" + code);
        data.put("pids", pids);
        map.put("userdata", data);

        // 设置选中
        if (ownList.contains(id)){
            map.put("checked", 1);
        }

        return map;
    }
}
