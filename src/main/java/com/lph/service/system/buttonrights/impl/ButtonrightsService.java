package com.lph.service.system.buttonrights.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.lph.dao.DaoSupport;
import com.lph.util.PageData;
import com.lph.service.system.buttonrights.ButtonrightsManager;

/**
 * 说明： 按钮权限
 *
 * @author lvpenghui
 * @since 2019-4-17 14:32:13
 */
@Service("buttonrightsService")
public class ButtonrightsService implements ButtonrightsManager {

    @Resource(name = "daoSupport")
    private DaoSupport dao;

    @Override
    public void save(PageData pd) throws Exception {
        dao.save("ButtonrightsMapper.save", pd);
    }

    @Override
    public PageData findById(PageData pd) throws Exception {
        return (PageData) dao.findForObject("ButtonrightsMapper.findById", pd);
    }

    @Override
    public void delete(PageData pd) throws Exception {
        dao.delete("ButtonrightsMapper.delete", pd);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<PageData> listAll(PageData pd) throws Exception {
        return (List<PageData>) dao.findForList("ButtonrightsMapper.listAll", pd);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<PageData> listAllBrAndQxname(PageData pd) {
        return (List<PageData>) dao.findForList("ButtonrightsMapper.listAllBrAndQxname", pd);
    }

}

