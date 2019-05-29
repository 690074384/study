package com.lph.service.system.fhsms;

import java.util.List;

import com.lph.entity.Page;
import com.lph.util.PageData;

/**
 * 说明： 站内信接口
 *
 * @author lvpenghui
 * @since 2019-4-17 15:47:23
 */
public interface FhsmsManager {

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
     * 获取未读总数
     *
     * @param username 用户名
     * @return PageData对象
     * @throws Exception 可能抛出的异常
     */
    PageData findFhsmsCount(String username) throws Exception;

    /**
     * 批量删除
     *
     * @param arrayDataIds 批量删除内容对应的主键
     * @throws Exception 可能抛出的异常
     */
    void deleteAll(String[] arrayDataIds) throws Exception;

}

