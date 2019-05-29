package com.lph.service.system.fhbutton.impl;

import java.util.List;
import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import com.lph.dao.DaoSupport;
import com.lph.entity.Page;
import com.lph.util.PageData;
import com.lph.service.system.fhbutton.FhbuttonManager;

/**
 * 说明： 按钮管理
 *
 * @author lvpenghui
 * @since 2019-4-17 15:39:56
 */
@Service("fhbuttonService")
public class FhbuttonService implements FhbuttonManager {

    @Resource(name = "daoSupport")
    private DaoSupport dao;

    @Override
    public void save(PageData pd) throws Exception {
        dao.save("FhbuttonMapper.save", pd);
    }

    @Override
    public void delete(PageData pd) throws Exception {
        dao.delete("FhbuttonMapper.delete", pd);
    }

    @Override
    public void edit(PageData pd) throws Exception {
        dao.update("FhbuttonMapper.edit", pd);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<PageData> list(Page page) throws Exception {
        return (List<PageData>) dao.findForList("FhbuttonMapper.datalistPage", page);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<PageData> listAll(PageData pd) throws Exception {
        return (List<PageData>) dao.findForList("FhbuttonMapper.listAll", pd);
    }

    @Override
    public PageData findById(PageData pd) throws Exception {
        return (PageData) dao.findForObject("FhbuttonMapper.findById", pd);
    }

    @Override
    public void deleteAll(String[] arrayDataIds) throws Exception {
        dao.delete("FhbuttonMapper.deleteAll", arrayDataIds);
    }

}

