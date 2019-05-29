package com.lph.service.system.user;

import java.util.List;

import com.lph.entity.Page;
import com.lph.entity.system.User;
import com.lph.util.PageData;


/**
 * 用户接口类
 *
 * @author lvpenghui
 * @since 2019-4-17 16:26:45
 */
public interface UserManager {

    /**
     * 登录判断
     *
     * @param pd PageData对象
     * @return PageData对象
     * @throws Exception 可能抛出的异常
     */
    PageData getUserByNameAndPwd(PageData pd) throws Exception;

    /**
     * 更新登录时间
     *
     * @param pd PageData对象
     * @throws Exception 可能抛出的异常
     */
    void updateLastLogin(PageData pd) throws Exception;

    /**
     * 通过用户ID获取用户信息和角色信息
     *
     * @param userId 用户ID
     * @return User对象
     * @throws Exception 可能抛出的异常
     */
    User getUserAndRoleById(String userId) throws Exception;

    /**
     * 通过USERNAEME获取数据
     *
     * @param pd PageData对象
     * @return PageData对象
     * @throws Exception 可能抛出的异常
     */
    PageData findByUsername(PageData pd) throws Exception;

    /**
     * 列出某角色下的所有用户
     *
     * @param pd PageData对象
     * @return List<PageData>对象
     * @throws Exception 可能抛出的异常
     */
    List<PageData> listAllUserByRoldId(PageData pd) throws Exception;

    /**
     * 保存用户IP
     *
     * @param pd PageData对象
     * @throws Exception 可能抛出的异常
     */
    void saveIP(PageData pd) throws Exception;

    /**
     * 用户列表
     *
     * @param page 分页
     * @return List<PageData>对象
     * @throws Exception 可能抛出的异常
     */
    List<PageData> listUsers(Page page) throws Exception;

    /**
     * 用户列表(弹窗选择用)
     *
     * @param page 分页
     * @return List<PageData>对象
     * @throws Exception 可能抛出的异常
     */
    List<PageData> listUsersBystaff(Page page) throws Exception;

    /**
     * 通过邮箱获取数据
     *
     * @param pd PageData对象
     * @return PageData对象
     * @throws Exception 可能抛出的异常
     */
    PageData findByUE(PageData pd) throws Exception;

    /**
     * 通过编号获取数据
     *
     * @param pd PageData对象
     * @return PageData对象
     * @throws Exception 可能抛出的异常
     */
    PageData findByUN(PageData pd) throws Exception;

    /**
     * 通过id获取数据
     *
     * @param pd PageData对象
     * @return PageData对象
     * @throws Exception 可能抛出的异常
     */
    PageData findById(PageData pd) throws Exception;

    /**
     * 修改用户
     *
     * @param pd PageData对象
     * @throws Exception 可能抛出的异常
     */
    void editU(PageData pd) throws Exception;

    /**
     * 保存用户
     *
     * @param pd PageData对象
     * @throws Exception 可能抛出的异常
     */
    void saveU(PageData pd) throws Exception;

    /**
     * 删除用户
     *
     * @param pd PageData对象
     * @throws Exception 可能抛出的异常
     */
    void deleteU(PageData pd) throws Exception;

    /**
     * 批量删除用户
     *
     * @param userIds 需要删除的id
     * @throws Exception 可能抛出的异常
     */
    void deleteAllU(String[] userIds) throws Exception;

    /**
     * 用户列表(全部)
     *
     * @param pd PageData对象
     * @return List<PageData>对象
     * @throws Exception 可能抛出的异常
     */
    List<PageData> listAllUser(PageData pd) throws Exception;

    /**
     * 获取总数
     *
     * @param value value
     * @return PageData对象
     * @throws Exception 可能抛出的异常
     */
    PageData getUserCount(String value) throws Exception;

}
