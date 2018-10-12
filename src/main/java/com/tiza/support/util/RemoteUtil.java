package com.tiza.support.util;

import ch.ethz.ssh2.*;
import com.tiza.support.model.ExecuteOut;
import com.tiza.web.devops.dto.Deploy;
import com.tiza.web.devops.dto.DevNode;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Properties;

/**
 * Description: RemoteUtil
 * Author: DIYILIU
 * Update: 2018-09-11 14:20
 */
public class RemoteUtil {

    public static ExecuteOut doStart(Deploy deploy) {
        String str = "source /etc/profile \n" +
                "nohup java -jar " + deploy.getPath() + ">/logs/chat.log 2>&1 &";

        ExecuteOut out = null;
        try {
            out = callProcess(deploy, str);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return out;
    }

    public static ExecuteOut doStop(Deploy deploy) {
        String path = deploy.getPath();
        String proc = path.substring(path.lastIndexOf("/") + 1, path.lastIndexOf("."));

        String str = "ps -ef|grep " + proc + "* |grep -v grep |awk '{print $2}'| sed -e \"s/^/kill -9 /g\" | sh -";
        ExecuteOut out = null;
        try {
            out = callProcess(deploy, str);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return out;
    }


    public static int checkStatus(Deploy deploy) {
        String path = deploy.getPath();
        String proc = path.substring(path.lastIndexOf("/") + 1, path.lastIndexOf("."));

        String str = "ps -ef|grep " + proc + "* | grep -v 'grep'";
        try {
            ExecuteOut out = callProcess(deploy, str);
            if (out.getResult() == 0 &&
                    StringUtils.isNotEmpty(out.getOutStr())) {

                return 1;
            }

            return 0;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return -1;
    }

    private static ExecuteOut callProcess(Deploy deploy, String str) throws Exception {
        String host = deploy.getHost();
        int port = deploy.getPort();
        String user = deploy.getUser();
        String password = deploy.getPwd();

        InputStream stdOut = null;
        InputStream stdErr = null;
        Connection conn = null;

        String outStr, outErr;
        try {
            conn = new Connection(host, port);
            conn.connect();
            boolean login = conn.authenticateWithPassword(user, password);
            if (login) {
                Session session = conn.openSession();
                // Execute a command on the remote machine.
                session.execCommand(str);
                stdOut = new StreamGobbler(session.getStdout());
                outStr = processStream(stdOut, "UTF-8");
                stdErr = new StreamGobbler(session.getStderr());
                outErr = processStream(stdErr, "UTF-8");
                session.waitForCondition(ChannelCondition.EXIT_STATUS, 5 * 60 * 1000);
                int ret = session.getExitStatus();

                ExecuteOut out = new ExecuteOut();
                out.setResult(ret);
                out.setOutStr(outStr);
                out.setOutErr(outErr);

                return out;
            } else {
                System.err.println("登录远程机器失败" + host); // 自定义异常类 实现略
            }
        } finally {
            if (conn != null) {
                conn.close();
            }
            IOUtils.closeQuietly(stdOut);
            IOUtils.closeQuietly(stdErr);
        }

        return null;
    }


    /**
     * @param in
     * @param charset
     * @return
     * @throws Exception
     */
    private static String processStream(InputStream in, String charset) throws Exception {
        byte[] buf = new byte[1024];
        StringBuilder sb = new StringBuilder();
        while (in.read(buf) != -1) {
            sb.append(new String(buf, charset));
        }
        return sb.toString();
    }


    public static ExecuteOut execCommand(DevNode node, String command) throws IOException {
        ExecuteOut out = new ExecuteOut();
        Connection conn = new Connection(node.getHost(), node.getPort());
        conn.connect();
        boolean isAuth = conn.authenticateWithPassword(node.getUser(), node.getPwd());
        if (isAuth) {
            Session session = conn.openSession();
            session.execCommand(command);

            try (InputStream streamOut = new StreamGobbler(session.getStdout());
                 InputStream streamErr = new StreamGobbler(session.getStdout())) {

                String outStr = IOUtils.toString(streamOut, StandardCharsets.UTF_8);
                String outErr = IOUtils.toString(streamErr, StandardCharsets.UTF_8);
                session.waitForCondition(ChannelCondition.EXIT_SIGNAL, Long.MAX_VALUE);

                int ret = session.getExitStatus();
                out.setResult(ret);
                out.setOutStr(outStr);
                out.setOutErr(outErr);
            }
            session.close();
        }
        conn.close();

        return out;
    }


    /**
     * 远程传输
     */
    public static void transferFile(DevNode node, File file, String targetDir) throws IOException {
        String fileName = file.getName();

        if (file.isDirectory()) {
            ExecuteOut out = execCommand(node, "mkdir -p " + targetDir + "/" + fileName);
            if (out.isOk()) {

                File[] files = file.listFiles();
                for (File f : files) {
                    String target = targetDir + "/" + fileName;
                    transferFile(node, f, target);
                }
            }

            return;
        }

        String cmd = "cd " + targetDir + "; rm" + fileName + "; touch" + fileName;
        execCommand(node, cmd);
        Connection conn = new Connection(node.getHost(), node.getPort());
        conn.connect();

        boolean isAuth = conn.authenticateWithPassword(node.getUser(), node.getPwd());
        if (isAuth) {
            SCPClient sCPClient = conn.createSCPClient();

            SCPOutputStream scpOutputStream = sCPClient.put(fileName, file.length(), targetDir, "7777");
            InputStream inputStream = new FileInputStream(file);
            if (fileName.endsWith(".properties")) {
                Properties properties = new Properties();
                properties.load(inputStream);
                inputStream.close();
                // 一定要在修改值之前关闭inputStream
                properties.setProperty("localhost", node.getHost());
                properties.store(scpOutputStream, "");
            }else {
                IOUtils.copy(inputStream, scpOutputStream);
                inputStream.close();
            }
        }
        conn.close();
    }
}


