package com.lph.controller.system.user;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.lph.controller.base.BaseController;
import com.lph.entity.Page;
import com.lph.entity.system.Role;
import com.lph.service.system.fhlog.FHlogManager;
import com.lph.service.system.role.RoleManager;
import com.lph.service.system.user.UserManager;
import com.lph.util.*;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 类名称：UserController
 *
 * @author lvpenghui
 * @since 2019-4-11 10:58:57
 */
@Controller
@RequestMapping(value = "/user")
public class UserController extends BaseController {

    private String menuUrl = "user/listUsers.do";
    @Resource(name = "userService")
    private UserManager userService;
    @Resource(name = "roleService")
    private RoleManager roleService;
    @Resource(name = "fhlogService")
    private FHlogManager fHlogManager;

    /**
     * 显示用户列表
     *
     * @param page 分页
     * @return ModelAndView对象
     * @throws Exception 可能抛出的异常
     */
    @RequestMapping(value = "/listUsers")
    public ModelAndView listUsers(Page page) throws Exception {
        ModelAndView mv = this.getModelAndView();
        PageData pd = this.getPageData();
        //关键词检索条件
        String keywords = pd.getString("keywords");
        if (StringUtils.isNotEmpty(keywords)) {
            pd.put("keywords", keywords.trim());
        }
        //开始时间
        String lastLoginStart = pd.getString("lastLoginStart");
        //结束时间
        String lastLoginEnd = pd.getString("lastLoginEnd");
        if (lastLoginStart != null && !"".equals(lastLoginStart)) {
            pd.put("lastLoginStart", lastLoginStart + " 00:00:00");
        }
        if (lastLoginEnd != null && !"".equals(lastLoginEnd)) {
            pd.put("lastLoginEnd", lastLoginEnd + " 00:00:00");
        }
        page.setPd(pd);
        //列出用户列表
        List<PageData> userList = userService.listUsers(page);
        pd.put("ROLE_ID", "1");
        //列出所有系统用户角色
        List<Role> roleList = roleService.listAllRolesByPId(pd);
        mv.setViewName("system/user/user_list");
        mv.addObject("userList", userList);
        mv.addObject("roleList", roleList);
        mv.addObject("pd", pd);
        //按钮权限
        mv.addObject("QX", Jurisdiction.getHC());
        return mv;
    }

    /**
     * 删除用户
     *
     * @param out out
     * @throws Exception 可能抛出的异常
     */
    @RequestMapping(value = "/deleteU")
    public void deleteU(PrintWriter out) throws Exception {
        if (!Jurisdiction.buttonJurisdiction(menuUrl, Constants.DELE)) {
            return;
        } //校验权限
        logBefore(logger, Jurisdiction.getUsername() + "删除user");
        PageData pd = this.getPageData();
        userService.deleteU(pd);
        fHlogManager.save(Jurisdiction.getUsername(), "删除系统用户：" + pd);
        out.write("success");
        out.close();
    }

    /**
     * 去新增用户页面
     *
     * @return ModelAndView对象
     * @throws Exception 可能抛出的异常
     */
    @RequestMapping(value = "/goAddU")
    public ModelAndView goAddU() throws Exception {
        if (!Jurisdiction.buttonJurisdiction(menuUrl, Constants.ADDD)) {
            return null;
        } //校验权限
        ModelAndView mv = this.getModelAndView();
        PageData pd = this.getPageData();
        pd.put("ROLE_ID", "1");
        //列出所有系统用户角色
        List<Role> roleList = roleService.listAllRolesByPId(pd);
        mv.setViewName("system/user/user_edit");
        mv.addObject("msg", "saveU");
        mv.addObject("pd", pd);
        mv.addObject("roleList", roleList);
        return mv;
    }

    /**
     * 保存用户
     *
     * @return ModelAndView对象
     * @throws Exception 可能抛出的异常
     */
    @RequestMapping(value = "/saveU")
    public ModelAndView saveU() throws Exception {
        //校验权限
        if (!Jurisdiction.buttonJurisdiction(menuUrl, Constants.ADDD)) {
            return null;
        }
        logBefore(logger, Jurisdiction.getUsername() + "新增user");
        ModelAndView mv = this.getModelAndView();
        PageData pd = this.getPageData();
        pd.put("USER_ID", this.get32UUID());
        pd.put("LAST_LOGIN", "");
        pd.put("IP", "");
        pd.put("STATUS", "0");
        pd.put("SKIN", "default");
        pd.put("RIGHTS", "");
        //密码加密
        pd.put("PASSWORD", new SimpleHash("SHA-1", pd.getString("USERNAME"), pd.getString("PASSWORD")).toString());
        //判断用户名是否存在
        if (null == userService.findByUsername(pd)) {
            //执行保存
            userService.saveU(pd);
            fHlogManager.save(Jurisdiction.getUsername(), "新增系统用户：" + pd.getString("USERNAME"));
            mv.addObject("msg", "success");
        } else {
            mv.addObject("msg", "failed");
        }
        mv.setViewName("save_result");
        return mv;
    }

    /**
     * 判断用户名是否存在
     *
     * @return 用户是否存在结果
     */
    @RequestMapping(value = "/hasU")
    @ResponseBody
    public Object hasU() {
        Map<String, String> map = Maps.newHashMap();
        String errInfo = "success";
        PageData pd = this.getPageData();
        try {
            if (userService.findByUsername(pd) != null) {
                errInfo = "error";
            }
        } catch (Exception e) {
            logger.error(e.toString(), e);
        }
        //返回结果
        map.put("result", errInfo);
        return AppUtil.returnObject(new PageData(), map);
    }

    /**
     * 判断邮箱是否存在
     *
     * @return 邮箱是否存在返回结果
     */
    @RequestMapping(value = "/hasE")
    @ResponseBody
    public Object hasE() {
        Map<String, String> map = Maps.newHashMap();
        String errInfo = "success";
        PageData pd = this.getPageData();
        try {
            if (userService.findByUE(pd) != null) {
                errInfo = "error";
            }
        } catch (Exception e) {
            logger.error(e.toString(), e);
        }
        //返回结果
        map.put("result", errInfo);
        return AppUtil.returnObject(new PageData(), map);
    }

    /**
     * 判断编码是否存在
     *
     * @return 编码是否存在
     */
    @RequestMapping(value = "/hasN")
    @ResponseBody
    public Object hasN() {
        Map<String, String> map = Maps.newHashMap();
        String errInfo = "success";
        PageData pd = this.getPageData();
        try {
            if (userService.findByUN(pd) != null) {
                errInfo = "error";
            }
        } catch (Exception e) {
            logger.error(e.toString(), e);
        }
        //返回结果
        map.put("result", errInfo);
        return AppUtil.returnObject(new PageData(), map);
    }

    /**
     * 去修改用户页面(系统用户列表修改)
     *
     * @return ModelAndView对象
     * @throws Exception 可能抛出的异常
     */
    @RequestMapping(value = "/goEditU")
    public ModelAndView goEditU() throws Exception {
        if (!Jurisdiction.buttonJurisdiction(menuUrl, Constants.EDIT)) {
            return null;
        } //校验权限
        ModelAndView mv = this.getModelAndView();
        PageData pd = this.getPageData();
        //不能修改admin用户
        if (Constants.ONE_STRING.equals(pd.getString(Constants.USER_ID))) {
            return null;
        }
        pd.put("ROLE_ID", "1");
        //列出所有系统用户角色
        List<Role> roleList = roleService.listAllRolesByPId(pd);
        mv.addObject("fx", "user");
        //根据ID读取
        pd = userService.findById(pd);
        mv.setViewName("system/user/user_edit");
        mv.addObject("msg", "editU");
        mv.addObject("pd", pd);
        mv.addObject("roleList", roleList);
        return mv;
    }

    /**
     * 去修改用户页面(个人修改)
     *
     * @return ModelAndView对象
     * @throws Exception 可能抛出的异常
     */
    @RequestMapping(value = "/goEditMyU")
    public ModelAndView goEditMyU() throws Exception {
        ModelAndView mv = this.getModelAndView();
        PageData pd = this.getPageData();
        mv.addObject("fx", "head");
        pd.put("ROLE_ID", "1");
        //列出所有系统用户角色
        List<Role> roleList = roleService.listAllRolesByPId(pd);
        pd.put("USERNAME", Jurisdiction.getUsername());
        //根据用户名读取
        pd = userService.findByUsername(pd);
        mv.setViewName("system/user/user_edit");
        mv.addObject("msg", "editU");
        mv.addObject("pd", pd);
        mv.addObject("roleList", roleList);
        return mv;
    }

    /**
     * 查看用户
     *
     * @return ModelAndView对象
     * @throws Exception 可能抛出的异常
     */
    @RequestMapping(value = "/view")
    public ModelAndView view() throws Exception {
        if (!Jurisdiction.buttonJurisdiction(menuUrl, Constants.SRCH)) {
            return null;
        } //校验权限
        ModelAndView mv = this.getModelAndView();
        PageData pd = this.getPageData();
        if (Constants.ADMIN.equals(pd.getString(Constants.USERNAME))) {
            return null;
        }    //不能查看admin用户
        pd.put("ROLE_ID", "1");
        //列出所有系统用户角色
        List<Role> roleList = roleService.listAllRolesByPId(pd);
        //根据ID读取
        pd = userService.findByUsername(pd);
        mv.setViewName("system/user/user_view");
        mv.addObject("msg", "editU");
        mv.addObject("pd", pd);
        mv.addObject("roleList", roleList);
        return mv;
    }

    /**
     * 去修改用户页面(在线管理页面打开)
     *
     * @return ModelAndView对象
     * @throws Exception 可能抛出的异常
     */
    @RequestMapping(value = "/goEditUfromOnline")
    public ModelAndView goEditUfromOnline() throws Exception {
        ModelAndView mv = this.getModelAndView();
        PageData pd = this.getPageData();
        //不能查看admin用户
        if (Constants.ADMIN.equals(pd.getString(Constants.USERNAME))) {
            return null;
        }
        pd.put("ROLE_ID", "1");
        //列出所有系统用户角色
        List<Role> roleList = roleService.listAllRolesByPId(pd);
        //根据ID读取
        pd = userService.findByUsername(pd);
        mv.setViewName("system/user/user_edit");
        mv.addObject("msg", "editU");
        mv.addObject("pd", pd);
        mv.addObject("roleList", roleList);
        return mv;
    }

    /**
     * 修改用户
     */
    @RequestMapping(value = "/editU")
    public ModelAndView editU() throws Exception {
        logBefore(logger, Jurisdiction.getUsername() + "修改ser");
        ModelAndView mv = this.getModelAndView();
        PageData pd = this.getPageData();
        //如果当前登录用户修改用户资料提交的用户名非本人
        if (!Jurisdiction.getUsername().equals(pd.getString(Constants.USERNAME))) {
            if (!Jurisdiction.buttonJurisdiction(menuUrl, Constants.SRCH)) {
                return null;
            }  //校验权限 判断当前操作者有无用户管理查看权限
            if (!Jurisdiction.buttonJurisdiction(menuUrl, Constants.EDIT)) {
                return null;
            } //校验权限判断当前操作者有无用户管理修改权限
            //非admin用户不能修改admin
            if (Constants.ADMIN.equals(pd.getString(Constants.USERNAME)) && !Constants.ADMIN.equals(Jurisdiction.getUsername())) {
                return null;
            }
        } else {
            //如果当前登录用户修改用户资料提交的用户名是本人，则不能修改本人的角色ID
            //对角色ID还原本人角色ID
            pd.put("ROLE_ID", userService.findByUsername(pd).getString("ROLE_ID"));
        }
        if (StringUtils.isNotEmpty(pd.getString(Constants.PASSWORD))) {
            pd.put("PASSWORD", new SimpleHash("SHA-1", pd.getString("USERNAME"), pd.getString("PASSWORD")).toString());
        }
        //执行修改
        userService.editU(pd);
        fHlogManager.save(Jurisdiction.getUsername(), "修改系统用户：" + pd.getString("USERNAME"));
        mv.addObject("msg", "success");
        mv.setViewName("save_result");
        return mv;
    }

    /**
     * 批量删除
     *
     * @return 批量删除返回结果
     * @throws Exception 可能出现的异常
     */
    @RequestMapping(value = "/deleteAllU")
    @ResponseBody
    public Object deleteAllU() throws Exception {
        if (!Jurisdiction.buttonJurisdiction(menuUrl, Constants.DELE)) {
            return null;
        } //校验权限
        logBefore(logger, Jurisdiction.getUsername() + "批量删除user");
        fHlogManager.save(Jurisdiction.getUsername(), "批量删除user");
        PageData pd = this.getPageData();
        Map<String, Object> map = Maps.newHashMap();
        List<PageData> pdList = Lists.newArrayList();
        String userIds = pd.getString("USER_IDS");
        if (StringUtils.isNotEmpty(userIds)) {
            String[] userDatas = userIds.split(",");
            userService.deleteAllU(userDatas);
            pd.put("msg", "ok");
        } else {
            pd.put("msg", "no");
        }
        pdList.add(pd);
        map.put("list", pdList);
        return AppUtil.returnObject(pd, map);
    }

    /**
     * 导出用户信息到EXCEL
     *
     * @return ModelAndView对象
     * @throws Exception 可能出现的异常
     */
    @RequestMapping(value = "/excel")
    public ModelAndView exportExcel() throws Exception {
        fHlogManager.save(Jurisdiction.getUsername(), "导出用户信息到EXCEL");
        ModelAndView mv = this.getModelAndView();
        PageData pd = this.getPageData();
        try {
            if (Jurisdiction.buttonJurisdiction(menuUrl, Constants.SRCH)) {
                //关键词检索条件
                String keywords = pd.getString("keywords");
                if (StringUtils.isNotEmpty(keywords)) {
                    pd.put("keywords", keywords.trim());
                }
                //开始时间
                String lastLoginStart = pd.getString("lastLoginStart");
                //结束时间
                String lastLoginEnd = pd.getString("lastLoginEnd");
                if (lastLoginStart != null && !"".equals(lastLoginStart)) {
                    pd.put("lastLoginStart", lastLoginStart + " 00:00:00");
                }
                if (lastLoginEnd != null && !"".equals(lastLoginEnd)) {
                    pd.put("lastLoginEnd", lastLoginEnd + " 00:00:00");
                }
                Map<String, Object> dataMap = Maps.newHashMap();
                List<String> titles = new ArrayList<String>();
                titles.add("用户名");
                titles.add("编号");
                titles.add("姓名");
                titles.add("职位");
                titles.add("手机");
                titles.add("邮箱");
                titles.add("最近登录");
                titles.add("上次登录IP");
                dataMap.put("titles", titles);
                List<PageData> userList = userService.listAllUser(pd);
                List<PageData> varList = new ArrayList<PageData>();
                for (PageData anUserList : userList) {
                    PageData vpd = new PageData();
                    vpd.put("var1", anUserList.getString("USERNAME"));
                    vpd.put("var2", anUserList.getString("NUMBER"));
                    vpd.put("var3", anUserList.getString("NAME"));
                    vpd.put("var4", anUserList.getString("ROLE_NAME"));
                    vpd.put("var5", anUserList.getString("PHONE"));
                    vpd.put("var6", anUserList.getString("EMAIL"));
                    vpd.put("var7", anUserList.getString("LAST_LOGIN"));
                    vpd.put("var8", anUserList.getString("IP"));
                    varList.add(vpd);
                }
                dataMap.put("varList", varList);
                //执行excel操作
                ObjectExcelView erv = new ObjectExcelView();
                mv = new ModelAndView(erv, dataMap);
            }
        } catch (Exception e) {
            logger.error(e.toString(), e);
        }
        return mv;
    }

    /**
     * 打开上传EXCEL页面
     *
     * @return ModelAndView对象
     */
    @RequestMapping(value = "/goUploadExcel")
    public ModelAndView goUploadExcel() {
        ModelAndView mv = this.getModelAndView();
        mv.setViewName("system/user/uploadexcel");
        return mv;
    }

    /**
     * 下载模版
     *
     * @param response HttpServletResponse
     * @throws Exception 可能出现的异常
     */
    @RequestMapping(value = "/downExcel")
    public void downExcel(HttpServletResponse response) throws Exception {
        FileDownload.fileDownload(response, PathUtil.getClasspath() + Const.FILEPATHFILE + "Users.xls", "Users.xls");
    }

    /**
     * 从EXCEL导入到数据库
     *
     * @param file MultipartFile对象
     * @return ModelAndView对象
     * @throws Exception 可能抛出的异常
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/readExcel")
    public ModelAndView readExcel(
            @RequestParam(value = "excel", required = false) MultipartFile file
    ) throws Exception {
        fHlogManager.save(Jurisdiction.getUsername(), "从EXCEL导入到数据库");
        ModelAndView mv = this.getModelAndView();
        PageData pd = new PageData();
        if (!Jurisdiction.buttonJurisdiction(menuUrl, Constants.ADDD)) {
            return null;
        }
        if (null != file && !file.isEmpty()) {
            //文件上传路径
            String filePath = PathUtil.getClasspath() + Const.FILEPATHFILE;
            //执行上传
            String fileName = FileUpload.fileUp(file, filePath, "userexcel");
            //执行读EXCEL操作,读出的数据导入List 2:从第3行开始；0:从第A列开始；0:第0个sheet
            List<PageData> listPd = (List) ObjectExcelRead.readExcel(filePath, fileName, 2, 0, 0);
            /*存入数据库操作======================================*/
            //权限
            pd.put("RIGHTS", "");
            //最后登录时间
            pd.put("LAST_LOGIN", "");
            pd.put("IP", "");
            pd.put("STATUS", "0");
            //默认皮肤
            pd.put("SKIN", "default");
            pd.put("ROLE_ID", "1");
            //列出所有系统用户角色
            List<Role> roleList = roleService.listAllRolesByPId(pd);
            //设置角色ID为随便第一个
            pd.put("ROLE_ID", roleList.get(0).getROLE_ID());
            // var0 :编号  var1 :姓名  var2 :手机  var3 :邮箱  var4 :备注
            for (PageData aListPd : listPd) {
                pd.put("USER_ID", this.get32UUID());
                pd.put("NAME", aListPd.getString("var1"));

                //根据姓名汉字生成全拼
                String username = GetPinyin.getPingYin(aListPd.getString("var1"));
                pd.put("USERNAME", username);
                //判断用户名是否重复
                if (userService.findByUsername(pd) != null) {
                    username = GetPinyin.getPingYin(aListPd.getString("var1")) + Tools.getRandomNum();
                    pd.put("USERNAME", username);
                }
                //备注
                pd.put("BZ", aListPd.getString("var4"));
                //邮箱格式不对就跳过
                if (Tools.checkEmail(aListPd.getString("var3"))) {
                    pd.put("EMAIL", aListPd.getString("var3"));
                    //邮箱已存在就跳过
                    if (userService.findByUE(pd) != null) {
                        continue;
                    }
                } else {
                    continue;
                }
                //编号已存在就跳过
                pd.put("NUMBER", aListPd.getString("var0"));
                //手机号
                pd.put("PHONE", aListPd.getString("var2"));
                //默认密码123
                pd.put("PASSWORD", new SimpleHash("SHA-1", username, "123").toString());
                if (userService.findByUN(pd) != null) {
                    continue;
                }
                userService.saveU(pd);
            }
            mv.addObject("msg", "success");
        }
        mv.setViewName("save_result");
        return mv;
    }

    /**
     * 显示用户列表(弹窗选择用)
     *
     * @param page 分页
     * @return ModelAndView对象
     * @throws Exception 可能抛出的异常
     */
    @RequestMapping(value = "/listUsersForWindow")
    public ModelAndView listUsersForWindow(Page page) throws Exception {
        ModelAndView mv = this.getModelAndView();
        PageData pd = this.getPageData();
        //关键词检索条件
        String keywords = pd.getString("keywords");
        if (StringUtils.isNotEmpty(keywords)) {
            pd.put("keywords", keywords.trim());
        }
        //开始时间
        String lastLoginStart = pd.getString("lastLoginStart");
        //结束时间
        String lastLoginEnd = pd.getString("lastLoginEnd");
        if (lastLoginStart != null && !"".equals(lastLoginStart)) {
            pd.put("lastLoginStart", lastLoginStart + " 00:00:00");
        }
        if (lastLoginEnd != null && !"".equals(lastLoginEnd)) {
            pd.put("lastLoginEnd", lastLoginEnd + " 00:00:00");
        }
        page.setPd(pd);
        //列出用户列表(弹窗选择用)
        List<PageData> userList = userService.listUsersBystaff(page);
        pd.put("ROLE_ID", "1");
        //列出所有系统用户角色
        List<Role> roleList = roleService.listAllRolesByPId(pd);
        mv.setViewName("system/user/window_user_list");
        mv.addObject("userList", userList);
        mv.addObject("roleList", roleList);
        mv.addObject("pd", pd);
        //按钮权限
        mv.addObject("QX", Jurisdiction.getHC());
        return mv;
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        binder.registerCustomEditor(Date.class, new CustomDateEditor(format, true));
    }

}
