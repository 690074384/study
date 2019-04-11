package com.lph.controller.system.tools;


import com.google.common.collect.Maps;
import com.lph.controller.base.BaseController;
import com.lph.util.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

/**
 * 类名称：ToolController 系统工具
 *
 * @author lvpenghui
 * @since 2019-4-11 10:45:34
 */
@Controller
@RequestMapping(value = "/tool")
public class ToolController extends BaseController {

    /**
     * 去接口测试页面
     *
     * @return ModelAndView对象
     */
    @RequestMapping(value = "/interfaceTest")
    public ModelAndView editEmail() {
        ModelAndView mv = this.getModelAndView();
        PageData pd = this.getPageData();
        mv.setViewName("system/tools/interfaceTest");
        mv.addObject("pd", pd);
        return mv;
    }

    /**
     * 接口内部请求
     */
    @RequestMapping(value = "/severTest")
    @ResponseBody
    public Object severTest() {
        Map<String, String> map = Maps.newHashMap();
        PageData pd = this.getPageData();
        String errInfo = "success", rTime = "";
        StringBuilder sb = new StringBuilder();
        try {
            //请求起始时间_毫秒
            long startTime = System.currentTimeMillis();
            URL url = new URL(pd.getString("serverUrl"));
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            //请求类型  POST or GET
            connection.setRequestMethod(pd.getString("requestMethod"));
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream(), "utf-8"));
            //请求结束时间_毫秒
            long endTime = System.currentTimeMillis();
            String temp;
            while ((temp = in.readLine()) != null) {
                sb.append(temp);
            }
            rTime = String.valueOf(endTime - startTime);
        } catch (Exception e) {
            errInfo = "error";
        }
        //状态信息
        map.put("errInfo", errInfo);
        //返回结果
        map.put("result", sb.toString());
        //服务器请求时间 毫秒
        map.put("rTime", rTime);
        return AppUtil.returnObject(new PageData(), map);
    }

    /**
     * 发送邮件页面
     *
     * @return ModelAndView对象
     */
    @RequestMapping(value = "/goSendEmail")
    public ModelAndView goSendEmail() {
        ModelAndView mv = this.getModelAndView();
        PageData pd = this.getPageData();
        mv.setViewName("system/tools/email");
        mv.addObject("pd", pd);
        return mv;
    }

    /**
     * 表单构建页面
     *
     * @return ModelAndView对象
     */
    @RequestMapping(value = "/goFormbuilder")
    public ModelAndView goFormbuilder() {
        ModelAndView mv = this.getModelAndView();
        PageData pd = this.getPageData();
        mv.setViewName("system/tools/form_builder");
        mv.addObject("pd", pd);
        return mv;
    }

    /**
     * 生成文件并下载（生成的表单构建页面代码放到jsp页面）
     *
     * @throws Exception 可能抛出的异常
     */
    @RequestMapping(value = "/downloadFormCode")
    public void downloadFormCode(HttpServletResponse response) throws Exception {
        PageData pd = this.getPageData();
        //创建数据模型
        Map<String, Object> root = Maps.newHashMap();
        root.put("htmlCode", pd.getString("htmlCode"));
        //生成代码前,先清空之前生成的代码
        DelAllFile.delFolder(PathUtil.getClasspath() + "admin/ftl");
        //存放路径
        String filePath = "admin/ftl/code/";
        //ftl路径
        String ftlPath = "createCode";
        //生成controller
        Freemarker.printFile("newJsp.ftl", root, "newJsp.jsp", filePath, ftlPath);
        FileDownload.fileDownload(response, PathUtil.getClasspath() + "admin/ftl/code/newJsp.jsp", "newJsp.jsp");
    }

    /**
     * 二维码页面
     *
     * @return ModelAndView对象
     */
    @RequestMapping(value = "/goTwoDimensionCode")
    public ModelAndView goTwoDimensionCode() {
        ModelAndView mv = this.getModelAndView();
        PageData pd = this.getPageData();
        mv.setViewName("system/tools/twoDimensionCode");
        mv.addObject("pd", pd);
        return mv;
    }

    /**
     * 生成二维码
     */
    @RequestMapping(value = "/createTwoDimensionCode")
    @ResponseBody
    public Object createTwoDimensionCode() {
        Map<String, String> map = Maps.newHashMap();
        PageData pd = this.getPageData();
        //encoderImgId此处二维码的图片名
        String errInfo = "success", encoderImgId = this.get32UUID() + ".png";
        //内容
        String encoderContent = pd.getString("encoderContent");
        if (null == encoderContent) {
            errInfo = "error";
        } else {
            try {
                //存放路径
                String filePath = PathUtil.getClasspath() + Const.FILEPATHTWODIMENSIONCODE + encoderImgId;
                //执行生成二维码
                TwoDimensionCode.encoderQRCode(encoderContent, filePath, "png");
            } catch (Exception e) {
                errInfo = "error";
            }
        }
        //返回结果
        map.put("result", errInfo);
        //二维码图片名
        map.put("encoderImgId", encoderImgId);
        return AppUtil.returnObject(new PageData(), map);
    }

    /**
     * 解析二维码
     *
     * @return 解析二维码结果
     */
    @RequestMapping(value = "/readTwoDimensionCode")
    @ResponseBody
    public Object readTwoDimensionCode() {
        Map<String, String> map = Maps.newHashMap();
        PageData pd = this.getPageData();
        String errInfo = "success", readContent = "";
        //内容
        String imgId = pd.getString("imgId");
        if (null == imgId) {
            errInfo = "error";
        } else {
            try {
                //存放路径
                String filePath = PathUtil.getClasspath() + Const.FILEPATHTWODIMENSIONCODE + imgId;
                //执行读取二维码
                readContent = TwoDimensionCode.decoderQRCode(filePath);
            } catch (Exception e) {
                errInfo = "error";
            }
        }
        //返回结果
        map.put("result", errInfo);
        //读取的内容
        map.put("readContent", readContent);
        return AppUtil.returnObject(new PageData(), map);
    }

    /**
     * 地图页面
     *
     * @return ModelAndView对象
     */
    @RequestMapping(value = "/map")
    public ModelAndView map() {
        ModelAndView mv = this.getModelAndView();
        PageData pd = this.getPageData();
        mv.setViewName("system/tools/map");
        mv.addObject("pd", pd);
        return mv;
    }

    /**
     * 获取地图坐标页面
     *
     * @return ModelAndView对象
     */
    @RequestMapping(value = "/mapXY")
    public ModelAndView mapXY() {
        ModelAndView mv = this.getModelAndView();
        PageData pd = this.getPageData();
        mv.setViewName("system/tools/mapXY");
        mv.addObject("pd", pd);
        return mv;
    }

    /**
     * 根据经纬度计算距离
     */
    @RequestMapping(value = "/getDistance")
    @ResponseBody
    public Object getDistance() {
        Map<String, String> map = Maps.newHashMap();
        PageData pd = this.getPageData();
        String errInfo = "success", distance = "";
        try {
            distance = MapDistance.getDistance(pd.getString("ZUOBIAO_Y"), pd.getString("ZUOBIAO_X"), pd.getString("ZUOBIAO_Y2"), pd.getString("ZUOBIAO_X2"));
        } catch (Exception e) {
            errInfo = "error";
        }
        //返回结果
        map.put("result", errInfo);
        //距离
        map.put("distance", distance);
        return AppUtil.returnObject(new PageData(), map);
    }

    /**
     * 图表报表demo页面
     *
     * @return ModelAndView对象
     */
    @RequestMapping(value = "/fusionchartsdemo")
    public ModelAndView fusionchartsdemo() {
        ModelAndView mv = this.getModelAndView();
        //FusionCharts 报表demo  用的时候，循环拼字符串即可
        String strXML = "<graph caption='前12个月订单销量柱状图' xAxisName='月份' yAxisName='值' decimalPrecision='0' formatNumberScale='0'>"
                + "<set name='2013-05' value='4' color='AFD8F8'/>"
                + "<set name='2013-04' value='0' color='AFD8F8'/>"
                + "<set name='2013-03' value='0' color='AFD8F8'/>"
                + "<set name='2013-02' value='0' color='AFD8F8'/>"
                + "<set name='2013-01' value='0' color='AFD8F8'/>"
                + "<set name='2012-01' value='0' color='AFD8F8'/>"
                + "<set name='2012-11' value='0' color='AFD8F8'/>"
                + "<set name='2012-10' value='0' color='AFD8F8'/>"
                + "<set name='2012-09' value='0' color='AFD8F8'/>"
                + "<set name='2012-08' value='0' color='AFD8F8'/>"
                + "<set name='2012-07' value='0' color='AFD8F8'/>"
                + "<set name='2012-06' value='0' color='AFD8F8'/>"
                + "</graph>";
        mv.addObject("strXML", strXML);
        mv.setViewName("system/tools/fusionchartsdemo");
        return mv;
    }

    /**
     * 打印测试页面
     *
     * @return ModelAndView对象
     */
    @RequestMapping(value = "/printTest")
    public ModelAndView printTest() {
        ModelAndView mv = this.getModelAndView();
        PageData pd = this.getPageData();
        mv.setViewName("system/tools/printTest");
        mv.addObject("pd", pd);
        return mv;
    }

    /**
     * 打印预览页面
     *
     * @return ModelAndView对象
     */
    @RequestMapping(value = "/printPage")
    public ModelAndView printPage() {
        ModelAndView mv = this.getModelAndView();
        PageData pd = this.getPageData();
        mv.setViewName("system/tools/printPage");
        mv.addObject("pd", pd);
        return mv;
    }
}