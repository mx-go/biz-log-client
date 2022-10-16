package com.github.mx.bizlog.util;

import eu.bitwalker.useragentutils.UserAgent;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;

/**
 * 工具类
 */
@Slf4j
public class LogHelper {

    public static final String DEFAULT_SUCCESS_EXPRESSION = "{{T(com.alibaba.fastjson.JSON).toJSONString(#_ret, new com.github.mx.bizlog.parser.CustomValueFilter())}}";
    public static final String DEFAULT_DETAIL_EXPRESSION = "{{T(com.alibaba.fastjson.JSON).toJSONString(#_params, new com.github.mx.bizlog.parser.CustomValueFilter())}}";
    public static final String CONDITION = "condition";
    public static final String SUCCESS = "success";

    private static final char SEPARATOR = '_';
    private static final String UNKNOWN = "unknown";

    private static long CACHE_STAMP;
    private static InetAddress ADDRESS;

    public static String getSystemCode() {
        return StringUtils.defaultString(System.getenv("SYSTEM_CODE"), getHostName());
    }

    /**
     * 驼峰命名法工具
     *
     * @return toCamelCase(" hello_world ") == "helloWorld"
     * toCapitalizeCamelCase("hello_world") == "HelloWorld"
     * toUnderScoreCase("helloWorld") = "hello_world"
     */
    public static String toCamelCase(String s) {
        if (s == null) {
            return null;
        }
        s = s.toLowerCase();
        StringBuilder sb = new StringBuilder(s.length());
        boolean upperCase = false;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == SEPARATOR) {
                upperCase = true;
            } else if (upperCase) {
                sb.append(Character.toUpperCase(c));
                upperCase = false;
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * 驼峰命名法工具
     *
     * @return toCamelCase(" hello_world ") == "helloWorld"
     * toCapitalizeCamelCase("hello_world") == "HelloWorld"
     * toUnderScoreCase("helloWorld") = "hello_world"
     */
    public static String toCapitalizeCamelCase(String s) {
        if (s == null) {
            return null;
        }
        s = toCamelCase(s);
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }

    /**
     * 驼峰命名法工具
     *
     * @return toCamelCase(" hello_world ") == "helloWorld"
     * toCapitalizeCamelCase("hello_world") == "HelloWorld"
     * toUnderScoreCase("helloWorld") = "hello_world"
     */
    static String toUnderScoreCase(String s) {
        if (s == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        boolean upperCase = false;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            boolean nextUpperCase = true;
            if (i < (s.length() - 1)) {
                nextUpperCase = Character.isUpperCase(s.charAt(i + 1));
            }
            if ((i > 0) && Character.isUpperCase(c)) {
                if (!upperCase || !nextUpperCase) {
                    sb.append(SEPARATOR);
                }
                upperCase = true;
            } else {
                upperCase = false;
            }
            sb.append(Character.toLowerCase(c));
        }
        return sb.toString();
    }

    /**
     * 获取ip地址
     */
    public static String getIpAddr(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        String comma = ",";
        if (ip.contains(comma)) {
            ip = ip.split(",")[0];
        }
        String localhost = "127.0.0.1";
        if (localhost.equals(ip) || "0.0.0.0.0.0.0.1".equals(ip)) {
            // 获取本机真正的ip地址
            ip = getLocalIp();
        }
        return ip;
    }

    /**
     * 获取调用者请求的浏览器
     */
    public static String getBrowser(HttpServletRequest request) {
        UserAgent userAgent = UserAgent.parseUserAgentString(request.getHeader("User-Agent"));
        return userAgent.getBrowser().getName();
    }

    /**
     * 获取调用者请求的操作系统
     */
    public static String getOs(HttpServletRequest request) {
        UserAgent userAgent = UserAgent.parseUserAgentString(request.getHeader("User-Agent"));
        return userAgent.getOperatingSystem().getName();
    }

    /**
     * 获得当天是周几
     */
    public static String getWeekDay() {
        String[] weekDays = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());

        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0) {
            w = 0;
        }
        return weekDays[w];
    }

    /**
     * 获取当前机器的IP
     */
    public static String getLocalIp() {
        try {
            InetAddress candidateAddress = null;
            // 遍历所有的网络接口
            for (Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces(); interfaces.hasMoreElements(); ) {
                NetworkInterface anInterface = interfaces.nextElement();
                // 在所有的接口下再遍历IP
                for (Enumeration<InetAddress> inetAddresses = anInterface.getInetAddresses(); inetAddresses.hasMoreElements(); ) {
                    InetAddress address = inetAddresses.nextElement();
                    // 排除loopback类型地址
                    if (!address.isLoopbackAddress()) {
                        if (address.isSiteLocalAddress()) {
                            // 如果是site-local地址，就是它了
                            return address.getHostAddress();
                        } else if (candidateAddress == null) {
                            // site-local类型的地址未被发现，先记录候选地址
                            candidateAddress = address;
                        }
                    }
                }
            }
            if (candidateAddress != null) {
                return candidateAddress.getHostAddress();
            }
            // 如果没有发现 non-loopback地址.只能用最次选的方案
            InetAddress jdkSuppliedAddress = InetAddress.getLocalHost();
            if (jdkSuppliedAddress == null) {
                return "";
            }
            return jdkSuppliedAddress.getHostAddress();
        } catch (Exception e) {
            return "";
        }
    }

    public static String getHostName() {
        return getCachedAddress().getHostName();
    }

    private static InetAddress getCachedAddress() {
        long now = System.currentTimeMillis();
        if (now - CACHE_STAMP > 300000) {
            CACHE_STAMP = now;
            try {
                ADDRESS = InetAddress.getLocalHost();
            } catch (UnknownHostException e) {
                log.error("unknown host", e);
            }
        }
        return ADDRESS;
    }

    /**
     * LocalDateTime转毫秒时间戳
     *
     * @param localDateTime localDateTime
     * @return 毫秒时间戳
     */
    public static long localDateTime2Stamp(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            throw new IllegalArgumentException("localDateTime is null");
        }
        return localDateTime.toInstant(ZoneOffset.of("+8")).toEpochMilli();
    }

    /**
     * 毫秒时间戳转LocalDateTime
     *
     * @param timestamp 毫秒时间戳
     * @return LocalDateTime
     */
    public static LocalDateTime stamp2LocalDateTime(Long timestamp) {
        if (timestamp == null) {
            return null;
        }
        Instant instant = Instant.ofEpochMilli(timestamp);
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }
}