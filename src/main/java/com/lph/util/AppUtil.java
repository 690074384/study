package com.lph.util;

import com.google.common.collect.Lists;
import org.codehaus.jackson.map.util.JSONPObject;

import java.util.List;
import java.util.Map;

/**
 * 接口参数校验
 *
 * @author lvpenghui
 * @since 2019-4-17 17:23:35
 */
public class AppUtil {

    protected static Logger logger = Logger.getLogger(AppUtil.class);

    /**
     * 检查参数是否完整
     *
     * @param method 方法
     * @param pd     PageData对象
     * @return 参数是否完整
     */
    public static boolean checkParam(String method, PageData pd) {
        boolean result = false;

        int falseCount = 0;
        String[] paramArray = new String[20];
        String[] valueArray = new String[20];
        //临时数组
        String[] tempArray = new String[20];

        // 注册
        if (Constants.REGISTER_SYS_USER.equals(method)) {
            //参数
            paramArray = Constants.SYSUSER_REGISTERED_PARAM_ARRAY;
            //参数名称
            valueArray = Constants.SYSUSER_REGISTERED_VALUE_ARRAY;
            //根据用户名获取会员信息
        } else if (Constants.GET_APP_USER_BY_NAME.equals(method)) {
            paramArray = Constants.APP_GETAPPUSER_PARAM_ARRAY;
            valueArray = Constants.APP_GETAPPUSER_VALUE_ARRAY;
        }
        int size = paramArray.length;
        for (int i = 0; i < size; i++) {
            String param = paramArray[i];
            if (!pd.containsKey(param)) {
                tempArray[falseCount] = valueArray[i] + "--" + param;
                falseCount += 1;
            }
        }
        if (falseCount > 0) {
            logger.error(method + "接口，请求协议中缺少 " + falseCount + "个 参数");
            for (int j = 1; j <= falseCount; j++) {
                logger.error("   第" + j + "个：" + tempArray[j - 1]);
            }
        } else {
            result = true;
        }

        return result;
    }

    /**
     * 设置分页的参数
     *
     * @param pd PageData对象
     * @return PageData对象
     */
    public static PageData setPageParam(PageData pd) {
        String pageNowStr = pd.get("page_now").toString();
        int pageNowInt = Integer.parseInt(pageNowStr) - 1;
        //每页显示记录数
        String pageSizeStr = pd.get("page_size").toString();
        int pageSizeInt = Integer.parseInt(pageSizeStr);
        String pageNow = pageNowInt + "";
        String pageStart = (pageNowInt * pageSizeInt) + "";
        pd.put("page_now", pageNow);
        pd.put("page_start", pageStart);
        return pd;
    }

    /**
     * 设置list中的distance
     *
     * @param list List<PageData>对象
     * @param pd   PageData对象
     * @return List<PageData>对象
     */
    public static List<PageData> setListDistance(List<PageData> list, PageData pd) {
        List<PageData> listReturn = Lists.newArrayList();
        String userLongitude = "";
        String userLatitude = "";
        try {
            //"117.11811"
            userLongitude = pd.get("user_longitude").toString();
            //"36.68484"
            userLatitude = pd.get("user_latitude").toString();

        } catch (Exception e) {
            logger.error("缺失参数--user_longitude和user_longitude");
            logger.error("lost param：user_longitude and user_longitude");
        }
        PageData pdTemp;
        for (PageData aList : list) {
            pdTemp = aList;
            String longitude = pdTemp.get("longitude").toString();
            String latitude = pdTemp.get("latitude").toString();
            String distance = MapDistance.getDistance(
                    userLongitude, userLatitude,
                    longitude, latitude
            );
            pdTemp.put("distance", distance);
            pdTemp.put("size", distance.length());
            listReturn.add(pdTemp);
        }
        return listReturn;
    }

    /**
     * @param pd  PageData对象
     * @param map Map对象
     * @return 返回结果
     */
    public static Object returnObject(PageData pd, Map map) {
        if (pd.containsKey(Constants.CALL_BACK)) {
            String callback = pd.get("callback").toString();
            return new JSONPObject(callback, map);
        } else {
            return map;
        }
    }
}
