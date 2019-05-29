package com.lph.service.fhoa.staff.impl;

import java.util.List;
import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import com.lph.dao.DaoSupport;
import com.lph.entity.Page;
import com.lph.util.PageData;
import com.lph.service.fhoa.staff.StaffManager;

/**
 * 说明： 员工管理
 *
 * @author lvpenghui
 * @since 2019-4-17 11:41:19
 */
@Service("staffService")
public class StaffService implements StaffManager {

    @Resource(name = "daoSupport")
    private DaoSupport dao;

    @Override
    public void save(PageData pd) throws Exception {
        dao.save("StaffMapper.save", pd);
    }

    @Override
    public void delete(PageData pd) throws Exception {
        dao.delete("StaffMapper.delete", pd);
    }

    @Override
    public void edit(PageData pd) throws Exception {
        dao.update("StaffMapper.edit", pd);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<PageData> list(Page page) throws Exception {
        return (List<PageData>) dao.findForList("StaffMapper.datalistPage", page);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<PageData> listAll(PageData pd) throws Exception {
        return (List<PageData>) dao.findForList("StaffMapper.listAll", pd);
    }

    @Override
    public PageData findById(PageData pd) throws Exception {
        return (PageData) dao.findForObject("StaffMapper.findById", pd);
    }

    @Override
    public void deleteAll(String[] arrayDataIds) throws Exception {
        dao.delete("StaffMapper.deleteAll", arrayDataIds);
    }

    @Override
    public void userBinding(PageData pd) throws Exception {
        dao.update("StaffMapper.userBinding", pd);
    }

}

