package com.lph.service.system.role;

import java.util.List;

import com.lph.entity.system.Role;
import com.lph.util.PageData;

/**
 * 角色接口类
 *
 * @author lvpenghui
 * @since 2019-4-17 16:22:32
 */
public interface RoleManager {

    /**
     * 列出此组下级角色
     *
     * @param pd PageData对象
     * @return List<Role>对象
     * @throws Exception 可能抛出的异常
     */
    List<Role> listAllRolesByPId(PageData pd) throws Exception;

    /**
     * 通过id查找
     *
     * @param pd PageData对象
     * @return PageData对象
     * @throws Exception 可能抛出的异常
     */
    PageData findObjectById(PageData pd) throws Exception;

    /**
     * 添加
     *
     * @param pd PageData对象
     * @throws Exception 可能抛出的异常
     */
    void add(PageData pd) throws Exception;

    /**
     * 保存修改
     *
     * @param pd PageData对象
     * @throws Exception 可能抛出的异常
     */
    void edit(PageData pd) throws Exception;

    /**
     * 删除角色
     *
     * @param roleId 角色id
     * @throws Exception 可能抛出的异常
     */
    void deleteRoleById(String roleId) throws Exception;

    /**
     * 给当前角色附加菜单权限
     *
     * @param role Role对象
     * @throws Exception 可能抛出的异常
     */
    void updateRoleRights(Role role) throws Exception;

    /**
     * 通过id查找
     *
     * @param roleId 角色id
     * @return Role对象
     * @throws Exception 可能抛出的异常
     */
    Role getRoleById(String roleId) throws Exception;

    /**
     * 给全部子角色加菜单权限
     *
     * @param pd PageData对象
     * @throws Exception 可能抛出的异常
     */
    void setAllRights(PageData pd) throws Exception;

    /**
     * 权限(增删改查)
     *
     * @param msg 区分增删改查
     * @param pd  PageData对象
     * @throws Exception 可能抛出的异常
     */
    void saveB4Button(String msg, PageData pd) throws Exception;

}
