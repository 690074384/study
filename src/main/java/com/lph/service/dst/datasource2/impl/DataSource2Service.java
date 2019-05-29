package com.lph.service.dst.datasource2.impl;

import com.lph.dao.DaoSupport2;
import com.lph.entity.Page;
import com.lph.service.dst.datasource2.DataSource2Manager;
import com.lph.util.PageData;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 说明： 第2数据源例子
 *
 * @author lvpenghui
 * @since 2019-4-17 10:36:11
 */
@Service("datasource2Service")
public class DataSource2Service implements DataSource2Manager {

    @Resource(name = "daoSupport2")
    private DaoSupport2 dao;

    /**
     * 新增
     *
     * @param pd 分页插件
     * @throws Exception 可能抛出的异常
     */
    @Override
    public void save(PageData pd) throws Exception {
        dao.save("DataSource2Mapper.save", pd);
    }

    /**
     * 删除
     *
     * @param pd 分页插件
     * @throws Exception 可能抛出的异常
     */
    @Override
    public void delete(PageData pd) throws Exception {
        dao.delete("DataSource2Mapper.delete", pd);
    }

    /**
     * 修改
     *
     * @param pd 分页插件
     * @throws Exception 可能抛出的异常
     */
    @Override
    public void edit(PageData pd) throws Exception {
        dao.update("DataSource2Mapper.edit", pd);
    }

    /**
     * 列表
     *
     * @param page 分页
     * @throws Exception 可能抛出的异常
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<PageData> list(Page page) throws Exception {
        return (List<PageData>) dao.findForList("DataSource2Mapper.datalistPage", page);
    }

    /**
     * 列表(全部)
     *
     * @param pd 分页插件
     * @throws Exception 可能抛出的异常
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<PageData> listAll(PageData pd) throws Exception {
        return (List<PageData>) dao.findForList("DataSource2Mapper.listAll", pd);
    }

    /**
     * 通过id获取数据
     *
     * @param pd 分页插件
     * @throws Exception 可能抛出的异常
     */
    @Override
    public PageData findById(PageData pd) throws Exception {
        return (PageData) dao.findForObject("DataSource2Mapper.findById", pd);
    }

    /**
     * 批量删除
     *
     * @param arrayDataIds 数据数组
     * @throws Exception 可能抛出的异常
     */
    @Override
    public void deleteAll(String[] arrayDataIds) throws Exception {
        dao.delete("DataSource2Mapper.deleteAll", arrayDataIds);
    }

}

