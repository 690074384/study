package com.lph.service.information.attachedmx.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.lph.dao.DaoSupport;
import com.lph.entity.Page;
import com.lph.util.PageData;
import com.lph.service.information.attachedmx.AttachedMxManager;

/**
 * 说明： 明细表
 *
 * @author lvpenghui
 * @since 2019-4-17 14:15:26
 */
@Service("attachedmxService")
public class AttachedMxService implements AttachedMxManager {

    @Resource(name = "daoSupport")
    private DaoSupport dao;

    @Override
    public void save(PageData pd) throws Exception {
        dao.save("AttachedMxMapper.save", pd);
    }

    @Override
    public void delete(PageData pd) throws Exception {
        dao.delete("AttachedMxMapper.delete", pd);
    }

    @Override
    public void edit(PageData pd) throws Exception {
        dao.update("AttachedMxMapper.edit", pd);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<PageData> list(Page page) throws Exception {
        return (List<PageData>) dao.findForList("AttachedMxMapper.datalistPage", page);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<PageData> listAll(PageData pd) throws Exception {
        return (List<PageData>) dao.findForList("AttachedMxMapper.listAll", pd);
    }

    @Override
    public PageData findById(PageData pd) throws Exception {
        return (PageData) dao.findForObject("AttachedMxMapper.findById", pd);
    }

    @Override
    public void deleteAll(String[] arrayDataIds) throws Exception {
        dao.delete("AttachedMxMapper.deleteAll", arrayDataIds);
    }

    @Override
    public PageData findCount(PageData pd) {
        return (PageData) dao.findForObject("AttachedMxMapper.findCount", pd);
    }

}

