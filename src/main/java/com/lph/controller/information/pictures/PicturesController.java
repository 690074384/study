package com.lph.controller.information.pictures;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.lph.controller.base.BaseController;
import com.lph.entity.Page;
import com.lph.service.information.pictures.PicturesManager;
import com.lph.util.*;
import org.apache.commons.lang.StringUtils;
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
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 类名称：图片管理
 *
 * @author lvpenghui
 * @since 2019-4-9 17:17:42
 */
@Controller
@RequestMapping(value = "/pictures")
public class PicturesController extends BaseController {

    /**
     * 菜单地址(权限用)
     */
    private String menuUrl = "pictures/list.do";
    @Resource(name = "picturesService")
    private PicturesManager picturesService;

    /**
     * 列表
     *
     * @param page 分页
     * @return ModelAndView 对象
     * @throws Exception 可能抛出的异常
     */
    @RequestMapping(value = "/list")
    public ModelAndView list(Page page) throws Exception {
        ModelAndView mv = this.getModelAndView();
        PageData pd = this.getPageData();
        //检索条件
        String keword = pd.getString("keyword");
        if (StringUtils.isNotBlank(keword)) {
            pd.put("KEYW", keword.trim());
        }
        page.setPd(pd);
        //列出Pictures列表
        List<PageData> varList = picturesService.list(page);
        mv.setViewName("information/pictures/pictures_list");
        mv.addObject("varList", varList);
        mv.addObject("pd", pd);
        //按钮权限
        mv.addObject("QX", Jurisdiction.getHC());
        return mv;
    }

    /**
     * 新增
     *
     * @param file 图片接收器
     * @return 保存结果
     * @throws Exception 可能抛出的异常
     */
    @RequestMapping(value = "/save")
    @ResponseBody
    public Object save(
            @RequestParam(required = false) MultipartFile file
    ) throws Exception {
        if (!Jurisdiction.buttonJurisdiction(menuUrl, Constants.ADDD)) {
            return null;
        } //校验权限
        logBefore(logger, Jurisdiction.getUsername() + "新增图片");
        Map<String, String> map = Maps.newHashMap();
        String ffile = DateUtil.getDays(), fileName = "";
        PageData pd = new PageData();
        if (Jurisdiction.buttonJurisdiction(menuUrl, Constants.ADDD)) {
            if (null != file && !file.isEmpty()) {
                //文件上传路径
                String filePath = PathUtil.getClasspath() + Constants.FILEPATHIMG + ffile;
                //执行上传
                fileName = FileUpload.fileUp(file, filePath, this.get32UUID());
            } else {
                System.out.println("上传失败");
            }
            pd.put("PICTURES_ID", this.get32UUID());
            pd.put("TITLE", "图片");
            pd.put("NAME", fileName);
            pd.put("PATH", ffile + "/" + fileName);
            pd.put("CREATETIME", Tools.date2Str(new Date()));
            //附属与
            pd.put("MASTER_ID", "1");
            //备注
            pd.put("BZ", "图片管理处上传");
            //加水印
            Watermark.setWatemark(PathUtil.getClasspath() + Constants.FILEPATHIMG + ffile + "/" + fileName);
            picturesService.save(pd);
        }
        map.put("result", "ok");
        return AppUtil.returnObject(pd, map);
    }

    /**
     * 删除
     *
     * @param out out
     * @throws Exception 可能抛出的异常
     */
    @RequestMapping(value = "/delete")
    public void delete(PrintWriter out) throws Exception {
        if (!Jurisdiction.buttonJurisdiction(menuUrl, Constants.DELE)) {
            return;
        } //校验权限
        logBefore(logger, Jurisdiction.getUsername() + "删除图片");
        PageData pd;
        if (Jurisdiction.buttonJurisdiction(menuUrl, Constants.DELE)) {
            pd = this.getPageData();
            //删除图片
            DelAllFile.delFolder(PathUtil.getClasspath() + Constants.FILEPATHIMG + pd.getString("PATH"));
            picturesService.delete(pd);
        }
        out.write("success");
        out.close();
    }

    /**
     * 修改
     *
     * @param file       文件
     * @param path       文件路径
     * @param picturesId 图片ID
     * @param title      标题
     * @param masterId   属于ID
     * @param remark     备注
     * @return ModelAndView对象
     * @throws Exception 可能抛出的异常
     */
    @RequestMapping(value = "/edit")
    public ModelAndView edit(
            @RequestParam(value = "tp", required = false) MultipartFile file,
            @RequestParam(value = "tpz", required = false) String path,
            @RequestParam(value = "PICTURES_ID", required = false) String picturesId,
            @RequestParam(value = "TITLE", required = false) String title,
            @RequestParam(value = "MASTER_ID", required = false) String masterId,
            @RequestParam(value = "BZ", required = false) String remark
    ) throws Exception {
        if (!Jurisdiction.buttonJurisdiction(menuUrl, Constants.EDIT)) {
            return null;
        } //校验权限
        logBefore(logger, Jurisdiction.getUsername() + "修改图片");
        ModelAndView mv = this.getModelAndView();
        PageData pd = this.getPageData();
        if (Jurisdiction.buttonJurisdiction(menuUrl, Constants.EDIT)) {
            pd.put("PICTURES_ID", picturesId);
            pd.put("TITLE", title);
            pd.put("MASTER_ID", masterId);
            pd.put("BZ", remark);
            if (null == path) {
                path = "";
            }
            String ffile = DateUtil.getDays(), fileName = "";
            if (null != file && !file.isEmpty()) {
                //文件上传路径
                String filePath = PathUtil.getClasspath() + Constants.FILEPATHIMG + ffile;
                //执行上传
                fileName = FileUpload.fileUp(file, filePath, this.get32UUID());
                //路径
                pd.put("PATH", ffile + "/" + fileName);
                pd.put("NAME", fileName);
            } else {
                pd.put("PATH", path);
            }
            //加水印
            Watermark.setWatemark(PathUtil.getClasspath() + Constants.FILEPATHIMG + ffile + "/" + fileName);
            //执行修改数据库
            picturesService.edit(pd);
        }
        mv.addObject("msg", "success");
        mv.setViewName("save_result");
        return mv;
    }

    /**
     * 去新增页面
     */
    @RequestMapping(value = "/goAdd")
    public ModelAndView goAdd() {
        ModelAndView mv = this.getModelAndView();
        PageData pd = this.getPageData();
        mv.setViewName("information/pictures/pictures_add");
        mv.addObject("pd", pd);
        return mv;
    }

    /**
     * 去修改页面
     *
     * @return ModelAndView对象
     * @throws Exception 可能抛出的异常
     */
    @RequestMapping(value = "/goEdit")
    public ModelAndView goEdit() throws Exception {
        ModelAndView mv = this.getModelAndView();
        PageData pd = this.getPageData();
        //根据ID读取
        pd = picturesService.findById(pd);
        mv.setViewName("information/pictures/pictures_edit");
        mv.addObject("msg", "edit");
        mv.addObject("pd", pd);
        return mv;
    }

    /**
     * 批量删除
     *
     * @return 批量删除结果
     * @throws Exception 可能抛出的异常
     */
    @RequestMapping(value = "/deleteAll")
    @ResponseBody
    public Object deleteAll() throws Exception {
        PageData pd = this.getPageData();
        Map<String, Object> map = Maps.newHashMap();
        if (Jurisdiction.buttonJurisdiction(menuUrl, Constants.DELE)) {
            List<PageData> pdList = Lists.newArrayList();
            List<PageData> pathList;
            String dataIds = pd.getString("DATA_IDS");
            if (StringUtils.isNotBlank(dataIds)) {
                String[] allDatas = dataIds.split(Constants.COMMA);
                pathList = picturesService.getAllById(allDatas);
                for (PageData aPathList : pathList) {
                    //删除图片
                    DelAllFile.delFolder(PathUtil.getClasspath() + Constants.FILEPATHIMG + aPathList.getString("PATH"));
                }
                picturesService.deleteAll(allDatas);
                pd.put("msg", "ok");
            } else {
                pd.put("msg", "no");
            }
            pdList.add(pd);
            map.put("list", pdList);
        }
        return AppUtil.returnObject(pd, map);
    }

    /**
     * 删除图片
     *
     * @param out out
     * @throws Exception 可能抛出的异常
     */
    @RequestMapping(value = "/deltp")
    public void deltp(PrintWriter out) throws Exception {
        PageData pd = this.getPageData();
        //图片路径
        String path = pd.getString("PATH");
        //删除图片
        DelAllFile.delFolder(PathUtil.getClasspath() + Constants.FILEPATHIMG + pd.getString("PATH"));
        if (null != path) {
            //删除数据库中图片数据
            picturesService.delTp(pd);
        }
        out.write("success");
        out.close();
    }

    /**
     * 去图片爬虫页面
     *
     * @return ModelAndView对象
     */
    @RequestMapping(value = "/goImageCrawler")
    public ModelAndView goImageCrawler() {
        ModelAndView mv = this.getModelAndView();
        mv.setViewName("information/pictures/imageCrawler");
        return mv;
    }

    /**
     * 请求连接获取网页中每个图片的地址
     */
    @RequestMapping(value = "/getImagePath")
    @ResponseBody
    public Object getImagePath() {
        Map<String, Object> map = Maps.newHashMap();
        PageData pd = this.getPageData();
        List<String> imgList = Lists.newArrayList();
        String errInfo = "success";
        //网页地址
        String serverUrl = pd.getString("serverUrl");
        //msg:save 时保存到服务器
        String msg = pd.getString("msg");
        //检验地址是否http://
        if (!serverUrl.startsWith(Constants.HTTP)) {
            //无效地址
            errInfo = "error";
        } else {
            try {
                imgList = GetWeb.getImagePathList(serverUrl);
                if (Constants.SAVE.equals(msg)) {
                    String ffile = DateUtil.getDays();
                    //文件上传路径
                    String filePath = PathUtil.getClasspath() + Constants.FILEPATHIMG + ffile;
                    //把网络图片保存到服务器硬盘，并数据库记录
                    for (String anImgList : imgList) {
                        //下载网络图片上传到服务器上
                        String fileName = FileUpload.getHtmlPicture(anImgList, filePath, null);
                        //保存到数据库
                        pd.put("PICTURES_ID", this.get32UUID());
                        pd.put("TITLE", "图片");
                        pd.put("NAME", fileName);
                        pd.put("PATH", ffile + "/" + fileName);
                        pd.put("CREATETIME", Tools.date2Str(new Date()));
                        pd.put("MASTER_ID", "1");
                        pd.put("BZ", serverUrl + "爬取");
                        Watermark.setWatemark(PathUtil.getClasspath() + Constants.FILEPATHIMG + ffile + "/" + fileName);
                        picturesService.save(pd);
                    }
                }
            } catch (Exception e) {
                errInfo = "error";
            }
        }
        map.put("imgList", imgList);
        map.put("result", errInfo);
        return AppUtil.returnObject(new PageData(), map);
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        binder.registerCustomEditor(Date.class, new CustomDateEditor(format, true));
    }
}
