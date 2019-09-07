package com.luckyDL.ftpcameldemo.other;

import java.io.InputStream;

/**
 * @author weid
 * @create 2019-09-07 9:42
 */
public interface FTPClientService {

    String readFileToBase64(String remoteFileName, String remoteDir);

    void download(String remoteFileName, String localFileName, String remoteDir);

    boolean uploadFile(InputStream inputStream, String originName, String remoteDir);

}
