package com.lph.controller.weixin.imgmsg;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.lph.controller.base.BaseController;
import com.lph.entity.Page;
import com.lph.service.weixin.imgmsg.ImgmsgService;
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
 * 类名称：ImgmsgController
 *
 * @author lvpenghui
 * @since 2019-4-11 16:48:51
 */
@Controller
@RequestMapping(value = "/imgmsg")
public class ImgmsgController extends BaseController {

    /**
     * 菜单地址(权限用)
     */
    private String menuUrl = "imgmsg/list.do";
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
        logBefore(logger, "新增Imgmsg");
        if (!Jurisdiction.buttonJurisdiction(menuUrl, Constants.ADDD)) {
            return null;
        } //校验权限
        ModelAndView mv = this.getModelAndView();
        PageData pd = this.getPageData();
        pd.put("IMGMSG_ID", this.get32UUID());
        pd.put("CREATETIME", Tools.date2Str(new Date()));
        imgmsgService.save(pd);
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
        logBefore(logger, "删除Imgmsg");
        if (!Jurisdiction.buttonJurisdiction(menuUrl, Constants.DELE)) {
            return;
        } //校验权限
        PageData pd = this.getPageData();
        try {
            logger.info("imgmsgService删除");
            imgmsgService.delete(pd);
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
        logBefore(logger, "修改Imgmsg");
        if (!Jurisdiction.buttonJurisdiction(menuUrl, Constants.EDIT)) {
            return null;
        } //校验权限
        ModelAndView mv = this.getModelAndView();
        PageData pd = this.getPageData();
        imgmsgService.edit(pd);
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
        logBefore(logger, "列表Imgmsg");
        ModelAndView mv = this.getModelAndView();
        PageData pd = this.getPageData();
        try {
            String keyword = pd.getString("KEYWORD");
            if (StringUtils.isNotEmpty(keyword)) {
                pd.put("KEYWORD", keyword.trim());
            }
            page.setPd(pd);
            //列出Imgmsg列表
            List<PageData> varList = imgmsgService.list(page);
            mv.setViewName("weixin/imgmsg/imgmsg_list");
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
        logBefore(logger, "去新增Imgmsg页面");
        ModelAndView mv = this.getModelAndView();
        PageData pd = this.getPageData();
        try {
            mv.setViewName("weixin/imgmsg/imgmsg_edit");
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
        logBefore(logger, "去修改Imgmsg页面");
        ModelAndView mv = this.getModelAndView();
        PageData pd = this.getPageData();
        try {
            //根据ID读取
            pd = imgmsgService.findById(pd);
            mv.setViewName("weixin/imgmsg/imgmsg_edit");
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
     * @return 批量删除返回的结果
     */
    @RequestMapping(value = "/deleteAll")
    @ResponseBody
    public Object deleteAll() {
        logBefore(logger, "批量删除Imgmsg");
        if (!Jurisdiction.buttonJurisdiction(menuUrl, Constants.DELE)) {
            return null;
        } //校验权限
        PageData pd = this.getPageData();
        Map<String, Object> map = Maps.newHashMap();
        List<PageData> pdList = Lists.newArrayList();
        String dataIds = pd.getString("DATA_IDS");
        try {

            if (StringUtils.isNotEmpty(dataIds)) {
                String[] allDatas = dataIds.split(",");
                logger.info("imgmsgService批量刪除");
                imgmsgService.deleteAll(allDatas);
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
        logBefore(logger, "导出Imgmsg到excel");
        if (!Jurisdiction.buttonJurisdiction(menuUrl, Constants.SRCH)) {
            return null;
        }
        ModelAndView mv = new ModelAndView();
        PageData pd = this.getPageData();
        try {
            Map<String, Object> dataMap = Maps.newHashMap();
            List<String> titles = getTitles();
            dataMap.put("titles", titles);
            List<PageData> varOList = imgmsgService.listAll(pd);
            List<PageData> varList = Lists.newArrayList();
            for (PageData aVarOList : varOList) {
                PageData vpd = new PageData();
                vpd.put("var1", aVarOList.getString("KEYWORD"));
                vpd.put("var2", aVarOList.getString("CREATETIME"));
                vpd.put("var3", aVarOList.get("STATUS").toString());
                vpd.put("var4", aVarOList.getString("BZ"));
                vpd.put("var5", aVarOList.getString("TITLE1"));
                vpd.put("var6", aVarOList.getString("DESCRIPTION1"));
                vpd.put("var7", aVarOList.getString("IMGURL1"));
                vpd.put("var8", aVarOList.getString("TOURL1"));
                vpd.put("var9", aVarOList.getString("TITLE2"));
                vpd.put("var10", aVarOList.getString("DESCRIPTION2"));
                vpd.put("var11", aVarOList.getString("IMGURL2"));
                vpd.put("var12", aVarOList.getString("TOURL2"));
                vpd.put("var13", aVarOList.getString("TITLE3"));
                vpd.put("var14", aVarOList.getString("DESCRIPTION3"));
                vpd.put("var15", aVarOList.getString("IMGURL3"));
                vpd.put("var16", aVarOList.getString("TOURL3"));
                vpd.put("var17", aVarOList.getString("TITLE4"));
                vpd.put("var18", aVarOList.getString("DESCRIPTION4"));
                vpd.put("var19", aVarOList.getString("IMGURL4"));
                vpd.put("var20", aVarOList.getString("TOURL4"));
                vpd.put("var21", aVarOList.getString("TITLE5"));
                vpd.put("var22", aVarOList.getString("DESCRIPTION5"));
                vpd.put("var23", aVarOList.getString("IMGURL5"));
                vpd.put("var24", aVarOList.getString("TOURL5"));
                vpd.put("var25", aVarOList.getString("TITLE6"));
                vpd.put("var26", aVarOList.getString("DESCRIPTION6"));
                vpd.put("var27", aVarOList.getString("IMGURL6"));
                vpd.put("var28", aVarOList.getString("TOURL6"));
                vpd.put("var29", aVarOList.getString("TITLE7"));
                vpd.put("var30", aVarOList.getString("DESCRIPTION7"));
                vpd.put("var31", aVarOList.getString("IMGURL7"));
                vpd.put("var32", aVarOList.getString("TOURL7"));
                vpd.put("var33", aVarOList.getString("TITLE8"));
                vpd.put("var34", aVarOList.getString("DESCRIPTION8"));
                vpd.put("var35", aVarOList.getString("IMGURL8"));
                vpd.put("var36", aVarOList.getString("TOURL8"));
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

    /**
     * 获取Excel表头
     *
     * @return excel表头对应的list
     */
    private List<String> getTitles() {
        List<String> titles = Lists.newArrayList();
        titles.add("关键词");
        titles.add("创建时间");
        titles.add("状态");
        titles.add("备注");
        titles.add("标题1");
        titles.add("描述1");
        titles.add("图片地址1");
        titles.add("超链接1");
        titles.add("标题2");
        titles.add("描述2");
        titles.add("图片地址2");
        titles.add("超链接2");
        titles.add("标题3");
        titles.add("描述3");
        titles.add("图片地址3");
        titles.add("超链接3");
        titles.add("标题4");
        titles.add("描述4");
        titles.add("图片地址4");
        titles.add("超链接4");
        titles.add("标题5");
        titles.add("描述5");
        titles.add("图片地址5");
        titles.add("超链接5");
        titles.add("标题6");
        titles.add("描述6");
        titles.add("图片地址6");
        titles.add("超链接6");
        titles.add("标题7");
        titles.add("描述7");
        titles.add("图片地址7");
        titles.add("超链接7");
        titles.add("标题8");
        titles.add("描述8");
        titles.add("图片地址8");
        titles.add("超链接8");
        return titles;
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        binder.registerCustomEditor(Date.class, new CustomDateEditor(format, true));
    }
}
