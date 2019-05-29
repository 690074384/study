package com.lph.service.information.pictures.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.lph.dao.DaoSupport;
import com.lph.entity.Page;
import com.lph.service.information.pictures.PicturesManager;
import com.lph.util.PageData;


/**
 * 图片管理
 *
 * @author lvpenghui
 * @since 2019-4-17 14:19:20
 */
@Service("picturesService")
public class PicturesService implements PicturesManager {

    @Resource(name = "daoSupport")
    private DaoSupport dao;

    @Override
    @SuppressWarnings("unchecked")
    public List<PageData> list(Page page) throws Exception {
        return (List<PageData>) dao.findForList("PicturesMapper.datalistPage", page);
    }

    @Override
    public void save(PageData pd) throws Exception {
        dao.save("PicturesMapper.save", pd);
    }

    @Override
    public void delete(PageData pd) throws Exception {
        dao.delete("PicturesMapper.delete", pd);
    }

    @Override
    public void edit(PageData pd) throws Exception {
        dao.update("PicturesMapper.edit", pd);
    }

    @Override
    public PageData findById(PageData pd) throws Exception {
        return (PageData) dao.findForObject("PicturesMapper.findById", pd);
    }

    @Override
    public void deleteAll(String[] arrayDataIds) throws Exception {
        dao.delete("PicturesMapper.deleteAll", arrayDataIds);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<PageData> getAllById(String[] arrayDataIds) {
        return (List<PageData>) dao.findForList("PicturesMapper.getAllById", arrayDataIds);
    }

    @Override
    public void delTp(PageData pd) throws Exception {
        dao.update("PicturesMapper.delTp", pd);
    }

}

