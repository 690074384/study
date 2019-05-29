package com.lph.service.weixin.command;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.lph.dao.DaoSupport;
import com.lph.entity.Page;
import com.lph.util.PageData;

/**
 * 类名称：CommandService
 *
 * @author lvpenghui
 * @since 2019-4-17 17:08:00
 */
@Service("commandService")
public class CommandService {

    @Resource(name = "daoSupport")
    private DaoSupport dao;

    /**
     * 新增
     *
     * @param pd PageData对象
     * @throws Exception 可能抛出的异常
     */
    public void save(PageData pd) throws Exception {
        dao.save("CommandMapper.save", pd);
    }

    /**
     * 删除
     *
     * @param pd PageData对象
     * @throws Exception 可能抛出的异常
     */
    public void delete(PageData pd) throws Exception {
        dao.delete("CommandMapper.delete", pd);
    }

    /**
     * 修改
     *
     * @param pd PageData对象
     * @throws Exception 可能抛出的异常
     */
    public void edit(PageData pd) throws Exception {
        dao.update("CommandMapper.edit", pd);
    }

    /**
     * 列表
     *
     * @param page 分页
     * @return List<PageData>对象
     * @throws Exception 可能抛出的异常
     */
    @SuppressWarnings("unchecked")
    public List<PageData> list(Page page) throws Exception {
        return (List<PageData>) dao.findForList("CommandMapper.datalistPage", page);
    }

    /**
     * 列表(全部)
     *
     * @param pd PageData对象
     * @return List<PageData>对象
     * @throws Exception 可能抛出的异常
     */
    @SuppressWarnings("unchecked")
    public List<PageData> listAll(PageData pd) throws Exception {
        return (List<PageData>) dao.findForList("CommandMapper.listAll", pd);
    }

    /**
     * 通过id获取数据
     *
     * @param pd PageData对象
     * @return PageData对象
     * @throws Exception 可能抛出的异常
     */
    public PageData findById(PageData pd) throws Exception {
        return (PageData) dao.findForObject("CommandMapper.findById", pd);
    }

    /**
     * 批量删除
     *
     * @param arrayDataIds 批量删除的id
     * @throws Exception 可能抛出的异常
     */
    public void deleteAll(String[] arrayDataIds) throws Exception {
        dao.delete("CommandMapper.deleteAll", arrayDataIds);
    }

    /**
     * 匹配关键词
     *
     * @param pd PageData对象
     * @return PageData对象
     */
    public PageData findByKw(PageData pd) {
        return (PageData) dao.findForObject("CommandMapper.findByKw", pd);
    }
}

