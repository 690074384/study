package com.lph.service.information.attached.impl;

import com.lph.dao.DaoSupport;
import com.lph.entity.Page;
import com.lph.service.information.attached.AttachedManager;
import com.lph.util.PageData;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 说明： 主附结构
 *
 * @author lvpenghui
 * @since 2019-4-17 11:45:30
 */
@Service("attachedService")
public class AttachedService implements AttachedManager {

    @Resource(name = "daoSupport")
    private DaoSupport dao;

    @Override
    public void save(PageData pd) throws Exception {
        dao.save("AttachedMapper.save", pd);
    }

    @Override
    public void delete(PageData pd) throws Exception {
        dao.delete("AttachedMapper.delete", pd);
    }

    @Override
    public void edit(PageData pd) throws Exception {
        dao.update("AttachedMapper.edit", pd);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<PageData> list(Page page) throws Exception {
        return (List<PageData>) dao.findForList("AttachedMapper.datalistPage", page);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<PageData> listAll(PageData pd) throws Exception {
        return (List<PageData>) dao.findForList("AttachedMapper.listAll", pd);
    }

    @Override
    public PageData findById(PageData pd) throws Exception {
        return (PageData) dao.findForObject("AttachedMapper.findById", pd);
    }

    @Override
    public void deleteAll(String[] arrayDataIds) throws Exception {
        dao.delete("AttachedMapper.deleteAll", arrayDataIds);
    }

}

