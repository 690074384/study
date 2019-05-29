package com.lph.controller.system.fhlog;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.lph.controller.base.BaseController;
import com.lph.entity.Page;
import com.lph.service.system.fhlog.FhLogManager;
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
 * 说明：操作日志记录
 *
 * @author lvpenghui
 * @since 2019-4-10 11:09:26
 */
@Controller
@RequestMapping(value = "/fhlog")
public class LogHistoryController extends BaseController {

    private String menuUrl = "fhlog/list.do";
    @Resource(name = "fhlogService")
    private FhLogManager fhlogService;

    /**
     * 删除
     *
     * @param out out
     * @throws Exception 可能抛出的异常
     */
    @RequestMapping(value = "/delete")
    public void delete(PrintWriter out) throws Exception {
        logBefore(logger, Jurisdiction.getUsername() + "删除FHlog");
        if (!Jurisdiction.buttonJurisdiction(menuUrl, Constants.DELE)) {
            return;
        }
        //校验权限
        PageData pd = this.getPageData();
        fhlogService.delete(pd);
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
        logBefore(logger, Jurisdiction.getUsername() + "列表FHlog");
        //校验权限(无权查看时页面会有提示,如果不注释掉这句代码就无法进入列表页面,所以根据情况是否加入本句代码)
        //if(!Jurisdiction.buttonJurisdiction(menuUrl, "cha")){return null;}
        ModelAndView mv = this.getModelAndView();
        PageData pd = this.getPageData();
        //关键词检索条件
        String keywords = pd.getString("keywords");
        if (null != keywords && !"".equals(keywords)) {
            pd.put("keywords", keywords.trim());
        }
        String lastStart = pd.getString("lastStart");
        String lastEnd = pd.getString("lastEnd");
        if (lastStart != null && !"".equals(lastStart)) {
            pd.put("lastStart", lastStart + " 00:00:00");
        }
        if (lastEnd != null && !"".equals(lastEnd)) {
            pd.put("lastEnd", lastEnd + " 00:00:00");
        }
        page.setPd(pd);
        List<PageData> varList = fhlogService.list(page);
        mv.setViewName("system/fhlog/fhlog_list");
        mv.addObject("varList", varList);
        mv.addObject("pd", pd);
        //按钮权限
        mv.addObject("QX", Jurisdiction.getHC());
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
        logBefore(logger, Jurisdiction.getUsername() + "批量删除FHlog");
        if (!Jurisdiction.buttonJurisdiction(menuUrl, Constants.DELE)) {
            return null;
        } //校验权限
        PageData pd = this.getPageData();
        Map<String, Object> map = Maps.newHashMap();
        List<PageData> pdList = Lists.newArrayList();
        String dataIds = pd.getString("DATA_IDS");
        if (StringUtils.isNotEmpty(dataIds)) {
            String[] allDatas = dataIds.split(",");
            fhlogService.deleteAll(allDatas);
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
        logBefore(logger, Jurisdiction.getUsername() + "导出FHlog到excel");
        if (!Jurisdiction.buttonJurisdiction(menuUrl, Constants.SRCH)) {
            return null;
        }
        PageData pd = this.getPageData();
        Map<String, Object> dataMap = Maps.newHashMap();
        List<String> titles = Lists.newArrayList();
        titles.add("用户名");
        titles.add("操作时间");
        titles.add("事件");
        dataMap.put("titles", titles);
        List<PageData> varOList = fhlogService.listAll(pd);
        List<PageData> varList = Lists.newArrayList();
        for (PageData aVarOList : varOList) {
            PageData vpd = new PageData();
            vpd.put("var1", aVarOList.getString("USERNAME"));
            vpd.put("var2", aVarOList.getString("CZTIME"));
            vpd.put("var3", aVarOList.getString("CONTENT"));
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
