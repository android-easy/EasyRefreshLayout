package com.androideasy.library.utils;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 日志工具类
 * 使打印日志变得简单
 * 自动识别调用日志函数的类名 方法名 与位置
 * 不需要繁琐的TAG
 *
 * @author Easy
 * QQ 1400100300
 */
public class EasyLog {
    private static String tag = "AppName";
    private static boolean OPEN = true;
    private static boolean OPEN_JSON = true;

    private static EasyLog log  ;
    private static final char HORIZONTAL_LINE = '│';
    private static final String HEAD_DIVIDER    = "┌ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ";
    private static final String CONTENT_DIVIDER = "┌ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ";
//    private static final String CONTENT_DIVIDER = "├ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ";
    private static final String FOOT_DIVIDER =    "└ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ";

    public static void setTag(String tag) {
        EasyLog.tag = tag;
    }

    public static void openLog(boolean OPEN) {
        EasyLog.OPEN = OPEN;
    }

    public static void setOpenJson(boolean openJson) {
        OPEN_JSON = openJson;
    }

    private EasyLog() {
    }

    public static EasyLog getInstance() {
        if (log == null) {
            synchronized (EasyLog.class) {
                if (log == null) {
                    log = new EasyLog();
                }
            }
        }
        return log;
    }

    /**
     * 获取调用log方法的名字
     *
     * @return Name
     */
    private String getFunctionName() {
        StackTraceElement[] sts = Thread.currentThread().getStackTrace();
        if (sts == null) {
            return null;
        }
        for (StackTraceElement st : sts) {
            if (st.isNativeMethod()) {
                continue;
            }
            if (st.getClassName().equals(Thread.class.getName())) {
                continue;
            }
            if (st.getClassName().equals(this.getClass().getName())) {
                continue;
            }
            return "  Thread : " + Thread.currentThread().getName() + " --- ("
                    + st.getFileName() + ":" + st.getLineNumber() + ") "
                    + st.getMethodName() + "  ";

//            return "│ Thread : " + Thread.currentThread().getName() + " --- ("
//                    + st.getFileName() + ":" + st.getLineNumber() + ") "
//                    + st.getMethodName() + "  ";
        }
        return null;
    }


    public static void i(Object str) {
        printLog(Log.INFO, str);
    }

    public static void d(Object str) {
        printLog(Log.DEBUG, str);
    }

    public static void v(Object str) {
        printLog(Log.VERBOSE, str);
    }

    public static void w(Object str) {
        printLog(Log.WARN, str);
    }

    public static void e(Object str) {
        printLog(Log.ERROR, str);
    }

    /**
     * 调用系统的打印
     *
     * @param index
     * @param str
     */
    private static void printLog(int index, Object str) {

        if(!OPEN){
            return;
        }
        log = getInstance();

        String function = log.getFunctionName();

        String result = getPrintStr(function, str);
//		if (function != null) {
//			str = function + " - " + str;
//		}

        result = function + "  " + result;

        switch (index) {
            case Log.VERBOSE:
 //        print.append("\n " + function);
//                Log.v(tag, " " + HEAD_DIVIDER );
//                Log.v(tag, " " + function );
                Log.v(tag, result);
                break;
            case Log.DEBUG:
//                Log.d(tag, " " + HEAD_DIVIDER );
//                Log.d(tag, " " + function );
                Log.d(tag, result);
                break;
            case Log.INFO:
//                Log.i(tag, " " + HEAD_DIVIDER );
//                Log.i(tag, " " + function );
                Log.i(tag, result);
                break;
            case Log.WARN:
//                Log.w(tag, " " + HEAD_DIVIDER );
//                Log.w(tag, " " + function );
                Log.w(tag, result);
                break;
            case Log.ERROR:
//                Log.e(tag, " " + HEAD_DIVIDER );
//                Log.e(tag, " " + function );
                Log.e(tag, result);
                break;
            default:
                break;
        }
    }

    protected static String getPrintStr(String function, Object str) {
        StringBuffer print = new StringBuffer();
//        print.append(" ").append(HEAD_DIVIDER);
//        print.append("\n " + function);
        str = toString(str);
        print.append("\n ").append(CONTENT_DIVIDER);
        print.append("\n ").append(HORIZONTAL_LINE).append(str);
        print.append("\n ").append(FOOT_DIVIDER);
        return print.toString();
    }

    public static String toString(Object object) {
        if (object == null) {
            return "null";
        }
        //Json处理
        if (object instanceof String) {
            if(OPEN_JSON){
                return jsonToString((String) object);
            }
            return object.toString();
        }
        //List处理
        if (object instanceof List) {
            return listToString((List) object);
        }
        //Map处理
        if (object instanceof Map) {
            return mapToString((Map) object);
        }
        //数组处理
        if (object.getClass().isArray()) {
            return getArrayString(object);
        }
        return object.toString();
    }

    //数组处理
    public static String getArrayString(Object object) {
        if (object instanceof boolean[]) {
            return Arrays.toString((boolean[]) object);
        }
        if (object instanceof byte[]) {
            return Arrays.toString((byte[]) object);
        }
        if (object instanceof char[]) {
            return Arrays.toString((char[]) object);
        }
        if (object instanceof short[]) {
            return Arrays.toString((short[]) object);
        }
        if (object instanceof int[]) {
            return Arrays.toString((int[]) object);
        }
        if (object instanceof long[]) {
            return Arrays.toString((long[]) object);
        }
        if (object instanceof float[]) {
            return Arrays.toString((float[]) object);
        }
        if (object instanceof double[]) {
            return Arrays.toString((double[]) object);
        }
        if (object instanceof Object[]) {
            return Arrays.deepToString((Object[]) object);
        }
        return "not Array";
    }

    /**
     * json 处理
     */
    static int JSON_INDENT = 2;
    static String LINE_SEPARATOR = System.getProperty("line.separator");

    public static String jsonToString(String jsonStr) {
        String message;
        try {
            if (jsonStr.startsWith("{")) {
                JSONObject jsonObject = new JSONObject(jsonStr);
                message = jsonObject.toString(JSON_INDENT); //这个是核心方法
            } else if (jsonStr.startsWith("[")) {
                JSONArray jsonArray = new JSONArray(jsonStr);
                message = jsonArray.toString(JSON_INDENT);
            } else {
                return jsonStr;
            }
        } catch (JSONException e) {
            return jsonStr;
        }

        String[] lines = message.split(LINE_SEPARATOR);
        StringBuilder stringBuffer = new StringBuilder();
        stringBuffer.append("Type : ").append(" Json");
        for (String line : lines) {
            stringBuffer.append("\n ").append(HORIZONTAL_LINE).append(line);
        }
        return stringBuffer.toString();
    }

    /**
     * 处理List
     */
    public static String listToString(List list) {
        if (list.size() < 1) {
            return "[]";
        }
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("   Type : ").append(list.getClass().getSimpleName());
        for (int i = 0; i < list.size(); i++) {
            if (i % 3 == 0) {
                stringBuffer.append("\n ").append(HORIZONTAL_LINE).append("   ");
            } else {
                stringBuffer.append(" , ");
            }
            stringBuffer.append(list.get(i));
        }
        return stringBuffer.toString();
    }

    /**
     * 处理Map
     */
    public static String mapToString(Map map) {
        Set set = map.entrySet();
        if (set.size() < 1) {
            return "[]";
        }
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("   Type : ").append(map.getClass().getSimpleName());
        for (Object aSet : set) {
            Map.Entry entry = (Map.Entry) aSet;
            stringBuffer.append("\n ").append(HORIZONTAL_LINE);
            stringBuffer.append("   key : ").append(entry.getKey()).append(" ---- value : ").append(entry.getValue());
        }
        return stringBuffer.toString();
    }
}
