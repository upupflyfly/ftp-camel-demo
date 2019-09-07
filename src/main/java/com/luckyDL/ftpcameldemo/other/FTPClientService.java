package com.luckyDL.ftpcameldemo.other;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author weid
 * @create 2019-09-07 9:42
 */
public interface FTPClientService {

    String readFileToBase64(String remoteFileName, String remoteDir);

    String readFile(String remoteDir) throws IOException;

    void download(String remoteFileName, String localFileName, String remoteDir);

    boolean uploadFile(InputStream inputStream, String originName, String remoteDir);

}
