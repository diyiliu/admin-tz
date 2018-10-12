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
    public void test6() throws Exception{
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



}