package com.lph.service.weixin.textmsg;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.lph.dao.DaoSupport;
import com.lph.entity.Page;
import com.lph.util.PageData;

/**
 * 类名称：TextmsgService
 *
 * @author lvpenghui
 * @since 2019-4-17 17:21:18
 */
@Service("textmsgService")
public class TextmsgService {

    @Resource(name = "daoSupport")
    private DaoSupport dao;

    /**
     * 新增
     *
     * @param pd PageData对象
     * @throws Exception 可能抛出的异常
     */
    public void save(PageData pd) throws Exception {
        dao.save("TextmsgMapper.save", pd);
    }

    /**
     * 删除
     *
     * @param pd PageData对象
     * @throws Exception 可能抛出的异常
     */
    public void delete(PageData pd) throws Exception {
        dao.delete("TextmsgMapper.delete", pd);
    }

    /**
     * 修改
     *
     * @param pd PageData对象
     * @throws Exception 可能抛出的异常
     */
    public void edit(PageData pd) throws Exception {
        dao.update("TextmsgMapper.edit", pd);
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
        return (List<PageData>) dao.findForList("TextmsgMapper.datalistPage", page);
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
        return (List<PageData>) dao.findForList("TextmsgMapper.listAll", pd);
    }

    /**
     * 通过id获取数据
     *
     * @param pd PageData对象
     * @return PageData对象
     * @throws Exception 可能抛出的异常
     */
    public PageData findById(PageData pd) throws Exception {
        return (PageData) dao.findForObject("TextmsgMapper.findById", pd);
    }

    /**
     * 批量删除
     *
     * @param arrayDataIds 需要删除记录对应的id
     * @throws Exception 可能抛出的异常
     */
    public void deleteAll(String[] arrayDataIds) throws Exception {
        dao.delete("TextmsgMapper.deleteAll", arrayDataIds);
    }

    /**
     * 匹配关键词
     *
     * @param pd PageData对象
     * @return PageData对象
     */
    public PageData findByKw(PageData pd) {
        return (PageData) dao.findForObject("TextmsgMapper.findByKw", pd);
    }
}

