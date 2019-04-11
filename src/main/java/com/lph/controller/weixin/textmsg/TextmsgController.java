package com.lph.controller.weixin.textmsg;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.lph.controller.base.BaseController;
import com.lph.entity.Page;
import com.lph.service.weixin.command.CommandService;
import com.lph.service.weixin.imgmsg.ImgmsgService;
import com.lph.service.weixin.textmsg.TextmsgService;
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
 * 类名称：TextmsgController
 *
 * @author lvpenghui
 * @since 2019-4-11 17:02:10
 */
@Controller
@RequestMapping(value = "/textmsg")
public class TextmsgController extends BaseController {

    /**
     * 菜单地址(权限用)
     */
    private String menuUrl = "textmsg/list.do";
    @Resource(name = "textmsgService")
    private TextmsgService textmsgService;
    @Resource(name = "commandService")
    private CommandService commandService;
    @Resource(name = "imgmsgService")
    private ImgmsgService imgmsgService;

    /**
     * 新增
     *
     * @return ModelAndView对象
     * @throws Exception 可能抛出的异常
     */
    @RequestMapping(value = "/save")
    public ModelAndView save() throws Exception {
        logBefore(logger, "新增Textmsg");
        if (!Jurisdiction.buttonJurisdiction(menuUrl, Constants.ADDD)) {
            return null;
        }
        ModelAndView mv = this.getModelAndView();
        PageData pd = this.getPageData();
        //主键
        pd.put("TEXTMSG_ID", this.get32UUID());
        //创建时间
        pd.put("CREATETIME", Tools.date2Str(new Date()));
        textmsgService.save(pd);
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
        logBefore(logger, "删除Textmsg");
        if (!Jurisdiction.buttonJurisdiction(menuUrl, Constants.DELE)) {
            return;
        }
        PageData pd = this.getPageData();
        try {
            logger.info("textmsgService删除操作");
            textmsgService.delete(pd);
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
        logBefore(logger, "修改Textmsg");
        if (!Jurisdiction.buttonJurisdiction(menuUrl, Constants.EDIT)) {
            return null;
        }
        ModelAndView mv = this.getModelAndView();
        PageData pd = this.getPageData();
        textmsgService.edit(pd);
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
        logBefore(logger, "列表Textmsg");
        ModelAndView mv = this.getModelAndView();
        PageData pd = this.getPageData();
        try {
            String keyword = pd.getString("KEYWORD");
            if (StringUtils.isNotEmpty(keyword)) {
                pd.put("KEYWORD", keyword.trim());
            }
            page.setPd(pd);
            //列出Textmsg列表
            List<PageData> varList = textmsgService.list(page);
            mv.setViewName("weixin/textmsg/textmsg_list");
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
        logBefore(logger, "去新增Textmsg页面");
        ModelAndView mv = this.getModelAndView();
        PageData pd = this.getPageData();
        try {
            mv.setViewName("weixin/textmsg/textmsg_edit");
            mv.addObject("msg", "save");
            mv.addObject("pd", pd);
        } catch (Exception e) {
            logger.error(e.toString(), e);
        }
        return mv;
    }

    /**
     * 去关注回复页面
     *
     * @return ModelAndView对象
     */
    @RequestMapping(value = "/goSubscribe")
    public ModelAndView goSubscribe() {
        logBefore(logger, "去关注回复页面");
        ModelAndView mv = this.getModelAndView();
        PageData pd = this.getPageData();
        try {
            pd.put("KEYWORD", "关注");
            PageData msgpd = textmsgService.findByKw(pd);
            if (null != msgpd) {
                mv.addObject("msg", "文本消息");
                mv.addObject("content", msgpd.getString("CONTENT"));
            } else {
                msgpd = imgmsgService.findByKw(pd);
                if (null != msgpd) {
                    mv.addObject("msg", "图文消息");
                    mv.addObject("content", "标题：" + msgpd.getString("TITLE1"));
                } else {
                    msgpd = commandService.findByKw(pd);
                    if (null != msgpd) {
                        mv.addObject("msg", "命令");
                        mv.addObject("content", "执行命令：" + msgpd.getString("COMMANDCODE"));
                    } else {
                        mv.addObject("msg", "无回复");
                    }
                }
            }
            mv.setViewName("weixin/subscribe");
            mv.addObject("pd", msgpd);
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
        logBefore(logger, "去修改Textmsg页面");
        ModelAndView mv = this.getModelAndView();
        PageData pd = this.getPageData();
        try {
            //根据ID读取
            pd = textmsgService.findById(pd);
            mv.setViewName("weixin/textmsg/textmsg_edit");
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
        logBefore(logger, "批量删除Textmsg");
        if (!Jurisdiction.buttonJurisdiction(menuUrl, Constants.DELE)) {
            return null;
        }
        PageData pd = this.getPageData();
        Map<String, Object> map = Maps.newHashMap();
        try {
            List<PageData> pdList = Lists.newArrayList();
            String dataIds = pd.getString("DATA_IDS");
            if (StringUtils.isNotEmpty(dataIds)) {
                String[] allDatas = dataIds.split(",");
                logger.info("textmsgService批量删除操作");
                textmsgService.deleteAll(allDatas);
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
     * 判断关键词是否存在
     *
     * @return 关键词是否存在
     */
    @RequestMapping(value = "/hasK")
    @ResponseBody
    public Object hasK() {
        Map<String, String> map = Maps.newHashMap();
        String errInfo = "success";
        PageData pd = this.getPageData();
        try {
            pd.put("STATUS", "3");
            if (textmsgService.findByKw(pd) != null || commandService.findByKw(pd) != null || imgmsgService.findByKw(pd) != null) {
                errInfo = "error";
            }
        } catch (Exception e) {
            logger.error(e.toString(), e);
        }
        //返回结果
        map.put("result", errInfo);
        return AppUtil.returnObject(new PageData(), map);
    }

    /**
     * 导出到excel
     *
     * @return ModelAndView对象
     */
    @RequestMapping(value = "/excel")
    public ModelAndView exportExcel() {
        logBefore(logger, "导出Textmsg到excel");
        if (!Jurisdiction.buttonJurisdiction(menuUrl, Constants.SRCH)) {
            return null;
        }
        ModelAndView mv = new ModelAndView();
        PageData pd = this.getPageData();
        try {
            Map<String, Object> dataMap = Maps.newHashMap();
            List<String> titles = Lists.newArrayList();
            titles.add("关键词");
            titles.add("内容");
            titles.add("创建时间");
            titles.add("状态");
            titles.add("备注");
            dataMap.put("titles", titles);
            List<PageData> varOList = textmsgService.listAll(pd);
            List<PageData> varList = Lists.newArrayList();
            for (PageData aVarOList : varOList) {
                PageData vpd = new PageData();
                vpd.put("var1", aVarOList.getString("KEYWORD"));
                vpd.put("var2", aVarOList.getString("CONTENT"));
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
