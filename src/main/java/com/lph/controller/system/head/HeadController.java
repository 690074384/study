package com.lph.controller.system.head;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.lph.controller.base.BaseController;
import com.lph.service.system.appuser.AppuserManager;
import com.lph.service.system.fhsms.FhsmsManager;
import com.lph.service.system.user.UserManager;
import com.lph.service.system.userphoto.UserPhotoManager;
import com.lph.util.*;
import com.lph.util.mail.SimpleMailSender;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.session.Session;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 类名称：HeadController
 *
 * @author lvpenghui
 * @since 2019-4-10 14:33:17
 */
@Controller
@RequestMapping(value = "/head")
public class HeadController extends BaseController {

    @Resource(name = "userService")
    private UserManager userService;
    @Resource(name = "appuserService")
    private AppuserManager appuserService;
    @Resource(name = "fhsmsService")
    private FhsmsManager fhsmsService;
    @Resource(name = "userphotoService")
    private UserPhotoManager userphotoService;

    /**
     * 去编辑头像页面
     *
     * @return ModelAndView对象
     */
    @RequestMapping(value = "/editPhoto")
    public ModelAndView editPhoto() {
        ModelAndView mv = this.getModelAndView();
        PageData pd = this.getPageData();
        mv.setViewName("system/userphoto/userphoto_edit");
        mv.addObject("pd", pd);
        return mv;
    }

    /**
     * 获取头部信息
     *
     * @return 头部信息list
     */
    @RequestMapping(value = "/getList")
    @ResponseBody
    public Object getList() {
        PageData pd = new PageData();
        Map<String, Object> map = Maps.newHashMap();
        try {
            pd = this.getPageData();
            List<PageData> pdList = Lists.newArrayList();
            Session session = Jurisdiction.getSession();
            PageData pds = (PageData) session.getAttribute(Const.SESSION_userpds);
            if (null == pds) {
                //当前登录者用户名
                pd.put("USERNAME", Jurisdiction.getUsername());
                pds = userService.findByUsername(pd);
                session.setAttribute(Const.SESSION_userpds, pds);
            }
            pdList.add(pds);
            map.put("list", pdList);
            PageData pdPhoto = userphotoService.findById(pds);
            //用户头像
            map.put("userPhoto", null == pdPhoto ? "static/ace/avatars/user.jpg" : pdPhoto.getString("PHOTO2"));
            //站内信未读总数
            map.put("fhsmsCount", fhsmsService.findFhsmsCount(Jurisdiction.getUsername()).get("fhsmsCount").toString());
            //读取WEBSOCKET配置
            String strWEBSOCKET = Tools.readTxtFile(Const.WEBSOCKET);
            if (null != strWEBSOCKET && !"".equals(strWEBSOCKET)) {
                String[] strIW = strWEBSOCKET.split(",fh,");
                if (strIW.length == Constants.FIVE) {
                    //即时聊天服务器IP和端口
                    map.put("wimadress", strIW[0] + ":" + strIW[1]);
                    //在线管理和站内信服务器IP和端口
                    map.put("oladress", strIW[2] + ":" + strIW[3]);
                    //站内信提示音效配置
                    map.put("FHsmsSound", strIW[4]);
                }
            }
        } catch (Exception e) {
            logger.error(e.toString(), e);
        } finally {
            logAfter(logger);
        }
        return AppUtil.returnObject(pd, map);
    }

    /**
     * 获取站内信未读总数
     *
     * @return 未读站内信数量
     */
    @RequestMapping(value = "/getFhsmsCount")
    @ResponseBody
    public Object getFhsmsCount() {
        PageData pd = new PageData();
        Map<String, Object> map = Maps.newHashMap();
        try {
            //站内信未读总数
            map.put("fhsmsCount", fhsmsService.findFhsmsCount(Jurisdiction.getUsername()).get("fhsmsCount").toString());
        } catch (Exception e) {
            logger.error(e.toString(), e);
        } finally {
            logAfter(logger);
        }
        return AppUtil.returnObject(pd, map);
    }

    /**
     * 去发送邮箱页面
     *
     * @return ModelAndView对象
     */
    @RequestMapping(value = "/editEmail")
    public ModelAndView editEmail() {
        ModelAndView mv = this.getModelAndView();
        PageData pd = this.getPageData();
        mv.setViewName("system/head/edit_email");
        mv.addObject("pd", pd);
        return mv;
    }

    /**
     * 去发送短信页面
     *
     * @return ModelAndView对象
     */
    @RequestMapping(value = "/goSendSms")
    public ModelAndView goSendSms() {
        ModelAndView mv = this.getModelAndView();
        PageData pd = this.getPageData();
        mv.setViewName("system/head/send_sms");
        mv.addObject("pd", pd);
        return mv;
    }


    /**
     * 发送短信
     *
     * @return 发送短信结果
     */
    @RequestMapping(value = "/sendSms")
    @ResponseBody
    public Object sendSms() {
        PageData pd = this.getPageData();
        Map<String, Object> map = Maps.newHashMap();
        //发送状态
        String msg = "ok";
        //统计发送成功条数
        int count = 0;
        //理论条数
        int zcount = 0;
        List<PageData> pdList = Lists.newArrayList();
        //对方邮箱
        String phones = pd.getString("PHONE");
        //内容
        String content = pd.getString("CONTENT");
        //是否发送给全体成员 yes or no
        String isAll = pd.getString("isAll");
        //类型 1：短信接口1   2：短信接口2
        String type = pd.getString("TYPE");
        //判断是系统用户还是会员 "appuser"为会员用户
        String fmsg = pd.getString("fmsg");
        if (Constants.YES.endsWith(isAll)) {
            try {
                List<PageData> userList = "appuser".equals(fmsg) ? appuserService.listAllUser(pd) : userService.listAllUser(pd);
                zcount = userList.size();
                try {
                    for (PageData anUserList : userList) {
                        //手机号格式不对就跳过
                        if (Tools.checkMobileNumber(anUserList.getString("PHONE"))) {
                            if ("1".equals(type)) {
                                //调用发短信函数1
                                SmsUtil.sendSms1(anUserList.getString("PHONE"), content);
                            } else {
                                //调用发短信函数2
                                SmsUtil.sendSms2(anUserList.getString("PHONE"), content);
                            }
                            count++;
                            msg = "ok";
                        }
                    }

                } catch (Exception e) {
                    msg = "error";
                }
            } catch (Exception e) {
                msg = "error";
            }
        } else {
            phones = phones.replaceAll("；", ";").replaceAll(" ", "");
            String[] arrTITLE = phones.split(";");
            zcount = arrTITLE.length;
            try {
                for (String anArrTITLE : arrTITLE) {
                    //手机号式不对就跳过
                    if (Tools.checkMobileNumber(anArrTITLE)) {
                        if ("1".equals(type)) {
                            //调用发短信函数1
                            SmsUtil.sendSms1(anArrTITLE, content);
                        } else {
                            //调用发短信函数2
                            SmsUtil.sendSms2(anArrTITLE, content);
                        }
                        count++;
                        msg = "ok";
                    }
                }

            } catch (Exception e) {
                msg = "error";
            }
        }
        pd.put("msg", msg);
        //成功数
        pd.put("count", count);
        //失败数
        pd.put("ecount", zcount - count);
        pdList.add(pd);
        map.put("list", pdList);
        return AppUtil.returnObject(pd, map);
    }

    /**
     * 去发送电子邮件页面
     *
     * @return ModelAndViewd对象
     */
    @RequestMapping(value = "/goSendEmail")
    public ModelAndView goSendEmail() {
        ModelAndView mv = this.getModelAndView();
        PageData pd = this.getPageData();
        mv.setViewName("system/head/send_email");
        mv.addObject("pd", pd);
        return mv;
    }

    /**
     * 发送电子邮件
     *
     * @return 发送电子邮件结果
     */
    @RequestMapping(value = "/sendEmail")
    @ResponseBody
    public Object sendEmail() {
        PageData pd = this.getPageData();
        Map<String, Object> map = Maps.newHashMap();
        String msg = "ok";
        int count = 0;
        int zcount = 0;
        //读取邮件配置
        String emaiStr = Tools.readTxtFile(Const.EMAIL);
        List<PageData> pdList = Lists.newArrayList();
        //对方邮箱
        String toEMAIL = pd.getString("EMAIL");
        //标题
        String title = pd.getString("TITLE");
        String content = pd.getString("CONTENT");
        String type = pd.getString("TYPE");
        //是否发送给全体成员 yes or no
        String isAll = pd.getString("isAll");
        //判断是系统用户还是会员 "appuser"为会员用户
        String fmsg = pd.getString("fmsg");
        if (StringUtils.isNotEmpty(emaiStr)) {
            String[] strs = emaiStr.split(",fh,");
            if (Constants.FOUR == strs.length) {
                if (Constants.YES.endsWith(isAll)) {
                    try {
                        List<PageData> userList = "appuser".equals(fmsg) ? appuserService.listAllUser(pd) : userService.listAllUser(pd);
                        zcount = userList.size();
                        try {
                            for (PageData anUserList : userList) {
                                //邮箱格式不对就跳过
                                if (Tools.checkEmail(anUserList.getString("EMAIL"))) {
                                    //调用发送邮件函数
                                    SimpleMailSender.sendEmail(strs[0], strs[1], strs[2], strs[3], anUserList.getString("EMAIL"), title, content, type);
                                    count++;
                                    msg = "ok";
                                }
                            }

                        } catch (Exception e) {
                            msg = "error";
                        }
                    } catch (Exception e) {
                        msg = "error";
                    }
                } else {
                    toEMAIL = toEMAIL.replaceAll("；", ";");
                    toEMAIL = toEMAIL.replaceAll(" ", "");
                    String[] arrTITLE = toEMAIL.split(";");
                    zcount = arrTITLE.length;
                    try {
                        for (String anArrTITLE : arrTITLE) {
                            //邮箱格式不对就跳过
                            if (Tools.checkEmail(anArrTITLE)) {
                                //调用发送邮件函数
                                SimpleMailSender.sendEmail(strs[0], strs[1], strs[2], strs[3], anArrTITLE, title, content, type);
                                count++;
                                msg = "ok";
                            }
                        }

                    } catch (Exception e) {
                        msg = "error";
                    }
                }
            } else {
                msg = "error";
            }
        } else {
            msg = "error";
        }
        pd.put("msg", msg);
        //成功数
        pd.put("count", count);
        //失败数
        pd.put("ecount", zcount - count);
        pdList.add(pd);
        map.put("list", pdList);
        return AppUtil.returnObject(pd, map);
    }

    /**
     * 去系统设置页面
     *
     * @return ModelAndView对象
     */
    @RequestMapping(value = "/goSystem")
    public ModelAndView goEditEmail() {
        //非admin用户不能修改
        if (Constants.ADMIN.equals(Jurisdiction.getUsername())) {
            return null;
        }
        ModelAndView mv = this.getModelAndView();
        PageData pd = this.getPageData();
        //读取系统名称
        pd.put("YSYNAME", Tools.readTxtFile(Const.SYSNAME));
        //读取每页条数
        pd.put("COUNTPAGE", Tools.readTxtFile(Const.PAGE));
        //读取邮件配置
        String strEMAIL = Tools.readTxtFile(Const.EMAIL);
        //读取短信1配置
        String strSMS1 = Tools.readTxtFile(Const.SMS1);
        //读取短信2配置
        String strSMS2 = Tools.readTxtFile(Const.SMS2);
        //读取文字水印配置
        String strFWATERM = Tools.readTxtFile(Const.FWATERM);
        //读取图片水印配置
        String strIWATERM = Tools.readTxtFile(Const.IWATERM);
        //读取微信配置
        pd.put("Token", Tools.readTxtFile(Const.WEIXIN));
        //读取微信配置
        String strWEBSOCKET = Tools.readTxtFile(Const.WEBSOCKET);
        //读取登录页面配置
        String strLOGINEDIT = Tools.readTxtFile(Const.LOGINEDIT);

        this.setParamToPd(pd, strEMAIL, strSMS1, strSMS2, strFWATERM, strIWATERM, strWEBSOCKET, strLOGINEDIT);
        mv.setViewName("system/head/sys_edit");
        mv.addObject("pd", pd);
        return mv;
    }

    private void setParamToPd(PageData pd, String strEMAIL, String strSMS1, String strSMS2, String strFWATERM, String strIWATERM, String strWEBSOCKET, String strLOGINEDIT) {
        this.setEmailToPd(pd, strEMAIL);
        this.setSms1ToPd(pd, strSMS1);
        this.setSms2ToPd(pd, strSMS2);
        this.setFWaterToPd(pd, strFWATERM);
        this.setIWaterToPd(pd, strIWATERM);
        this.setWebsocketToPd(pd, strWEBSOCKET);
        this.setOthersToPd(pd, strLOGINEDIT);
    }

    private void setOthersToPd(PageData pd, String strLOGINEDIT) {
        if (StringUtils.isNotEmpty(strLOGINEDIT)) {
            String[] strLo = strLOGINEDIT.split(",fh,");
            if (strLo.length == Constants.TWO) {
                pd.put("isZhuce", strLo[0]);
                pd.put("isMusic", strLo[1]);
            }
        }
    }

    private void setWebsocketToPd(PageData pd, String strWEBSOCKET) {
        if (null != strWEBSOCKET && !"".equals(strWEBSOCKET)) {
            String[] strIW = strWEBSOCKET.split(",fh,");
            if (strIW.length == Constants.FIVE) {
                pd.put("WIMIP", strIW[0]);
                pd.put("WIMPORT", strIW[1]);
                pd.put("OLIP", strIW[2]);
                pd.put("OLPORT", strIW[3]);
                pd.put("FHsmsSound", strIW[4]);
            }
        }
    }

    private void setIWaterToPd(PageData pd, String strIWATERM) {
        if (StringUtils.isNotEmpty(strIWATERM)) {
            String[] strIW = strIWATERM.split(",fh,");
            if (strIW.length == Constants.FOUR) {
                pd.put("isCheck2", strIW[0]);
                pd.put("imgUrl", strIW[1]);
                pd.put("imgX", strIW[2]);
                pd.put("imgY", strIW[3]);
            }
        }
    }

    private void setFWaterToPd(PageData pd, String strFWATERM) {
        if (StringUtils.isNotEmpty(strFWATERM)) {
            String[] strFW = strFWATERM.split(",fh,");
            if (strFW.length == Constants.FIVE) {
                pd.put("isCheck1", strFW[0]);
                pd.put("fcontent", strFW[1]);
                pd.put("fontSize", strFW[2]);
                pd.put("fontX", strFW[3]);
                pd.put("fontY", strFW[4]);
            }
        }
    }

    private void setSms2ToPd(PageData pd, String strSMS2) {
        if (StringUtils.isNotEmpty(strSMS2)) {
            String[] strS2 = strSMS2.split(",fh,");
            if (strS2.length == Constants.TWO) {
                pd.put("SMSU2", strS2[0]);
                pd.put("SMSPAW2", strS2[1]);
            }
        }
    }

    private void setSms1ToPd(PageData pd, String strSMS1) {
        if (StringUtils.isNotEmpty(strSMS1)) {
            String[] strS1 = strSMS1.split(",fh,");
            if (strS1.length == Constants.TWO) {
                pd.put("SMSU1", strS1[0]);
                pd.put("SMSPAW1", strS1[1]);
            }
        }
    }

    private void setEmailToPd(PageData pd, String strEMAIL) {
        if (StringUtils.isNotEmpty(strEMAIL)) {
            String[] strEM = strEMAIL.split(",fh,");
            if (strEM.length == Constants.FOUR) {
                pd.put("SMTP", strEM[0]);
                pd.put("PORT", strEM[1]);
                pd.put("EMAIL", strEM[2]);
                pd.put("PAW", strEM[3]);
            }
        }
    }

    /**
     * 保存系统设置1
     *
     * @return ModelAndView对象
     */
    @RequestMapping(value = "/saveSys")
    public ModelAndView saveSys() {
        if (!Constants.ADMIN.equals(Jurisdiction.getUsername())) {
            return null;
        }    //非admin用户不能修改
        ModelAndView mv = this.getModelAndView();
        PageData pd = this.getPageData();
        //写入系统名称
        Tools.writeFile(Const.SYSNAME, pd.getString("YSYNAME"));
        //写入每页条数
        Tools.writeFile(Const.PAGE, pd.getString("COUNTPAGE"));
        //写入邮件服务器配置
        Tools.writeFile(Const.EMAIL, pd.getString("SMTP") + ",fh," + pd.getString("PORT") + ",fh," + pd.getString("EMAIL") + ",fh," + pd.getString("PAW"));
        //写入短信1配置
        Tools.writeFile(Const.SMS1, pd.getString("SMSU1") + ",fh," + pd.getString("SMSPAW1"));
        //写入短信2配置
        Tools.writeFile(Const.SMS2, pd.getString("SMSU2") + ",fh," + pd.getString("SMSPAW2"));
        mv.addObject("msg", "OK");
        mv.setViewName("save_result");
        return mv;
    }

    /**
     * 保存系统设置2
     *
     * @return ModelAndView对象
     */
    @RequestMapping(value = "/saveSys2")
    public ModelAndView saveSys2() {
        //非admin用户不能修改
        if (!Constants.ADMIN.equals(Jurisdiction.getUsername())) {
            return null;
        }
        ModelAndView mv = this.getModelAndView();
        PageData pd = this.getPageData();
        //文字水印配置
        Tools.writeFile(Const.FWATERM, pd.getString("isCheck1") + ",fh," + pd.getString("fcontent") + ",fh," + pd.getString("fontSize") + ",fh," + pd.getString("fontX") + ",fh," + pd.getString("fontY"));
        //图片水印配置
        Tools.writeFile(Const.IWATERM, pd.getString("isCheck2") + ",fh," + pd.getString("imgUrl") + ",fh," + pd.getString("imgX") + ",fh," + pd.getString("imgY"));
        Watermark.fushValue();
        mv.addObject("msg", "OK");
        mv.setViewName("save_result");
        return mv;
    }

    /**
     * 保存系统设置3
     *
     * @return ModelAndView对象
     */
    @RequestMapping(value = "/saveSys3")
    public ModelAndView saveSys3() {
        //非admin用户不能修改
        if (!Constants.ADMIN.equals(Jurisdiction.getUsername())) {
            return null;
        }
        ModelAndView mv = this.getModelAndView();
        PageData pd = this.getPageData();
        //写入微信配置
        Tools.writeFile(Const.WEIXIN, pd.getString("Token"));
        //websocket配置
        Tools.writeFile(Const.WEBSOCKET, pd.getString("WIMIP") + ",fh," + pd.getString("WIMPORT") + ",fh," + pd.getString("OLIP") + ",fh," + pd.getString("OLPORT") + ",fh," + pd.getString("FHsmsSound"));
        mv.addObject("msg", "OK");
        mv.setViewName("save_result");
        return mv;
    }

    /**
     * 保存系统设置4
     *
     * @return ModelAndView对象
     */
    @RequestMapping(value = "/saveSys4")
    public ModelAndView saveSys4() {
        if (!Constants.ADMIN.equals(Jurisdiction.getUsername())) {
            return null;
        }    //非admin用户不能修改
        ModelAndView mv = this.getModelAndView();
        PageData pd = this.getPageData();
        //登录页面配置
        Tools.writeFile(Const.LOGINEDIT, pd.getString("isZhuce") + ",fh," + pd.getString("isMusic"));
        mv.addObject("msg", "OK");
        mv.setViewName("save_result");
        return mv;
    }

}
