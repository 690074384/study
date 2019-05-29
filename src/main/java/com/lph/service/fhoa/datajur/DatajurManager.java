package com.lph.service.fhoa.datajur;

import com.lph.util.PageData;

/**
 * 说明： 组织数据权限接口
 *
 * @author lvpenghui
 * @since 2019-4-17 10:54:35
 */
public interface DatajurManager {

    /**
     * 新增
     *
     * @param pd PageData对象
     * @throws Exception 可能抛出的异常
     */
    void save(PageData pd) throws Exception;

    /**
     * 修改
     *
     * @param pd PageData对象
     * @throws Exception 可能抛出的异常
     */
    void edit(PageData pd) throws Exception;

    /**
     * 通过id获取数据
     *
     * @param pd PageData对象
     * @throws Exception 可能抛出的异常
     */
    PageData findById(PageData pd) throws Exception;

    /**
     * 取出某用户的组织数据权限
     *
     * @param username 用户名
     * @throws Exception 可能抛出的异常
     */
    PageData getDepartmentIds(String username) throws Exception;

}

