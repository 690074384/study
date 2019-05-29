package com.lph.controller.system.buttonrights;

import com.google.common.collect.Maps;
import com.lph.controller.base.BaseController;
import com.lph.entity.system.Role;
import com.lph.service.system.buttonrights.ButtonrightsManager;
import com.lph.service.system.fhbutton.FhbuttonManager;
import com.lph.service.system.fhlog.FhLogManager;
import com.lph.service.system.role.RoleManager;
import com.lph.util.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 说明：按钮权限
 *
 * @author lvpenghui
 * @since 2019-4-9 18:10:01
 */
@Controller
@RequestMapping(value = "/buttonrights")
public class ButtonrightsController extends BaseController {

    @Resource(name = "buttonrightsService")
    private ButtonrightsManager buttonrightsService;
    @Resource(name = "roleService")
    private RoleManager roleService;
    @Resource(name = "fhbuttonService")
    private FhbuttonManager fhbuttonService;
    @Resource(name = "fhlogService")
    private FhLogManager fHlogManager;

    /**
     * 列表
     *
     * @throws Exception 可能抛出的异常
     */
    @RequestMapping(value = "/list")
    public ModelAndView list() throws Exception {
        logBefore(logger, Jurisdiction.getUsername() + "列表Buttonrights");
        ModelAndView mv = this.getModelAndView();
        PageData pd = this.getPageData();
        String type = pd.getString("type");
        type = Tools.isEmpty(type) ? "0" : type;
        if (StringUtils.isEmpty(pd.getString(Constants.ROLE_ID))) {
            //默认列出第一组角色(初始设计系统用户和会员组不能删除)
            pd.put("ROLE_ID", "1");
        }
        PageData fpd = new PageData();
        fpd.put("ROLE_ID", "0");
        //列出组(页面横向排列的一级组)
        List<Role> roleList = roleService.listAllRolesByPId(fpd);
        //列出此组下架角色
        List<Role> nodeRoleList = roleService.listAllRolesByPId(pd);
        //列出所有按钮
        List<PageData> buttonlist = fhbuttonService.listAll(pd);
        //列出所有角色按钮关联数据
        List<PageData> roleFhbuttonlist = buttonrightsService.listAll(pd);
        //取得点击的角色组(横排的)
        pd = roleService.findObjectById(pd);
        mv.addObject("pd", pd);
        mv.addObject("roleList", roleList);
        mv.addObject("roleList_z", nodeRoleList);
        mv.addObject("buttonlist", buttonlist);
        mv.addObject("roleFhbuttonlist", roleFhbuttonlist);
        //按钮权限
        mv.addObject("QX", Jurisdiction.getHC());
        if (Constants.TWO_STRING.equals(type)) {
            mv.setViewName("system/buttonrights/buttonrights_list_r");
        } else {
            mv.setViewName("system/buttonrights/buttonrights_list");
        }

        return mv;
    }

    /**
     * 点击按钮处理关联表
     *
     * @return 点击按钮处理更新结果
     * @throws Exception 可能抛出的异常
     */
    @RequestMapping(value = "/upRb")
    @ResponseBody
    public Object updateRolebuttonrightd() throws Exception {
        // 菜单地址(权限用)
        String menuUrl = "buttonrights/list.do";
        if (!Jurisdiction.buttonJurisdiction(menuUrl, Constants.EDIT)) {
            return null;
        } //校验权限
        logBefore(logger, Jurisdiction.getUsername() + "分配按钮权限");
        Map<String, String> map = Maps.newHashMap();
        PageData pd = this.getPageData();
        String errInfo = "success";
        //判断关联表是否有数据 是:删除/否:新增
        if (null != buttonrightsService.findById(pd)) {
            buttonrightsService.delete(pd);
            fHlogManager.save(Jurisdiction.getUsername(), "删除按钮权限" + pd);
        } else {
            pd.put("RB_ID", this.get32UUID());
            buttonrightsService.save(pd);
            fHlogManager.save(Jurisdiction.getUsername(), "新增按钮权限pd" + pd);
        }
        map.put("result", errInfo);
        return AppUtil.returnObject(new PageData(), map);
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        binder.registerCustomEditor(Date.class, new CustomDateEditor(format, true));
    }
}
