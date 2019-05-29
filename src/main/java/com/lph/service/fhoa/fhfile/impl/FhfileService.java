package com.lph.service.fhoa.fhfile.impl;

import java.util.List;
import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import com.lph.dao.DaoSupport;
import com.lph.entity.Page;
import com.lph.util.PageData;
import com.lph.service.fhoa.fhfile.FhfileManager;

/**
 * 说明： 文件管理
 *
 * @author lvpenghui
 * @since 2019-4-17 11:36:50
 */
@Service("fhfileService")
public class FhfileService implements FhfileManager {

    @Resource(name = "daoSupport")
    private DaoSupport dao;

    @Override
    public void save(PageData pd) throws Exception {
        dao.save("FhfileMapper.save", pd);
    }

    @Override
    public void delete(PageData pd) throws Exception {
        dao.delete("FhfileMapper.delete", pd);
    }

    @Override
    public void edit(PageData pd) throws Exception {
        dao.update("FhfileMapper.edit", pd);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<PageData> list(Page page) throws Exception {
        return (List<PageData>) dao.findForList("FhfileMapper.datalistPage", page);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<PageData> listAll(PageData pd) throws Exception {
        return (List<PageData>) dao.findForList("FhfileMapper.listAll", pd);
    }

    @Override
    public PageData findById(PageData pd) throws Exception {
        return (PageData) dao.findForObject("FhfileMapper.findById", pd);
    }

    @Override
    public void deleteAll(String[] arrayDataIds) throws Exception {
        dao.delete("FhfileMapper.deleteAll", arrayDataIds);
    }

}

