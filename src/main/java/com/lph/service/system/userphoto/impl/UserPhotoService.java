package com.lph.service.system.userphoto.impl;

import java.util.List;
import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import com.lph.dao.DaoSupport;
import com.lph.entity.Page;
import com.lph.util.PageData;
import com.lph.service.system.userphoto.UserPhotoManager;

/**
 * 说明： 用户头像
 *
 * @author lvpenghui
 * @since 2019-4-17 17:06:35
 */
@Service("userphotoService")
public class UserPhotoService implements UserPhotoManager {

    @Resource(name = "daoSupport")
    private DaoSupport dao;

    @Override
    public void save(PageData pd) throws Exception {
        dao.save("UserPhotoMapper.save", pd);
    }

    @Override
    public void delete(PageData pd) throws Exception {
        dao.delete("UserPhotoMapper.delete", pd);
    }

    @Override
    public void edit(PageData pd) throws Exception {
        dao.update("UserPhotoMapper.edit", pd);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<PageData> list(Page page) throws Exception {
        return (List<PageData>) dao.findForList("UserPhotoMapper.datalistPage", page);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<PageData> listAll(PageData pd) throws Exception {
        return (List<PageData>) dao.findForList("UserPhotoMapper.listAll", pd);
    }

    @Override
    public PageData findById(PageData pd) throws Exception {
        return (PageData) dao.findForObject("UserPhotoMapper.findById", pd);
    }

    @Override
    public void deleteAll(String[] arrayDataIds) throws Exception {
        dao.delete("UserPhotoMapper.deleteAll", arrayDataIds);
    }

}

