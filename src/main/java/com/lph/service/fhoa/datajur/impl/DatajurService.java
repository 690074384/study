package com.lph.service.fhoa.datajur.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.lph.dao.DaoSupport;
import com.lph.util.PageData;
import com.lph.service.fhoa.datajur.DatajurManager;

/**
 * 说明： 组织数据权限表
 *
 * @author lvpenghui
 * @since 2019-4-17 10:57:19
 */
@Service("datajurService")
public class DatajurService implements DatajurManager {

    @Resource(name = "daoSupport")
    private DaoSupport dao;

    @Override
    public void save(PageData pd) throws Exception {
        dao.save("DatajurMapper.save", pd);
    }

    @Override
    public void edit(PageData pd) throws Exception {
        dao.update("DatajurMapper.edit", pd);
    }

    @Override
    public PageData findById(PageData pd) throws Exception {
        return (PageData) dao.findForObject("DatajurMapper.findById", pd);
    }

    @Override
    public PageData getDepartmentIds(String username) {
        return (PageData) dao.findForObject("DatajurMapper.getDepartmentIds", username);
    }

}

