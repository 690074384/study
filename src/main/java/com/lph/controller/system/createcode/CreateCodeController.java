package com.lph.controller.system.createcode;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.lph.controller.base.BaseController;
import com.lph.entity.Page;
import com.lph.service.system.createcode.CreateCodeManager;
import com.lph.util.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.*;

/**
 * 类名称： 代码生成器
 *
 * @author lvpenghui
 * @since 2019-4-9 19:24:30
 */
@Controller
@RequestMapping(value = "/createCode")
public class CreateCodeController extends BaseController {

    /**
     * 菜单地址(权限用)
     */
    private String menuUrl = "createcode/list.do";
    @Resource(name = "createcodeService")
    private CreateCodeManager createcodeService;

    /**
     * 列表
     *
     * @param page 分页
     * @return ModelAndView对象
     */
    @RequestMapping(value = "/list")
    public ModelAndView list(Page page) throws Exception {
        if (!Jurisdiction.buttonJurisdiction(menuUrl, Constants.SRCH)) {
            return null;
        }
        //校验权限
        ModelAndView mv = this.getModelAndView();
        PageData pd = this.getPageData();
        //检索条件
        String keywords = pd.getString("keywords");
        if (null != keywords && !"".equals(keywords)) {
            keywords = keywords.trim();
            pd.put("keywords", keywords);
        }
        page.setPd(pd);
        //列出CreateCode列表
        List<PageData> varList = createcodeService.list(page);
        mv.setViewName("system/createcode/createcode_list");
        mv.addObject("varList", varList);
        mv.addObject("pd", pd);
        //按钮权限
        mv.addObject("QX", Jurisdiction.getHC());
        return mv;
    }

    /**
     * 去代码生成器页面(进入弹窗)
     *
     * @return ModelAndView对象
     * @throws Exception 可能抛出的异常
     */
    @RequestMapping(value = "/goProductCode")
    public ModelAndView goProductCode() throws Exception {
        ModelAndView mv = this.getModelAndView();
        PageData pd = this.getPageData();
        String createCodeId = pd.getString("CREATECODE_ID");
        if (!Constants.ADDD.equals(createCodeId)) {
            pd = createcodeService.findById(pd);
            mv.addObject("pd", pd);
            mv.addObject("msg", "edit");

        } else {
            mv.addObject("msg", "add");
        }
        //列出所有主表结构的
        List<PageData> varList = createcodeService.listFa();
        mv.addObject("varList", varList);
        mv.setViewName("system/createcode/productCode");
        return mv;
    }

    /**
     * 生成代码
     *
     * @param response HttpServletResponse对象
     * @throws Exception 可能抛出的异常
     */
    @RequestMapping(value = "/proCode")
    public void proCode(HttpServletResponse response) throws Exception {
        if (!Jurisdiction.buttonJurisdiction(menuUrl, Constants.ADDD)) {
            return;
        }
        //校验权限
        logBefore(logger, Jurisdiction.getUsername() + "执行代码生成器");
        PageData pd = this.getPageData();
        this.save(pd);
        this.makeDir(response, pd);
    }

    private void makeDir(HttpServletResponse response, PageData pd) throws Exception {
        //主表名				========参数0-1 主附结构用
        String faobject = pd.getString("faobject");
        //模块类型			========参数0-2 类型，单表、树形结构、主表明细表
        String fhType = pd.getString("FHTYPE");
        //说明				========参数0
        String title = pd.getString("TITLE");
        //包名				========参数1
        String packageName = pd.getString("packageName");
        //类名				========参数2
        String objectName = pd.getString("objectName");
        //表前缀				========参数3
        String tabletop = pd.getString("tabletop");
        //表前缀转大写
        tabletop = null == tabletop ? "" : tabletop.toUpperCase();
        //属性总数
        String zindext = pd.getString("zindex");
        int zindex = 0;
        if (null != zindext && !"".equals(zindext)) {
            zindex = Integer.parseInt(zindext);
        }
        //属性集合			========参数4
        List<String[]> fieldList = Lists.newArrayList();
        for (int i = 0; i < zindex; i++) {
            //属性放到集合里面
            fieldList.add(pd.getString("field" + i).split(",fh,"));
        }
        //创建数据模型
        Map<String, Object> root = Maps.newHashMap();
        root.put("fieldList", fieldList);
        //主附结构用，主表名
        root.put("faobject", faobject.toUpperCase());
        //说明
        root.put("TITLE", title);
        //包名
        root.put("packageName", packageName);
        //类名
        root.put("objectName", objectName);
        //类名(全小写)
        root.put("objectNameLower", objectName.toLowerCase());
        //类名(全大写)
        root.put("objectNameUpper", objectName.toUpperCase());
        //表前缀
        root.put("tabletop", tabletop);
        //当前日期
        root.put("nowDate", new Date());
        //生成代码前,先清空之前生成的代码
        DelAllFile.delFolder(PathUtil.getClasspath() + "admin/ftl");
        //存放路径
        //ftl路径
        String ftlPath = "createCode";

        //文件路径

        String filePath = "admin/ftl/code";
        if (Constants.TREE.equals(fhType)) {
            ftlPath = "createTreeCode";
            /*生成实体类*/
            Freemarker.printFile("entityTemplate.ftl", root, "entity/" + packageName + "/" + objectName + ".java", filePath, ftlPath);
            /*生成jsp_tree页面*/
            Freemarker.printFile("jsp_tree_Template.ftl", root, "jsp/" + packageName + "/" + objectName.toLowerCase() + "/" + objectName.toLowerCase() + "_tree.jsp", filePath, ftlPath);
        } else if (Constants.FATHER_TABLE.equals(fhType)) {
            //主表
            ftlPath = "createFaCode";
        } else if (Constants.SON_TABLE.equals(fhType)) {
            //明细表
            ftlPath = "createSoCode";
        }
        this.makeFile(packageName, objectName, tabletop, root, ftlPath, filePath);
        //this.print("oracle_SQL_Template.ftl", root);  控制台打印
        /*生成的全部代码压缩成zip文件*/
        if (FileZip.zip(PathUtil.getClasspath() + filePath, PathUtil.getClasspath() + filePath + Constants.POINT + Constants.ZIP)) {
            /*下载代码*/
            FileDownload.fileDownload(response, PathUtil.getClasspath() + filePath + Constants.POINT + Constants.ZIP, Constants.CODE + Constants.POINT + Constants.ZIP);
        }
    }

    private void makeFile(String packageName, String objectName, String tabletop, Map<String, Object> root, String ftlPath, String filePath) throws Exception {
        /*生成controller*/
        Freemarker.printFile("controllerTemplate.ftl", root, "controller/" + packageName + "/" + objectName.toLowerCase() + "/" + objectName + "Controller.java", filePath, ftlPath);
        /*生成service*/
        Freemarker.printFile("serviceTemplate.ftl", root, "service/" + packageName + "/" + objectName.toLowerCase() + "/impl/" + objectName + "Service.java", filePath, ftlPath);
        /*生成manager*/
        Freemarker.printFile("managerTemplate.ftl", root, "service/" + packageName + "/" + objectName.toLowerCase() + "/" + objectName + "Manager.java", filePath, ftlPath);
        /*生成mybatis xml*/
        Freemarker.printFile("mapperMysqlTemplate.ftl", root, "mybatis_mysql/" + packageName + "/" + objectName + "Mapper.xml", filePath, ftlPath);
        Freemarker.printFile("mapperOracleTemplate.ftl", root, "mybatis_oracle/" + packageName + "/" + objectName + "Mapper.xml", filePath, ftlPath);
        Freemarker.printFile("mapperSqlserverTemplate.ftl", root, "mybatis_sqlserver/" + packageName + "/" + objectName + "Mapper.xml", filePath, ftlPath);
        /*生成SQL脚本*/
        Freemarker.printFile("mysql_SQL_Template.ftl", root, "mysql数据库脚本/" + tabletop + objectName.toUpperCase() + ".sql", filePath, ftlPath);
        Freemarker.printFile("oracle_SQL_Template.ftl", root, "oracle数据库脚本/" + tabletop + objectName.toUpperCase() + ".sql", filePath, ftlPath);
        Freemarker.printFile("sqlserver_SQL_Template.ftl", root, "sqlserver数据库脚本/" + tabletop + objectName.toUpperCase() + ".sql", filePath, ftlPath);
        /*生成jsp页面*/
        Freemarker.printFile("jsp_list_Template.ftl", root, "jsp/" + packageName + "/" + objectName.toLowerCase() + "/" + objectName.toLowerCase() + "_list.jsp", filePath, ftlPath);
        Freemarker.printFile("jsp_edit_Template.ftl", root, "jsp/" + packageName + "/" + objectName.toLowerCase() + "/" + objectName.toLowerCase() + "_edit.jsp", filePath, ftlPath);
        /*生成说明文档*/
        Freemarker.printFile("docTemplate.ftl", root, "部署说明.doc", filePath, ftlPath);
    }

    /**
     * 保存到数据库
     *
     * @throws Exception 可能抛出的异常
     */
    public void save(PageData pd) throws Exception {
        pd.put("PACKAGENAME", pd.getString("packageName"));
        pd.put("OBJECTNAME", pd.getString("objectName"));
        pd.put("TABLENAME", pd.getString("tabletop") + ",lph," + pd.getString("objectName").toUpperCase());
        pd.put("FIELDLIST", pd.getString("FIELDLIST"));
        pd.put("CREATETIME", DateUtil.getTime());
        pd.put("TITLE", pd.getString("TITLE"));
        pd.put("CREATECODE_ID", this.get32UUID());
        createcodeService.save(pd);
    }

    /**
     * 通过ID获取数据
     */
    @RequestMapping(value = "/findById")
    @ResponseBody
    public Object findById() throws Exception {
        if (!Jurisdiction.buttonJurisdiction(menuUrl, Constants.SRCH)) {
            return null;
        } //校验权限
        PageData pd = this.getPageData();
        Map<String, Object> map = Maps.newHashMap();
        try {
            pd = createcodeService.findById(pd);
        } catch (Exception e) {
            logger.error(e.toString(), e);
        } finally {
            logAfter(logger);
        }
        map.put("pd", pd);
        return AppUtil.returnObject(pd, map);
    }

    /**
     * 删除
     *
     * @param out out
     */
    @RequestMapping(value = "/delete")
    public void delete(PrintWriter out) throws Exception {
        logBefore(logger, Jurisdiction.getUsername() + "删除CreateCode");
        if (!Jurisdiction.buttonJurisdiction(menuUrl, Constants.DELE)) {
            return;
        } //校验权限
        PageData pd = this.getPageData();
        createcodeService.delete(pd);
        out.write("success");
        out.close();
    }

    /**
     * 批量删除
     */
    @RequestMapping(value = "/deleteAll")
    @ResponseBody
    public Object deleteAll() throws Exception {
        logBefore(logger, Jurisdiction.getUsername() + "批量删除CreateCode");
        if (!Jurisdiction.buttonJurisdiction(menuUrl, Constants.DELE)) {
            return null;
        }
        //校验权限
        PageData pd = new PageData();
        Map<String, Object> map = Maps.newHashMap();
        try {
            pd = this.getPageData();
            List<PageData> pdList = Lists.newArrayList();
            String dataIds = pd.getString("DATA_IDS");
            if (StringUtils.isNotEmpty(dataIds)) {
                String[] allDatas = dataIds.split(Constants.COMMA);
                createcodeService.deleteAll(allDatas);
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

}