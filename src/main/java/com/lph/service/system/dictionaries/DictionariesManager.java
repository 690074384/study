package com.lph.service.system.dictionaries;

import java.util.List;

import com.lph.entity.Page;
import com.lph.entity.system.Dictionaries;
import com.lph.util.PageData;

/**
 * 说明： 数据字典接口类
 *
 * @author lvpenghui
 * @since 2019-4-17 14:44:59
 */
public interface DictionariesManager {

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
     * 通过id获取数据
     *
     * @param pd PageData对象
     * @return PageData对象
     * @throws Exception 可能抛出的异常
     */
    PageData findById(PageData pd) throws Exception;

    /**
     * 通过编码获取数据
     *
     * @param pd PageData对象
     * @return PageData对象
     * @throws Exception 可能抛出的异常
     */
    PageData findByBianma(PageData pd) throws Exception;

    /**
     * 通过ID获取其子级列表
     *
     * @param parentId 父id
     * @return List<Dictionaries>对象
     * @throws Exception 可能抛出的异常
     */
    List<Dictionaries> listSubDictByParentId(String parentId) throws Exception;

    /**
     * 获取所有数据并填充每条数据的子级列表(递归处理)
     *
     * @param parentId 父id
     * @return List<Dictionaries>对象
     * @throws Exception 可能抛出的异常
     */
    List<Dictionaries> listAllDict(String parentId) throws Exception;

    /**
     * 排查表检查是否被占用
     *
     * @param pd PageData对象
     * @return PageData对象
     * @throws Exception 可能抛出的异常
     */
    PageData findFromTbs(PageData pd) throws Exception;

}

