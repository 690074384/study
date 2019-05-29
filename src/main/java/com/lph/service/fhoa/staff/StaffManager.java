package com.lph.service.fhoa.staff;

import java.util.List;

import com.lph.entity.Page;
import com.lph.util.PageData;

/**
 * 说明： 员工管理接口
 *
 * @author lvpenghui
 * @since 2019-4-17 11:38:28
 */
public interface StaffManager {

    /**
     * 新增
     *
     * @param pd PageData对象
     * @throws Exception 可能抛出的异常
     */
    void save(PageData pd) throws Exception;

    /**
     * 删除
     *
     * @param pd PageData对象
     * @throws Exception 可能抛出的异常
     */
    void delete(PageData pd) throws Exception;

    /**
     * 修改
     *
     * @param pd PageData对象
     * @throws Exception 可能抛出的异常
     */
    void edit(PageData pd) throws Exception;

    /**
     * 列表
     *
     * @param page 分页
     * @return List<PageData>对象
     * @throws Exception 可能抛出的异常
     */
    List<PageData> list(Page page) throws Exception;

    /**
     * 列表(全部)
     *
     * @param pd PageData对象
     * @return List<PageData>对象
     * @throws Exception 可能抛出的异常
     */
    List<PageData> listAll(PageData pd) throws Exception;

    /**
     * 通过id获取数据
     *
     * @param pd PageData对象
     * @return PageData对象
     * @throws Exception 可能抛出的异常
     */
    PageData findById(PageData pd) throws Exception;

    /**
     * 批量删除
     *
     * @param arrayDataIds 对象数组
     * @throws Exception 可能抛出的异常
     */
    void deleteAll(String[] arrayDataIds) throws Exception;

    /**
     * 绑定用户
     *
     * @param pd PageData对象
     * @throws Exception 可能抛出的异常
     */
    void userBinding(PageData pd) throws Exception;
}

