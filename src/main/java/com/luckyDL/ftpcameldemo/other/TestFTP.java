package com.luckyDL.ftpcameldemo.other;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.io.*;

/**
 * @author weid
 * @create 2019-09-07 9:46
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class TestFTP {

    @Autowired
    private FTPClientService ftpClientService;

    @Test
    public void test1() {
        try {
            InputStream inputStream = new FileInputStream(new File("E:\\upload\\timg.jpg"));
//            ftpClientService.uploadFile(inputStream, "000.jpg", "./");
            ftpClientService.readFile("/mytest");

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
