package com.lph.service.system.fhlog.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.lph.dao.DaoSupport;
import com.lph.entity.Page;
import com.lph.util.PageData;
import com.lph.util.Tools;
import com.lph.util.UuidUtil;
import com.lph.service.system.fhlog.FhLogManager;

/**
 * 说明： 操作日志记录
 *
 * @author lvpenghui
 * @since 2019-4-17 15:45:03
 */
@Service("fhlogService")
public class FhLogService implements FhLogManager {

    @Resource(name = "daoSupport")
    private DaoSupport dao;

    @Override
    public void save(String username, String content) throws Exception {
        PageData pd = new PageData();
        //用户名
        pd.put("USERNAME", username);
        //事件
        pd.put("CONTENT", content);
        //主键
        pd.put("FHLOG_ID", UuidUtil.get32UUID());
        //操作时间
        pd.put("CZTIME", Tools.date2Str(new Date()));
        dao.save("FHlogMapper.save", pd);
    }

    @Override
    public void delete(PageData pd) throws Exception {
        dao.delete("FHlogMapper.delete", pd);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<PageData> list(Page page) throws Exception {
        return (List<PageData>) dao.findForList("FHlogMapper.datalistPage", page);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<PageData> listAll(PageData pd) throws Exception {
        return (List<PageData>) dao.findForList("FHlogMapper.listAll", pd);
    }

    @Override
    public PageData findById(PageData pd) throws Exception {
        return (PageData) dao.findForObject("FHlogMapper.findById", pd);
    }

    @Override
    public void deleteAll(String[] arrayDataIds) throws Exception {
        dao.delete("FHlogMapper.deleteAll", arrayDataIds);
    }

}

