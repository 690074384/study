package com.lph.service.system.loginimg.impl;

import java.util.List;
import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import com.lph.dao.DaoSupport;
import com.lph.entity.Page;
import com.lph.util.PageData;
import com.lph.service.system.loginimg.LogInImgManager;

/**
 * 说明： 登录页面背景图片
 *
 * @author lvpenghui
 * @since 2019-4-17 16:10:55
 */
@Service("loginimgService")
public class LogInImgService implements LogInImgManager {

    @Resource(name = "daoSupport")
    private DaoSupport dao;

    @Override
    public void save(PageData pd) throws Exception {
        dao.save("LogInImgMapper.save", pd);
    }

    @Override
    public void delete(PageData pd) throws Exception {
        dao.delete("LogInImgMapper.delete", pd);
    }

    @Override
    public void edit(PageData pd) throws Exception {
        dao.update("LogInImgMapper.edit", pd);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<PageData> list(Page page) throws Exception {
        return (List<PageData>) dao.findForList("LogInImgMapper.datalistPage", page);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<PageData> listAll(PageData pd) throws Exception {
        return (List<PageData>) dao.findForList("LogInImgMapper.listAll", pd);
    }

    @Override
    public PageData findById(PageData pd) throws Exception {
        return (PageData) dao.findForObject("LogInImgMapper.findById", pd);
    }

    @Override
    public void deleteAll(String[] arrayDataIds) throws Exception {
        dao.delete("LogInImgMapper.deleteAll", arrayDataIds);
    }

}

