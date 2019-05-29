package com.lph.controller.system.role;

import com.google.common.collect.Maps;
import com.lph.controller.base.BaseController;
import com.lph.entity.system.Menu;
import com.lph.entity.system.Role;
import com.lph.service.system.appuser.AppuserManager;
import com.lph.service.system.fhlog.FhLogManager;
import com.lph.service.system.menu.MenuManager;
import com.lph.service.system.role.RoleManager;
import com.lph.service.system.user.UserManager;
import com.lph.util.*;
import net.sf.json.JSONArray;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;

/**
 * 类名称：RoleController 角色权限管理
 *
 * @author lvpenghui
 * @since 2019-4-10 22:10:33
 */
@Controller
@RequestMapping(value = "/role")
public class RoleController extends BaseController {

    /**
     * 菜单地址(权限用)
     */
    private String menuUrl = "role.do";
    @Resource(name = "menuService")
    private MenuManager menuService;
    @Resource(name = "roleService")
    private RoleManager roleService;
    @Resource(name = "userService")
    private UserManager userService;
    @Resource(name = "appuserService")
    private AppuserManager appuserService;
    @Resource(name = "fhlogService")
    private FhLogManager fHlogManager;

    /**
     * 进入权限首页
     *
     * @return ModelAndView对象
     * @throws Exception 可能抛出的异常
     */
    @RequestMapping
    public ModelAndView list() throws Exception {
        ModelAndView mv = this.getModelAndView();
        PageData pd = this.getPageData();
        try {
            if (StringUtils.isBlank(pd.getString(Constants.ROLE_ID))) {
                //默认列出第一组角色(初始设计系统用户和会员组不能删除)
                pd.put("ROLE_ID", "1");
            }
            PageData fpd = new PageData();
            fpd.put("ROLE_ID", "0");
            //列出组(页面横向排列的一级组)
            List<Role> roleList = roleService.listAllRolesByPId(fpd);
            //列出此组下架角色
            List<Role> sonRoleList = roleService.listAllRolesByPId(pd);
            //取得点击的角色组(横排的)
            pd = roleService.findObjectById(pd);
            mv.addObject("pd", pd);
            mv.addObject("roleList", roleList);
            mv.addObject("roleList_z", sonRoleList);
            //按钮权限
            mv.addObject("QX", Jurisdiction.getHC());
            mv.setViewName("system/role/role_list");
        } catch (Exception e) {
            logger.error(e.toString(), e);
        }
        return mv;
    }

    /**
     * 去新增页面
     *
     * @return ModelAndView对象
     */
    @RequestMapping(value = "/toAdd")
    public ModelAndView toAdd() {
        ModelAndView mv = this.getModelAndView();
        PageData pd = this.getPageData();
        try {
            mv.addObject("msg", "add");
            mv.setViewName("system/role/role_edit");
            mv.addObject("pd", pd);
        } catch (Exception e) {
            logger.error(e.toString(), e);
        }
        return mv;
    }

    /**
     * 保存新增角色
     *
     * @return ModelAndView对象
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public ModelAndView add() {
        if (!Jurisdiction.buttonJurisdiction(menuUrl, Constants.ADDD)) {
            return null;
        } //校验权限
        logBefore(logger, Jurisdiction.getUsername() + "新增角色");
        ModelAndView mv = this.getModelAndView();
        PageData pd = this.getPageData();
        try {
            //父类角色id
            String parentId = pd.getString("PARENT_ID");
            pd.put("ROLE_ID", parentId);
            if (Constants.ZERO_STRING.equals(parentId)) {
                //菜单权限
                pd.put("RIGHTS", "");
            } else {
                String rights = roleService.findObjectById(pd).getString("RIGHTS");
                //组菜单权限
                pd.put("RIGHTS", (null == rights) ? "" : rights);
            }
            pd.put("ROLE_ID", this.get32UUID());
            //初始新增权限为否
            pd.put("ADD_QX", "0");
            //删除权限
            pd.put("DEL_QX", "0");
            //修改权限
            pd.put("EDIT_QX", "0");
            //查看权限
            pd.put("CHA_QX", "0");
            roleService.add(pd);
            fHlogManager.save(Jurisdiction.getUsername(), "新增角色:" + pd.getString("ROLE_NAME"));
        } catch (Exception e) {
            logger.error(e.toString(), e);
            mv.addObject("msg", "failed");
        }
        mv.setViewName("save_result");
        return mv;
    }

    /**
     * 请求编辑
     *
     * @param roleId 角色ID
     * @return ModelAndView对象
     */
    @RequestMapping(value = "/toEdit")
    public ModelAndView toEdit(String roleId) {
        ModelAndView mv = this.getModelAndView();
        PageData pd = this.getPageData();
        try {
            pd.put("ROLE_ID", roleId);
            pd = roleService.findObjectById(pd);
            mv.addObject("msg", "edit");
            mv.addObject("pd", pd);
            mv.setViewName("system/role/role_edit");
        } catch (Exception e) {
            logger.error(e.toString(), e);
        }
        return mv;
    }

    /**
     * 保存修改
     *
     * @return ModelAndView对象
     * @throws Exception 可能抛出的异常
     */
    @RequestMapping(value = "/edit")
    public ModelAndView edit() throws Exception {
        if (!Jurisdiction.buttonJurisdiction(menuUrl, Constants.EDIT)) {
            return null;
        } //校验权限
        logBefore(logger, Jurisdiction.getUsername() + "修改角色");
        ModelAndView mv = this.getModelAndView();
        PageData pd = this.getPageData();
        try {
            roleService.edit(pd);
            fHlogManager.save(Jurisdiction.getUsername(), "修改角色:" + pd.getString("ROLE_NAME"));
            mv.addObject("msg", "success");
        } catch (Exception e) {
            logger.error(e.toString(), e);
            mv.addObject("msg", "failed");
        }
        mv.setViewName("save_result");
        return mv;
    }

    /**
     * 删除角色
     *
     * @param roleId 角色id
     * @return 删除角色返回结果
     */
    @RequestMapping(value = "/delete")
    @ResponseBody
    public Object deleteRole(@RequestParam String roleId) {
        if (!Jurisdiction.buttonJurisdiction(menuUrl, Constants.DELE)) {
            return null;
        } //校验权限
        logBefore(logger, Jurisdiction.getUsername() + "删除角色");
        Map<String, String> map = Maps.newHashMap();
        PageData pd = new PageData();
        String errInfo = "";
        try {
            pd.put("ROLE_ID", roleId);
            //列出此部门的所有下级
            List<Role> sonRoleList = roleService.listAllRolesByPId(pd);
            if (sonRoleList.size() > 0) {
                //下级有数据时，删除失败
                errInfo = "false";
            } else {
                //此角色下的用户
                List<PageData> userlist = userService.listAllUserByRoldId(pd);
                //此角色下的会员
                List<PageData> appuserlist = appuserService.listAllAppuserByRorlid(pd);
                //此角色已被使用就不能删除
                if (userlist.size() > 0 || appuserlist.size() > 0) {
                    errInfo = "false2";
                } else {
                    //执行删除
                    roleService.deleteRoleById(roleId);
                    fHlogManager.save(Jurisdiction.getUsername(), "删除角色ID为:" + roleId);
                    errInfo = "success";
                }
            }
        } catch (Exception e) {
            logger.error(e.toString(), e);
        }
        map.put("result", errInfo);
        return AppUtil.returnObject(new PageData(), map);
    }

    /**
     * 显示菜单列表ztree(菜单授权菜单)
     *
     * @param model  Model对象
     * @param roleId 角色Id
     * @return ModelAndView对象
     */
    @RequestMapping(value = "/menuqx")
    public ModelAndView listAllMenu(Model model, String roleId) {
        ModelAndView mv = this.getModelAndView();
        try {
            //根据角色ID获取角色对象
            Role role = roleService.getRoleById(roleId);
            //取出本角色菜单权限
            String roleRights = role.getRIGHTS();
            //获取所有菜单
            List<Menu> menuList = menuService.listAllMenuQx("0");
            //根据角色权限处理菜单权限状态(递归处理)
            List<Menu> returnMenuList = this.readMenu(menuList, roleRights);
            JSONArray arr = JSONArray.fromObject(returnMenuList);
            String json = arr.toString();
            json = json
                    .replaceAll("MENU_ID", "id")
                    .replaceAll("PARENT_ID", "pId")
                    .replaceAll("MENU_NAME", "name")
                    .replaceAll("subMenu", "nodes")
                    .replaceAll("hasMenu", "checked");
            model.addAttribute("zTreeNodes", json);
            mv.addObject("ROLE_ID", roleId);
            mv.setViewName("system/role/menuqx");
        } catch (Exception e) {
            logger.error(e.toString(), e);
        }
        return mv;
    }

    /**
     * 保存角色菜单权限
     *
     * @param roleId  角色ID
     * @param menuIds 菜单ID集合
     * @param out     out
     * @throws Exception 可能抛出的异常
     */
    @RequestMapping(value = "/saveMenuqx")
    public void saveMenuqx(@RequestParam String roleId, @RequestParam String menuIds, PrintWriter out) throws Exception {
        if (!Jurisdiction.buttonJurisdiction(menuUrl, Constants.EDIT)) {
            return;
        } //校验权限
        logBefore(logger, Jurisdiction.getUsername() + "修改菜单权限");
        fHlogManager.save(Jurisdiction.getUsername(), "修改角色菜单权限，角色ID为:" + roleId);
        PageData pd = new PageData();
        try {
            if (null != menuIds && !"".equals(menuIds.trim())) {
                //用菜单ID做权处理
                BigInteger rights = RightsHelper.sumRights(Tools.str2StrArray(menuIds));
                //通过id获取角色对象
                Role role = roleService.getRoleById(roleId);
                role.setRIGHTS(rights.toString());
                //更新当前角色菜单权限
                roleService.updateRoleRights(role);
                pd.put("rights", rights.toString());
            } else {
                Role role = new Role();
                role.setRIGHTS("");
                role.setROLE_ID(roleId);
                //更新当前角色菜单权限(没有任何勾选)
                roleService.updateRoleRights(role);
                pd.put("rights", "");
            }
            pd.put("ROLE_ID", roleId);
            //当修改admin权限时,不修改其它角色权限
            if (!Constants.ONE_STRING.equals(roleId)) {
                //更新此角色所有子角色的菜单权限
                roleService.setAllRights(pd);
            }
            out.write("success");
            out.close();
        } catch (Exception e) {
            logger.error(e.toString(), e);
        }
    }

    /**
     * 请求角色按钮授权页面(增删改查)
     *
     * @param roleId 角色ID
     * @param msg    区分增删改查
     * @param model  Model对象
     * @return ModelAndView对象
     */
    @RequestMapping(value = "/b4Button")
    public ModelAndView b4Button(@RequestParam String roleId, @RequestParam String msg, Model model) {
        ModelAndView mv = this.getModelAndView();
        try {
            //根据角色ID获取角色对象
            Role role = roleService.getRoleById(roleId);
            String roleRights = "";
            if (Constants.ADDD_QX.equals(msg)) {
                roleRights = role.getADD_QX();
            } else if (Constants.DELE_QX.equals(msg)) {
                roleRights = role.getDEL_QX();
            } else if (Constants.EDIT_QX.equals(msg)) {
                roleRights = role.getEDIT_QX();
            } else if (Constants.SRCH_QX.equals(msg)) {
                roleRights = role.getCHA_QX();
            }
            //获取所有菜单
            List<Menu> menuList = menuService.listAllMenuQx("0");
            //根据角色权限处理菜单权限状态(递归处理)
            List<Menu> returnMenuList = this.readMenu(menuList, roleRights);
            JSONArray arr = JSONArray.fromObject(returnMenuList);
            String json = arr.toString();
            json = json
                    .replaceAll("MENU_ID", "id")
                    .replaceAll("PARENT_ID", "pId")
                    .replaceAll("MENU_NAME", "name")
                    .replaceAll("subMenu", "nodes")
                    .replaceAll("hasMenu", "checked");
            model.addAttribute("zTreeNodes", json);
            mv.addObject("ROLE_ID", roleId);
            mv.addObject("msg", msg);
        } catch (Exception e) {
            logger.error(e.toString(), e);
        }
        mv.setViewName("system/role/b4Button");
        return mv;
    }

    /**
     * 根据角色权限处理权限状态(递归处理)
     *
     * @param menuList：传入的总菜单
     * @param roleRights：加密的权限字符串
     * @return 菜单列表
     */
    private List<Menu> readMenu(List<Menu> menuList, String roleRights) {
        for (Menu aMenuList : menuList) {
            aMenuList.setHasMenu(RightsHelper.testRights(roleRights, aMenuList.getMENU_ID()));
            //是：继续排查其子菜单
            this.readMenu(aMenuList.getSubMenu(), roleRights);
        }
        return menuList;
    }

    /**
     * @param roleId  角色id
     * @param menuIds 菜单id
     * @param msg     区分增删改查
     * @param out     out
     * @throws Exception 可能抛出的异常
     */
    @RequestMapping(value = "/saveB4Button")
    public void saveB4Button(@RequestParam String roleId, @RequestParam String menuIds, @RequestParam String msg, PrintWriter out) throws Exception {
        //校验权限
        if (!Jurisdiction.buttonJurisdiction(menuUrl, Constants.EDIT)) {
            return;
        }
        logBefore(logger, Jurisdiction.getUsername() + "修改" + msg + "权限");
        fHlogManager.save(Jurisdiction.getUsername(), "修改" + msg + "权限，角色ID为:" + roleId);
        PageData pd = this.getPageData();
        try {
            if (null != menuIds && !"".equals(menuIds.trim())) {
                BigInteger rights = RightsHelper.sumRights(Tools.str2StrArray(menuIds));
                pd.put("value", rights.toString());
            } else {
                pd.put("value", "");
            }
            pd.put("ROLE_ID", roleId);
            roleService.saveB4Button(msg, pd);
            out.write("success");
            out.close();
        } catch (Exception e) {
            logger.error(e.toString(), e);
        }
    }

}