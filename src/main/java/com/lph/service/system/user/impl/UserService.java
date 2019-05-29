package com.lph.service.system.user.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.lph.dao.DaoSupport;
import com.lph.entity.Page;
import com.lph.entity.system.User;
import com.lph.service.system.user.UserManager;
import com.lph.util.PageData;


/**
 * 系统用户
 *
 * @author lvpenghui
 * @since 2019-4-17 16:59:05
 */
@Service("userService")
public class UserService implements UserManager {

    @Resource(name = "daoSupport")
    private DaoSupport dao;

    @Override
    public PageData getUserByNameAndPwd(PageData pd) {
        return (PageData) dao.findForObject("UserMapper.getUserInfo", pd);
    }

    @Override
    public void updateLastLogin(PageData pd) throws Exception {
        dao.update("UserMapper.updateLastLogin", pd);
    }

    @Override
    public User getUserAndRoleById(String userId) {
        return (User) dao.findForObject("UserMapper.getUserAndRoleById", userId);
    }

    @Override
    public PageData findByUsername(PageData pd) {
        return (PageData) dao.findForObject("UserMapper.findByUsername", pd);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<PageData> listAllUserByRoldId(PageData pd) {
        return (List<PageData>) dao.findForList("UserMapper.listAllUserByRoldId", pd);

    }

    @Override
    public void saveIP(PageData pd) throws Exception {
        dao.update("UserMapper.saveIP", pd);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<PageData> listUsers(Page page) {
        return (List<PageData>) dao.findForList("UserMapper.userlistPage", page);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<PageData> listUsersBystaff(Page page) {
        return (List<PageData>) dao.findForList("UserMapper.userBystafflistPage", page);
    }

    @Override
    public PageData findByUE(PageData pd) {
        return (PageData) dao.findForObject("UserMapper.findByUE", pd);
    }

    @Override
    public PageData findByUN(PageData pd) {
        return (PageData) dao.findForObject("UserMapper.findByUN", pd);
    }

    @Override
    public PageData findById(PageData pd) {
        return (PageData) dao.findForObject("UserMapper.findById", pd);
    }

    @Override
    public void saveU(PageData pd) throws Exception {
        dao.save("UserMapper.saveU", pd);
    }

    @Override
    public void editU(PageData pd) throws Exception {
        dao.update("UserMapper.editU", pd);
    }

    @Override
    public void deleteU(PageData pd) throws Exception {
        dao.delete("UserMapper.deleteU", pd);
    }

    @Override
    public void deleteAllU(String[] userId) throws Exception {
        dao.delete("UserMapper.deleteAllU", userId);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<PageData> listAllUser(PageData pd) {
        return (List<PageData>) dao.findForList("UserMapper.listAllUser", pd);
    }

    @Override
    public PageData getUserCount(String value) {
        return (PageData) dao.findForObject("UserMapper.getUserCount", value);
    }

}
