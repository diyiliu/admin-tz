package com.tiza.support.util;

import ch.ethz.ssh2.*;
import com.tiza.support.model.ExecuteOut;
import com.tiza.web.devops.dto.DevNode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Properties;

/**
 * Description: RemoteUtil
 * Author: DIYILIU
 * Update: 2018-09-11 14:20
 */

@Slf4j
public class RemoteUtil {

    public static ExecuteOut run(DevNode devNode, String path, String args, int status) {
        String dir = path.substring(0, path.lastIndexOf("/"));
        String cmd = "";
        // 启动
        if (status == 1) {
            cmd += "source /etc/profile \n " +
                    "cd " + dir + "\n ";

            if (StringUtils.isNotEmpty(args)) {

                cmd += args;
            } else {
                cmd += "nohup java -jar " + path + " >log.txt 2>&1 &";
            }
        }
        // 停止
        else if (status == 0) {
            String proc = path.substring(path.lastIndexOf("/") + 1, path.lastIndexOf("."));
            cmd = "ps -ef|grep " + proc + "* |grep -v grep |awk '{print $2}'| sed -e \"s/^/kill -9 /g\" | sh -";

        }
        // 检测状态
        else if (status == -1) {
            String proc = path.substring(path.lastIndexOf("/") + 1, path.lastIndexOf("."));
            cmd = "ps -ef|grep " + proc + "* | grep -v 'grep'";
        }

        ExecuteOut out = null;
        try {
            Connection connection = new Connection(devNode.getHost(), devNode.getPort());
            connection.connect();
            boolean isAuth = connection.authenticateWithPassword(devNode.getUser(), devNode.getPwd());
            if (isAuth) {
                out = execCommand(connection, cmd);
            }
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return out;
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

        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File f : files) {
                String dir = targetDir + "/" + fileName;
                transferFile(connection, f, dir, config);
            }

            return;
        }

        String cmd = "mkdir -p " + targetDir + ";cd " + targetDir + ";rm " + fileName + "; touch " + fileName;
        execCommand(connection, cmd);

        // 修改配置文件信息
        if (MapUtils.isNotEmpty(config) && fileName.equals("config.properties")) {
            InputStream inputStream = new FileInputStream(file);
            Properties properties = new Properties();
            properties.load(inputStream);
            inputStream.close();
            properties.putAll(config);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            properties.store(outputStream, "");
            byte[] bytes = outputStream.toByteArray();

            SCPClient sCPClient = connection.createSCPClient();
            try (SCPOutputStream scpOutputStream = sCPClient.put(fileName, bytes.length, targetDir, "7777")) {

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
    public static ExecuteOut execCommand(Connection connection, String command) throws IOException {
        ExecuteOut out = new ExecuteOut();

        Session session = connection.openSession();
        session.execCommand(command);
        try (InputStream streamOut = new StreamGobbler(session.getStdout());
             InputStream streamErr = new StreamGobbler(session.getStderr())) {

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


