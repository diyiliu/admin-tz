import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.ScheduledFuture;

/**
 * Description: TestMain
 * Author: DIYILIU
 * Update: 2018-09-18 20:21
 */
public class TestMain {


    @Test
    public void test() {

        String name = "pic.jpg";

        System.out.println(name.substring(name.lastIndexOf(".")));
    }

    @Test
    public void test2() {


        BigDecimal scaleX = new BigDecimal(256.00).divide(new BigDecimal(474), 2, RoundingMode.FLOOR);

        System.out.println(scaleX);
    }


    @Test
    public void test3() {

        String str = ".\\upload\\photo\\mem1926648502757059758.jpg";

        System.out.println(str.lastIndexOf(File.separator));
        System.out.println(str.substring(str.lastIndexOf(File.separator)));
    }

    @Test
    public void test4() {
        String str = "34,35,";

        String[] array = str.split(",");

        System.out.println(array.length);

        Long[] ids = (Long[]) ConvertUtils.convert(array, Long.class);
    }

    @Test
    public void test5() {


        int i = Double.valueOf(Math.random() * 3 + 1).intValue();
        System.out.println(i);
    }


    @Test
    public void test6() throws Exception {
        File file1 = new File("C:\\Users\\DIYILIU\\Desktop\\config.properties");
        File file2 = new File("C:\\Users\\DIYILIU\\Desktop\\config2.properties");

        InputStream inputStream = new FileInputStream(file1);
        Properties properties = new Properties();
        properties.load(inputStream);
        //inputStream.close();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        properties.setProperty("localhost", "192.168.1.181");
        properties.store(outputStream, "");

        FileOutputStream fileOutputStream = new FileOutputStream(file2);
        fileOutputStream.write(outputStream.toByteArray());
    }

    @Test
    public void test7() {

        System.out.println(mb2Gb(2049));
    }

    private int mb2Gb(int size) {

        return new BigDecimal(size).divide(new BigDecimal(1024), 0, RoundingMode.UP).intValue();
    }

    @Test
    public void test77() {
        double d = 0.123;

        System.out.println(String.format("%.1f%%", d * 100));
    }

    @Test
    public void test8() {
        String str = "USER       PID";

        System.out.println(str.replaceAll("\\s+", ""));

        System.out.println(str.replaceAll("\\s{1,}", ""));
    }

    @Test
    public void test9() {
        String str = "USER       PID %CPU %MEM    VSZ   RSS TTY      STAT START   TIME COMMAND\n" +
                     "root      2002  4.5  8.2 2509468 159224 pts/1  Ssl+ 16:13   3:27 java -server -Xms256M -jar estar-gw.jar\n" +
                     "root      7844  1.0  5.2 2517128 100436 ?      Sl   17:05   0:15 java -jar /opt/java/monitor/monitor-client.jar\n" +
                     "root     15737  2.2  0.2 100348  4016 ?        Ss   17:28   0:00 sshd: root@notty \n" +
                     "root     29003  0.0  0.2 100348  4056 ?        Ss   16:03   0:00 sshd: root@pts/0 \n" +
                     "root     29199  0.0  0.2 101984  5700 ?        Ss   16:03   0:02 sshd: root@notty \n";


        String[] array = str.split("\n");
        String header = array[0];
        int index = header.lastIndexOf(" ");
        for (int i = 1; i < array.length; i++){
            String info = array[i];
            String content = info.substring(0, index).replaceAll("\\s+", " ");
            String name = info.substring(index);

            String[] items = content.split(" ");
            String pid = items[1];
            String cpu = items[2];
            String mem = items[3];

            System.out.println(pid + ", " + cpu + ", " + mem + ", " + name);
        }
    }
}