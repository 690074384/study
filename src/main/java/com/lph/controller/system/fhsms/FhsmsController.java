package com.lph.controller.system.fhsms;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.lph.controller.base.BaseController;
import com.lph.entity.Page;
import com.lph.service.system.fhsms.FhsmsManager;
import com.lph.util.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 说明：站内信
 *
 * @author lvpenghui
 * @since 2019-4-10 11:21:59
 */
@Controller
@RequestMapping(value = "/fhsms")
public class FhsmsController extends BaseController {

    /**
     * 菜单地址(权限用)
     */
    private String menuUrl = "fhsms/list.do";
    @Resource(name = "fhsmsService")
    private FhsmsManager fhsmsService;

    /**
     * 发送站内信
     *
     * @throws Exception 可能抛出的异常
     */
    @RequestMapping(value = "/save")
    @ResponseBody
    public Object save() throws Exception {
        logBefore(logger, Jurisdiction.getUsername() + "发送站内信");
        //校验权限（站内信用独立的按钮权限,在此就不必校验新增权限）
        //if(!Jurisdiction.buttonJurisdiction(menuUrl, "add")){return null;}
        PageData pd = this.getPageData();
        Map<String, Object> map = Maps.newHashMap();
        List<PageData> pdList = Lists.newArrayList();
        //发送状态
        String msg;
        //统计发送成功条数
        int count = 0;
        //理论条数
        int zcount = 0;
        //对方用户名
        String username = pd.getString("USERNAME");
        if (StringUtils.isNotEmpty(username)) {
            username = username.replaceAll("；", ";");
            username = username.replaceAll(" ", "");
            String[] arrUSERNAME = username.split(";");
            zcount = arrUSERNAME.length;
            try {
                pd.put("STATUS", "2");
                for (String anArrUSERNAME : arrUSERNAME) {
                    //共同ID
                    this.getPdByUserName(pd, anArrUSERNAME);
                    count++;
                }
                msg = "ok";
            } catch (Exception e) {
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

    private void getPdByUserName(PageData pd, String anArrUSERNAME) throws Exception {
        pd.put("SANME_ID", this.get32UUID());
        //发送时间
        pd.put("SEND_TIME", DateUtil.getTime());
        //主键1
        pd.put("FHSMS_ID", this.get32UUID());
        //类型2：发信
        pd.put("TYPE", "2");
        //发信人
        pd.put("FROM_USERNAME", Jurisdiction.getUsername());
        //收信人
        pd.put("TO_USERNAME", anArrUSERNAME);
        //存入发信
        fhsmsService.save(pd);
        //主键2
        pd.put("FHSMS_ID", this.get32UUID());
        //类型1：收信
        pd.put("TYPE", "1");
        //发信人
        pd.put("FROM_USERNAME", anArrUSERNAME);
        //收信人
        pd.put("TO_USERNAME", Jurisdiction.getUsername());
        fhsmsService.save(pd);
    }

    /**
     * 删除
     *
     * @param out out
     * @throws Exception 可能抛出的异常
     */
    @RequestMapping(value = "/delete")
    public void delete(PrintWriter out) throws Exception {
        logBefore(logger, Jurisdiction.getUsername() + "删除Fhsms");
        if (!Jurisdiction.buttonJurisdiction(menuUrl, Constants.DELE)) {
            return;
        } //校验权限
        PageData pd = this.getPageData();
        fhsmsService.delete(pd);
        out.write("success");
        out.close();
    }

    /**
     * 列表
     *
     * @param page 分页
     * @throws Exception 可能抛出的异常
     */
    @RequestMapping(value = "/list")
    public ModelAndView list(Page page) throws Exception {
        logBefore(logger, Jurisdiction.getUsername() + "列表Fhsms");
        ModelAndView mv = this.getModelAndView();
        PageData pd = this.getPageData();
        //关键词检索条件
        String keywords = pd.getString("keywords");
        if (null != keywords && !"".equals(keywords)) {
            pd.put("keywords", keywords.trim());
        }
        //开始时间
        String lastLoginStart = pd.getString("lastLoginStart");
        //结束时间
        String lastLoginEnd = pd.getString("lastLoginEnd");
        if (lastLoginStart != null && !"".equals(lastLoginStart)) {
            pd.put("lastLoginStart", lastLoginStart + " 00:00:00");
        }
        if (lastLoginEnd != null && !"".equals(lastLoginEnd)) {
            pd.put("lastLoginEnd", lastLoginEnd + " 00:00:00");
        }
        if (!Constants.TWO_STRING.equals(pd.getString(Constants.TYPE))) {
            pd.put("TYPE", 1);
        }
        //当前用户名
        pd.put("FROM_USERNAME", Jurisdiction.getUsername());
        page.setPd(pd);
        //列出Fhsms列表
        List<PageData> varList = fhsmsService.list(page);
        mv.setViewName("system/fhsms/fhsms_list");
        mv.addObject("varList", varList);
        mv.addObject("pd", pd);
        //按钮权限
        mv.addObject("QX", Jurisdiction.getHC());
        return mv;
    }

    /**
     * 去发站内信界面
     */
    @RequestMapping(value = "/goAdd")
    public ModelAndView goAdd() {
        ModelAndView mv = this.getModelAndView();
        PageData pd = this.getPageData();
        mv.setViewName("system/fhsms/fhsms_edit");
        mv.addObject("msg", "save");
        mv.addObject("pd", pd);
        return mv;
    }

    /**
     * 去查看页面
     *
     * @throws Exception 可能抛出的异常
     */
    @RequestMapping(value = "/goView")
    public ModelAndView goView() throws Exception {
        ModelAndView mv = this.getModelAndView();
        PageData pd = this.getPageData();
        //在收信箱里面查看未读的站内信时去数据库改变未读状态为已读
        if (Constants.ONE_STRING.equals(pd.getString(Constants.TYPE)) && Constants.TWO_STRING.equals(pd.getString(Constants.STATUS))) {
            fhsmsService.edit(pd);
        }
        //根据ID读取
        pd = fhsmsService.findById(pd);
        mv.setViewName("system/fhsms/fhsms_view");
        mv.addObject("pd", pd);
        return mv;
    }

    /**
     * 批量删除
     *
     * @throws Exception 可能抛出的异常
     */
    @RequestMapping(value = "/deleteAll")
    @ResponseBody
    public Object deleteAll() throws Exception {
        logBefore(logger, Jurisdiction.getUsername() + "批量删除Fhsms");
        if (!Jurisdiction.buttonJurisdiction(menuUrl, Constants.DELE)) {
            return null;
        } //校验权限
        PageData pd = this.getPageData();
        Map<String, Object> map = Maps.newHashMap();
        List<PageData> pdList = Lists.newArrayList();
        String dataIds = pd.getString("DATA_IDS");
        if (StringUtils.isNotEmpty(dataIds)) {
            String[] allDatas = dataIds.split(",");
            logger.info("fhsmsService批量删除数据！");
            fhsmsService.deleteAll(allDatas);
            pd.put("msg", "ok");
        } else {
            pd.put("msg", "no");
        }
        pdList.add(pd);
        map.put("list", pdList);
        return AppUtil.returnObject(pd, map);
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        binder.registerCustomEditor(Date.class, new CustomDateEditor(format, true));
    }
}
