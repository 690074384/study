package com.lph.controller.system.login;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.lph.controller.base.BaseController;
import com.lph.entity.system.Menu;
import com.lph.entity.system.Role;
import com.lph.entity.system.User;
import com.lph.service.fhoa.datajur.DatajurManager;
import com.lph.service.system.appuser.AppuserManager;
import com.lph.service.system.buttonrights.ButtonrightsManager;
import com.lph.service.system.fhbutton.FhbuttonManager;
import com.lph.service.system.fhlog.FhLogManager;
import com.lph.service.system.loginimg.LogInImgManager;
import com.lph.service.system.menu.MenuManager;
import com.lph.service.system.role.RoleManager;
import com.lph.service.system.user.UserManager;
import com.lph.util.*;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Nullable;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * 总入口
 *
 * @author lvpenghui
 * @since 2019-4-10 20:15:36
 */
@Controller
public class LoginController extends BaseController {

    @Resource(name = "userService")
    private UserManager userService;
    @Resource(name = "menuService")
    private MenuManager menuService;
    @Resource(name = "roleService")
    private RoleManager roleService;
    @Resource(name = "buttonrightsService")
    private ButtonrightsManager buttonrightsService;
    @Resource(name = "fhbuttonService")
    private FhbuttonManager fhbuttonService;
    @Resource(name = "appuserService")
    private AppuserManager appuserService;
    @Resource(name = "datajurService")
    private DatajurManager datajurService;
    @Resource(name = "fhlogService")
    private FhLogManager fHlogManager;
    @Resource(name = "loginimgService")
    private LogInImgManager loginimgService;

    /**
     * 访问登录页
     *
     * @return ModelAndView对象
     */
    @RequestMapping(value = "/login_toLogin")
    public ModelAndView toLogin() {
        ModelAndView mv = this.getModelAndView();
        PageData pd = this.getPageData();
        //设置登录页面的配置参数
        PageData pdd = this.setLoginPd(pd);
        mv.setViewName("system/index/login");
        mv.addObject("pd", pdd);
        return mv;
    }

    /**
     * 请求登录，验证用户
     *
     * @return ModelAndView对象
     * @throws Exception 可能抛出的异常
     */
    @RequestMapping(value = "/login_login", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public Object login() throws Exception {
        Map<String, String> map = Maps.newHashMap();
        PageData pd = this.getPageData();
        String errInfo = "";
        String[] keyData = pd.getString("KEYDATA").replaceAll("qq690074384fh", "").replaceAll("QQ690074384fh", "").split(",fh,");
        if (keyData.length == Constants.THREE) {
            Session session = Jurisdiction.getSession();
            //获取session中的验证码
            String sessionCode = (String) session.getAttribute(Constants.SESSION_SECURITY_CODE);
            String code = keyData[2];
            //判断效验码
            if (StringUtils.isEmpty(code)) {
                //效验码为空
                errInfo = "nullcode";
            } else {
                //登录过来的用户名
                String username = keyData[0];
                //登录过来的密码
                String password = keyData[1];
                pd.put("USERNAME", username);
                //判断登录验证码
                if (Tools.notEmpty(sessionCode) && sessionCode.equalsIgnoreCase(code)) {
                    //密码加密
                    String passwd = new SimpleHash("SHA-1", username, password).toString();
                    pd.put("PASSWORD", passwd);
                    //根据用户名和密码去读取用户信息
                    pd = userService.getUserByNameAndPwd(pd);
                    if (pd != null) {
                        pd.put("LAST_LOGIN", DateUtil.getTime());
                        userService.updateLastLogin(pd);
                        User user = new User();
                        user.setUSER_ID(pd.getString("USER_ID"));
                        user.setUSERNAME(pd.getString("USERNAME"));
                        user.setPASSWORD(pd.getString("PASSWORD"));
                        user.setNAME(pd.getString("NAME"));
                        user.setRIGHTS(pd.getString("RIGHTS"));
                        user.setROLE_ID(pd.getString("ROLE_ID"));
                        user.setLAST_LOGIN(pd.getString("LAST_LOGIN"));
                        user.setIP(pd.getString("IP"));
                        user.setSTATUS(pd.getString("STATUS"));
                        //把用户信息放session中
                        session.setAttribute(Constants.SESSION_USER, user);
                        //清除登录验证码的session
                        session.removeAttribute(Constants.SESSION_SECURITY_CODE);
                        //shiro加入身份验证
                        Subject subject = SecurityUtils.getSubject();
                        UsernamePasswordToken token = new UsernamePasswordToken(username, password);
                        try {
                            subject.login(token);
                        } catch (AuthenticationException e) {
                            errInfo = "身份验证失败！";
                        }
                    } else {
                        //用户名或密码有误
                        errInfo = "usererror";
                        logBefore(logger, username + "登录系统密码或用户名错误");
                        fHlogManager.save(username, "登录系统密码或用户名错误");
                    }
                } else {
                    //验证码输入有误
                    errInfo = "codeerror";
                }
                if (Tools.isEmpty(errInfo)) {
                    //验证成功
                    errInfo = "success";
                    logBefore(logger, username + "登录系统");
                    fHlogManager.save(username, "登录系统");
                }
            }
        } else {
            //缺少参数
            errInfo = "error";
        }
        map.put("result", errInfo);
        return AppUtil.returnObject(new PageData(), map);
    }

    /**
     * 访问系统首页
     *
     * @param changeMenu 切换菜单参数
     * @return ModelAndView对象
     */
    @RequestMapping(value = "/main/{changeMenu}")
    public ModelAndView loginIndex(@PathVariable("changeMenu") String changeMenu) {
        ModelAndView mv = this.getModelAndView();
        PageData pd = this.getPageData();
        try {
            Session session = Jurisdiction.getSession();
            //读取session中的用户信息(单独用户信息)
            User user = (User) session.getAttribute(Constants.SESSION_USER);
            if (user != null) {
                //读取session中的用户信息(含角色信息)
                User userRole = (User) session.getAttribute(Constants.SESSION_USERROL);
                if (null == userRole) {
                    //通过用户ID读取用户信息和角色信息
                    user = userService.getUserAndRoleById(user.getUSER_ID());
                    //存入session
                    session.setAttribute(Constants.SESSION_USERROL, user);
                } else {
                    user = userRole;
                }
                String username = user.getUSERNAME();
                //获取用户角色
                Role role = user.getRole();
                //角色权限(菜单权限)
                String roleRights = role != null ? role.getRIGHTS() : "";
                //将角色权限存入session
                session.setAttribute(username + Constants.SESSION_ROLE_RIGHTS, roleRights);
                //放入用户名到session
                session.setAttribute(Constants.SESSION_USERNAME, username);
                //把用户的组织机构权限放到session里面
                this.setAllDepartmentIds(session, username);
                //菜单缓存
                List<Menu> allmenuList = this.getAttributeMenu(session, username, roleRights);
                //切换菜单
                List<Menu> menuList = this.changeMenuF(allmenuList, session, username, changeMenu);
                if (null == session.getAttribute(username + Constants.SESSION_QX)) {
                    //按钮权限放到session中
                    session.setAttribute(username + Constants.SESSION_QX, this.getUQX(username));
                }
                //更新登录IP
                this.getRemortIP(username);
                mv.setViewName("system/index/main");
                mv.addObject("user", user);
                mv.addObject("menuList", menuList);
            } else {
                //session失效后跳转登录页面
                mv.setViewName("system/index/login");
            }
        } catch (Exception e) {
            mv.setViewName("system/index/login");
            logger.error(e.getMessage(), e);
        }
        //读取系统名称
        pd.put("SYSNAME", Tools.readTxtFile(Constants.SYSNAME));
        mv.addObject("pd", pd);
        return mv;
    }

    /**
     * 菜单缓存
     *
     * @param session    session
     * @param username   用户名
     * @param roleRights 用户权限
     * @return 菜单列表
     * @throws Exception 可能抛出的异常
     */
    @SuppressWarnings("unchecked")
    @Nullable
    private List<Menu> getAttributeMenu(Session session, String username, String roleRights) throws Exception {
        List<Menu> allmenuList;
        List<Menu> returnMenuList;
        if (null == session.getAttribute(username + Constants.SESSION_allmenuList)) {
            //获取所有菜单
            allmenuList = menuService.listAllMenuQx("0");
            //根据角色权限获取本权限的菜单列表
            if (Tools.notEmpty(roleRights)) {
                returnMenuList = this.readMenu(allmenuList, roleRights);
                allmenuList = returnMenuList;
            }
            //菜单权限放入session中
            session.setAttribute(username + Constants.SESSION_allmenuList, allmenuList);
        } else {
            allmenuList = (List<Menu>) session.getAttribute(username + Constants.SESSION_allmenuList);
        }
        return allmenuList;
    }

    /**
     * 根据角色权限获取本权限的菜单列表(递归处理)
     *
     * @param menuList：传入的总菜单
     * @param roleRights：加密的权限字符串
     * @return 菜单列表
     */
    private List<Menu> readMenu(List<Menu> menuList, String roleRights) {
        for (Menu aMenuList : menuList) {
            aMenuList.setHasMenu(RightsHelper.testRights(roleRights, aMenuList.getMENU_ID()));
            //判断是否有此菜单权限
            if (aMenuList.isHasMenu()) {
                //是：继续排查其子菜单
                this.readMenu(aMenuList.getSubMenu(), roleRights);
            }
        }
        return menuList;
    }

    /**
     * 切换菜单处理
     *
     * @param allmenuList 所有菜单列表
     * @param session     session
     * @param username    用户名
     * @param changeMenu  修改菜单
     * @return 菜单列表
     */
    @SuppressWarnings("unchecked")
    private List<Menu> changeMenuF(List<Menu> allmenuList, Session session, String username, String changeMenu) {
        List<Menu> menuList;
        if (null == session.getAttribute(username + Constants.SESSION_menuList) || (Constants.YES.equals(changeMenu))) {
            List<Menu> menuList1 = Lists.newArrayList();
            List<Menu> menuList2 = Lists.newArrayList();
            //拆分菜单
            for (Menu menu : allmenuList) {
                if ("1".equals(menu.getMENU_TYPE())) {
                    menuList1.add(menu);
                } else {
                    menuList2.add(menu);
                }
            }
            session.removeAttribute(username + Constants.SESSION_menuList);
            if (Constants.TWO_STRING.equals(session.getAttribute(Constants.CHANGE_MENU))) {
                session.setAttribute(username + Constants.SESSION_menuList, menuList1);
                session.removeAttribute("changeMenu");
                session.setAttribute("changeMenu", "1");
                menuList = menuList1;
            } else {
                session.setAttribute(username + Constants.SESSION_menuList, menuList2);
                session.removeAttribute("changeMenu");
                session.setAttribute("changeMenu", "2");
                menuList = menuList2;
            }
        } else {
            menuList = (List<Menu>) session.getAttribute(username + Constants.SESSION_menuList);
        }
        return menuList;
    }

    /**
     * 把用户的组织机构权限放到session里面
     *
     * @param session  session
     * @param username 用户名
     * @throws Exception 可能抛出的异常
     */
    private void setAllDepartmentIds(Session session, String username) throws Exception {
        String departmentIds = "0", departmentId = "0";
        if (!Constants.ADMIN.equals(username)) {
            PageData pd = datajurService.getDepartmentIds(username);
            departmentIds = null == pd ? "无权" : pd.getString("DEPARTMENT_IDS");
            departmentId = null == pd ? "无权" : pd.getString("DEPARTMENT_ID");
        }
        //把用户的组织机构权限集合放到session里面
        session.setAttribute(Constants.DEPARTMENT_IDS, departmentIds);
        //把用户的最高组织机构权限放到session里面
        session.setAttribute(Constants.DEPARTMENT_ID, departmentId);
    }

    /**
     * 进入tab标签
     *
     * @return tab地址
     */
    @RequestMapping(value = "/tab")
    public String tab() {
        return "system/index/tab";
    }

    /**
     * 进入首页后的默认页面
     *
     * @return ModelAndView对象
     * @throws Exception 可能抛出的异常
     */
    @RequestMapping(value = "/login_default")
    public ModelAndView defaultPage() throws Exception {
        ModelAndView mv = this.getModelAndView();
        PageData pd = new PageData();
        //系统用户数
        pd.put("userCount", Integer.parseInt(userService.getUserCount("").get("userCount").toString()) - 1);
        //会员数
        pd.put("appUserCount", Integer.parseInt(appuserService.getAppUserCount("").get("appUserCount").toString()));
        mv.addObject("pd", pd);
        mv.setViewName("system/index/default");
        return mv;
    }

    /**
     * 用户注销
     *
     * @return ModelAndView对象
     * @throws Exception 可能抛出的异常
     */
    @RequestMapping(value = "/logout")
    public ModelAndView logout() throws Exception {
        //当前登录的用户名
        String username = Jurisdiction.getUsername();
        logBefore(logger, username + "退出系统");
        fHlogManager.save(username, "退出");
        ModelAndView mv = this.getModelAndView();
        PageData pd;
        //以下清除session缓存
        Session session = Jurisdiction.getSession();
        session.removeAttribute(Constants.SESSION_USER);
        session.removeAttribute(username + Constants.SESSION_ROLE_RIGHTS);
        session.removeAttribute(username + Constants.SESSION_allmenuList);
        session.removeAttribute(username + Constants.SESSION_menuList);
        session.removeAttribute(username + Constants.SESSION_QX);
        session.removeAttribute(Constants.SESSION_USERPDS);
        session.removeAttribute(Constants.SESSION_USERNAME);
        session.removeAttribute(Constants.SESSION_USERROL);
        session.removeAttribute("changeMenu");
        session.removeAttribute("DEPARTMENT_IDS");
        session.removeAttribute("DEPARTMENT_ID");
        //shiro销毁登录
        Subject subject = SecurityUtils.getSubject();
        subject.logout();
        pd = this.getPageData();
        pd.put("msg", pd.getString("msg"));
        //设置登录页面的配置参数
        PageData pdd = this.setLoginPd(pd);
        mv.setViewName("system/index/login");
        mv.addObject("pd", pdd);
        return mv;
    }

    /**
     * 设置登录页面的配置参数
     *
     * @param pd PageData对象
     * @return PageData对象
     */
    private PageData setLoginPd(PageData pd) {
        //读取系统名称
        pd.put("SYSNAME", Tools.readTxtFile(Constants.SYSNAME));
        //读取登录页面配置
        String strLOGINEDIT = Tools.readTxtFile(Constants.LOGINEDIT);
        if (null != strLOGINEDIT && !"".equals(strLOGINEDIT)) {
            String[] strLo = strLOGINEDIT.split(",fh,");
            if (strLo.length == Constants.TWO) {
                pd.put("isZhuce", strLo[0]);
                pd.put("isMusic", strLo[1]);
            }
        }
        try {
            //登录背景图片
            List<PageData> listImg = loginimgService.listAll(pd);
            pd.put("listImg", listImg);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pd;
    }

    /**
     * 获取用户权限
     *
     * @param username 用户名
     * @return 用户权限
     */
    private Map<String, String> getUQX(String username) {
        PageData pd = new PageData();
        Map<String, String> map = Maps.newHashMap();
        try {
            pd.put(Constants.SESSION_USERNAME, username);
            //获取角色ID
            pd.put("ROLE_ID", userService.findByUsername(pd).get("ROLE_ID").toString());
            //获取角色信息
            pd = roleService.findObjectById(pd);
            map.put("adds", pd.getString("ADD_QX"));
            map.put("dels", pd.getString("DEL_QX"));
            map.put("edits", pd.getString("EDIT_QX"));
            map.put("chas", pd.getString("CHA_QX"));
            List<PageData> buttonQXnamelist;
            if (Constants.ADMIN.equals(username)) {
                //admin用户拥有所有按钮权限
                buttonQXnamelist = fhbuttonService.listAll(pd);
            } else {
                //此角色拥有的按钮权限标识列表
                buttonQXnamelist = buttonrightsService.listAllBrAndQxname(pd);
            }
            for (PageData aButtonQXnamelist : buttonQXnamelist) {
                //按钮权限
                map.put(aButtonQXnamelist.getString("QX_NAME"), "1");
            }
        } catch (Exception e) {
            logger.error(e.toString(), e);
        }
        return map;
    }

    /**
     * 更新登录用户的IP
     *
     * @param username 用户名
     * @throws Exception 可能抛出的异常
     */
    private void getRemortIP(String username) throws Exception {
        PageData pd = new PageData();
        HttpServletRequest request = this.getRequest();
        String ip;
        if (request.getHeader(Constants.HTTP_FORWARD) == null) {
            ip = request.getRemoteAddr();
        } else {
            ip = request.getHeader("x-forwarded-for");
        }
        pd.put("USERNAME", username);
        pd.put("IP", ip);
        userService.saveIP(pd);
    }

}
