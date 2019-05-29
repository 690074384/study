package com.lph.service.system.menu.impl;

import com.lph.dao.DaoSupport;
import com.lph.entity.system.Menu;
import com.lph.service.system.menu.MenuManager;
import com.lph.util.PageData;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 类名称：MenuService 菜单处理
 *
 * @author lvpenghui
 * @since 2019-4-17 16:20:06
 */
@Service("menuService")
public class MenuService implements MenuManager {

    @Resource(name = "daoSupport")
    private DaoSupport dao;

    @Override
    @SuppressWarnings("unchecked")
    public List<Menu> listSubMenuByParentId(String parentId) {
        return (List<Menu>) dao.findForList("MenuMapper.listSubMenuByParentId", parentId);
    }

    @Override
    public PageData getMenuById(PageData pd) {
        return (PageData) dao.findForObject("MenuMapper.getMenuById", pd);
    }

    @Override
    public void saveMenu(Menu menu) throws Exception {
        dao.save("MenuMapper.insertMenu", menu);
    }

    @Override
    public PageData findMaxId(PageData pd) {
        return (PageData) dao.findForObject("MenuMapper.findMaxId", pd);
    }

    @Override
    public void deleteMenuById(String menuId) throws Exception {
        dao.save("MenuMapper.deleteMenuById", menuId);
    }

    @Override
    public void edit(Menu menu) throws Exception {
        dao.update("MenuMapper.updateMenu", menu);
    }

    @Override
    public PageData editicon(PageData pd) {
        return (PageData) dao.findForObject("MenuMapper.editicon", pd);
    }

    @Override
    public List<Menu> listAllMenu(String menuId) {
        List<Menu> menuList = this.listSubMenuByParentId(menuId);
        for (Menu menu : menuList) {
            menu.setMENU_URL("menu/toEdit.do?MENU_ID=" + menu.getMENU_ID());
            menu.setSubMenu(this.listAllMenu(menu.getMENU_ID()));
            menu.setTarget("treeFrame");
        }
        return menuList;
    }

    @Override
    public List<Menu> listAllMenuQx(String menuId) {
        List<Menu> menuList = this.listSubMenuByParentId(menuId);
        for (Menu menu : menuList) {
            menu.setSubMenu(this.listAllMenuQx(menu.getMENU_ID()));
            menu.setTarget("treeFrame");
        }
        return menuList;
    }

}
