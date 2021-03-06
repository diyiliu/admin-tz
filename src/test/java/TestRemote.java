import ch.ethz.ssh2.*;
import com.tiza.support.model.ExecuteOut;
import com.tiza.web.devops.dto.DevNode;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.junit.Test;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

/**
 * Description: TestRemote
 * Author: DIYILIU
 * Update: 2018-08-28 10:44
 */
public class TestRemote {

    private String host = "192.168.1.181";
    private String user = "root";
    private String password = "123456";

    @Test
    public void test() throws Exception {
        String cmds = "/opt/java/chat/autostart.sh stop";
        exec(cmds, host, user, password);
    }


    @Test
    public void test1() throws Exception {
        String cmds = "ps -ef|grep  chat-server* |grep -v 'grep'";

        exec(cmds, host, user, password);
    }


    @Test
    public void test2() throws Exception {
        String str = "export JAVA_HOME=/usr/local/java/jdk1.8.0_151 \n " +
                "export PATH=$PATH:$JAVA_HOME/bin:$JAVA_HOME/jre/bin \n " +
                "screen -d -m java -server -Xms256M -jar /opt/java/chat/*.jar";

        exec(str, host, user, password);
    }

    /**
     * windows 也可以开启SSH
     *
     * @throws Exception
     */
    @Test
    public void test3() throws Exception {

    }

    private void exec(String str, String host, String user, String password) throws Exception {
        InputStream stdOut = null;
        InputStream stdErr = null;
        String outStr = "";
        String outErr = "";
        int ret = -1;

        Connection conn = null;
        try {
            conn = new Connection(host);
            conn.connect();
            boolean login = conn.authenticateWithPassword(user, password);
            if (login) {
                // Open a new {@link Session} on this connection
                Session session = conn.openSession();

                // Execute a command on the remote machine.
                session.execCommand(str);
                stdOut = new StreamGobbler(session.getStdout());
                outStr = processStream(stdOut, "UTF-8");
                stdErr = new StreamGobbler(session.getStderr());
                outErr = processStream(stdErr, "UTF-8");
                session.waitForCondition(ChannelCondition.EXIT_STATUS, 5 * 60 * 1000);

                ret = session.getExitStatus();
                System.out.println("result: " + ret);

                System.out.println("outStr = " + outStr);
                System.out.println("outErr = " + outErr);
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

    }

    /**
     * @param in
     * @param charset
     * @return
     * @throws Exception
     */
    private String processStream(InputStream in, String charset) throws IOException {
        byte[] buf = new byte[1024];
        StringBuilder sb = new StringBuilder();
        while (in.read(buf) != -1) {
            sb.append(new String(buf, charset));
        }
        return sb.toString();
    }

    @Test
    public void test4() {
        String path = "/opt/java/chat/chat-server.jar";

        String proc = path.substring(path.lastIndexOf("/") + 1, path.lastIndexOf("."));
        System.out.println(proc);
    }

    @Test
    public void test5() {

        try {
            Connection conn = new Connection(host);
            conn.connect();
            boolean isAuth = conn.authenticateWithPassword(user, password);
            if (isAuth) {
                connection = conn;

                String localFile = "C:\\Users\\DIYILIU\\Desktop\\chat-server.jar";
                String remoteDir = "/opt/java";

                transferFile(localFile, remoteDir);

                connection.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void test6() {
        try {
            Connection conn = new Connection(host);
            conn.connect();
            boolean isAuth = conn.authenticateWithPassword(user, password);
            if (isAuth) {
                connection = conn;

                String localFile = "C:\\Users\\DIYILIU\\Desktop\\monitor";
                String remoteDir = "/opt/java/monitor";

                transferDirectory(localFile, remoteDir);

                connection.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private Connection connection;

    /**
     * 远程传输单个文件	 * 	 * @param localFile	 * @param remoteTargetDirectory	 * @throws IOException
     */
    public void transferFile(String localFile, String remoteTargetDirectory) throws IOException {
        File file = new File(localFile);
        if (file.isDirectory()) {
            throw new RuntimeException(localFile + "  is not a file");
        }
        String fileName = file.getName();
        execCommand("cd " + remoteTargetDirectory + "; rm " + fileName + "; touch " + fileName);
        SCPClient sCPClient = connection.createSCPClient();
        SCPOutputStream scpOutputStream = sCPClient.put(fileName, file.length(), remoteTargetDirectory, "7777");
        IOUtils.copy(new FileInputStream(file), scpOutputStream);
        scpOutputStream.close();
    }

    /**
     * 传输整个目录	 * 	 * @param localFile	 * @param remoteTargetDirectory	 * @throws IOException
     */
    public void transferDirectory(String localDirectory, String remoteTargetDirectory) throws IOException {
        File dir = new File(localDirectory);
        if (!dir.isDirectory()) {
            throw new RuntimeException(localDirectory + " is not directory");
        }
        String[] files = dir.list();
        for (String file : files) {
            if (file.startsWith(".")) {
                continue;
            }
            String fullName = localDirectory + "/" + file;
            if (new File(fullName).isDirectory()) {
                String rdir = remoteTargetDirectory + "/" + file;
                execCommand("mkdir -p " + remoteTargetDirectory + "/" + file);
                transferDirectory(fullName, rdir);
            } else {
                transferFile(fullName, remoteTargetDirectory);
            }
        }
    }

    /**
     * Why can't I execute several commands in one single session?	 *
     * If you use Session.execCommand(), then you indeed can only execute only one command per session.This is not a restriction of the library, but rather an enforcement by the underlying SSH-2 protocol (a Session object models the underlying SSH-2 session).	 *
     * There are several solutions:	 * 	 * Simple: Execute several commands in one batch, e.g., something like Session.execCommand("echo Hello && echo again").
     * Simple: The intended way: simply open a new session for each command - once you have opened a connection, you can ask for as many sessions as you want, they are only a "virtual" construct.
     * Advanced: Don't use Session.execCommand(), but rather aquire a shell with Session.startShell().	 *
     *
     * @param command * @return	 * @throws IOException
     */
    public String execCommand(String command) throws IOException {
        Session session = connection.openSession();
        session.execCommand(command, StandardCharsets.UTF_8.toString());
        InputStream streamGobbler = new StreamGobbler(session.getStdout());

        String result = processStream(streamGobbler, "UTF-8");

        session.waitForCondition(ChannelCondition.EXIT_SIGNAL, Long.MAX_VALUE);
        if (session.getExitStatus().intValue() == 0) {
            System.out.println("execCommand:  success ");
        } else {
            System.err.println("execCommand :  fail");
        }
        IOUtils.closeQuietly(streamGobbler);
        session.close();
        return result;
    }


    @Test
    public void test11() throws Exception {
        DevNode node = new DevNode();
        node.setHost("192.168.1.181");
        node.setPort(22);
        node.setUser("root");
        node.setPwd("123456");

        String targetDir = "/opt/java";
        File file = new File("C:\\Users\\DIYILIU\\Desktop\\monitor\\config.properties");

        transferFile(node, file, targetDir);
    }

    /**
     * 远程传输
     */
    public static void transferFile(DevNode node, File file, String targetDir) throws IOException {
        String fileName = file.getName();

        String cmd = "cd " + targetDir + "; rm" + fileName + "; touch" + fileName;
        execCommand(node, cmd);

        Connection connection = new Connection(node.getHost(), node.getPort());
        connection.connect();
        boolean isAuth = connection.authenticateWithPassword(node.getUser(), node.getPwd());
        if (!isAuth) {
            System.out.println("连接建立失败");
            return;
        }
        SCPClient scpClient = new SCPClient(connection);
        SCPOutputStream os = scpClient.put(fileName, file.length(), targetDir, "7777");

        FileInputStream in = new FileInputStream(file);
        Properties properties = new Properties();
        properties.load(in);
        in.close();

        File file1 = new File("C:\\Users\\DIYILIU\\Desktop\\monitor\\config1.properties");
        properties.setProperty("localhost", "192.168.1.1");
        properties.store(new FileOutputStream(file1), "");

        file = file1;

        in = new FileInputStream(file);
        byte[] bytes = new byte[4096];
        int length;
        while ((length = in.read(bytes)) != -1) {
            os.write(bytes, 0, length);
        }

        os.flush();
        in.close();
        os.close();
        connection.close();
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

                String outStr = org.apache.commons.io.IOUtils.toString(streamOut, StandardCharsets.UTF_8);
                String outErr = org.apache.commons.io.IOUtils.toString(streamErr, StandardCharsets.UTF_8);
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
}
