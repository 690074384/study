package com.lph.controller.fhdb.brdb;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.lph.controller.base.BaseController;
import com.lph.entity.Page;
import com.lph.service.fhdb.brdb.BRdbManager;
import com.lph.util.*;
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
import java.util.concurrent.ExecutionException;

/**
 * 数据库管理(备份和还原)
 *
 * @author lvpenghui
 * @since 2019-4-8 17:37:24
 */
@Controller
@RequestMapping(value = "/brdb")
public class DatabaseController extends BaseController {

    /**
     * 菜单地址(权限用)数据还原菜单
     */
    private String menuUrl = "brdb/list.do";
    /**
     * 菜单地址(权限用)数据备份菜单
     */
    private String menuUrlb = "brdb/listAllTable.do";
    @Resource(name = "brdbService")
    private BRdbManager brdbService;

    /**
     * 列出所有表
     *
     * @throws Exception 可能出现的异常
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/listAllTable")
    public ModelAndView listAllTable() throws Exception {
        logBefore(logger, Jurisdiction.getUsername() + "列出所有表");
        if (!Jurisdiction.buttonJurisdiction(menuUrlb, Constants.SRCH)) {
            return null;
        }
        //校验权限(无权查看时页面会有提示,如果不注释掉这句代码就无法进入列表页面,所以根据情况是否加入本句代码)
        ModelAndView mv = this.getModelAndView();
        Object[] arrOb = DbFH.getTables();
        List<String> tblist = (List<String>) arrOb[1];
        mv.setViewName("fhdb/brdb/table_list");
        //所有表
        mv.addObject("varList", tblist);
        //数据库类型
        mv.addObject("dbtype", arrOb[2]);
        //数据库名
        mv.addObject("databaseName", arrOb[0]);
        //按钮权限
        mv.addObject("QX", Jurisdiction.getHC());
        return mv;
    }

    /**
     * 备份全库
     */
    @RequestMapping(value = "/backupAll")
    @ResponseBody
    public Object backupAll() {
        String username = Jurisdiction.getUsername();
        logBefore(logger, username + "备份全库");
        if (!Jurisdiction.buttonJurisdiction(menuUrlb, Constants.ADDD)) {
            return null;
        } //校验权限
        PageData pd = this.getPageData();
        Map<String, Object> map = Maps.newHashMap();
        List<PageData> pdList = Lists.newArrayList();
        String kackupPath;
        try {
            //调用数据库备份
            kackupPath = DbFH.getDbFH().backup("").toString();
            if (Tools.notEmpty(kackupPath) && !Constants.ERRER.equals(kackupPath)) {
                //主键
                pd.put("FHDB_ID", this.get32UUID());
                //操作用户
                pd.put("USERNAME", username);
                //备份时间
                pd.put("BACKUP_TIME", Tools.date2Str(new Date()));
                //表名
                pd.put("TABLENAME", "整库");
                //存储位置
                pd.put("SQLPATH", kackupPath);
                //文件大小
                pd.put("DBSIZE", FileUtil.getFilesize(kackupPath));
                //1: 备份整库，2：备份某表
                pd.put("TYPE", 1);
                //备注
                pd.put("BZ", username + "备份全库操作");
                pd.put("msg", "ok");
                try {
                    brdbService.save(pd);
                } catch (Exception e) {
                    pd.put("msg", "no");
                }
            } else {
                pd.put("msg", "no");
            }
        } catch (InterruptedException e) {
            pd.put("msg", "no");
            e.printStackTrace();
        } catch (ExecutionException e) {
            pd.put("msg", "no");
            e.printStackTrace();
        }
        pdList.add(pd);
        map.put("list", pdList);
        return AppUtil.returnObject(pd, map);
    }

    /**
     * 备份单表
     */
    @RequestMapping(value = "/backupTable")
    @ResponseBody
    public Object backupTable() {
        String username = Jurisdiction.getUsername();
        logBefore(logger, username + "备份单表");
        if (!Jurisdiction.buttonJurisdiction(menuUrlb, Constants.ADDD)) {
            return null;
        } //校验权限
        PageData pd = this.getPageData();
        Map<String, Object> map = Maps.newHashMap();
        //页面ajax传过来的表名
        String tableName = pd.getString("fhtable");
        List<PageData> pdList = Lists.newArrayList();
        String kackupPath;
        try {
            //调用数据库备份
            kackupPath = DbFH.getDbFH().backup(tableName).toString();
            if (Tools.notEmpty(kackupPath) && !Constants.ERRER.equals(kackupPath)) {
                //主键
                pd.put("FHDB_ID", this.get32UUID());
                //操作用户
                pd.put("USERNAME", username);
                //备份时间
                pd.put("BACKUP_TIME", Tools.date2Str(new Date()));
                //表名
                pd.put("TABLENAME", tableName);
                //存储位置
                pd.put("SQLPATH", kackupPath);
                //文件大小
                pd.put("DBSIZE", FileUtil.getFilesize(kackupPath));
                //1: 备份整库，2：备份某表
                pd.put("TYPE", 2);
                //备注
                pd.put("BZ", username + "备份单表");
                pd.put("msg", "ok");
                try {
                    brdbService.save(pd);
                } catch (Exception e) {
                    pd.put("msg", "no");
                }
            } else {
                pd.put("msg", "no");
            }
        } catch (InterruptedException e) {
            pd.put("msg", "no");
            e.printStackTrace();
        } catch (ExecutionException e) {
            pd.put("msg", "no");
            e.printStackTrace();
        }
        pdList.add(pd);
        map.put("list", pdList);
        return AppUtil.returnObject(pd, map);
    }

    /**
     * 数据还原操作
     */
    @RequestMapping(value = "/dbRecover")
    @ResponseBody
    public Object dbRecover() {
        String username = Jurisdiction.getUsername();
        logBefore(logger, username + "数据还原操作");
        if (!Jurisdiction.buttonJurisdiction(menuUrl, Constants.EDIT)) {
            return null;
        } //校验权限
        PageData pd = this.getPageData();
        Map<String, Object> map = Maps.newHashMap();
        List<PageData> pdList = Lists.newArrayList();
        //页面ajax传过来的表名或数据库名
        String tableName = pd.getString("TABLENAME");
        //页面ajax传过来的备份文件完整路径
        String sqlPath = pd.getString("SQLPATH");
        try {
            String returnStr = DbFH.getDbFH().recover(tableName, sqlPath).toString();
            if (Constants.OK.equals(returnStr)) {
                pd.put("msg", "ok");
            } else {
                pd.put("msg", "no");
            }
        } catch (InterruptedException e) {
            pd.put("msg", "no");
            e.printStackTrace();
        } catch (ExecutionException e) {
            pd.put("msg", "no");
            e.printStackTrace();
        }
        pdList.add(pd);
        map.put("list", pdList);
        return AppUtil.returnObject(pd, map);
    }

    /**
     * 删除
     *
     * @param out out
     * @throws Exception 可能出现的异常
     */
    @RequestMapping(value = "/delete")
    public void delete(PrintWriter out) throws Exception {
        logBefore(logger, Jurisdiction.getUsername() + "删除Fhdb");
        if (!Jurisdiction.buttonJurisdiction(menuUrl, Constants.DELE)) {
            return;
        }
        //校验权限
        PageData pd = this.getPageData();
        brdbService.delete(pd);
        out.write("success");
        out.close();
    }

    /**
     * 修改
     *
     * @throws Exception 可能出现的异常
     */
    @RequestMapping(value = "/edit")
    public ModelAndView edit() throws Exception {
        logBefore(logger, Jurisdiction.getUsername() + "修改Fhdb");
        if (!Jurisdiction.buttonJurisdiction(menuUrl, Constants.EDIT)) {
            return null;
        } //校验权限
        ModelAndView mv = this.getModelAndView();
        PageData pd = this.getPageData();
        brdbService.edit(pd);
        mv.addObject("msg", "success");
        mv.setViewName("save_result");
        return mv;
    }

    /**
     * 列表
     *
     * @param page 分页
     * @throws Exception 可能出现的异常
     */
    @RequestMapping(value = "/list")
    public ModelAndView list(Page page) throws Exception {
        logBefore(logger, Jurisdiction.getUsername() + "列表Fhdb");
        //校验权限(无权查看时页面会有提示,如果不注释掉这句代码就无法进入列表页面,所以根据情况是否加入本句代码)
        //if(!Jurisdiction.buttonJurisdiction(menuUrl, "cha")){return null;}
        ModelAndView mv = this.getModelAndView();
        PageData pd = this.getPageData();
        //关键词检索条件
        String keywords = pd.getString("keywords");
        if (null != keywords && !"".equals(keywords)) {
            pd.put("keywords", keywords.trim());
        }
        //开始时间
        String lastStart = pd.getString("lastStart");
        //结束时间
        String lastEnd = pd.getString("lastEnd");
        if (Tools.notEmpty(lastStart)) {
            pd.put("lastLoginStart", lastStart + " 00:00:00");
        }
        if (Tools.notEmpty(lastEnd)) {
            pd.put("lastLoginEnd", lastEnd + " 00:00:00");
        }
        page.setPd(pd);
        //列出Fhdb列表
        List<PageData> varList = brdbService.list(page);
        Map<String, String> databaseMap = DbFH.getDBParameter();
        mv.setViewName("fhdb/brdb/brdb_list");
        mv.addObject("varList", varList);
        mv.addObject("pd", pd);
        //数据库类型
        mv.addObject("dbtype", databaseMap.get("dbtype"));
        //是否远程备份数据库 yes or no
        mv.addObject("remoteDB", databaseMap.get("remoteDB"));
        //按钮权限
        mv.addObject("QX", Jurisdiction.getHC());
        return mv;
    }

    /**
     * 去修改页面
     *
     * @throws Exception 可能出现的异常
     */
    @RequestMapping(value = "/goEdit")
    public ModelAndView goEdit() throws Exception {
        ModelAndView mv = this.getModelAndView();
        PageData pd = this.getPageData();
        //根据ID读取
        pd = brdbService.findById(pd);
        mv.setViewName("fhdb/brdb/brdb_edit");
        mv.addObject("msg", "edit");
        mv.addObject("pd", pd);
        return mv;
    }

    /**
     * 批量删除
     *
     * @throws Exception 可能出现的异常
     */
    @RequestMapping(value = "/deleteAll")
    @ResponseBody
    public Object deleteAll() throws Exception {
        logBefore(logger, Jurisdiction.getUsername() + "批量删除备份记录");
        if (!Jurisdiction.buttonJurisdiction(menuUrl, Constants.DELE)) {
            return null;
        }
        //校验权限
        PageData pd = this.getPageData();
        Map<String, Object> map = Maps.newHashMap();
        List<PageData> pdList = Lists.newArrayList();
        String dataIds = pd.getString("DATA_IDS");
        if (null != dataIds && !"".equals(dataIds)) {
            String[] allDatas = dataIds.split(",");
            brdbService.deleteAll(allDatas);
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
