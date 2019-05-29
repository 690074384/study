package com.lph.service.dst.datasource2;

import java.util.List;

import com.lph.entity.Page;
import com.lph.util.PageData;

/**
 * 说明： 第2数据源例子接口
 *
 * @author lvpenghui
 * @since 2019-4-17 10:39:11
 */
public interface DataSource2Manager {

    /**
     * 新增
     *
     * @param pd 分页插件
     * @throws Exception 可能抛出的异常
     */
    void save(PageData pd) throws Exception;

    /**
     * 删除
     *
     * @param pd 分页插件
     * @throws Exception 可能抛出的异常
     */
    void delete(PageData pd) throws Exception;

    /**
     * 修改
     *
     * @param pd 分页插件
     * @throws Exception 可能抛出的异常
     */
    void edit(PageData pd) throws Exception;

    /**
     * 列表
     *
     * @param page 分页
     * @return List<PageData>
     * @throws Exception 可能抛出的异常
     */
    List<PageData> list(Page page) throws Exception;

    /**
     * 列表(全部)
     *
     * @param pd 分页插件
     * @return List<PageData>
     * @throws Exception 可能抛出的异常
     */
    List<PageData> listAll(PageData pd) throws Exception;

    /**
     * 通过id获取数据
     *
     * @param pd 分页插件
     * @return 返回的PageData对象
     * @throws Exception 可能抛出的异常
     */
    PageData findById(PageData pd) throws Exception;

    /**
     * 批量删除
     *
     * @param arrayDataIds 数据数组
     * @throws Exception 可能抛出的异常
     */
    void deleteAll(String[] arrayDataIds) throws Exception;

}

