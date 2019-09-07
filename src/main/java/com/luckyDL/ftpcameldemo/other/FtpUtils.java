package com.luckyDL.ftpcameldemo.other;

//import org.apache.commons.io.IOUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import sun.misc.IOUtils;


import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;


/**
 * @compony sunyard
 * @Auther: jiangtao
 * @Date: 2018/11/22 16:05
 * @Description: <p> Ftp 文件上传工具类 </p>
 */
public class FtpUtils {


    /**
     * 日志对象
     **/
    private static final Logger log = LoggerFactory.getLogger(FtpUtils.class);

    /**
     * 本地字符编码
     **/
    private static String localCharset = "UTF-8";

    /**
     * FTP协议里面，规定文件名编码为iso-8859-1
     **/
    private static String serverCharset = "ISO-8859-1";

    /**
     * UTF-8字符编码
     **/
    private static final String CHARSET_UTF8 = "UTF-8";

    /**
     * OPTS UTF8字符串常量
     **/
    private static final String OPTS_UTF8 = "OPTS UTF8";

    /**
     * 设置缓冲区大小4M
     **/
    private static final int BUFFER_SIZE = 1024 * 1024 * 4;

    /**
     * FTPClient对象
     **/
    private static FTPClient ftpClient = null;
    /**
     * 财务系统上送文件标识
     */
    private static final String CW_FTP = "cw";
    /**
     * 核心系统上文文件标识
     */
    private static final String HX_FTP = "hx";
    /**
     * 财务系统ftp连接信息
     */


    @Autowired
    private ApplicationEntity applicationEntity;



    public static void main(String[] args) throws Exception {
        FtpUtils ftpUtils = new FtpUtils();
        InputStream in = new FileInputStream("E:\\upload\\timg.jpg");
        ftpUtils.uploadFile(in, "aa", "/mytest", "jpg");
    }

    /**
     * 输入流上传到FTP服务器
     *
     * @param sbs      输入流
     * @param fileName 上传到FTP服务的文件名，例如：666.txt
     * @return boolean 成功返回true，否则返回false
     */
    public boolean uploadFile(InputStream sbs, String fileName, String fpath, String type) throws IOException {
        String dxp_username = applicationEntity.getFtpUsername();
        String dxp_address = applicationEntity.getFtpHost();
        int dxp_port = applicationEntity.getFtpPort();
        String dxp_password = applicationEntity.getFtpPassword();
        String BASE_PATH = null;
        String creat_path = null;
        if (CW_FTP.equals(type)) {
            //登录财务系统ftp
            login(dxp_address, dxp_port, dxp_username, dxp_password);
            BASE_PATH = "/mytest/";
            creat_path = "";
        }
        boolean flag = false;
        if (ftpClient != null) {
            try {
                ftpClient.setBufferSize(BUFFER_SIZE);
                // 设置编码：开启服务器对UTF-8的支持，如果服务器支持就用UTF-8编码，否则就使用本地编码（GBK）
                if (FTPReply.isPositiveCompletion(ftpClient.sendCommand(OPTS_UTF8, "ON"))) {
                    localCharset = CHARSET_UTF8;
                }
                ftpClient.setControlEncoding(localCharset);
                ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
                String path = changeEncoding(BASE_PATH + fpath);// 基础目录+相对路径
                // 目录不存在，则递归创建
                if (!ftpClient.changeWorkingDirectory(path)) {
                    createDirectorys(creat_path + fpath);
                }
                // 设置被动模式，开通一个端口来传输数据
                ftpClient.enterLocalPassiveMode();
                // 上传文件
                flag = ftpClient.storeFile(new String(fileName.getBytes(localCharset), serverCharset), sbs);
                log.info("输入流上传FTP结果 : " + flag);
            } catch (Exception e) {
                log.error("输入流上传FTP失败 ", e);
            } finally {
                sbs.close();
                closeConnect();
            }
        }
        return flag;
    }


    /**
     * 连接FTP服务器
     *
     * @param address  地址，如：127.0.0.1
     * @param port     端口，如：21
     * @param username 用户名，如：root
     * @param password 密码，如：root
     */
    private static void login(String address, int port, String username, String password) {
        ftpClient = new FTPClient();
        try {
            log.info("登陆ftp服务器: " + address + ":" + port);
            ftpClient.connect(address, port);
            ftpClient.login(username, password);
            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
            int reply = ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                closeConnect();
                log.error("FTP服务器连接失败");
            }
        } catch (Exception e) {
            log.error("FTP登录失败", e);
        }
    }

    /**
     * 关闭FTP连接
     */
    private static void closeConnect() {
        if (ftpClient != null && ftpClient.isConnected()) {
            try {
                ftpClient.logout();
                ftpClient.disconnect();
            } catch (IOException e) {
                log.error("关闭FTP连接失败", e);
            }
        }
    }

    /**
     * FTP服务器路径编码转换
     *
     * @param ftpPath FTP服务器路径
     * @return String
     */
    private static String changeEncoding(String ftpPath) {
        String directory = null;
        try {
            if (FTPReply.isPositiveCompletion(ftpClient.sendCommand(OPTS_UTF8, "ON"))) {
                localCharset = CHARSET_UTF8;
            }
            directory = new String(ftpPath.getBytes(localCharset), serverCharset);
        } catch (Exception e) {
            log.error("路径编码转换失败", e);
        }
        return directory;
    }

    /**
     * 在服务器上递归创建目录
     *
     * @param dirPath 上传目录路径
     * @return
     */
    private static void createDirectorys(String dirPath) {
        try {
            if (!dirPath.endsWith("/")) {
                dirPath += "/";
            }
            String directory = dirPath.substring(0, dirPath.lastIndexOf("/") + 1);
            ftpClient.makeDirectory("/");
            int start = 0;
            int end = 0;
            if (directory.startsWith("/")) {
                start = 1;
            } else {
                start = 0;
            }
            end = directory.indexOf("/", start);
            while (true) {
                String subDirectory = new String(dirPath.substring(start, end));
                if (!ftpClient.changeWorkingDirectory(subDirectory)) {
                    if (ftpClient.makeDirectory(subDirectory)) {
                        ftpClient.changeWorkingDirectory(subDirectory);
                    } else {
                        log.info("创建目录失败");
                        return;
                    }
                }
                start = end + 1;
                end = directory.indexOf("/", start);
                //检查所有目录是否创建完毕
                if (end <= start) {
                    break;
                }
            }
        } catch (Exception e) {
            log.error("上传目录创建失败", e);
        }
    }


}
