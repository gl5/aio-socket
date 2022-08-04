/*******************************************************************************
 * Copyright (c) 2017-2020, org.smartboot. All rights reserved.
 * project name: smart-http
 * file name: StaticResourceHandle.java
 * Date: 2020-01-01
 * Author: sandao (zhengjunweimail@163.com)
 ******************************************************************************/

package io.github.mxd888.http.server.handler;



import io.github.mxd888.http.common.enums.HeaderNameEnum;
import io.github.mxd888.http.common.enums.HttpMethodEnum;
import io.github.mxd888.http.common.enums.HttpStatus;
import io.github.mxd888.http.common.logging.Logger;
import io.github.mxd888.http.common.logging.LoggerFactory;
import io.github.mxd888.http.common.utils.DateUtils;
import io.github.mxd888.http.common.utils.Mimetypes;
import io.github.mxd888.http.common.utils.StringUtils;
import io.github.mxd888.http.server.HttpRequest;
import io.github.mxd888.http.server.HttpResponse;
import io.github.mxd888.http.server.HttpServerHandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Date;

/**
 * 静态资源加载Handle
 *
 * @author 三刀
 * @version V1.0 , 2018/2/7
 */
public class HttpStaticResourceHandler extends HttpServerHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpStaticResourceHandler.class);
    private static final int READ_BUFFER = 1024 * 1024;
    private static final String URL_404 =
            "<html>" +
                    "<head>" +
                    "<title>smart-http 404</title>" +
                    "</head>" +
                    "<body><h1>smart-http 找不到你所请求的地址资源，404</h1></body>" +
                    "</html>";

    private final File baseDir;

    public HttpStaticResourceHandler(String baseDir) {
        this.baseDir = new File(new File(baseDir).getAbsolutePath());
        if (!this.baseDir.isDirectory()) {
            throw new RuntimeException(baseDir + " is not a directory");
        }
        if(LOGGER.isInfoEnabled())
            LOGGER.info("dir is:{}", this.baseDir.getAbsolutePath());
    }

    @Override
    public void handle(HttpRequest request, HttpResponse response) throws IOException {
        String fileName = request.getRequestURI();
        String method = request.getMethod();
        if (StringUtils.endsWith(fileName, "/")) {
            fileName += "index.html";
        }
        if(LOGGER.isInfoEnabled())
            LOGGER.info("请求URL: " + fileName);
        File file = new File(baseDir, URLDecoder.decode(fileName, "utf8"));
        //404
        if (!file.isFile()) {
            LOGGER.warn("file: {} not found!", request.getRequestURI());
            response.setHttpStatus(HttpStatus.NOT_FOUND);
            response.setHeader(HeaderNameEnum.CONTENT_TYPE.getName(), "text/html; charset=utf-8");

            if (!HttpMethodEnum.HEAD.getMethod().equals(method)) {
                response.write(URL_404.getBytes());
            }
            return;
        }
        //304
        Date lastModifyDate = new Date(file.lastModified());
        try {
            String requestModified = request.getHeader(HeaderNameEnum.IF_MODIFIED_SINCE.getName());
            if (StringUtils.isNotBlank(requestModified) && lastModifyDate.getTime() <= DateUtils.parseLastModified(requestModified).getTime()) {
                response.setHttpStatus(HttpStatus.NOT_MODIFIED);
                return;
            }
        } catch (Exception e) {
            LOGGER.error("exception", e);
        }
        response.setHeader(HeaderNameEnum.LAST_MODIFIED.getName(), DateUtils.formatLastModified(lastModifyDate));


        String contentType = Mimetypes.getInstance().getMimetype(file);
        response.setHeader(HeaderNameEnum.CONTENT_TYPE.getName(), contentType + "; charset=utf-8");
        //HEAD不输出内容
        if (HttpMethodEnum.HEAD.getMethod().equals(method)) {
            return;
        }

        if (!file.getName().endsWith("html") && !file.getName().endsWith("htm")) {
            response.setContentLength((int) file.length());
        }


        FileInputStream fis = new FileInputStream(file);
        FileChannel fileChannel = fis.getChannel();
        long fileSize = fileChannel.size();
        long readPos = 0;
        while (readPos < fileSize) {
            MappedByteBuffer mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, readPos, fileSize - readPos > READ_BUFFER ? READ_BUFFER : fileSize - readPos);
            readPos += mappedByteBuffer.remaining();
            byte[] data = new byte[mappedByteBuffer.remaining()];
            mappedByteBuffer.get(data);
            response.write(data);
        }
        fileChannel.close();
        fis.close();
    }
}
