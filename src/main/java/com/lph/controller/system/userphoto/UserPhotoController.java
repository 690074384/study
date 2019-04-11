package com.lph.controller.system.userphoto;

import com.google.common.collect.Maps;
import com.lph.controller.base.BaseController;
import com.lph.service.system.userphoto.UserPhotoManager;
import com.lph.util.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.Map;

/**
 * 说明：用户头像
 *
 * @author lvpenghui
 * @since 2019-4-11 16:12:38
 */
@Controller
@RequestMapping(value = "/userphoto")
public class UserPhotoController extends BaseController {

    @Resource(name = "userphotoService")
    private UserPhotoManager userphotoService;

    /**
     * 保存
     *
     * @throws Exception 可能抛出的异常
     */
    @RequestMapping(value = "/save")
    @ResponseBody
    public Object save() throws Exception {
        Map<String, Object> map = Maps.newHashMap();
        String errInfo = "success";
        PageData pd = this.getPageData();
        //用户名
        pd.put("USERNAME", Jurisdiction.getUsername());
        //类型，1：带原图的。2不带原图
        String type = pd.getString("type");
        //图片路径拼接
        String strphotos = pd.getString("strphotos");
        String[] arrayStr = strphotos.split(",fh,");
        if (Constants.ONE_STRING.equals(type)) {
            String tu0 = arrayStr[0].split("angle=")[0];
            tu0 = tu0.substring(0, tu0.length() - 1);
            //原图
            pd.put("PHOTO0", tu0);
            //头像1
            pd.put("PHOTO1", arrayStr[1]);
            //头像2
            pd.put("PHOTO2", arrayStr[2]);
            //头像3
            pd.put("PHOTO3", arrayStr[3]);
        } else {
            //原图
            pd.put("PHOTO0", "");
            //头像1
            pd.put("PHOTO1", arrayStr[0]);
            //头像2
            pd.put("PHOTO2", arrayStr[1]);
            //头像3
            pd.put("PHOTO3", arrayStr[2]);
        }
        map.put("userPhoto", pd.getString("PHOTO2"));
        PageData ypd = userphotoService.findById(pd);
        //没有数据就新增，否则就修改
        if (null == ypd) {
            //主键
            pd.put("USERPHOTO_ID", this.get32UUID());
            userphotoService.save(pd);
        } else {
            userphotoService.edit(pd);
            String photo0 = ypd.getString("PHOTO0");
            String photo1 = ypd.getString("PHOTO1");
            String photo2 = ypd.getString("PHOTO2");
            String photo3 = ypd.getString("PHOTO3");
            if (Tools.notEmpty(photo0)) {
                //删除原图
                DelAllFile.delFolder(PathUtil.getClasspath() + photo0);
            }
            //删除图1
            DelAllFile.delFolder(PathUtil.getClasspath() + photo1);
            //删除图2
            DelAllFile.delFolder(PathUtil.getClasspath() + photo2);
            //删除图3
            DelAllFile.delFolder(PathUtil.getClasspath() + photo3);
        }
        //返回结果
        map.put("result", errInfo);
        return AppUtil.returnObject(new PageData(), map);
    }

}
