package com.cy.config;

import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.forest.interceptor.Interceptor;
import com.dtflys.forest.logging.DefaultLogHandler;
import com.dtflys.forest.logging.ResponseLogMessage;
import com.dtflys.forest.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

/**
 * Forest日志拦截器
 *
 * @param <T>
 * @author cypei
 */
@Component
public class ForestLogInterceptor<T> extends DefaultLogHandler implements Interceptor<T> {

    private static final Logger LOG = LoggerFactory.getLogger(ForestLogInterceptor.class);

    @Override
    public void logContent(String content) {
        //日志均走onSuccess、onError方法
    }

    /**
     * 使用该方式而不使用forest自带日志打印原因如下：
     * <ol>
     *     <li>forest自带日志请求、响应打印，存在交叉紊乱情况（多个请求同时调用）</li>
     * </ol>
     */
    @Override
    public void onSuccess(T data, ForestRequest req, ForestResponse res) {
        log(req, res, LOG::info);
    }

    /**
     * 该方法在请求发送失败时被调用
     */
    @Override
    public void onError(ForestRuntimeException ex, ForestRequest req, ForestResponse res) {
        log(req, res, LOG::error);
        LOG.warn(ex.getMessage(), ex);
    }

    private void log(ForestRequest req, ForestResponse res, Consumer<String> action) {
        String content = log(req, res);
        action.accept(content);
    }

    private String log(ForestRequest req, ForestResponse res) {
        String requestContent = requestLoggingContent(req.getRequestLogMessage());

        ResponseLogMessage responseLogMessage = new ResponseLogMessage(res, res.getStatusCode());
        String responseStatus = responseLoggingContent(responseLogMessage);
        String responseContent = logResponseContentStr(responseLogMessage);

        return String.format("[FOREST完整请求日志] %s\n%s\n%s", requestContent, responseStatus, responseContent);
    }

    private String logResponseContentStr(ResponseLogMessage responseLogMessage) {
        if (responseLogMessage.getResponse() != null) {
            String content = responseLogMessage.getResponse().getContent();
            if (StringUtils.isNotEmpty(content)) {
                return "Response Content:\n\t" + content;
            }
        }
        return "Response Content:\n\t null";
    }

}
