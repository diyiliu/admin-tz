package com.tiza.web;

import com.tiza.support.util.ImageUtil;
import com.tiza.support.util.JacksonUtil;
import com.tiza.web.sys.dto.SysAsset;
import com.tiza.web.sys.dto.SysUser;
import com.tiza.web.sys.facade.SysAssetJpa;
import com.tiza.web.sys.facade.SysUserJpa;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.ExcessiveAttemptsException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.springframework.core.env.Environment;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

/**
 * Description: HomeController
 * Author: DIYILIU
 * Update: 2018-08-26 16:45
 */

@Slf4j
@Controller
public class HomeController {

    @Resource
    private Environment environment;

    @Resource
    private SysAssetJpa sysAssetJpa;

    @Resource
    private SysUserJpa sysUserJpa;


    @GetMapping("/login")
    public String login() {

        return "login";
    }

    @PostMapping("/login")
    public String login(HttpServletRequest request, RedirectAttributes redirectAttributes) {
        String exceptionClassName = (String) request.getAttribute("shiroLoginFailure");

        if (UnknownAccountException.class.getName().equals(exceptionClassName)
                || IncorrectCredentialsException.class.getName().equals(exceptionClassName)) {

            redirectAttributes.addFlashAttribute("error", "用户名或密码错误");
            return "redirect:/login";
        } else if (ExcessiveAttemptsException.class.getName().equals(exceptionClassName)) {

            redirectAttributes.addFlashAttribute("error", "登录错误次数超限，请稍后再试！");
            return "redirect:/login";

        } else if (exceptionClassName != null) {
            redirectAttributes.addFlashAttribute("error", "登录异常：" + exceptionClassName);

            return "redirect:/login";
        }

        return "home";
    }

    @GetMapping("/logout")
    public String logout() {
        SecurityUtils.getSubject().logout();

        return "redirect:/login";
    }

    @GetMapping("/")
    public String index(HttpSession session) {
        SysAsset asset = sysAssetJpa.findByCode("index");
        session.setAttribute("active", asset);

        return "home";
    }


    @GetMapping("/home/{menu}")
    public String show(@PathVariable("menu") String menu, HttpServletRequest request,
                       @ModelAttribute("type") String type, @ModelAttribute("page") String page) {

        SysAsset asset = sysAssetJpa.findByController("home/" + menu);
        if (asset == null) {
            return "error/404";
        }
        String view = asset.getView();
        // 设置当前页
        request.getSession().setAttribute("active", asset);

        // 添加参数
        Map data = new HashMap();
        if (StringUtils.isNotEmpty(type)) {
            data.put("type", Integer.valueOf(type));
        }
        if (StringUtils.isNotEmpty(page)) {
            data.put("page", Integer.valueOf(page));
        }
        if (MapUtils.isNotEmpty(data)) {
            request.setAttribute("data", data);
        }

        return view;
    }


    @GetMapping("/profile")
    public String profile(HttpSession session, Model model) {
        session.removeAttribute("active");
        model.addAttribute("user", session.getAttribute("user"));

        return "profile";
    }

    @GetMapping("/image/{dir}/{name:.+}")
    public void uploadedImage(@PathVariable("name") String name, @PathVariable("dir") String dir,
                              HttpServletResponse response) {
        String imagePath = "";
        if (dir.equalsIgnoreCase("user")) {
            imagePath = environment.getProperty("upload.user");
        }
        try {
            org.springframework.core.io.Resource imgRes = new UrlResource(imagePath + name);
            if (imgRes != null && imgRes.exists()) {
                response.setHeader("Content-Type", URLConnection.guessContentTypeFromName(imgRes.getFilename()));
                FileCopyUtils.copy(imgRes.getInputStream(), response.getOutputStream());
                response.flushBuffer();
            }
        } catch (IOException e) {
            log.info("Error writing file to output stream. Filename was '{}'", name, e);
            throw new RuntimeException("IOError writing file to output stream");
        }
    }

    @ResponseBody
    @PostMapping("/image/user")
    public Map userIcon(MultipartFile file, HttpServletRequest request) throws Exception {
        Map respMap = new HashMap();
        if (file.isEmpty()) {
            respMap.put("status", 0);

            return respMap;
        }

        String data = request.getParameter("data");
        Map dataMap = JacksonUtil.toObject(data, HashMap.class);
        String fileName = (String) dataMap.get("name");

        String picDir = environment.getProperty("upload.user");
        org.springframework.core.io.Resource resDir = new UrlResource(picDir);
        // 创建临时文件
        File tempFile = File.createTempFile("icon", fileName.substring(fileName.lastIndexOf(".")).toLowerCase(), resDir.getFile());
        FileCopyUtils.copy(file.getBytes(), tempFile);
        // 剪切图片
        cutPic(tempFile.getPath(), dataMap);

        String username = (String) SecurityUtils.getSubject().getPrincipal();
        SysUser user = sysUserJpa.findByUsername(username);
        // 历史图片
        String oldIcon = user.getUserIcon();

        user.setUserIcon(tempFile.getName());
        user = sysUserJpa.save(user);
        if (user == null) {
            respMap.put("status", 0);

            return respMap;
        }

        // 删除文件
        if (StringUtils.isNotEmpty(oldIcon)){
            org.springframework.core.io.Resource localRes = new UrlResource(picDir + oldIcon);
            if (localRes.exists()) {
                if (!localRes.getFile().delete()) {
                    System.err.println("删除文件失败!");
                }
            }
        }

        // 更新session
        request.getSession().setAttribute("user", user);

        respMap.put("status", 1);
        respMap.put("path", user.getUserIcon());

        return respMap;
    }


    @PostMapping("/home/preview/{type}/{page}")
    public String preview(@PathVariable int type, @PathVariable int page,
                          RedirectAttributes redirectAttributes) {

        redirectAttributes.addFlashAttribute("type", type);
        redirectAttributes.addFlashAttribute("page", page);

        return "redirect:/home/preview";
    }

    /**
     * 图片裁切, 缩放
     *
     * @param imagePath
     * @param data
     */
    private void cutPic(String imagePath, Map data) throws Exception {
        int x = new BigDecimal(String.valueOf(data.get("x"))).intValue();
        int y = new BigDecimal(String.valueOf(data.get("y"))).intValue();
        int w = new BigDecimal(String.valueOf(data.get("width"))).intValue();
        int h = new BigDecimal(String.valueOf(data.get("height"))).intValue();

        // 裁切
        ImageUtil.crop(imagePath, x, y, w, h, imagePath);
        // 缩放
        ImageUtil.scale(imagePath, 256, 256, imagePath);
    }
}
