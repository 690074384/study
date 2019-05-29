package com.lph.controller.fhoa.fhfile;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.lph.controller.base.BaseController;
import com.lph.entity.Page;
import com.lph.service.fhoa.fhfile.FhfileManager;
import com.lph.util.*;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 说明：文件管理
 *
 * @author lvpenghui
 * @since 2019-4-8 22:03:40
 */
@Controller
@RequestMapping(value = "/fhfile")
public class FhfileController extends BaseController {

    private String menuUrl = "fhfile/list.do";
    @Resource(name = "fhfileService")
    private FhfileManager fhfileService;

    /**
     * 保存
     *
     * @throws Exception 可能抛出的异常
     */
    @RequestMapping(value = "/save")
    public ModelAndView save() throws Exception {
        logBefore(logger, Jurisdiction.getUsername() + "新增Fhfile");
        if (!Jurisdiction.buttonJurisdiction(menuUrl, Constants.ADDD)) {
            return null;
        } //校验权限
        ModelAndView mv = this.getModelAndView();
        PageData pd = this.getPageData();
        pd.put("FHFILE_ID", this.get32UUID());
        //上传时间
        pd.put("CTIME", Tools.date2Str(new Date()));
        //上传者
        pd.put("USERNAME", Jurisdiction.getUsername());
        //部门ID
        pd.put("DEPARTMENT_ID", Jurisdiction.getDEPARTMENT_ID());
        //文件大小
        pd.put("FILESIZE", FileUtil.getFilesize(PathUtil.getClasspath() + Constants.FILEPATHFILEOA + pd.getString("FILEPATH")));
        fhfileService.save(pd);
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
        logBefore(logger, Jurisdiction.getUsername() + "删除Fhfile");
        if (!Jurisdiction.buttonJurisdiction(menuUrl, Constants.DELE)) {
            return;
        }
        //校验权限
        PageData pd = this.getPageData();
        pd = fhfileService.findById(pd);
        fhfileService.delete(pd);
        //删除文件
        DelAllFile.delFolder(PathUtil.getClasspath() + Constants.FILEPATHFILEOA + pd.getString("FILEPATH"));
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
        logBefore(logger, Jurisdiction.getUsername() + "列表Fhfile");
        //校验权限(无权查看时页面会有提示,如果不注释掉这句代码就无法进入列表页面,所以根据情况是否加入本句代码)
        //if(!Jurisdiction.buttonJurisdiction(menuUrl, "cha")){return null;}
        ModelAndView mv = this.getModelAndView();
        PageData pd = this.getPageData();
        //关键词检索条件
        String keywords = pd.getString("keywords");
        if (null != keywords && !"".equals(keywords)) {
            pd.put("keywords", keywords.trim());
        }
        String item = Jurisdiction.getDEPARTMENT_IDS();
        if (Constants.ZERO_STRING.equals(item) || Constants.WU_QUAN.equals(item)) {
            //根据部门ID过滤
            pd.put("item", "");
        } else {
            pd.put("item", item.replaceFirst("\\(", "\\('" + Jurisdiction.getDEPARTMENT_ID() + "',"));
        }
        page.setPd(pd);
        //列出Fhfile列表
        List<PageData> varList = fhfileService.list(page);
        mv.setViewName("fhoa/fhfile/fhfile_list");
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
        mv.setViewName("fhoa/fhfile/fhfile_edit");
        mv.addObject("msg", "save");
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
        logBefore(logger, Jurisdiction.getUsername() + "批量删除Fhfile");
        if (!Jurisdiction.buttonJurisdiction(menuUrl, Constants.DELE)) {
            return null;
        } //校验权限
        PageData pd = this.getPageData();
        Map<String, Object> map = Maps.newHashMap();
        List<PageData> pdList = Lists.newArrayList();
        String dataIds = pd.getString("DATA_IDS");
        if (null != dataIds && !"".equals(dataIds)) {
            String[] allDatas = dataIds.split(Constants.COMMA);
            PageData fpd = new PageData();
            for (String allData : allDatas) {
                fpd.put("FHFILE_ID", allData);
                fpd = fhfileService.findById(fpd);
                //删除物理文件
                DelAllFile.delFolder(PathUtil.getClasspath() + Constants.FILEPATHFILEOA + fpd.getString("FILEPATH"));
            }
            //删除数据库记录
            fhfileService.deleteAll(allDatas);
            pd.put("msg", "ok");
        } else {
            pd.put("msg", "no");
        }
        pdList.add(pd);
        map.put("list", pdList);
        return AppUtil.returnObject(pd, map);
    }

    /**
     * 下载
     *
     * @param response 响应数据
     * @throws Exception 可能抛出的异常
     */
    @RequestMapping(value = "/download")
    public void downExcel(HttpServletResponse response) throws Exception {
        PageData pd = this.getPageData();
        pd = fhfileService.findById(pd);
        String fileName = pd.getString("FILEPATH");
        FileDownload.fileDownload(response, PathUtil.getClasspath() + Constants.FILEPATHFILEOA + fileName, pd.getString("NAME") + fileName.substring(19));
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        binder.registerCustomEditor(Date.class, new CustomDateEditor(format, true));
    }
}
