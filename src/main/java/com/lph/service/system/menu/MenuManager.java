package com.lph.service.system.menu;

import java.util.List;

import com.lph.entity.system.Menu;
import com.lph.util.PageData;


/**
 * 说明：MenuService 菜单处理接口
 *
 * @author lvpenghui
 * @since 2019-4-17 16:14:45
 */
public interface MenuManager {

    /**
     * 通过ID获取其子一级菜单
     *
     * @param parentId 父id
     * @return List<Menu>对象
     * @throws Exception 可能抛出的异常
     */
    List<Menu> listSubMenuByParentId(String parentId) throws Exception;

    /**
     * 通过菜单ID获取数据
     *
     * @param pd PageData对象
     * @return PageData对象
     * @throws Exception 可能抛出的异常
     */
    PageData getMenuById(PageData pd) throws Exception;

    /**
     * 新增菜单
     *
     * @param menu 菜单
     * @throws Exception 可能抛出的异常
     */
    void saveMenu(Menu menu) throws Exception;

    /**
     * 取最大ID
     *
     * @param pd PageData对象
     * @return PageData对象
     * @throws Exception 可能抛出的异常
     */
    PageData findMaxId(PageData pd) throws Exception;

    /**
     * 删除菜单
     *
     * @param menuId 菜单id
     * @throws Exception 可能抛出的异常
     */
    void deleteMenuById(String menuId) throws Exception;

    /**
     * 编辑
     *
     * @param menu 菜单
     * @throws Exception 可能抛出的异常
     */
    void edit(Menu menu) throws Exception;

    /**
     * 保存菜单图标
     *
     * @param pd PageData对象
     * @return PageData对象
     * @throws Exception 可能抛出的异常
     */
    PageData editicon(PageData pd) throws Exception;

    /**
     * 获取所有菜单并填充每个菜单的子菜单列表(菜单管理)(递归处理)
     *
     * @param menuId 菜单id
     * @return List<Menu>对象
     * @throws Exception 可能抛出的异常
     */
    List<Menu> listAllMenu(String menuId) throws Exception;

    /**
     * 获取所有菜单并填充每个菜单的子菜单列表(系统菜单列表)(递归处理)
     *
     * @param menuId 菜单id
     * @return List<Menu>对象
     * @throws Exception 可能抛出的异常
     */
    List<Menu> listAllMenuQx(String menuId) throws Exception;
}
