import ch.ethz.ssh2.ChannelCondition;
import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.junit.Test;

import javax.persistence.Table;
import java.io.InputStream;

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
        exec(cmds, host, user,password);
    }


    @Test
    public void test1() throws Exception {
        String cmds = "ps -ef|grep  chat-server* |grep -v 'grep'";

        exec(cmds, host, user,password);
    }


    @Test
    public void test2() throws Exception{
        String str = "export JAVA_HOME=/usr/local/java/jdk1.8.0_151 \n " +
                "export PATH=$PATH:$JAVA_HOME/bin:$JAVA_HOME/jre/bin \n " +
                "screen -d -m java -server -Xms256M -jar /opt/java/chat/*.jar";

        exec(str, host, user,password);
    }

    /**
     * windows 也可以开启SSH
     *
     * @throws Exception
     */
    @Test
    public void test3() throws Exception{

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
    private String processStream(InputStream in, String charset) throws Exception {
        byte[] buf = new byte[1024];
        StringBuilder sb = new StringBuilder();
        while (in.read(buf) != -1) {
            sb.append(new String(buf, charset));
        }
        return sb.toString();
    }

    @Test
    public void test4(){
        String path = "/opt/java/chat/chat-server.jar";

        String proc = path.substring(path.lastIndexOf("/") + 1, path.lastIndexOf("."));
        System.out.println(proc);
    }
}
