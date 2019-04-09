package com.lph.controller.app.appuser;

import com.google.common.collect.Maps;
import com.lph.controller.base.BaseController;
import com.lph.service.system.appuser.AppuserManager;
import com.lph.util.AppUtil;
import com.lph.util.Constants;
import com.lph.util.PageData;
import com.lph.util.Tools;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.Map;


/**
 * 会员-接口类
 * 相关参数协议：
 * 00	请求失败
 * 01	请求成功
 * 02	返回空值
 * 03	请求协议参数不完整
 * 04  用户名或密码错误
 * 05  FKEY验证失败
 *
 * @author lvpenghui
 * @since 2019-4-8 17:00:50
 */
@Controller
@RequestMapping(value = "/appuser")
public class IntAppuserController extends BaseController {

    @Resource(name = "appuserService")
    private AppuserManager appuserService;

    /**
     * 根据用户名获取会员信息
     *
     * @return appuser对象
     */
    @RequestMapping(value = "/getAppuserByUm")
    @ResponseBody
    public Object getAppuserByUsername() {
        logBefore(logger, "根据用户名获取会员信息");
        Map<String, Object> map = Maps.newHashMap();
        PageData pd = this.getPageData();
        String result = "00";
        try {
            //检验请求key值是否合法
            if (Tools.checkKey(Constants.USERNAME, pd.getString(Constants.FKEY))) {
                //检查参数
                if (AppUtil.checkParam(Constants.GET_APP_USER_BY_NAME, pd)) {
                    pd = appuserService.findByUsername(pd);
                    map.put("pd", pd);
                    result = (null == pd) ? "02" : "01";
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
	
 