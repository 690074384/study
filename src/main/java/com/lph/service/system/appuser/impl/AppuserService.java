package com.lph.service.system.appuser.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.lph.dao.DaoSupport;
import com.lph.entity.Page;
import com.lph.service.system.appuser.AppuserManager;
import com.lph.util.PageData;


/**
 * 类名称：AppuserService
 *
 * @author lvpenghui
 * @since 2019-4-17 14:25:28
 */
@Service("appuserService")
public class AppuserService implements AppuserManager {

    @Resource(name = "daoSupport")
    private DaoSupport dao;

    @Override
    @SuppressWarnings("unchecked")
    public List<PageData> listAllAppuserByRorlid(PageData pd) {
        return (List<PageData>) dao.findForList("AppuserMapper.listAllAppuserByRorlid", pd);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<PageData> listPdPageUser(Page page) {
        return (List<PageData>) dao.findForList("AppuserMapper.userlistPage", page);
    }

    @Override
    public PageData findByUsername(PageData pd) {
        return (PageData) dao.findForObject("AppuserMapper.findByUsername", pd);
    }

    @Override
    public PageData findByEmail(PageData pd) {
        return (PageData) dao.findForObject("AppuserMapper.findByEmail", pd);
    }

    @Override
    public PageData findByNumber(PageData pd) {
        return (PageData) dao.findForObject("AppuserMapper.findByNumber", pd);
    }

    @Override
    public void saveU(PageData pd) throws Exception {
        dao.save("AppuserMapper.saveU", pd);
    }

    @Override
    public void deleteU(PageData pd) throws Exception {
        dao.delete("AppuserMapper.deleteU", pd);
    }

    @Override
    public void editU(PageData pd) throws Exception {
        dao.update("AppuserMapper.editU", pd);
    }

    @Override
    public PageData findByUiId(PageData pd) {
        return (PageData) dao.findForObject("AppuserMapper.findByUiId", pd);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<PageData> listAllUser(PageData pd) {
        return (List<PageData>) dao.findForList("AppuserMapper.listAllUser", pd);
    }

    @Override
    public void deleteAllU(String[] userIds) throws Exception {
        dao.delete("AppuserMapper.deleteAllU", userIds);
    }

    @Override
    public PageData getAppUserCount(String value) {
        return (PageData) dao.findForObject("AppuserMapper.getAppUserCount", value);
    }

}

