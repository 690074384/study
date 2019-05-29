package com.lph.service.system.appuser;

import java.util.List;

import com.lph.entity.Page;
import com.lph.util.PageData;


/**
 * 会员接口类
 *
 * @author lvpenghui
 * @since 2019-4-17 14:21:01
 */
public interface AppuserManager {

    /**
     * 列出某角色下的所有会员
     *
     * @param pd PageData对象
     * @return List<PageData>对象
     * @throws Exception 可能抛出的异常
     */
    List<PageData> listAllAppuserByRorlid(PageData pd) throws Exception;

    /**
     * 会员列表
     *
     * @param page 分页
     * @return List<PageData>对象
     * @throws Exception 可能抛出的异常
     */
    List<PageData> listPdPageUser(Page page) throws Exception;

    /**
     * 通过用户名获取数据
     *
     * @param pd PageData对象
     * @return PageData对象
     * @throws Exception 可能抛出的异常
     */
    PageData findByUsername(PageData pd) throws Exception;

    /**
     * 通过邮箱获取数据
     *
     * @param pd PageData对象
     * @return PageData对象
     * @throws Exception 可能抛出的异常
     */
    PageData findByEmail(PageData pd) throws Exception;

    /**
     * 通过编号获取数据
     *
     * @param pd PageData对象
     * @return PageData对象
     * @throws Exception 可能抛出的异常
     */
    PageData findByNumber(PageData pd) throws Exception;

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
     * 修改用户
     *
     * @param pd PageData对象
     * @throws Exception 可能抛出的异常
     */
    void editU(PageData pd) throws Exception;

    /**
     * 通过id获取数据
     *
     * @param pd PageData对象
     * @return PageData对象
     * @throws Exception 可能抛出的异常
     */
    PageData findByUiId(PageData pd) throws Exception;

    /**
     * 全部会员
     *
     * @param pd PageData对象
     * @return List<PageData>对象
     * @throws Exception 可能抛出的异常
     */
    List<PageData> listAllUser(PageData pd) throws Exception;

    /**
     * 批量删除用户
     *
     * @param userIds 需要删除的用户id
     * @throws Exception 可能抛出的异常
     */
    void deleteAllU(String[] userIds) throws Exception;

    /**
     * 获取总数
     *
     * @param value 空串
     * @return PageData对象
     * @throws Exception 可能抛出的异常
     */
    PageData getAppUserCount(String value) throws Exception;

}

