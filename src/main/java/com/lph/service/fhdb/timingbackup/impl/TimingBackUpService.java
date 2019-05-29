package com.lph.service.fhdb.timingbackup.impl;

import com.lph.dao.DaoSupport;
import com.lph.entity.Page;
import com.lph.service.fhdb.timingbackup.TimingBackUpManager;
import com.lph.util.PageData;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 说明： 定时备份
 *
 * @author lvpenghui
 */
@Service("timingbackupService")
public class TimingBackUpService implements TimingBackUpManager {

    @Resource(name = "daoSupport")
    private DaoSupport dao;

    @Override
    public void save(PageData pd) throws Exception {
        dao.save("TimingBackUpMapper.save", pd);
    }

    @Override
    public void delete(PageData pd) throws Exception {
        dao.delete("TimingBackUpMapper.delete", pd);
    }

    @Override
    public void edit(PageData pd) throws Exception {
        dao.update("TimingBackUpMapper.edit", pd);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<PageData> list(Page page) throws Exception {
        return (List<PageData>) dao.findForList("TimingBackUpMapper.datalistPage", page);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<PageData> listAll(PageData pd) throws Exception {
        return (List<PageData>) dao.findForList("TimingBackUpMapper.listAll", pd);
    }

    @Override
    public PageData findById(PageData pd) throws Exception {
        return (PageData) dao.findForObject("TimingBackUpMapper.findById", pd);
    }

    @Override
    public void deleteAll(String[] arrayDataIds) throws Exception {
        dao.delete("TimingBackUpMapper.deleteAll", arrayDataIds);
    }

    @Override
    public void changeStatus(PageData pd) throws Exception {
        dao.update("TimingBackUpMapper.changeStatus", pd);
    }

}

