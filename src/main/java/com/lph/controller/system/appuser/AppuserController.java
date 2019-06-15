package com.lph.controller.system.appuser;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.lph.controller.base.BaseController;
import com.lph.entity.Page;
import com.lph.entity.system.Role;
import com.lph.service.system.appuser.AppuserManager;
import com.lph.service.system.role.RoleManager;
import com.lph.util.*;
import com.lph.util.restful.JavaNetUrlRestfulClient;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 类名称：会员管理
 *
 * @author lvpenghui
 * @since 2019-4-9 17:48:07
 */
@Controller
@RequestMapping(value = "/happuser")
public class AppuserController extends BaseController {
	
	/**
	 * 菜单地址(权限用)
	 */
	private String menuUrl = "happuser/listUsers.do";
	@Resource(name = "appuserService")
	private AppuserManager appuserService;
	@Resource(name = "roleService")
	private RoleManager roleService;
	
	/**
	 * 显示用户列表
	 *
	 * @param page 分页
	 * @return ModelAndView对象
	 */
	@RequestMapping(value = "/listUsers")
	public ModelAndView listUsers(Page page) {
		ModelAndView mv = this.getModelAndView();
		PageData pd;
		try {
			pd = this.getPageData();
			//检索条件 关键词
			String keywords = pd.getString("keywords");
			if (null != keywords && !"".equals(keywords)) {
				pd.put("keywords", keywords.trim());
			}
			page.setPd(pd);
			//列出会员列表
			List<PageData> userList = appuserService.listPdPageUser(page);
			pd.put("ROLE_ID", "2");
			//列出会员组角色
			List<Role> roleList = roleService.listAllRolesByPId(pd);
			mv.setViewName("system/appuser/appuser_list");
			mv.addObject("userList", userList);
			mv.addObject("roleList", roleList);
			mv.addObject("pd", pd);
			//按钮权限
			mv.addObject("QX", Jurisdiction.getHC());
		} catch (Exception e) {
			logger.error(e.toString(), e);
		}
		return mv;
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
		pd.put("ROLE_ID", "2");
		//列出会员组角色
		List<Role> roleList = roleService.listAllRolesByPId(pd);
		mv.setViewName("system/appuser/appuser_edit");
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
		if (!Jurisdiction.buttonJurisdiction(menuUrl, Constants.ADDD)) {
			return null;
		} //校验权限
		logBefore(logger, Jurisdiction.getUsername() + "新增会员");
		ModelAndView mv = this.getModelAndView();
		PageData pd = this.getPageData();
		pd.put("USER_ID", this.get32UUID());
		pd.put("RIGHTS", "");
		//最后登录时间
		pd.put("LAST_LOGIN", "");
		//IP
		pd.put("IP", "");
		pd.put("PASSWORD", MD5.md5(pd.getString("PASSWORD")));
		//判断新增权限
		if (null == appuserService.findByUsername(pd)) {
			appuserService.saveU(pd);
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
	 * @return 用户名是否存在结果
	 */
	@RequestMapping(value = "/hasU")
	@ResponseBody
	public Object hasU() {
		Map<String, String> map = Maps.newHashMap();
		String errInfo = "success";
		PageData pd;
		try {
			pd = this.getPageData();
			if (appuserService.findByUsername(pd) != null) {
				errInfo = "error";
			}
		} catch (Exception e) {
			logger.error("判断用户名是否存在出现异常：", e);
		}
		//返回结果
		map.put("result", errInfo);
		return AppUtil.returnObject(new PageData(), map);
	}
	
	/**
	 * 判断邮箱是否存在
	 *
	 * @return 判断邮箱是否存在返回结果
	 */
	@RequestMapping(value = "/hasE")
	@ResponseBody
	public Object hasE() {
		Map<String, String> map = Maps.newHashMap();
		String errInfo = "success";
		PageData pd;
		try {
			pd = this.getPageData();
			if (appuserService.findByEmail(pd) != null) {
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
	 * @return 判断编码是否存在返回结果
	 */
	@RequestMapping(value = "/hasN")
	@ResponseBody
	public Object hasN() {
		Map<String, String> map = Maps.newHashMap();
		String errInfo = "success";
		PageData pd;
		try {
			pd = this.getPageData();
			if (appuserService.findByNumber(pd) != null) {
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
		logBefore(logger, Jurisdiction.getUsername() + "删除会员");
		PageData pd = this.getPageData();
		appuserService.deleteU(pd);
		out.write("success");
		out.close();
	}
	
	/**
	 * 修改用户
	 *
	 * @return 用户修改结果对象
	 * @throws Exception 可能抛出的异常
	 */
	@RequestMapping(value = "/editU")
	public ModelAndView editU() throws Exception {
		if (!Jurisdiction.buttonJurisdiction(menuUrl, Constants.EDIT)) {
			return null;
		} //校验权限
		logBefore(logger, Jurisdiction.getUsername() + "修改会员");
		ModelAndView mv = this.getModelAndView();
		PageData pd = this.getPageData();
		if (StringUtils.isNotEmpty(pd.getString(Constants.PASSWORD))) {
			pd.put("PASSWORD", MD5.md5(pd.getString("PASSWORD")));
		}
		appuserService.editU(pd);
		mv.addObject("msg", "success");
		mv.setViewName("save_result");
		return mv;
	}
	
	/**
	 * 去修改用户页面
	 *
	 * @return ModelAndView对象
	 */
	@RequestMapping(value = "/goEditU")
	public ModelAndView goEditU() {
		if (!Jurisdiction.buttonJurisdiction(menuUrl, Constants.SRCH)) {
			return null;
		} //校验权限
		ModelAndView mv = this.getModelAndView();
		PageData pd = this.getPageData();
		try {
			pd.put("ROLE_ID", "2");
			//列出会员组角色
			List<Role> roleList = roleService.listAllRolesByPId(pd);
			//根据ID读取
			pd = appuserService.findByUiId(pd);
			mv.setViewName("system/appuser/appuser_edit");
			mv.addObject("msg", "editU");
			mv.addObject("pd", pd);
			mv.addObject("roleList", roleList);
		} catch (Exception e) {
			logger.error(e.toString(), e);
		}
		return mv;
	}
	
	/**
	 * 批量删除
	 *
	 * @return 删除全部用户结果
	 */
	@RequestMapping(value = "/deleteAllU")
	@ResponseBody
	public Object deleteAllU() {
		if (!Jurisdiction.buttonJurisdiction(menuUrl, Constants.DELE)) {
			return null;
		}
		//校验权限
		logBefore(logger, Jurisdiction.getUsername() + "批量删除会员");
		PageData pd = new PageData();
		Map<String, Object> map = Maps.newHashMap();
		try {
			pd = this.getPageData();
			List<PageData> pdList = Lists.newArrayList();
			String userIds = pd.getString("USER_IDS");
			if (null != userIds && !"".equals(userIds)) {
				String[] allDatas = userIds.split(Constants.COMMA);
				appuserService.deleteAllU(allDatas);
				pd.put("msg", "ok");
			} else {
				pd.put("msg", "no");
			}
			pdList.add(pd);
			map.put("list", pdList);
		} catch (Exception e) {
			logger.error(e.toString(), e);
		} finally {
			logAfter(logger);
		}
		return AppUtil.returnObject(pd, map);
	}
	
	/**
	 * 导出会员信息到excel
	 *
	 * @return ModelAndView对象
	 */
	@RequestMapping(value = "/excel")
	public ModelAndView exportExcel() {
		logBefore(logger, Jurisdiction.getUsername() + "导出会员资料");
		ModelAndView mv = this.getModelAndView();
		PageData pd = this.getPageData();
		try {
			if (Jurisdiction.buttonJurisdiction(menuUrl, Constants.SRCH)) {
				String keywords = pd.getString("keywords");
				if (null != keywords && !"".equals(keywords)) {
					pd.put("keywords", keywords.trim());
				}
				String lastLoginStart = pd.getString("lastLoginStart");
				String lastLoginEnd = pd.getString("lastLoginEnd");
				if (lastLoginStart != null && !"".equals(lastLoginStart)) {
					pd.put("lastLoginStart", lastLoginStart + " 00:00:00");
				}
				if (lastLoginEnd != null && !"".equals(lastLoginEnd)) {
					pd.put("lastLoginEnd", lastLoginEnd + " 00:00:00");
				}
				Map<String, Object> dataMap = Maps.newHashMap();
				List<String> titles = Lists.newArrayList();
				titles.add("用户名");
				titles.add("编号");
				titles.add("姓名");
				titles.add("手机号");
				titles.add("身份证号");
				titles.add("等级");
				titles.add("邮箱");
				titles.add("最近登录");
				titles.add("到期时间");
				titles.add("上次登录IP");
				dataMap.put("titles", titles);
				List<PageData> userList = appuserService.listAllUser(pd);
				List<PageData> varList = Lists.newArrayList();
				for (PageData anUserList : userList) {
					PageData vpd = new PageData();
					vpd.put("var1", anUserList.getString("USERNAME"));
					vpd.put("var2", anUserList.getString("NUMBER"));
					vpd.put("var3", anUserList.getString("NAME"));
					vpd.put("var4", anUserList.getString("PHONE"));
					vpd.put("var5", anUserList.getString("SFID"));
					vpd.put("var6", anUserList.getString("ROLE_NAME"));
					vpd.put("var7", anUserList.getString("EMAIL"));
					vpd.put("var8", anUserList.getString("LAST_LOGIN"));
					vpd.put("var9", anUserList.getString("END_TIME"));
					vpd.put("var10", anUserList.getString("IP"));
					varList.add(vpd);
				}
				dataMap.put("varList", varList);
				ObjectExcelView erv = new ObjectExcelView();
				mv = new ModelAndView(erv, dataMap);
			}
		} catch (Exception e) {
			logger.error(e.toString(), e);
		}
		return mv;
	}
	
	@InitBinder
	public void initBinder(WebDataBinder binder) {
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		binder.registerCustomEditor(Date.class, new CustomDateEditor(format, true));
	}
	
	/**
	 * 交易
	 */
	@RequestMapping(value = "/trade")
	@ResponseBody
	public void trade() {
		PageData pd = this.getPageData();
		final String coinType = pd.getString("coinType");
		final String coinMin = pd.getString("coinMin");
		final String coinMax = pd.getString("coinMax");
		JavaNetUrlRestfulClient.sell(coinType, coinMax);
		JavaNetUrlRestfulClient.buy(coinType, coinMin);
		//wangGe(coinType, coinMin, coinMax);
	}
	
	/**
	 * 交易
	 */
	@RequestMapping(value = "/revoke")
	@ResponseBody
	public void revoke() {
		PageData pd = this.getPageData();
		String coinType = pd.getString("coinType");
		JavaNetUrlRestfulClient.revertAllOrder(coinType);
	}
	
	private void wangGe(final String coinType, final String coinMin, final String coinMax) {
		// 单位: 毫秒
		
		final long timeInterval = 1000;
		
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				int i = 0;
				while (true) {
					i++;
					logger.info("*************************第" + i + "次网格交易开始****************************");
					logger.info("网格交易币种：【" + coinType + "】，网格交易买价【" + coinMin + "】，网格交易卖价【" + coinMax + "】");
					JavaNetUrlRestfulClient.sell(coinType, coinMax);
					JavaNetUrlRestfulClient.buy(coinType, coinMin);
					// ------- ends here
					try {
						Thread.sleep(timeInterval);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		};
		Thread thread = new Thread(runnable);
		thread.start();
	}
	
}
