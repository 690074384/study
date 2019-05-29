package com.lph.service.fhdb.brdb.impl;

import com.lph.dao.DaoSupport;
import com.lph.entity.Page;
import com.lph.service.fhdb.brdb.BrDbManager;
import com.lph.util.PageData;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 说明： 数据库管理
 *
 * @author lvpenghui
 * @since 2019-4-17 10:42:49
 */
@Service("brdbService")
public class BrDbService implements BrDbManager {

    @Resource(name = "daoSupport")
    private DaoSupport dao;

    @Override
    public void save(PageData pd) throws Exception {
        dao.save("FhdbMapper.save", pd);
    }

    @Override
    public void delete(PageData pd) throws Exception {
        dao.delete("FhdbMapper.delete", pd);
    }

    @Override
    public void edit(PageData pd) throws Exception {
        dao.update("FhdbMapper.edit", pd);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<PageData> list(Page page) throws Exception {
        return (List<PageData>) dao.findForList("FhdbMapper.datalistPage", page);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<PageData> listAll(PageData pd) throws Exception {
        return (List<PageData>) dao.findForList("FhdbMapper.listAll", pd);
    }

    @Override
    public PageData findById(PageData pd) throws Exception {
        return (PageData) dao.findForObject("FhdbMapper.findById", pd);
    }

    @Override
    public void deleteAll(String[] arrayDataIds) throws Exception {
        dao.delete("FhdbMapper.deleteAll", arrayDataIds);
    }

}

