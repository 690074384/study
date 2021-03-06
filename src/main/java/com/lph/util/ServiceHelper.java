package com.lph.util;

import com.lph.service.system.menu.impl.MenuService;
import com.lph.service.system.role.impl.RoleService;
import com.lph.service.system.user.UserManager;


/**
 * @author Administrator
 * 获取Spring容器中的service bean
 */
public final class ServiceHelper {
	
	private static Object getService(String serviceName){
		//WebApplicationContextUtils.
		return Constants.WEB_APP_CONTEXT.getBean(serviceName);
	}
	
	public static UserManager getUserService(){
		return (UserManager) getService("userService");
	}
	
	public static RoleService getRoleService(){
		return (RoleService) getService("roleService");
	}
	
	public static MenuService getMenuService(){
		return (MenuService) getService("menuService");
	}
}
