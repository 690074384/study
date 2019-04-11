package com.lph.controller.information.attached;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.lph.controller.base.BaseController;
import com.lph.entity.Page;
import com.lph.service.information.attached.AttachedManager;
import com.lph.service.information.attachedmx.AttachedMxManager;
import com.lph.util.*;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 说明：主附结构
 *
 * @author lvpenghui
 * @since 2019-4-9 16:37:16
 */
@Controller
@RequestMapping(value = "/attached")
public class AttachedController extends BaseController {

    /**
     * 菜单地址(权限用)
     */
    private String menuUrl = "attached/list.do";
    @Resource(name = "attachedService")
    private AttachedManager attachedService;

    @Resource(name = "attachedmxService")
    private AttachedMxManager attachedmxService;

    /**
     * 保存
     *
     * @throws Exception 可能抛出的异常
     */
    @RequestMapping(value = "/save")
    public ModelAndView save() throws Exception {
        logBefore(logger, Jurisdiction.getUsername() + "新增Attached");
        if (!Jurisdiction.buttonJurisdiction(menuUrl, Constants.ADDD)) {
            return null;
        } //校验权限
        ModelAndView mv = this.getModelAndView();
        PageData pd = this.getPageData();
        pd.put("ATTACHED_ID", this.get32UUID());
        //创建时间
        pd.put("CTIME", Tools.date2Str(new Date()));
        attachedService.save(pd);
        mv.addObject("msg", "success");
        mv.setViewName("save_result");
        return mv;
    }

    /**
     * 删除
     *
     * @throws Exception 可能抛出的异常
     */
    @RequestMapping(value = "/delete")
    @ResponseBody
    public Object delete() throws Exception {
        logBefore(logger, Jurisdiction.getUsername() + "删除Attached");
        if (!Jurisdiction.buttonJurisdiction(menuUrl, Constants.DELE)) {
            return null;
        } //校验权限
        Map<String, String> map = Maps.newHashMap();
        PageData pd = this.getPageData();
        String errInfo = "success";
        if (Integer.parseInt(attachedmxService.findCount(pd).get(Constants.ZS).toString()) > 0) {
            errInfo = "false";
        } else {
            attachedService.delete(pd);
        }
        map.put("result", errInfo);
        return AppUtil.returnObject(new PageData(), map);
    }

    /**
     * 修改
     *
     * @throws Exception 可能抛出的异常
     */
    @RequestMapping(value = "/edit")
    public ModelAndView edit() throws Exception {
        logBefore(logger, Jurisdiction.getUsername() + "修改Attached");
        if (!Jurisdiction.buttonJurisdiction(menuUrl, Constants.EDIT)) {
            return null;
        } //校验权限
        ModelAndView mv = this.getModelAndView();
        PageData pd = this.getPageData();
        attachedService.edit(pd);
        mv.addObject("msg", "success");
        mv.setViewName("save_result");
        return mv;
    }

    /**
     * 列表
     *
     * @param page 分页
     * @throws Exception 可能抛出的异常
     */
    @RequestMapping(value = "/list")
    public ModelAndView list(Page page) throws Exception {
        logBefore(logger, Jurisdiction.getUsername() + "列表Attached");
        //校验权限(无权查看时页面会有提示,如果不注释掉这句代码就无法进入列表页面,所以根据情况是否加入本句代码)
        //if(!Jurisdiction.buttonJurisdiction(menuUrl, "cha")){return null;}
        ModelAndView mv = this.getModelAndView();
        PageData pd = this.getPageData();
        //关键词检索条件
        String keywords = pd.getString("keywords");
        if (null != keywords && !"".equals(keywords)) {
            pd.put("keywords", keywords.trim());
        }
        page.setPd(pd);
        //列出Attached列表
        List<PageData> varList = attachedService.list(page);
        mv.setViewName("information/attached/attached_list");
        mv.addObject("varList", varList);
        mv.addObject("pd", pd);
        //按钮权限
        mv.addObject("QX", Jurisdiction.getHC());
        return mv;
    }

    /**
     * 去新增页面
     */
    @RequestMapping(value = "/goAdd")
    public ModelAndView goAdd() {
        ModelAndView mv = this.getModelAndView();
        PageData pd = this.getPageData();
        mv.setViewName("information/attached/attached_edit");
        mv.addObject("msg", "save");
        mv.addObject("pd", pd);
        return mv;
    }

    /**
     * 去修改页面
     *
     * @throws Exception 可能抛出的异常
     */
    @RequestMapping(value = "/goEdit")
    public ModelAndView goEdit() throws Exception {
        ModelAndView mv = this.getModelAndView();
        PageData pd = this.getPageData();
        //根据ID读取
        pd = attachedService.findById(pd);
        mv.setViewName("information/attached/attached_edit");
        mv.addObject("msg", "edit");
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
        logBefore(logger, Jurisdiction.getUsername() + "批量删除Attached");
        if (!Jurisdiction.buttonJurisdiction(menuUrl, Constants.DELE)) {
            return null;
        } //校验权限
        PageData pd = this.getPageData();
        Map<String, Object> map = Maps.newHashMap();
        List<PageData> pdList = Lists.newArrayList();
        String dataIds = pd.getString("DATA_IDS");
        if (null != dataIds && !"".equals(dataIds)) {
            String[] allDatas = dataIds.split(Constants.COMMA);
            logger.info("attachedService批量删除操作！");
            attachedService.deleteAll(allDatas);
            pd.put("msg", "ok");
        } else {
            pd.put("msg", "no");
        }
        pdList.add(pd);
        map.put("list", pdList);
        return AppUtil.returnObject(pd, map);
    }

    /**
     * 导出到excel
     *
     * @throws Exception 可能抛出的异常
     */
    @RequestMapping(value = "/excel")
    public ModelAndView exportExcel() throws Exception {
        logBefore(logger, Jurisdiction.getUsername() + "导出Attached到excel");
        if (!Jurisdiction.buttonJurisdiction(menuUrl, Constants.SRCH)) {
            return null;
        }
        PageData pd = this.getPageData();
        Map<String, Object> dataMap = Maps.newHashMap();
        List<String> titles = Lists.newArrayList();
        titles.add("名称");
        titles.add("描述");
        titles.add("价格");
        titles.add("创建时间");
        dataMap.put("titles", titles);
        List<PageData> varOList = attachedService.listAll(pd);
        List<PageData> varList = Lists.newArrayList();
        for (PageData aVarOList : varOList) {
            PageData vpd = new PageData();
            vpd.put("var1", aVarOList.getString("NAME"));
            vpd.put("var2", aVarOList.getString("FDESCRIBE"));
            vpd.put("var3", aVarOList.get("PRICE").toString());
            vpd.put("var4", aVarOList.getString("CTIME"));
            varList.add(vpd);
        }
        dataMap.put("varList", varList);
        ObjectExcelView erv = new ObjectExcelView();
        return new ModelAndView(erv, dataMap);
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        binder.registerCustomEditor(Date.class, new CustomDateEditor(format, true));
    }
}
