package com.lph.controller.fhdb.sqledit;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.lph.controller.base.BaseController;
import com.lph.util.*;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * SQL编辑器
 *
 * @author lvpenghui
 * @since 2019-4-8 17:51:17
 */
@Controller
@RequestMapping(value = "/sqledit")
public class SqlEditController extends BaseController {

    /**
     * 菜单地址(权限用)
     */
    private String menuUrl = "sqledit/view.do";

    /**
     * 进入页面
     */
    @RequestMapping(value = "/view")
    public ModelAndView view() {
        if (!Jurisdiction.buttonJurisdiction(menuUrl, Constants.SRCH)) {
            return null;
        }
        //校验权限
        logBefore(logger, Jurisdiction.getUsername() + "进入SQL编辑页面");
        if (!Jurisdiction.buttonJurisdiction(menuUrl, Constants.SRCH)) {
            return null;
        }
        //校验权限(无权查看时页面会有提示,如果不注释掉这句代码就无法进入列表页面,所以根据情况是否加入本句代码)
        ModelAndView mv = this.getModelAndView();
        mv.setViewName("fhdb/sqledit/sql_edit");
        //按钮权限
        mv.addObject("QX", Jurisdiction.getHC());
        return mv;
    }

    /**
     * 执行查询语句
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/executeQuery")
    @ResponseBody
    public Object executeQuery() {
        logBefore(logger, Jurisdiction.getUsername() + "执行查询语句");
        if (!Jurisdiction.buttonJurisdiction(menuUrl, Constants.SRCH)) {
            return null;
        } //校验权限
        Map<String, Object> map = Maps.newHashMap();
        List<PageData> pdList = Lists.newArrayList();
        PageData pd = this.getPageData();
        //前台传过来的sql语句
        String sql = pd.getString("sql");
        //存放字段名
        List<String> columnList = Lists.newArrayList();
        //存放数据(从数据库读出来的一条条的数据)
        List<List<Object>> dataList = new ArrayList<List<Object>>();
        //请求起始时间_毫秒
        long startTime = System.currentTimeMillis();
        Object[] arrOb;
        try {
            arrOb = DbFH.executeQueryFH(sql);
            //请求结束时间_毫秒
            long endTime = System.currentTimeMillis();
            //存入数据库查询时间
            pd.put("rTime", String.valueOf((endTime - startTime) / 1000.000));
            if (null != arrOb) {
                columnList = (List<String>) arrOb[0];
                dataList = (List<List<Object>>) arrOb[1];
                pd.put("msg", "ok");
            } else {
                pd.put("msg", "no");
            }
        } catch (Exception e) {
            pd.put("msg", "no");
            logger.error("执行SQL报错", e);
        }
        pdList.add(pd);
        //存放字段名
        map.put("columnList", columnList);
        //存放数据(从数据库读出来的一条条的数据)
        map.put("dataList", dataList);
        //消息类型
        map.put("list", pdList);
        return AppUtil.returnObject(pd, map);
    }

    /**
     * 执行 INSERT、UPDATE 或 DELETE
     */
    @RequestMapping(value = "/executeUpdate")
    @ResponseBody
    public Object executeUpdate() {
        logBefore(logger, Jurisdiction.getUsername() + "执行更新语句");
        if (!Jurisdiction.buttonJurisdiction(menuUrl, Constants.EDIT)) {
            return null;
        }
        //校验权限
        Map<String, Object> map = Maps.newHashMap();
        List<PageData> pdList = Lists.newArrayList();
        PageData pd = this.getPageData();
        //前台传过来的sql语句
        String sql = pd.getString("sql");
        //请求起始时间_毫秒
        long startTime = System.currentTimeMillis();
        try {
            DbFH.executeUpdateFH(sql);
            pd.put("msg", "ok");
        } catch (ClassNotFoundException e) {
            pd.put("msg", "no");
            e.printStackTrace();
        } catch (SQLException e) {
            pd.put("msg", "no");
            e.printStackTrace();
        }
        //请求结束时间_毫秒
        long endTime = System.currentTimeMillis();
        //存入数据库查询时间
        pd.put("rTime", String.valueOf((endTime - startTime) / 1000.000));
        pdList.add(pd);
        //消息类型
        map.put("list", pdList);
        return AppUtil.returnObject(pd, map);
    }

    /**
     * 导出数据到EXCEL
     *
     * @return modelAndView对象
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/excel")
    public ModelAndView exportExcel() {
        ModelAndView mv = this.getModelAndView();
        PageData pd = this.getPageData();
        try {
            if (Jurisdiction.buttonJurisdiction(menuUrl, Constants.SRCH)) {
                //前台传过来的sql语句
                String sql = pd.getString("sql");
                //存放字段名
                List<String> columnList;
                //存放数据(从数据库读出来的一条条的数据)
                List<List<Object>> dataList;
                Object[] arrOb;
                try {
                    arrOb = DbFH.executeQueryFH(sql);
                    if (null != arrOb) {
                        columnList = (List<String>) arrOb[0];
                        dataList = (List<List<Object>>) arrOb[1];
                    } else {
                        return null;
                    }
                } catch (Exception e) {
                    logger.error("导出excelSQL报错", e);
                    return null;
                }
                Map<String, Object> dataMap = Maps.newHashMap();
                List<String> titles = Lists.newArrayList();
                titles.addAll(columnList);
                dataMap.put("titles", titles);
                List<PageData> varList = Lists.newArrayList();
                for (List<Object> aDataList : dataList) {
                    PageData vpd = new PageData();
                    for (int j = 0; j < aDataList.size(); j++) {
                        vpd.put("var" + (j + 1), aDataList.get(j).toString());
                    }
                    varList.add(vpd);
                }
                dataMap.put("varList", varList);
                //执行excel操作
                ObjectExcelView erv = new ObjectExcelView();
                mv = new ModelAndView(erv, dataMap);
            }
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
