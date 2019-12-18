package com.lsj.plugin;

import java.util.HashMap;
import java.util.Map;

/**
 * 暂时不考虑多线程问题
 *
 * @author shijun_lin
 * @date 2019/12/18
 */
public class TimeCostCache {
    public static Map<String, Long> sStartTime = new HashMap<>();
    public static Map<String, Long> sEndTime = new HashMap<>();

    public static void setStartTime(String methodName, long time) {
        sStartTime.put(methodName, time);
    }

    public static void setEndTime(String methodName, long time) {
        sEndTime.put(methodName, time);
    }

    public static String getCostTime(String methodName) {
        long start = sStartTime.get(methodName);
        long end = sEndTime.get(methodName);
        return "method: " + methodName + " cost " + Long.valueOf(end - start) + " ns";
    }

}
