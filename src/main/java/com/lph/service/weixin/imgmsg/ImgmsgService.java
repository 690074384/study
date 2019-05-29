package com.lph.service.weixin.imgmsg;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.lph.dao.DaoSupport;
import com.lph.entity.Page;
import com.lph.util.PageData;


/**
 * 类名称：ImgmsgService
 *
 * @author lvpenghui
 * @since 2019-4-17 17:18:34
 */
@Service("imgmsgService")
public class ImgmsgService {

    @Resource(name = "daoSupport")
    private DaoSupport dao;

    /**
     * 新增
     *
     * @param pd PageData对象
     * @throws Exception 可能抛出的异常
     */
    public void save(PageData pd) throws Exception {
        dao.save("ImgmsgMapper.save", pd);
    }

    /**
     * 删除
     *
     * @param pd PageData对象
     * @throws Exception 可能抛出的异常
     */
    public void delete(PageData pd) throws Exception {
        dao.delete("ImgmsgMapper.delete", pd);
    }

    /**
     * 修改
     *
     * @param pd PageData对象
     * @throws Exception 可能抛出的异常
     */
    public void edit(PageData pd) throws Exception {
        dao.update("ImgmsgMapper.edit", pd);
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
        return (List<PageData>) dao.findForList("ImgmsgMapper.datalistPage", page);
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
        return (List<PageData>) dao.findForList("ImgmsgMapper.listAll", pd);
    }

    /**
     * 通过id获取数据
     *
     * @param pd PageData对象
     * @return PageData对象
     * @throws Exception 可能抛出的异常
     */
    public PageData findById(PageData pd) throws Exception {
        return (PageData) dao.findForObject("ImgmsgMapper.findById", pd);
    }

    /**
     * 批量删除
     *
     * @param arrayDataIds 删除数据的id
     * @throws Exception 可能抛出的异常
     */
    public void deleteAll(String[] arrayDataIds) throws Exception {
        dao.delete("ImgmsgMapper.deleteAll", arrayDataIds);
    }

    /**
     * 匹配关键词
     *
     * @param pd PageData对象
     * @return PageData对象
     */
    public PageData findByKw(PageData pd) {
        return (PageData) dao.findForObject("ImgmsgMapper.findByKw", pd);
    }
}

