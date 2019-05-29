package com.lph.service.system.createcode.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.lph.dao.DaoSupport;
import com.lph.entity.Page;
import com.lph.service.system.createcode.CreateCodeManager;
import com.lph.util.PageData;


/**
 * 类名称：CreateCodeService 代码生成器
 *
 * @author lvpenghui
 * @since 2019-4-17 14:36:04
 */
@Service("createcodeService")
public class CreateCodeService implements CreateCodeManager {

    @Resource(name = "daoSupport")
    private DaoSupport dao;

    @Override
    public void save(PageData pd) throws Exception {
        dao.save("CreateCodeMapper.save", pd);
    }

    @Override
    public void delete(PageData pd) throws Exception {
        dao.delete("CreateCodeMapper.delete", pd);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<PageData> list(Page page) throws Exception {
        return (List<PageData>) dao.findForList("CreateCodeMapper.datalistPage", page);
    }

    @Override
    public PageData findById(PageData pd) throws Exception {
        return (PageData) dao.findForObject("CreateCodeMapper.findById", pd);
    }

    @Override
    public void deleteAll(String[] arrayDataIds) throws Exception {
        dao.delete("CreateCodeMapper.deleteAll", arrayDataIds);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<PageData> listFa() {
        return (List<PageData>) dao.findForList("CreateCodeMapper.listFa", "");
    }

}

