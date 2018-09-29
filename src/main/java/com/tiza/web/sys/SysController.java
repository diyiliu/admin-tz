package com.tiza.web.sys;

import com.tiza.support.shiro.helper.PasswordHelper;
import com.tiza.support.util.DateUtil;
import com.tiza.web.sys.dto.SysAsset;
import com.tiza.web.sys.dto.SysPrivilege;
import com.tiza.web.sys.dto.SysRole;
import com.tiza.web.sys.dto.SysUser;
import com.tiza.web.sys.facade.SysAssetJpa;
import com.tiza.web.sys.facade.SysPrivilegeJpa;
import com.tiza.web.sys.facade.SysRoleJpa;
import com.tiza.web.sys.facade.SysUserJpa;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Description: SysController
 * Author: DIYILIU
 * Update: 2018-05-01 22:39
 */

@RestController
@RequestMapping("/sys")
public class SysController {

    @Resource
    private SysAssetJpa sysAssetJpa;

    @Resource
    private SysUserJpa sysUserJpa;

    @Resource
    private SysRoleJpa sysRoleJpa;

    @Resource
    private SysPrivilegeJpa sysPrivilegeJpa;

    @Resource
    private PasswordHelper passwordHelper;


    @PostMapping("/assetList")
    public List<SysAsset> assetList() {

        return sysAssetJpa.findAll(Sort.by("sort"));
    }

    @GetMapping("/assetTree/{roleId}")
    public List assetTree(@PathVariable("roleId") long roleId) {
        List<SysPrivilege> privilegeList = sysPrivilegeJpa.findByMasterAndMasterValue("role", roleId);
        List<Long> list = privilegeList.stream().map(SysPrivilege::getAccessValue).collect(Collectors.toList());

        List<SysAsset> assetList = sysAssetJpa.findAll(new Sort(Sort.Direction.ASC, new String[]{"pid", "sort"}));
        // 根节点
        List<SysAsset> rootList = assetList.stream().filter(a -> a.getPid() == 0).collect(Collectors.toList());
        // 子节点
        Map<Long, List<SysAsset>> menuMap = assetList.stream().filter(a -> a.getPid() > 0)
                .collect(Collectors.groupingBy(SysAsset::getPid));

        List treeList = new ArrayList();
        for (SysAsset asset : rootList) {
            loadAsset(asset, menuMap);
            treeList.add(toTree("home", asset, list));
        }

        return treeList;
    }

    /**
     * 加载数枝干
     *
     * @param asset
     * @param menuMap
     */
    private void loadAsset(SysAsset asset, Map<Long, List<SysAsset>> menuMap) {
        long id = asset.getId();
        if (menuMap.containsKey(id)) {
            List<SysAsset> list = menuMap.get(id);
            asset.setChildren(list);
            for (SysAsset a : list) {
                loadAsset(a, menuMap);
            }
        }
    }

    /**
     * 转为树形结构数据
     *
     * @param code
     * @param sysAsset
     * @return
     */
    private Map toTree(String code, SysAsset sysAsset, List<Long> ownList) {
        Map map = sysAsset.toTreeItem(code, ownList);

        if (CollectionUtils.isNotEmpty(sysAsset.getChildren())) {
            map.put("open", 1);
            map.put("checkbox", "hidden");

            List items = new ArrayList();
            List<SysAsset> list = sysAsset.getChildren();
            for (SysAsset asset : list) {
                String parentCode = sysAsset.getCode();
                items.add(toTree(parentCode, asset, ownList));
            }
            map.put("items", items);
        }

        return map;
    }


    @PostMapping("/asset")
    public Integer saveAsset(SysAsset asset) {
        asset = sysAssetJpa.save(asset);
        if (asset == null) {

            return 0;
        }

        return 1;
    }


    @PostMapping("/asset/{id}")
    public SysAsset asset(@PathVariable("id") long id) {
        return sysAssetJpa.findById(id).get();
    }

    @DeleteMapping("/asset")
    public Integer deleteAsset(@RequestBody List<Long> ids) {
        sysAssetJpa.deleteByIdIn(ids);

        return 1;
    }

    @PostMapping("/userList")
    public Map userList(@RequestParam int pageNo, @RequestParam int pageSize,
                        @RequestParam(required = false) String search) {

        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, Sort.by(Sort.Direction.DESC, "lastLoginTime"));

        Page<SysUser> userPage;
        if (StringUtils.isEmpty(search)) {
            userPage = sysUserJpa.findAll(pageable);
        } else {
            String like = "%" + search + "%";
            userPage = sysUserJpa.findByUsernameLikeOrNameLike(like, like, pageable);
        }

        Map respMap = new HashMap();
        respMap.put("data", userPage.getContent());
        respMap.put("total", userPage.getTotalElements());

        return respMap;
    }

    @PostMapping("/user")
    public Integer saveUser(SysUser user, @RequestParam("roleId") Long roleId) {
        if (user.getId() != null) {

            return modifyUser(user, roleId);
        }

        Subject subject = SecurityUtils.getSubject();

        // 默认密码
        user.setPassword("123456");
        passwordHelper.encryptPassword(user);
        user.setStatus(1);
        user.setCreateTime(new Date());
        user.setExpireTime(DateUtil.stringToDate(user.getExpireTimeStr()));
        user.setCreateUser((String) subject.getPrincipal());
        if (roleId != null) {
            user.setRoleIds(new Long[]{roleId});
        }

        user = sysUserJpa.save(user);
        if (user == null) {

            return 0;
        }

        return 1;
    }

    private Integer modifyUser(SysUser user, Long roleId) {
        SysUser temp = sysUserJpa.findById(user.getId()).get();

        temp.setName(user.getName());
        temp.setTel(user.getTel());
        temp.setEmail(user.getEmail());
        temp.setExpireTime(DateUtil.stringToDate(user.getExpireTimeStr()));
        if (roleId != null) {
            temp.setRoleIds(new Long[]{roleId});
        }

        temp = sysUserJpa.save(temp);
        if (temp == null) {

            return 0;
        }

        return 1;
    }

    @PutMapping("/user")
    public Integer updateUser(SysUser user, HttpSession session) {
        SysUser temp = sysUserJpa.findById(user.getId()).get();
        temp.setTel(user.getTel());
        temp.setEmail(user.getEmail());
        temp.setNote(user.getNote());
        temp = sysUserJpa.save(temp);
        if (temp == null) {

            return 0;
        }
        session.setAttribute("user", temp);


        return 1;
    }

    @DeleteMapping("/user")
    public Integer deleteUser(@RequestBody List<Long> ids) {
        sysUserJpa.deleteByIdIn(ids);

        return 1;
    }

    /**
     * 密码重置
     *
     * @param userId
     * @return
     */
    @PutMapping("/userPwd/{id}")
    public Integer resetPwd(@PathVariable("id") Long userId) {
        SysUser temp = sysUserJpa.findById(userId).get();
        temp.setPassword("123456");
        passwordHelper.encryptPassword(temp);

        temp = sysUserJpa.save(temp);
        if (temp == null) {

            return 0;
        }

        return 1;
    }


    @PutMapping("/userPwd")
    public Map modifyPwd(@RequestParam("oldPwd") String oldPwd, @RequestParam("newPwd") String newPwd,
                         HttpSession session) {

        SysUser current = (SysUser) session.getAttribute("user");
        SysUser temp = sysUserJpa.findById(current.getId()).get();

        Map respMap = new HashMap();

        String enPwd = passwordHelper.encryptPassword(temp.getUsername(), oldPwd, temp.getSalt());
        if (enPwd.equals(temp.getPassword())) {
            temp.setPassword(newPwd);
            passwordHelper.encryptPassword(temp);

            temp = sysUserJpa.save(temp);
            if (temp == null) {

                respMap.put("result", "0");
                respMap.put("msg", "修改密码失败!");

            } else {
                respMap.put("result", "1");
                respMap.put("msg", "修改密码成功!");
            }

            return respMap;
        }

        respMap.put("result", "0");
        respMap.put("msg", "原密码错误!");

        return respMap;
    }


    @PostMapping("/roleList")
    public Map roleList(@RequestParam int pageNo, @RequestParam int pageSize) {
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, Sort.by(Sort.Direction.DESC, "createTime"));
        Page<SysRole> rolePage = sysRoleJpa.findAll(pageable);

        Map respMap = new HashMap();
        respMap.put("data", rolePage.getContent());
        respMap.put("total", rolePage.getTotalElements());

        return respMap;
    }

    @PostMapping("/role")
    public Integer addRole(SysRole role) {
        if (role.getId() != null) {
            return modifyRole(role);
        }

        Subject subject = SecurityUtils.getSubject();
        role.setCreateTime(new Date());
        role.setCreateUser((String) subject.getPrincipal());

        role = sysRoleJpa.save(role);
        if (role == null) {

            return 0;
        }

        return 1;
    }

    private Integer modifyRole(SysRole role) {
        SysRole oldRole = sysRoleJpa.findById(role.getId()).get();
        oldRole.setName(role.getName());
        oldRole.setCode(role.getCode());
        oldRole.setComment(role.getComment());

        role = sysRoleJpa.save(oldRole);
        if (role == null) {

            return 0;
        }

        return 1;
    }

    @DeleteMapping("/role")
    public Integer deleteRole(@RequestBody List<Long> ids) {
        sysRoleJpa.deleteByIdIn(ids);

        return 1;
    }

    @PostMapping("/roleAsset/{roleId}")
    public Integer roleAsset(@PathVariable("roleId") Long roleId, @RequestBody List<SysPrivilege> privilegeList) {
        // 删除
        sysPrivilegeJpa.deleteByMasterAndMasterValue("role", roleId);

        // 添加
        if (CollectionUtils.isNotEmpty(privilegeList)) {
            privilegeList = sysPrivilegeJpa.saveAll(privilegeList);
            if (privilegeList == null) {

                return 0;
            }
        }

        return 1;
    }
}
