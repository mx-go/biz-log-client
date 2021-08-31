package com.github.mx.bizlog.extend.defaults;

import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.common.utils.StringUtils;
import com.github.mx.bizlog.extend.ParseFunction;
import com.github.mx.bizlog.util.LogHelper;
import com.google.common.collect.Maps;
import eu.bitwalker.useragentutils.OperatingSystem;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * Create by max on 2021/02/27
 **/
public class DefaultParseFunction implements ParseFunction {

    @Override
    public String functionName() {
        return "USERAGENT";
    }

    @Override
    public String apply(String value) {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes != null) {
            HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
            Map<String, String> map = Maps.newHashMap();
            String ipAddr = LogHelper.getIpAddr(request);
            String browser = LogHelper.getBrowser(request);
            String os = LogHelper.getOs(request);
            if (isLegal(ipAddr)) {
                map.put("ip", LogHelper.getIpAddr(request));
            }
            if (isLegal(browser)) {
                map.put("browser", browser);
            }
            if (isLegal(os)) {
                map.put("os", LogHelper.getOs(request));
            }
            return map.isEmpty() ? null : JSON.toJSONString(map);
        }
        return null;
    }

    private boolean isLegal(String param) {
        return StringUtils.isNotBlank(param) && !OperatingSystem.UNKNOWN.getName().equalsIgnoreCase(param);
    }
}