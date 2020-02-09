package com.xuecheng.manage_cms.service;

import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.CmsTemplate;
import com.xuecheng.framework.domain.cms.request.QueryPageRequest;
import com.xuecheng.framework.domain.cms.response.CmsCode;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_cms.dao.CmsPageRepository;
import com.xuecheng.manage_cms.dao.CmsTemplateRepository;
import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

/**
 * @Description: page service
 * @Author: Niem
 * @Date: 2020/1/31-14:03
 */
@Service
public class PageService  {

    @Autowired
    CmsPageRepository cmsPageRepository;

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    CmsTemplateRepository cmsTemplateRepository;

    @Autowired
    GridFSBucket gridFSBucket;

    @Autowired
    GridFsTemplate gridFsTemplate;
    /**
    * @Description:  分页查询页面信息
    * @Param: [page, size, queryPageRequest]
    * @Return: com.xuecheng.framework.model.response.QueryResponseResult
    * @Date: 2020/2/6
    */
    public QueryResponseResult findList(int page, int size, QueryPageRequest queryPageRequest){
        //条件匹配器
        //页面名称模糊查询，需要自定义字符串的匹配器实现模糊查询
        ExampleMatcher exampleMatcher = ExampleMatcher.matching()
                .withMatcher("pageAliase", ExampleMatcher.GenericPropertyMatchers.contains());
        //条件值
        CmsPage cmsPage = new CmsPage();
        //站点ID
        if(StringUtils.isNotEmpty(queryPageRequest.getSiteId())){
            cmsPage.setSiteId(queryPageRequest.getSiteId());
        }
        //页面别名
        if(StringUtils.isNotEmpty(queryPageRequest.getPageAliase())){
            cmsPage.setPageAliase(queryPageRequest.getPageAliase());
        }
        //创建条件实例
        Example<CmsPage> example = Example.of(cmsPage, exampleMatcher);
        //页码
        page = page-1;
        //分页对象
        Pageable pageable = new PageRequest(page, size);
        //分页查询
        Page<CmsPage> all = cmsPageRepository.findAll(example,pageable);
        QueryResult<CmsPage> cmsPageQueryResult = new QueryResult<CmsPage>();
        cmsPageQueryResult.setList(all.getContent());
        cmsPageQueryResult.setTotal(all.getTotalElements());
        //返回结果
        return new QueryResponseResult(CommonCode.SUCCESS,cmsPageQueryResult);
    }

    /**
    * @Description: 新增页面信息 
    * @Param: [cmsPage]
    * @Return: com.xuecheng.framework.domain.cms.response.CmsPageResult
    * @Date: 2020/2/6
    */
    public CmsPageResult add(CmsPage cmsPage){
        //校验页面是否存在，根据页面名称、站点Id、页面webpath查询
        CmsPage cmsPage1 = cmsPageRepository.findByPageNameAndSiteIdAndPageWebPath(cmsPage.getPageName(),
                        cmsPage.getSiteId(), cmsPage.getPageWebPath());
        if(cmsPage1!=null){
            ExceptionCast.cast(CmsCode.CMS_ADDPAGE_EXISTSNAME);
        }else {
            cmsPage.setPageId(null);//添加页面主键由spring data 自动生成
            cmsPageRepository.save(cmsPage);
            //返回结果
            CmsPageResult cmsPageResult = new CmsPageResult(CommonCode.SUCCESS,cmsPage);
            return cmsPageResult;
        }
        return new CmsPageResult(CommonCode.FAIL,null);
    }

    /**
    * @Description: 根据id查询页面信息
    * @Param: [id]
    * @Return: com.xuecheng.framework.domain.cms.CmsPage
    * @Date: 2020/2/6
    */
    public CmsPage getById(String id){
        Optional<CmsPage> optional = cmsPageRepository.findById(id);
        if(optional.isPresent()){
            CmsPage cmsPage = optional.get();
            return cmsPage;
        }
        return null;
    }

    /**
    * @Description: 修改页面信息
    * @Param: [id, cmsPage]
    * @Return: com.xuecheng.framework.domain.cms.response.CmsPageResult
    * @Date: 2020/2/6
    */
    public CmsPageResult update(String id,CmsPage cmsPage){
        //根据id从数据库查询页面信息
        CmsPage one = this.getById(id);
        if(one!=null){
            //准备更新数据
            //设置要修改的数据
            //更新模板id
            one.setTemplateId(cmsPage.getTemplateId());
            //更新所属站点
            one.setSiteId(cmsPage.getSiteId());
            //更新页面别名
            one.setPageAliase(cmsPage.getPageAliase());
            //更新页面名称
            one.setPageName(cmsPage.getPageName());
            //更新访问路径
            one.setPageWebPath(cmsPage.getPageWebPath());
            //更新物理路径
            one.setPagePhysicalPath(cmsPage.getPagePhysicalPath());
            //更新dataUrl
            one.setDataUrl(cmsPage.getDataUrl());
            //提交修改;
            cmsPageRepository.save(one);
            return new CmsPageResult(CommonCode.SUCCESS,one);
        }
        //修改失败
        return new CmsPageResult(CommonCode.FAIL,null);

    }

    /**
    * @Description: 根据id删除页面
    * @Param: [id]
    * @Return: com.xuecheng.framework.model.response.ResponseResult
    * @Date: 2020/2/6
    */
    public ResponseResult delete(String id){
        //先查询一下
        Optional<CmsPage> optional = cmsPageRepository.findById(id);
        if(optional.isPresent()){
            cmsPageRepository.deleteById(id);
            return new ResponseResult(CommonCode.SUCCESS);
        }
        return new ResponseResult(CommonCode.FAIL);
    }


    /**
    * @Description: 静态化程序
    * @Param: [pageId]
    * @Return: java.lang.String
    * @Date: 2020/2/9
    */
    public String getHtml(String pageId){
        //获取模型数据
        Map model = this.getModelByPageId(pageId);
        if (model == null){
            ExceptionCast.cast(CommonCode.CMS_GENERATEHTML_DATAISNULL);
        }
        //获取模板数据
        String template = this.getTemplateById(pageId);
        if (StringUtils.isEmpty(template)){
            ExceptionCast.cast(CommonCode.CMS_GENERATEHTML_TEMPLATE_ISNULL);
        }
        //执行静态化
        String html = this.generateHtml(model, template);
        if (StringUtils.isEmpty(html)){
            ExceptionCast.cast(CommonCode.CMS_GENERATEHTML_HTML_ISNULL);
        }
        return html;
    }

    /**
    * @Description: 获取模型数据
    * @Param: [pageId]
    * @Return: java.util.Map
    * @Date: 2020/2/7
    */
    private Map getModelByPageId(String pageId){
        //获取页面
        CmsPage cmsPage = this.getById(pageId);
        if (cmsPage==null){
            ExceptionCast.cast(CommonCode.CMS_PAGE_NOTEXISTS);
        }
        //获取页面dataUrl
        String dataUrl = cmsPage.getDataUrl();
        if (StringUtils.isEmpty(dataUrl)){
            ExceptionCast.cast(CommonCode.CMS_GENERATEHTML_DATAURLISNULL);
        }
        //根据dataUrl远程请求模型数据
        ResponseEntity<Map> entity = restTemplate.getForEntity(dataUrl, Map.class);
        Map map = entity.getBody();
        return map;
    }

    /**
    * @Description: 获取模板文件
    * @Param: [pageId]
    * @Return: java.lang.String
    * @Date: 2020/2/9
    */
    private String getTemplateById(String pageId){
        //查询页面信息
        //获取页面
        CmsPage cmsPage = this.getById(pageId);
        if (cmsPage==null){
            ExceptionCast.cast(CommonCode.CMS_PAGE_NOTEXISTS);
        }
        String templateId = cmsPage.getTemplateId();
        //根据模板id获取模板
        if (StringUtils.isEmpty(templateId)){
            ExceptionCast.cast(CommonCode.CMS_GENERATEHTML_TEMPLATEID_ISNULL);
        }
        Optional<CmsTemplate> optional = cmsTemplateRepository.findById(templateId);
        if (optional.isPresent()){
            //获取模板文件id
            String templateFileId = optional.get().getTemplateFileId();
            //根据模板文件id取出模板文件
            GridFSFile gridFSFile = gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(templateFileId)));
            //打开下载流
            GridFSDownloadStream gridFSDownloadStream = gridFSBucket.openDownloadStream(gridFSFile.getObjectId());
            GridFsResource gridFsResource = new GridFsResource(gridFSFile, gridFSDownloadStream);
            try {
                String string = IOUtils.toString(gridFsResource.getInputStream(), "utf-8");
                return string;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
    * @Description: 执行静态化
    * @Param: [model, template]
    * @Return: java.lang.String
    * @Date: 2020/2/9
    */
    private String generateHtml(Map model,String template){
        //生成配合对象
        Configuration configuration = new Configuration(Configuration.getVersion());
        //生成模板加载器
        StringTemplateLoader stringTemplateLoader = new StringTemplateLoader();
        stringTemplateLoader.putTemplate("template",template);
        //设置模板加载类
        configuration.setTemplateLoader(stringTemplateLoader);
        try {
            Template template1 = configuration.getTemplate("template");
            String html = FreeMarkerTemplateUtils.processTemplateIntoString(template1, model);
            return html;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
