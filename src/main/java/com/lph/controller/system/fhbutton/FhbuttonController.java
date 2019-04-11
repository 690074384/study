package com.lph.controller.system.fhbutton;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.lph.controller.base.BaseController;
import com.lph.entity.Page;
import com.lph.service.system.fhbutton.FhbuttonManager;
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
import java.util.*;

/**
 * 说明：按钮管理
 *
 * @author lvpenghui
 * @since 2019-4-9 21:02:51
 */
@Controller
@RequestMapping(value = "/fhbutton")
public class FhbuttonController extends BaseController {


    private String menuUrl = "fhbutton/list.do";
    @Resource(name = "fhbuttonService")
    private FhbuttonManager fhbuttonService;

    /**
     * 保存
     *
     * @throws Exception 可能抛出的异常
     */
    @RequestMapping(value = "/save")
    public ModelAndView save() throws Exception {
        logBefore(logger, Jurisdiction.getUsername() + "新增Fhbutton");
        if (!Jurisdiction.buttonJurisdiction(menuUrl, Constants.ADDD)) {
            return null;
        } //校验权限
        ModelAndView mv = this.getModelAndView();
        PageData pd = this.getPageData();
        //主键
        pd.put("FHBUTTON_ID", this.get32UUID());
        fhbuttonService.save(pd);
        mv.addObject("msg", "success");
        mv.setViewName("save_result");
        return mv;
    }

    /**
     * 删除
     *
     * @param out out
     * @throws Exception 可能抛出的异常
     */
    @RequestMapping(value = "/delete")
    public void delete(PrintWriter out) throws Exception {
        logBefore(logger, Jurisdiction.getUsername() + "删除Fhbutton");
        if (!Jurisdiction.buttonJurisdiction(menuUrl, Constants.DELE)) {
            return;
        } //校验权限
        PageData pd = this.getPageData();
        fhbuttonService.delete(pd);
        out.write("success");
        out.close();
    }

    /**
     * 修改
     *
     * @throws Exception 可能抛出的异常
     */
    @RequestMapping(value = "/edit")
    public ModelAndView edit() throws Exception {
        logBefore(logger, Jurisdiction.getUsername() + "修改Fhbutton");
        if (!Jurisdiction.buttonJurisdiction(menuUrl, Constants.EDIT)) {
            return null;
        } //校验权限
        ModelAndView mv = this.getModelAndView();
        PageData pd = this.getPageData();
        fhbuttonService.edit(pd);
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
        logBefore(logger, Jurisdiction.getUsername() + "列表Fhbutton");
        ModelAndView mv = this.getModelAndView();
        PageData pd = this.getPageData();
        //关键词检索条件
        String keywords = pd.getString("keywords");
        if (null != keywords && !"".equals(keywords)) {
            pd.put("keywords", keywords.trim());
        }
        page.setPd(pd);
        //列出Fhbutton列表
        List<PageData> varList = fhbuttonService.list(page);
        mv.setViewName("system/fhbutton/fhbutton_list");
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
        mv.setViewName("system/fhbutton/fhbutton_edit");
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
        pd = fhbuttonService.findById(pd);
        mv.setViewName("system/fhbutton/fhbutton_edit");
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
        logBefore(logger, Jurisdiction.getUsername() + "批量删除Fhbutton");
        if (!Jurisdiction.buttonJurisdiction(menuUrl, Constants.DELE)) {
            return null;
        } //校验权限
        PageData pd = this.getPageData();
        Map<String, Object> map = Maps.newHashMap();
        List<PageData> pdList = Lists.newArrayList();
        String dataIds = pd.getString("DATA_IDS");
        if (StringUtils.isNotEmpty(dataIds)) {
            String[] allDatas = dataIds.split(Constants.COMMA);
            logger.info("fhbuttonService批量删除操作！");
            fhbuttonService.deleteAll(allDatas);
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
        logBefore(logger, Jurisdiction.getUsername() + "导出Fhbutton到excel");
        if (!Jurisdiction.buttonJurisdiction(menuUrl, Constants.SRCH)) {
            return null;
        }
        ModelAndView mv;
        PageData pd;
        pd = this.getPageData();
        Map<String, Object> dataMap = Maps.newHashMap();
        List<String> titles = Lists.newArrayList();
        titles.add("名称");
        titles.add("权限标识");
        titles.add("备注");
        dataMap.put("titles", titles);
        List<PageData> varOList = fhbuttonService.listAll(pd);
        List<PageData> varList = Lists.newArrayList();
        for (PageData aVarOList : varOList) {
            PageData vpd = new PageData();
            vpd.put("var1", aVarOList.getString("NAME"));
            vpd.put("var2", aVarOList.getString("QX_NAME"));
            vpd.put("var3", aVarOList.getString("BZ"));
            varList.add(vpd);
        }
        dataMap.put("varList", varList);
        ObjectExcelView erv = new ObjectExcelView();
        mv = new ModelAndView(erv, dataMap);
        return mv;
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        binder.registerCustomEditor(Date.class, new CustomDateEditor(format, true));
    }
}
