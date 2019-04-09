package com.lph.controller.fhdb.timingbackup;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.lph.controller.base.BaseController;
import com.lph.entity.Page;
import com.lph.service.fhdb.timingbackup.TimingBackUpManager;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 说明：定时备份
 *
 * @author lvpenghui
 * @since 2019-4-8 19:39:31
 */
@Controller
@RequestMapping(value = "/timingbackup")
public class TimingBackUpController extends BaseController {
    /**
     * 任务组
     */
    private static String JOB_GROUP_NAME = "DB_JOBGROUP_NAME";
    /**
     * 触发器组
     */
    private static String TRIGGER_GROUP_NAME = "DB_TRIGGERGROUP_NAME";
    /**
     * 菜单地址(权限用)
     */
    private String menuUrl = "timingbackup/list.do";

    @Resource(name = "timingbackupService")
    private TimingBackUpManager timingbackupService;

    /**
     * 保存
     *
     * @throws Exception 可能出现的异常
     */
    @RequestMapping(value = "/save")
    public ModelAndView save() throws Exception {
        logBefore(logger, Jurisdiction.getUsername() + "新增TimingBackUp");
        if (!Jurisdiction.buttonJurisdiction(menuUrl, Constants.ADDD)) {
            return null;
        }
        //校验权限
        ModelAndView mv = this.getModelAndView();
        PageData pd = this.getPageData();
        //任务名称
        String jobName = pd.getString("TABLENAME") + "_" + Tools.getRandomNum();
        //时间规则
        String fhTime = pd.getString("FHTIME");
        //表名or整库(all)
        String tableName = pd.getString("TABLENAME");
        //主键
        String backUpTimeId = this.get32UUID();
        pd.put("TIMINGBACKUP_ID", backUpTimeId);
        //任务名称
        pd.put("JOBNAME", jobName);
        //创建时间
        pd.put("CREATE_TIME", Tools.date2Str(new Date()));
        //状态
        pd.put("STATUS", "1");
        timingbackupService.save(pd);
        //添加任务
        this.addJob(jobName, fhTime, tableName, backUpTimeId);
        mv.addObject("msg", "success");
        mv.setViewName("save_result");
        return mv;
    }

    /**
     * 删除
     *
     * @param out 请求参数
     * @throws Exception 可能抛出的异常
     */
    @RequestMapping(value = "/delete")
    public void delete(PrintWriter out) throws Exception {
        logBefore(logger, Jurisdiction.getUsername() + "删除TimingBackUp");
        if (!Jurisdiction.buttonJurisdiction(menuUrl, Constants.DELE)) {
            return;
        }
        //校验权限
        PageData pd = this.getPageData();
        //删除任务
        this.removeJob(timingbackupService.findById(pd).getString("JOBNAME"));
        //删除数据库记录
        timingbackupService.delete(pd);
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
        logBefore(logger, Jurisdiction.getUsername() + "修改TimingBackUp");
        if (!Jurisdiction.buttonJurisdiction(menuUrl, Constants.EDIT)) {
            return null;
        }    //校验权限
        ModelAndView mv = this.getModelAndView();
        PageData pd = this.getPageData();
        //删除任务(修改时可能会修改要备份的表，所以任务名称会改变，所以执行删除任务再新增任务来完成修改任务的效果)
        this.removeJob(timingbackupService.findById(pd).getString("JOBNAME"));
        //任务名称
        String jobName = pd.getString("TABLENAME") + "_" + Tools.getRandomNum();
        //时间规则
        String fhTime = pd.getString("FHTIME");
        //表名or整库(all)
        String tableName = pd.getString("TABLENAME");
        //任务数据库记录的ID
        String backUpTimeId = pd.getString("TIMINGBACKUP_ID");
        //添加任务
        this.addJob(jobName, fhTime, tableName, backUpTimeId);
        //任务名称
        pd.put("JOBNAME", jobName);
        //创建时间
        pd.put("CREATE_TIME", Tools.date2Str(new Date()));
        //状态
        pd.put("STATUS", "1");
        timingbackupService.edit(pd);
        mv.addObject("msg", "success");
        mv.setViewName("save_result");
        return mv;
    }

    /**
     * 列表
     *
     * @param page page
     * @throws Exception 可能抛出的异常
     */
    @RequestMapping(value = "/list")
    public ModelAndView list(Page page) throws Exception {
        logBefore(logger, Jurisdiction.getUsername() + "列表TimingBackUp");
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
        //TimingBackUp列表
        List<PageData> varList = timingbackupService.list(page);
        mv.setViewName("fhdb/timingbackup/timingbackup_list");
        mv.addObject("varList", varList);
        mv.addObject("pd", pd);
        //按钮权限
        mv.addObject("QX", Jurisdiction.getHC());
        return mv;
    }

    /**
     * 去新增页面
     *
     * @throws Exception 可能抛出的异常
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/goAdd")
    public ModelAndView goAdd() throws Exception {
        ModelAndView mv = this.getModelAndView();
        PageData pd = this.getPageData();
        Object[] arrOb = DbFH.getTables();
        List<String> tblist = (List<String>) arrOb[1];
        //所有表
        mv.addObject("varList", tblist);
        //数据库类型
        mv.addObject("dbtype", arrOb[2]);
        mv.setViewName("fhdb/timingbackup/timingbackup_edit");
        mv.addObject("msg", "save");
        mv.addObject("pd", pd);
        return mv;
    }

    /**
     * 去修改页面
     *
     * @throws Exception 可能抛出的异常
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/goEdit")
    public ModelAndView goEdit() throws Exception {
        ModelAndView mv = this.getModelAndView();
        PageData pd = this.getPageData();
        Object[] arrOb = DbFH.getTables();
        List<String> tblist = (List<String>) arrOb[1];
        //所有表
        mv.addObject("varList", tblist);
        //数据库类型
        mv.addObject("dbtype", arrOb[2]);
        //根据ID读取
        pd = timingbackupService.findById(pd);
        mv.setViewName("fhdb/timingbackup/timingbackup_edit");
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
        logBefore(logger, Jurisdiction.getUsername() + "批量删除TimingBackUp");
        if (!Jurisdiction.buttonJurisdiction(menuUrl, Constants.DELE)) {
            return null;
        } //校验权限
        PageData pd = this.getPageData();
        Map<String, Object> map = Maps.newHashMap();
        List<PageData> pdList = Lists.newArrayList();
        String dataIds = pd.getString("DATA_IDS");
        if (Tools.notEmpty(dataIds)) {
            String[] allDatas = dataIds.split(",");
            for (String allData : allDatas) {
                pd.put("TIMINGBACKUP_ID", allData);
                //删除任务
                this.removeJob(timingbackupService.findById(pd).getString("JOBNAME"));
            }
            //删除数据库记录
            timingbackupService.deleteAll(allDatas);
            pd.put("msg", "ok");
        } else {
            pd.put("msg", "no");
        }
        pdList.add(pd);
        map.put("list", pdList);
        return AppUtil.returnObject(pd, map);
    }

    /**
     * 切换状态
     *
     * @throws Exception 可能抛出的异常
     */
    @RequestMapping(value = "/changeStatus")
    @ResponseBody
    public Object changeStatus() throws Exception {
        logBefore(logger, Jurisdiction.getUsername() + "切换状态");
        if (!Jurisdiction.buttonJurisdiction(menuUrl, Constants.EDIT)) {
            return null;
        } //校验权限
        PageData pd = this.getPageData();
        Map<String, Object> map = Maps.newHashMap();
        List<PageData> pdList = Lists.newArrayList();
        int status = Integer.parseInt(pd.get("STATUS").toString());
        //根据ID读取
        pd = timingbackupService.findById(pd);
        if (Constants.TWO == status) {
            pd.put("STATUS", 2);
            //删除任务
            this.removeJob(pd.getString("JOBNAME"));
        } else {
            pd.put("STATUS", 1);
            //任务名称
            String jobName = pd.getString("JOBNAME");
            //时间规则
            String fhTime = pd.getString("FHTIME");
            //表名or整库(all)
            String tableName = pd.getString("TABLENAME");
            //任务数据库记录的ID
            String backUpTimeId = pd.getString("TIMINGBACKUP_ID");
            //添加任务
            this.addJob(jobName, fhTime, tableName, backUpTimeId);
        }
        timingbackupService.changeStatus(pd);
        pd.put("msg", "ok");
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
        logBefore(logger, Jurisdiction.getUsername() + "导出TimingBackUp到excel");
        if (!Jurisdiction.buttonJurisdiction(menuUrl, Constants.SRCH)) {
            return null;
        }
        ModelAndView mv;
        PageData pd = this.getPageData();
        Map<String, Object> dataMap = Maps.newHashMap();
        List<String> titles = new ArrayList<String>();
        titles.add("任务名称");
        titles.add("创建时间");
        titles.add("表名");
        titles.add("状态");
        titles.add("时间规则");
        titles.add("规则说明");

        titles.add("备注");
        dataMap.put("titles", titles);
        List<PageData> varOList = timingbackupService.listAll(pd);
        List<PageData> varList = Lists.newArrayList();
        for (PageData aVarOList : varOList) {
            PageData vpd = new PageData();
            vpd.put("var1", aVarOList.getString("JOBNAME"));
            vpd.put("var2", aVarOList.getString("CREATE_TIME"));
            vpd.put("var3", aVarOList.getString("TABLENAME"));
            vpd.put("var4", aVarOList.get("STATUS").toString());
            vpd.put("var5", aVarOList.getString("FHTIME"));
            vpd.put("var6", aVarOList.getString("TIMEEXPLAIN"));
            vpd.put("var7", aVarOList.getString("BZ"));
            varList.add(vpd);
        }
        dataMap.put("varList", varList);
        ObjectExcelView erv = new ObjectExcelView();
        mv = new ModelAndView(erv, dataMap);
        return mv;
    }

    /**
     * 新增任务
     *
     * @param jobName      任务名称
     * @param fhTime       时间规则
     * @param tableName    传的参数
     * @param backUpTimeId 定时备份任务的ID
     */
    private void addJob(String jobName, String fhTime, String tableName, String backUpTimeId) {
        Map<String, Object> parameter = Maps.newHashMap();
        parameter.put("TABLENAME", tableName);
        parameter.put("TIMINGBACKUP_ID", backUpTimeId);
        QuartzManager.addJob(jobName, JOB_GROUP_NAME, jobName, TRIGGER_GROUP_NAME, DbBackupQuartzJob.class, fhTime, parameter);
    }

    /**
     * 删除任务
     *
     * @param jobName 任务名称
     */
    private void removeJob(String jobName) {
        QuartzManager.removeJob(jobName, JOB_GROUP_NAME, jobName, TRIGGER_GROUP_NAME);
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        binder.registerCustomEditor(Date.class, new CustomDateEditor(format, true));
    }
}
