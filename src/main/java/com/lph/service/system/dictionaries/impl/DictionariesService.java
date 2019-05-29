package com.lph.service.system.dictionaries.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.lph.dao.DaoSupport;
import com.lph.entity.Page;
import com.lph.entity.system.Dictionaries;
import com.lph.util.PageData;
import com.lph.service.system.dictionaries.DictionariesManager;

/**
 * 说明： 数据字典
 *
 * @author lvpenghui
 * @since 2019-4-17 15:35:05
 */
@Service("dictionariesService")
public class DictionariesService implements DictionariesManager {

    @Resource(name = "daoSupport")
    private DaoSupport dao;

    @Override
    public void save(PageData pd) throws Exception {
        dao.save("DictionariesMapper.save", pd);
    }

    @Override
    public void delete(PageData pd) throws Exception {
        dao.delete("DictionariesMapper.delete", pd);
    }

    @Override
    public void edit(PageData pd) throws Exception {
        dao.update("DictionariesMapper.edit", pd);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<PageData> list(Page page) throws Exception {
        return (List<PageData>) dao.findForList("DictionariesMapper.datalistPage", page);
    }

    @Override
    public PageData findById(PageData pd) throws Exception {
        return (PageData) dao.findForObject("DictionariesMapper.findById", pd);
    }

    @Override
    public PageData findByBianma(PageData pd) {
        return (PageData) dao.findForObject("DictionariesMapper.findByBianma", pd);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Dictionaries> listSubDictByParentId(String parentId) {
        return (List<Dictionaries>) dao.findForList("DictionariesMapper.listSubDictByParentId", parentId);
    }

    @Override
    public List<Dictionaries> listAllDict(String parentId) {
        List<Dictionaries> dictList = this.listSubDictByParentId(parentId);
        for (Dictionaries dict : dictList) {
            dict.setTreeurl("dictionaries/list.do?DICTIONARIES_ID=" + dict.getDICTIONARIES_ID());
            dict.setSubDict(this.listAllDict(dict.getDICTIONARIES_ID()));
            dict.setTarget("treeFrame");
        }
        return dictList;
    }

    @Override
    public PageData findFromTbs(PageData pd) {
        return (PageData) dao.findForObject("DictionariesMapper.findFromTbs", pd);
    }

}

