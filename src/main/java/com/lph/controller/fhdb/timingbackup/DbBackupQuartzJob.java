package com.lph.controller.fhdb.timingbackup;

import com.lph.controller.base.BaseController;
import com.lph.service.fhdb.brdb.impl.BRdbService;
import com.lph.service.fhdb.timingbackup.impl.TimingBackUpService;
import com.lph.util.*;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

import java.util.Date;
import java.util.Map;

/**
 * quartz 定时任务调度 数据库自动备份工作域
 *
 * @author lvpenghui
 * @since 2019-4-8 19:34:07
 */
public class DbBackupQuartzJob extends BaseController implements Job {

    @Override
    @SuppressWarnings("unchecked")
    public void execute(JobExecutionContext context) {
        // TODO Auto-generated method stub
        JobDataMap dataMap = context.getJobDetail().getJobDataMap();
        Map<String, Object> parameter = (Map<String, Object>) dataMap.get("parameterList");
        String tableName = parameter.get("TABLENAME").toString();
        tableName = "all".equals(tableName) ? "" : tableName;

        //普通类从spring容器中拿出service
        WebApplicationContext webctx = ContextLoader.getCurrentWebApplicationContext();
        BRdbService brdbService = (BRdbService) webctx.getBean("brdbService");
        PageData pd = new PageData();
        try {
            //调用数据库备份
            String kackupPath = DbFH.getDbFH().backup(tableName).toString();
            if (Tools.notEmpty(kackupPath) && !Constants.ERRER.equals(kackupPath)) {
                pd.put("FHDB_ID", this.get32UUID());
                pd.put("USERNAME", "系统");
                //备份时间
                pd.put("BACKUP_TIME", Tools.date2Str(new Date()));
                //表名or整库
                pd.put("TABLENAME", "".equals(tableName) ? "整库" : tableName);
                //存储位置
                pd.put("SQLPATH", kackupPath);
                //文件大小
                pd.put("DBSIZE", FileUtil.getFilesize(kackupPath));
                //1: 备份整库，2：备份某表
                pd.put("TYPE", "".equals(tableName) ? 1 : 2);
                //备注
                pd.put("BZ", "定时备份操作");
                //存入备份记录
                brdbService.save(pd);
            } else {
                shutdownJob(context, pd, parameter, webctx);
            }
        } catch (Exception e) {
            logger.error(e.toString(), e);
            try {
                shutdownJob(context, pd, parameter, webctx);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }

    /**
     * 把定时备份任务状态改为关闭
     *
     * @param context   job上下文
     * @param pd        PageData对象
     * @param parameter map对象
     * @param webctx    WebApplicationContext对象
     */
    private void shutdownJob(JobExecutionContext context, PageData pd, Map<String, Object> parameter, WebApplicationContext webctx) {
        try {
            //备份异常时关闭任务
            context.getScheduler().shutdown();
            TimingBackUpService timingbackupService = (TimingBackUpService) webctx.getBean("timingbackupService");
            //改变定时运行状态为2，关闭
            pd.put("STATUS", 2);
            //定时备份ID
            pd.put("TIMINGBACKUP_ID", parameter.get("TIMINGBACKUP_ID").toString());
            timingbackupService.changeStatus(pd);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
