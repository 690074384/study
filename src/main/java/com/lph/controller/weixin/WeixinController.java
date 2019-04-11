package com.lph.controller.weixin;

import com.lph.controller.base.BaseController;
import com.lph.service.weixin.command.CommandService;
import com.lph.service.weixin.imgmsg.ImgmsgService;
import com.lph.service.weixin.textmsg.TextmsgService;
import com.lph.util.Const;
import com.lph.util.Constants;
import com.lph.util.PageData;
import com.lph.util.Tools;
import net.sf.json.JSONObject;
import org.marker.weixin.DefaultSession;
import org.marker.weixin.HandleMessageAdapter;
import org.marker.weixin.MySecurity;
import org.marker.weixin.msg.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.net.ssl.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URL;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 类描述： 微信公共平台开发
 *
 * @author lvpenghui
 * @since 设计
 */
@Controller
@RequestMapping(value = "/weixin")
public class WeixinController extends BaseController {

    @Resource(name = "textmsgService")
    private TextmsgService textmsgService;
    @Resource(name = "commandService")
    private CommandService commandService;
    @Resource(name = "imgmsgService")
    private ImgmsgService imgmsgService;

    /**
     * 接口验证,总入口
     *
     * @param out      out
     * @param request  HttpServletRequest对象
     * @param response HttpServletResponse对象
     */
    @RequestMapping(value = "/index")
    public void index(
            PrintWriter out,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        logBefore(logger, "微信接口");
        PageData pd = this.getPageData();
        try {
            //微信加密签名
            String signature = pd.getString("signature");
            //时间戳
            String timestamp = pd.getString("timestamp");
            //随机数
            String nonce = pd.getString("nonce");
            //字符串
            String echostr = pd.getString("echostr");

            //接口验证
            if (null != signature && null != timestamp && null != nonce && null != echostr) {
                logBefore(logger, "进入身份验证");
                List<String> list = new ArrayList<String>(3) {
                    private static final long serialVersionUID = 2621444383666420433L;

                    @Override
                    public String toString() {  // 重写toString方法，得到三个参数的拼接字符串
                        return this.get(0) + this.get(1) + this.get(2);
                    }
                };
                //读取Token(令牌)
                list.add(Tools.readTxtFile(Const.WEIXIN));
                list.add(timestamp);
                list.add(nonce);
                // 排序
                Collections.sort(list);
                // SHA-1加密
                String tmpStr = new MySecurity().encode(list.toString(),
                        MySecurity.SHA_1);

                if (signature.equals(tmpStr)) {
                    // 请求验证成功，返回随机码
                    out.write(echostr);
                } else {
                    out.write("");
                }
                out.flush();
                out.close();
            } else {/* 消息处理  */
                logBefore(logger, "进入消息处理");
                response.reset();
                sendMsg(request, response);
            }
        } catch (Exception e) {
            logger.error(e.toString(), e);
        }
    }

    /**
     * 处理微信服务器发过来的各种消息，包括：文本、图片、地理位置、音乐等等
     *
     * @param request  HttpServletRequest
     * @param response HttpServletResponse
     * @throws Exception 可能抛出的异常
     */
    private void sendMsg(HttpServletRequest request, HttpServletResponse response) throws Exception {

        InputStream is = request.getInputStream();
        OutputStream os = response.getOutputStream();

        final DefaultSession session = DefaultSession.newInstance();
        session.addOnHandleMessageListener(new HandleMessageAdapter() {
            /**
             * 事件
             */
            @Override
            public void onEventMsg(Msg4Event msg) {
                // unsubscribe：取消关注 ; subscribe：关注
                if (Constants.SUBSCRIBE.equals(msg.getEvent())) {
                    returnMSg(msg, null, "关注");
                }
            }

            /**
             * 收到的文本消息
             */
            @Override
            public void onTextMsg(Msg4Text msg) {
                returnMSg(null, msg, msg.getContent().trim());
            }

            @Override
            public void onImageMsg(Msg4Image msg) {
                super.onImageMsg(msg);
            }

            @Override
            public void onLocationMsg(Msg4Location msg) {
                super.onLocationMsg(msg);
            }

            @Override
            public void onLinkMsg(Msg4Link msg) {
                super.onLinkMsg(msg);
            }

            @Override
            public void onVideoMsg(Msg4Video msg) {
                super.onVideoMsg(msg);
            }

            @Override
            public void onVoiceMsg(Msg4Voice msg) {
                super.onVoiceMsg(msg);
            }

            @Override
            public void onErrorMsg(int errorCode) {
                super.onErrorMsg(errorCode);
            }

            /**
             * 返回消息
             * @param emsg emsg
             * @param tmsg tmsg
             * @param getmsg getmsg
             */
            private void returnMSg(Msg4Event emsg, Msg4Text tmsg, String getmsg) {
                PageData msgpd;
                PageData pd = new PageData();
                String toUserName, fromUserName, createTime;
                if (null == emsg) {
                    toUserName = tmsg.getToUserName();
                    fromUserName = tmsg.getFromUserName();
                    createTime = tmsg.getCreateTime();
                } else {
                    toUserName = emsg.getToUserName();
                    fromUserName = emsg.getFromUserName();
                    createTime = emsg.getCreateTime();
                }
                pd.put("KEYWORD", getmsg);
                try {
                    msgpd = textmsgService.findByKw(pd);
                    if (null != msgpd) {
                        Msg4Text rmsg = new Msg4Text();
                        rmsg.setFromUserName(toUserName);
                        rmsg.setToUserName(fromUserName);
                        //rmsg.setFuncFlag("0");
                        //回复文字消息
                        rmsg.setContent(msgpd.getString("CONTENT"));
                        session.callback(rmsg);
                    } else {
                        msgpd = imgmsgService.findByKw(pd);
                        if (null != msgpd) {
                            Msg4ImageText mit = new Msg4ImageText();
                            mit.setFromUserName(toUserName);
                            mit.setToUserName(fromUserName);
                            mit.setCreateTime(createTime);
                            //回复图文消息
                            if (null != msgpd.getString("TITLE1") && null != msgpd.getString("IMGURL1")) {
                                Data4Item d1 = new Data4Item(msgpd.getString("TITLE1"), msgpd.getString("DESCRIPTION1"), msgpd.getString("IMGURL1"), msgpd.getString("TOURL1"));
                                mit.addItem(d1);

                                if (null != msgpd.getString("TITLE2") && null != msgpd.getString("IMGURL2") && !"".equals(msgpd.getString("TITLE2").trim()) && !"".equals(msgpd.getString("IMGURL2").trim())) {
                                    Data4Item d2 = new Data4Item(msgpd.getString("TITLE2"), msgpd.getString("DESCRIPTION2"), msgpd.getString("IMGURL2"), msgpd.getString("TOURL2"));
                                    mit.addItem(d2);
                                }
                                if (null != msgpd.getString("TITLE3") && null != msgpd.getString("IMGURL3") && !"".equals(msgpd.getString("TITLE3").trim()) && !"".equals(msgpd.getString("IMGURL3").trim())) {
                                    Data4Item d3 = new Data4Item(msgpd.getString("TITLE3"), msgpd.getString("DESCRIPTION3"), msgpd.getString("IMGURL3"), msgpd.getString("TOURL3"));
                                    mit.addItem(d3);
                                }
                                if (null != msgpd.getString("TITLE4") && null != msgpd.getString("IMGURL4") && !"".equals(msgpd.getString("TITLE4").trim()) && !"".equals(msgpd.getString("IMGURL4").trim())) {
                                    Data4Item d4 = new Data4Item(msgpd.getString("TITLE4"), msgpd.getString("DESCRIPTION4"), msgpd.getString("IMGURL4"), msgpd.getString("TOURL4"));
                                    mit.addItem(d4);
                                }
                                if (null != msgpd.getString("TITLE5") && null != msgpd.getString("IMGURL5") && !"".equals(msgpd.getString("TITLE5").trim()) && !"".equals(msgpd.getString("IMGURL5").trim())) {
                                    Data4Item d5 = new Data4Item(msgpd.getString("TITLE5"), msgpd.getString("DESCRIPTION5"), msgpd.getString("IMGURL5"), msgpd.getString("TOURL5"));
                                    mit.addItem(d5);
                                }
                                if (null != msgpd.getString("TITLE6") && null != msgpd.getString("IMGURL6") && !"".equals(msgpd.getString("TITLE6").trim()) && !"".equals(msgpd.getString("IMGURL6").trim())) {
                                    Data4Item d6 = new Data4Item(msgpd.getString("TITLE6"), msgpd.getString("DESCRIPTION6"), msgpd.getString("IMGURL6"), msgpd.getString("TOURL6"));
                                    mit.addItem(d6);
                                }
                                if (null != msgpd.getString("TITLE7") && null != msgpd.getString("IMGURL7") && !"".equals(msgpd.getString("TITLE7").trim()) && !"".equals(msgpd.getString("IMGURL7").trim())) {
                                    Data4Item d7 = new Data4Item(msgpd.getString("TITLE7"), msgpd.getString("DESCRIPTION7"), msgpd.getString("IMGURL7"), msgpd.getString("TOURL7"));
                                    mit.addItem(d7);
                                }
                                if (null != msgpd.getString("TITLE8") && null != msgpd.getString("IMGURL8") && !"".equals(msgpd.getString("TITLE8").trim()) && !"".equals(msgpd.getString("IMGURL8").trim())) {
                                    Data4Item d8 = new Data4Item(msgpd.getString("TITLE8"), msgpd.getString("DESCRIPTION8"), msgpd.getString("IMGURL8"), msgpd.getString("TOURL8"));
                                    mit.addItem(d8);
                                }
                            }
                            //mit.setFuncFlag("0");
                            session.callback(mit);
                        } else {
                            msgpd = commandService.findByKw(pd);
                            if (null != msgpd) {
                                Runtime runtime = Runtime.getRuntime();
                                runtime.exec(msgpd.getString("COMMANDCODE"));
                            } else {
                                Msg4Text rmsg = new Msg4Text();
                                rmsg.setFromUserName(toUserName);
                                rmsg.setToUserName(fromUserName);
                                rmsg.setContent("无匹配结果");
                                session.callback(rmsg);
                            }
                        }
                    }
                } catch (Exception e1) {
                    logBefore(logger, "匹配错误");
                }
            }

        });

        /*必须调用这两个方法   如果不调用close方法，将会出现响应数据串到其它Servlet中。*/
        //处理微信消息
        session.process(is, os);
        //关闭Session
        session.close();
    }

    /**
     * 获取关注列表
     */
    private final static String GZ_URL = "https://api.weixin.qq.com/cgi-bin/user/get?access_token=ACCESS_TOKEN&next_openid=";

    /**
     * 获取access_token
     *
     * @param out out
     */
    @RequestMapping(value = "/getGz")
    public void getGz(PrintWriter out) {
        logBefore(logger, "获取关注列表");
        try {
            String accessToken = readTxtFile("e:/access_token.txt");

            System.out.println(accessToken + "============");

            String requestUrl = GZ_URL.replace("ACCESS_TOKEN", accessToken);

            System.out.println(requestUrl + "============");

            JSONObject jsonObject = httpRequst(requestUrl, "GET", null);
            System.out.println(jsonObject);
            PrintWriter pw;
            try {
                pw = new PrintWriter(new FileWriter("e:/gz.txt"));
                pw.print(jsonObject.getString("total"));
                pw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            out.write("success");
            out.close();
        } catch (Exception e) {
            logger.error(e.toString(), e);
        }
    }

    /**
     * 读取文件
     *
     * @param filePath 文件路径
     * @return 文件内容
     */
    private String readTxtFile(String filePath) {
        try {
            String encoding = "utf-8";
            File file = new File(filePath);
            // 判断文件是否存在
            if (file.isFile() && file.exists()) {
                // 考虑到编码格式
                InputStreamReader read = new InputStreamReader(
                        new FileInputStream(file), encoding);
                BufferedReader bufferedReader = new BufferedReader(read);
                String lineTxt;
                while ((lineTxt = bufferedReader.readLine()) != null) {
                    return lineTxt;
                }
                read.close();
            } else {
                System.out.println("找不到指定的文件");
            }
        } catch (Exception e) {
            System.out.println("读取文件内容出错");
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 获取access_token
     */
    private final static String ACCESS_TOKEN_URL = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APPID&secret=APPSECRET";

    /**
     * 获取access_token
     *
     * @param out out
     */
    @RequestMapping(value = "/getAt")
    public void getAt(PrintWriter out) {
        logBefore(logger, "获取access_token");
        try {
            String appid = "wx9f43c8daa1c13934";
            String appsecret = "2c7f6552a5a845b49d47f65dd90beb50";

            String requestUrl = ACCESS_TOKEN_URL.replace("APPID", appid).replace("APPSECRET", appsecret);
            JSONObject jsonObject = httpRequst(requestUrl, "GET", null);

            PrintWriter pw;
            try {
                pw = new PrintWriter(new FileWriter("e:/access_token.txt"));
                pw.print(jsonObject.getString("access_token"));
                pw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            out.write("success");
            out.close();
        } catch (Exception e) {
            logger.error(e.toString(), e);
        }
    }

    private JSONObject httpRequst(String requestUrl, String requetMethod, String outputStr) {
        JSONObject jsonobject = null;
        StringBuilder buffer = new StringBuilder();
        try {
            //创建SSLContext对象，并使用我们指定的新人管理器初始化
            TrustManager[] tm = {new MyX509TrustManager()};
            SSLContext sslcontext = SSLContext.getInstance("SSL", "SunJSSE");
            sslcontext.init(null, tm, new java.security.SecureRandom());
            //从上述SSLContext对象中得到SSLSocktFactory对象
            SSLSocketFactory ssf = sslcontext.getSocketFactory();

            URL url = new URL(requestUrl);
            HttpsURLConnection httpUrlConn = (HttpsURLConnection) url.openConnection();
            httpUrlConn.setSSLSocketFactory(ssf);

            httpUrlConn.setDoOutput(true);
            httpUrlConn.setDoInput(true);
            httpUrlConn.setUseCaches(false);
            //设置请求方式（GET/POST）
            httpUrlConn.setRequestMethod(requetMethod);

            if (Constants.GET.equalsIgnoreCase(requetMethod)) {
                httpUrlConn.connect();
            }

            //当有数据需要提交时
            if (null != outputStr) {
                OutputStream outputStream = httpUrlConn.getOutputStream();
                //注意编码格式，防止中文乱码
                outputStream.write(outputStr.getBytes("UTF-8"));
                outputStream.close();
            }

            //将返回的输入流转换成字符串
            InputStream inputStream = httpUrlConn.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);


            String str;
            while ((str = bufferedReader.readLine()) != null) {
                buffer.append(str);
            }
            bufferedReader.close();
            inputStreamReader.close();
            //释放资源
            inputStream.close();
            httpUrlConn.disconnect();
            jsonobject = JSONObject.fromObject(buffer.toString());
        } catch (Exception e) {
            logger.error("出现异常");
        }
        return jsonobject;
    }
}


/**
 * 获取access token
 */
class MyX509TrustManager implements X509TrustManager {

    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType) {

    }

    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType) {

    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return null;
    }
}