package com.lph.service.system.fhsms.impl;

import java.util.List;
import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import com.lph.dao.DaoSupport;
import com.lph.entity.Page;
import com.lph.util.PageData;
import com.lph.service.system.fhsms.FhsmsManager;

/**
 * 说明： 站内信
 *
 * @author lvpenghui
 * @since 2019-4-17 15:53:37
 */
@Service("fhsmsService")
public class FhsmsService implements FhsmsManager {

    @Resource(name = "daoSupport")
    private DaoSupport dao;

    @Override
    public void save(PageData pd) throws Exception {
        dao.save("FhsmsMapper.save", pd);
    }

    @Override
    public void delete(PageData pd) throws Exception {
        dao.delete("FhsmsMapper.delete", pd);
    }

    @Override
    public void edit(PageData pd) throws Exception {
        dao.update("FhsmsMapper.edit", pd);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<PageData> list(Page page) throws Exception {
        return (List<PageData>) dao.findForList("FhsmsMapper.datalistPage", page);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<PageData> listAll(PageData pd) throws Exception {
        return (List<PageData>) dao.findForList("FhsmsMapper.listAll", pd);
    }

    @Override
    public PageData findById(PageData pd) throws Exception {
        return (PageData) dao.findForObject("FhsmsMapper.findById", pd);
    }

    @Override
    public PageData findFhsmsCount(String username) {
        return (PageData) dao.findForObject("FhsmsMapper.findFhsmsCount", username);
    }

    @Override
    public void deleteAll(String[] arrayDataIds) throws Exception {
        dao.delete("FhsmsMapper.deleteAll", arrayDataIds);
    }

}

