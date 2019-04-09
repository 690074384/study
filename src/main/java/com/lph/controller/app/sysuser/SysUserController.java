package com.lph.controller.app.sysuser;

import com.lph.controller.base.BaseController;
import com.lph.service.system.fhlog.FHlogManager;
import com.lph.service.system.user.UserManager;
import com.lph.util.*;
import com.google.common.collect.Maps;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.session.Session;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.Map;


/**
 * 系统用户-接口类
 * 相关参数协议：
 * 00	请求失败
 * 01	请求成功
 * 02	返回空值
 * 03	请求协议参数不完整
 * 04   用户名或密码错误
 * 05   FKEY验证失败
 *
 * @author lvpenghui
 * @since 2019-4-8 17:22:07
 */
@Controller
@RequestMapping(value = "/appSysUser")
public class SysUserController extends BaseController {

    @Resource(name = "userService")
    private UserManager userService;
    @Resource(name = "fhlogService")
    private FHlogManager fHlogManager;

    /**
     * 系统用户注册接口
     *
     * @return jsonObject
     */
    @RequestMapping(value = "/registerSysUser")
    @ResponseBody
    public Object registerSysUser() {
        logBefore(logger, "系统用户注册接口");
        Map<String, Object> map = Maps.newHashMap();
        PageData pd = this.getPageData();
        String result = "00";
        try {
            //检验请求key值是否合法
            if (Tools.checkKey(Constants.USERNAME, pd.getString(Constants.FKEY))) {
                //检查参数
                if (AppUtil.checkParam(Constants.REGISTER_SYS_USER, pd)) {

                    Session session = Jurisdiction.getSession();
                    //获取session中的验证码
                    String sessionCode = (String) session.getAttribute(Const.SESSION_SECURITY_CODE);
                    String rcode = pd.getString("rcode");
                    //判断登录验证码
                    if (Tools.notEmpty(sessionCode) && sessionCode.equalsIgnoreCase(rcode)) {
                        //ID 主键
                        pd.put("USER_ID", this.get32UUID());
                        //角色ID 3 为注册用户
                        pd.put("ROLE_ID", "3");
                        //编号
                        pd.put("NUMBER", "");
                        //手机号
                        pd.put("PHONE", "");
                        //备注
                        pd.put("BZ", "注册用户");
                        //最后登录时间
                        pd.put("LAST_LOGIN", "");
                        //IP
                        pd.put("IP", "");
                        //状态
                        pd.put("STATUS", "0");
                        pd.put("SKIN", "default");
                        pd.put("RIGHTS", "");
                        //密码加密
                        pd.put("PASSWORD", new SimpleHash("SHA-1", pd.getString("USERNAME"), pd.getString("PASSWORD")).toString());
                        //判断用户名是否存在
                        if (null == userService.findByUsername(pd)) {
                            //执行保存
                            userService.saveU(pd);
                            fHlogManager.save(pd.getString("USERNAME"), "新注册");
                        } else {
                            //用户名已存在
                            result = "04";
                        }
                    } else {
                        //验证码错误
                        result = "06";
                    }
                } else {
                    result = "03";
                }
            } else {
                result = "05";
            }
        } catch (Exception e) {
            logger.error(e.toString(), e);
        } finally {
            map.put("result", result);
            logAfter(logger);
        }
        return AppUtil.returnObject(new PageData(), map);
    }


}
	
 