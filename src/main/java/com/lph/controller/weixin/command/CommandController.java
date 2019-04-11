package com.lph.controller.weixin.command;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.lph.controller.base.BaseController;
import com.lph.entity.Page;
import com.lph.service.weixin.command.CommandService;
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
 * 类名称：CommandController
 *
 * @author lvpenghui
 * @since 2019-4-11 16:18:17
 */
@Controller
@RequestMapping(value = "/command")
public class CommandController extends BaseController {

    /**
     * 菜单地址(权限用)
     */
    private String menuUrl = "command/list.do";
    @Resource(name = "commandService")
    private CommandService commandService;

    /**
     * 新增
     *
     * @return ModelAndView对象
     * @throws Exception 可能抛出的异常
     */
    @RequestMapping(value = "/save")
    public ModelAndView save() throws Exception {
        logBefore(logger, "新增Command");
        if (!Jurisdiction.buttonJurisdiction(menuUrl, Constants.ADDD)) {
            return null;
        } //校验权限
        ModelAndView mv = this.getModelAndView();
        PageData pd = this.getPageData();
        //主键
        pd.put("COMMAND_ID", this.get32UUID());
        //创建时间
        pd.put("CREATETIME", Tools.date2Str(new Date()));
        commandService.save(pd);
        mv.addObject("msg", "success");
        mv.setViewName("save_result");
        return mv;
    }

    /**
     * 删除
     *
     * @param out out
     */
    @RequestMapping(value = "/delete")
    public void delete(PrintWriter out) {
        logBefore(logger, "删除Command");
        if (!Jurisdiction.buttonJurisdiction(menuUrl, Constants.DELE)) {
            return;
        } //校验权限
        PageData pd = this.getPageData();
        try {
            commandService.delete(pd);
            out.write("success");
            out.close();
        } catch (Exception e) {
            logger.error(e.toString(), e);
        }
    }

    /**
     * 修改
     *
     * @return ModelAndView对象
     * @throws Exception 可能抛出的异常
     */
    @RequestMapping(value = "/edit")
    public ModelAndView edit() throws Exception {
        logBefore(logger, "修改Command");
        //校验权限
        if (!Jurisdiction.buttonJurisdiction(menuUrl, Constants.EDIT)) {
            return null;
        }
        ModelAndView mv = this.getModelAndView();
        PageData pd = this.getPageData();
        commandService.edit(pd);
        mv.addObject("msg", "success");
        mv.setViewName("save_result");
        return mv;
    }

    /**
     * 列表
     *
     * @param page 分页
     * @return ModelAndView对象
     */
    @RequestMapping(value = "/list")
    public ModelAndView list(Page page) {
        logBefore(logger, "列表Command");
        ModelAndView mv = this.getModelAndView();
        PageData pd = this.getPageData();
        try {
            String keyword = pd.getString("KEYWORD");
            if (StringUtils.isNotEmpty(keyword)) {
                pd.put("KEYWORD", keyword.trim());
            }
            page.setPd(pd);
            //列出Command列表
            List<PageData> varList = commandService.list(page);
            mv.setViewName("weixin/command/command_list");
            mv.addObject("varList", varList);
            mv.addObject("pd", pd);
            //按钮权限
            mv.addObject("QX", Jurisdiction.getHC());
        } catch (Exception e) {
            logger.error(e.toString(), e);
        }
        return mv;
    }

    /**
     * 去新增页面
     *
     * @return ModelAndView对象
     */
    @RequestMapping(value = "/goAdd")
    public ModelAndView goAdd() {
        logBefore(logger, "去新增Command页面");
        ModelAndView mv = this.getModelAndView();
        PageData pd = this.getPageData();
        try {
            mv.setViewName("weixin/command/command_edit");
            mv.addObject("msg", "save");
            mv.addObject("pd", pd);
        } catch (Exception e) {
            logger.error(e.toString(), e);
        }
        return mv;
    }

    /**
     * 去修改页面
     *
     * @return ModelAndView对象
     */
    @RequestMapping(value = "/goEdit")
    public ModelAndView goEdit() {
        logBefore(logger, "去修改Command页面");
        ModelAndView mv = this.getModelAndView();
        PageData pd = this.getPageData();
        try {
            //根据ID读取
            pd = commandService.findById(pd);
            mv.setViewName("weixin/command/command_edit");
            mv.addObject("msg", "edit");
            mv.addObject("pd", pd);
        } catch (Exception e) {
            logger.error(e.toString(), e);
        }
        return mv;
    }

    /**
     * 批量删除
     *
     * @return 批量删除返回结果
     */
    @RequestMapping(value = "/deleteAll")
    @ResponseBody
    public Object deleteAll() {
        logBefore(logger, "批量删除Command");
        if (!Jurisdiction.buttonJurisdiction(menuUrl, Constants.DELE)) {
            return null;
        } //校验权限
        PageData pd = new PageData();
        Map<String, Object> map = Maps.newHashMap();
        try {
            pd = this.getPageData();
            List<PageData> pdList = Lists.newArrayList();
            String dataIds = pd.getString("DATA_IDS");
            if (StringUtils.isNotEmpty(dataIds)) {
                String[] allDatas = dataIds.split(",");
                logger.info("commandService批量删除操作");
                commandService.deleteAll(allDatas);
                pd.put("msg", "ok");
            } else {
                pd.put("msg", "no");
            }
            pdList.add(pd);
            map.put("list", pdList);
        } catch (Exception e) {
            logger.error(e.toString(), e);
        } finally {
            logAfter(logger);
        }
        return AppUtil.returnObject(pd, map);
    }

    /**
     * 导出到excel
     *
     * @return ModelAndView对象
     */
    @RequestMapping(value = "/excel")
    public ModelAndView exportExcel() {
        logBefore(logger, "导出Command到excel");
        if (!Jurisdiction.buttonJurisdiction(menuUrl, Constants.SRCH)) {
            return null;
        }
        ModelAndView mv = new ModelAndView();
        PageData pd = this.getPageData();
        try {
            Map<String, Object> dataMap = Maps.newHashMap();
            List<String> titles = Lists.newArrayList();
            titles.add("关键词");
            titles.add("应用路径");
            titles.add("创建时间");
            titles.add("状态");
            titles.add("备注");
            dataMap.put("titles", titles);
            List<PageData> varOList = commandService.listAll(pd);
            List<PageData> varList = Lists.newArrayList();
            for (PageData aVarOList : varOList) {
                PageData vpd = new PageData();
                vpd.put("var1", aVarOList.getString("KEYWORD"));
                vpd.put("var2", aVarOList.getString("COMMANDCODE"));
                vpd.put("var3", aVarOList.getString("CREATETIME"));
                vpd.put("var4", aVarOList.get("STATUS").toString());
                vpd.put("var5", aVarOList.getString("BZ"));
                varList.add(vpd);
            }
            dataMap.put("varList", varList);
            ObjectExcelView erv = new ObjectExcelView();
            mv = new ModelAndView(erv, dataMap);
        } catch (Exception e) {
            logger.error(e.toString(), e);
        }
        return mv;
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        binder.registerCustomEditor(Date.class, new CustomDateEditor(format, true));
    }
}
