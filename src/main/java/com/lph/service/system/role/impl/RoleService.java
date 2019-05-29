package com.lph.service.system.role.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.lph.dao.DaoSupport;
import com.lph.entity.system.Role;
import com.lph.service.system.role.RoleManager;
import com.lph.util.PageData;

/**
 * 角色
 *
 * @author lvpenghui
 * @since 2019-4-17 16:25:20
 */
@Service("roleService")
public class RoleService implements RoleManager {

    @Resource(name = "daoSupport")
    private DaoSupport dao;

    @Override
    @SuppressWarnings("unchecked")
    public List<Role> listAllRolesByPId(PageData pd) {
        return (List<Role>) dao.findForList("RoleMapper.listAllRolesByPId", pd);
    }

    @Override
    public PageData findObjectById(PageData pd) {
        return (PageData) dao.findForObject("RoleMapper.findObjectById", pd);
    }

    @Override
    public void add(PageData pd) throws Exception {
        dao.save("RoleMapper.insert", pd);
    }

    @Override
    public void edit(PageData pd) throws Exception {
        dao.update("RoleMapper.edit", pd);
    }

    @Override
    public void deleteRoleById(String roleId) throws Exception {
        dao.delete("RoleMapper.deleteRoleById", roleId);
    }

    @Override
    public void updateRoleRights(Role role) throws Exception {
        dao.update("RoleMapper.updateRoleRights", role);
    }

    @Override
    public Role getRoleById(String roleId) {
        return (Role) dao.findForObject("RoleMapper.getRoleById", roleId);
    }

    @Override
    public void setAllRights(PageData pd) throws Exception {
        dao.update("RoleMapper.setAllRights", pd);
    }

    @Override
    public void saveB4Button(String msg, PageData pd) throws Exception {
        dao.update("RoleMapper." + msg, pd);
    }

}
