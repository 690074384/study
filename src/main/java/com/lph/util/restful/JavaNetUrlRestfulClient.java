package com.lph.util.restful;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lph.util.Constants;
import com.lph.util.MD5;
import com.lph.util.restful.bean.OrderDetail;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

/**
 * @author lvpenghui
 * @since 2019-5-24 15:14:05
 */
public class JavaNetUrlRestfulClient {
    private static Logger logger = Logger.getLogger(JavaNetUrlRestfulClient.class);

    private static final String KEY = "";
    private static final String SKEY = "";
    private static final String ID = "";

    public static void main(String[] args) {
        //revertAllOrder("gat");
        //卖
          sell("bchabc", "3139.00");
        //买
     //   buy("bchabc", "3117.00");

    }

    /**
     * 卖
     *
     * @param coinType 交易类型 1买入，2卖出
     * @param coinType 币种
     * @param price    单价
     */
    private static void sell(String coinType, String price) {
        String myAccount = getAccount();
        JSONObject jsonObject = JSONObject.parseObject(myAccount);
        for (String s : jsonObject.keySet()) {
            logger.info(s + "--" + jsonObject.getString(s));
        }
        String amount = jsonObject.getString(coinType + "_balance");
        logger.info(submitOrder("2", price, amount.substring(0, amount.length() - 2), coinType));

    }

    /**
     * 卖
     *
     * @param coinType 交易类型 1买入，2卖出
     * @param coinType 币种
     * @param price    单价
     */
    private static void buy(String coinType, String price) {
        String myAccount = getAccount();
        JSONObject jsonObject = JSONObject.parseObject(myAccount);
        for (String s : jsonObject.keySet()) {
            logger.info(s + "--" + jsonObject.getString(s));
        }
        String amount = jsonObject.getString("cnc_balance");
        logger.info("cnc:{" + amount);
        logger.info(amount.substring(0, amount.length() - 2));
        Double total = Double.parseDouble(amount);
        total /= Double.parseDouble(price);
        logger.info(String.valueOf(total).substring(0, amount.length() - 6));
        logger.info(submitOrder("1", price, String.valueOf(total).substring(0, amount.length() - 6), coinType));

    }

    /**
     * 撤销某币种的全部订单
     *
     * @param coinType 币种
     */
    private static void revertAllOrder(String coinType) {
        String currentOrder = myCurrentOrder(coinType);
        if (StringUtils.isNotBlank(currentOrder) && !Constants.NO_ORDER.equals(currentOrder)) {
            List<OrderDetail> orderDetails = JSONArray.parseArray(currentOrder, OrderDetail.class);
            for (OrderDetail orderDetail : orderDetails) {
                logger.info("订单id:" + orderDetail + "撤销结果:" + revertOrder(orderDetail.getId(), orderDetail.getCoinname()));
            }
        } else {
            logger.info("当前交易对:【" + coinType + "】,未挂单!");
        }
    }

    /**
     * 所有接口
     */
    private static void test() {
        String coinType = "gat";
        String tradeNum = "5";
        logger.info("获取全部交易对信息：" + getTradeMessage());
        logger.info("获取单个交易对【" + coinType + "】价格：" + getTradeMessage(coinType));
        logger.info("获取账户余额：" + getAccount());
        logger.info("获取单个交易对【" + coinType + "】深度：" + getDepth(coinType));
        logger.info("获取单个交易对【" + coinType + "】近期交易情况：" + getNearByTrade(coinType));
        logger.info("获取单个交易对【" + coinType + "】近【" + tradeNum + "】笔交易情况：" + getNearByTrade(coinType, tradeNum));
        String s = submitOrder("2", "2", "100", coinType);
        String orderId = s.split("\\|")[1];
        logger.info("卖出【" + coinType + "】：" + s);
        logger.info("撤销订单【" + coinType + "】，订单号：【" + orderId + "】" + "撤单结果:【" + revertOrder(orderId, coinType) + "】");
        logger.info("当前的单子:" + myCurrentOrder(coinType));
    }


    /**
     * 获取当前币种挂着的单子
     *
     * @param coinname 币种
     * @return 撤单结果
     */
    private static String myCurrentOrder(String coinname) {
        String url = "https://api.aex.zone/getOrderList.php";
        String time = getTime();
        String content = "key=" + KEY + "&time=" + time + "&md5=" + getMd5(time) + "&mk_type=cnc&coinname=" + coinname;
        return postMessage(url, content);
    }

    /**
     * 交易撤销接口
     *
     * @param orderId  订单号
     * @param coinname 币种
     * @return 撤单结果
     */
    private static String revertOrder(String orderId, String coinname) {
        String url = "https://api.aex.zone/cancelOrder.php";
        String time = getTime();
        String content = "key=" + KEY + "&time=" + time + "&md5=" + getMd5(time) + "&mk_type=cnc" + "&order_id=" + orderId + "&coinname=" + coinname;
        return postMessage(url, content);
    }

    /**
     * 交易接口
     *
     * @param tradeType 1买入，2卖出
     * @param price     单价
     * @param amount    数量
     * @param coinname  币种
     * @return 返回结果
     */
    private static String submitOrder(String tradeType, String price, String amount, String coinname) {
        String url = "https://api.aex.zone/submitOrder.php";
        String time = getTime();
        String content = "key=" + KEY + "&time=" + time + "&md5=" + getMd5(time) + "&type=" + tradeType + "&mk_type=cnc&price=" + price + "&amount=" + amount + "&coinname=" + coinname;
        return postMessage(url, content);
    }

    /**
     * 获取近期交易
     *
     * @param coinType 币种
     * @param tradeNum 最近交易笔数
     * @return 近期交易详情
     */
    private static String getNearByTrade(String coinType, String tradeNum) {
        String url = "https://api.aex.zone/trades.php?c=" + coinType + "&mk_type=cnc&tid=" + tradeNum;
        return getMessage(url);
    }

    /**
     * 获取近期交易
     *
     * @param coinType 币种
     * @return 近期交易详情
     */
    private static String getNearByTrade(String coinType) {
        String url = "https://api.aex.zone/trades.php?c=" + coinType + "&mk_type=cnc";
        return getMessage(url);
    }

    /**
     * 获取市场深度详情
     *
     * @return 市场深度字符串
     */
    private static String getDepth(String coinType) {
        String url = "https://api.aex.zone/depth.php?c=" + coinType + "&mk_type=cnc";
        return getMessage(url);
    }

    /**
     * 获取所有交易对详情
     *
     * @return 交易对详情json字符串
     */
    private static String getTradeMessage() {
        String url = "https://api.aex.zone/ticker.php?c=all&mk_type=cnc";
        return getMessage(url);
    }

    /**
     * 获取所有交易对详情
     *
     * @return 交易对详情json字符串
     */
    private static String getTradeMessage(String coinType) {
        String url = "https://api.aex.zone/ticker.php?c=" + coinType + "&mk_type=cnc";
        return getMessage(url);
    }

    /**
     * 获取账户信息
     *
     * @return 账户信息字符串
     */
    private static String getAccount() {
        String url = "https://api.aex.zone/getMyBalance.php";
        String time = getTime();
        String content = "key=" + KEY + "&time=" + time + "&md5=" + getMd5(time);
        return postMessage(url, content);
    }

    /**
     * 通过get请求根据url获取数据
     *
     * @param url aex提供的url
     * @return 获取到的json
     */
    private static String getMessage(String url) {
        String message = null;
        try {

            //设置请求头并进行连接
            URL restServiceURL = new URL(url);
            HttpURLConnection httpConnection = (HttpURLConnection) restServiceURL.openConnection();
            httpConnection.setRequestMethod("GET");
            httpConnection.setRequestProperty("Accept", "application/json");

            //判断连接是否成功
            if (httpConnection.getResponseCode() != Constants.HTTP_OK) {
                throw new RuntimeException("HTTP GET Request Failed with Error code : "
                        + httpConnection.getResponseCode());
            }
            //打印输出内容
            BufferedReader responseBuffer = new BufferedReader(new InputStreamReader(
                    (httpConnection.getInputStream())));

            String output;

            while ((output = responseBuffer.readLine()) != null) {
                message = output;
            }
            httpConnection.disconnect();
        } catch (MalformedURLException e) {
            logger.error("MalformedURLException:", e);
        } catch (IOException e) {
            logger.error("IOException:", e);
        }
        return message;
    }

    private static String postMessage(String url, String content) {
        String message = null;
        try {

            //设置请求头并进行连接
            URL restServiceURL = new URL(url);
            HttpURLConnection httpConnection = (HttpURLConnection) restServiceURL.openConnection();
            httpConnection.setRequestMethod("POST");
            httpConnection.setRequestProperty("Content-type", "application/x-www-form-urlencoded");
            // 发送POST请求必须设置如下两行
            httpConnection.setDoOutput(true);
            httpConnection.setDoInput(true);

            DataOutputStream out = new DataOutputStream(httpConnection.getOutputStream());
            // The URL-encoded contend
            // 正文，正文内容其实跟get的URL中 '? '后的参数字符串一致

            out.writeBytes(content);

            out.flush();
            out.close();

            //判断连接是否成功
            if (httpConnection.getResponseCode() != Constants.HTTP_OK) {
                throw new RuntimeException("HTTP GET Request Failed with Error code : "
                        + httpConnection.getResponseCode());
            }
            //打印输出内容
            BufferedReader responseBuffer = new BufferedReader(new InputStreamReader(
                    (httpConnection.getInputStream())));

            String output;

            while ((output = responseBuffer.readLine()) != null) {
                message = output;
            }
            httpConnection.disconnect();
        } catch (MalformedURLException e) {
            logger.error("MalformedURLException:", e);
        } catch (IOException e) {
            logger.error("IOException:", e);
        }
        return message;
    }

    /**
     * 生成接口中所需的md5加密串
     *
     * @param time 接口所需的时间类型字符串
     * @return 接口中所需的md5加密串
     */
    private static String getMd5(String time) {
        return MD5.md5(KEY + "_" + ID + "_" + SKEY + "_" + time);
    }

    /**
     * 生成接口中所需的时间类型字符串
     *
     * @return 接口所需的时间类型字符串
     */
    private static String getTime() {
        String time = String.valueOf(System.currentTimeMillis());
        return time.substring(0, time.length() - 3);
    }

}