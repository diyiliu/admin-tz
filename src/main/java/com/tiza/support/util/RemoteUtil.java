package com.tiza.support.util;

import ch.ethz.ssh2.*;
import com.tiza.support.model.ExecuteOut;
import com.tiza.web.devops.dto.Deploy;
import com.tiza.web.devops.dto.DevNode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.groovy.tools.shell.IO;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Map;
import java.util.Properties;

/**
 * Description: RemoteUtil
 * Author: DIYILIU
 * Update: 2018-09-11 14:20
 */

@Slf4j
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

    /**
     * 远程拷贝文件(包括文件夹)
     *
     * @param devNode
     * @param file
     * @param dir
     * @param config
     * @throws IOException
     */
    public static void copyFile(DevNode devNode, File file, String dir, Map config) throws IOException {
        Connection connection = new Connection(devNode.getHost(), devNode.getPort());
        connection.connect();
        boolean isAuth = connection.authenticateWithPassword(devNode.getUser(), devNode.getPwd());
        if (isAuth) {
            transferFile(connection, file, dir, config);
        }
        connection.close();
    }

    /**
     * 远程传输
     *
     * @param connection
     * @param file
     * @param targetDir
     * @param config
     * @throws IOException
     */
    private static void transferFile(Connection connection, File file, String targetDir, Map config) throws IOException {
        String fileName = file.getName();

        ExecuteOut out;
        if (file.isDirectory()) {
            String cmd = "mkdir -p " + targetDir + "/" + fileName;
            out = execCommand(connection, cmd);
            if (out.isOk()) {

                File[] files = file.listFiles();
                for (File f : files) {
                    String target = targetDir + "/" + fileName;
                    transferFile(connection, f, target, config);
                }
            } else {
                log.error("执行 SSH 指令[{}]异常!", cmd);
            }

            return;
        }

        String cmd = "cd " + targetDir + "; rm " + fileName + "; touch " + fileName;
        out = execCommand(connection, cmd);
        if (!out.isOk()) {
            log.error("执行 SSH 指令[{}]异常!", cmd);
            return;
        }

        // 修改配置文件信息
        if (fileName.equals("config.properties")) {
            InputStream inputStream = new FileInputStream(file);
            Properties properties = new Properties();
            properties.load(inputStream);
            inputStream.close();
            properties.putAll(config);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            properties.store(outputStream, "");
            byte[] bytes = outputStream.toByteArray();

            SCPClient sCPClient = connection.createSCPClient();
            try (SCPOutputStream scpOutputStream = sCPClient.put(fileName, bytes.length, targetDir, "7777")){

                scpOutputStream.write(bytes);
                scpOutputStream.flush();
            }
            return;
        }

        // 普通文件拷贝
        SCPClient sCPClient = connection.createSCPClient();
        try (SCPOutputStream scpOutputStream = sCPClient.put(fileName, file.length(), targetDir, "7777");
             InputStream inputStream = new FileInputStream(file)) {

            IOUtils.copy(inputStream, scpOutputStream);
        }
    }

    /**
     * 指令远程指令
     *
     * @param connection
     * @param command
     * @return
     * @throws IOException
     */
    private static ExecuteOut execCommand(Connection connection, String command) throws IOException {
        ExecuteOut out = new ExecuteOut();

        Session session = connection.openSession();
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

        return out;
    }
}


