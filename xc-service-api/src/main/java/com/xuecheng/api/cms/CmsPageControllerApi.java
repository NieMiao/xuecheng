package com.xuecheng.api.cms;

import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.request.QueryPageRequest;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

/**
 * @description: 页面管理接口
 * @author: Niem
 * @Date: 2020/1/31-10:34
 */
@Api(value="cms页面管理接口",description = "cms页面管理接口，提供页面的增、删、改、查")
public interface CmsPageControllerApi {


    /**
    * @Description: 页面查询
    * @Param: [page, size, queryPageRequest]
    * @Return: com.xuecheng.framework.model.response.QueryResponseResult
    * @Date: 2020/1/31
    */
    @ApiOperation("分页查询页面列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name="page",value = "页码",required=true,paramType="path",dataType="int"),
            @ApiImplicitParam(name="size",value = "每页记录数",required=true,paramType="path",dataType="int")
    })
    public QueryResponseResult findList(int page, int size, QueryPageRequest queryPageRequest) ;

    /**
    * @Description:  新增页面
    * @Param: [cmsPage]
    * @Return: com.xuecheng.framework.domain.cms.response.CmsPageResult
    * @Date: 2020/2/2
    */
    @ApiOperation("新增页面")
    @ApiImplicitParams({
            @ApiImplicitParam(name="cmsPage",value = "页面模型",required = true,paramType = "body",dataType = "CmsPage")
    })
    public CmsPageResult add(CmsPage cmsPage) ;

    /**
    * @Description:  根据页面id查询页面信息
    * @Param: [id]
    * @Return: com.xuecheng.framework.domain.cms.CmsPage
    * @Date: 2020/2/2
    */
    @ApiOperation("根据页面id查询页面信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name="id",value = "id",required=true,paramType="path",dataType="String"),
    })
    public CmsPage findById(String id);


    /**
    * @Description:  修改页面
    * @Param: [id, cmsPage]
    * @Return: com.xuecheng.framework.domain.cms.response.CmsPageResult
    * @Date: 2020/2/2
    */
    @ApiOperation("修改页面")
    @ApiImplicitParams({
            @ApiImplicitParam(name="id",value = "id",required=true,paramType="path",dataType="String"),
            @ApiImplicitParam(name="cmsPage",value = "页面模型",required = true,paramType = "body",dataType = "CmsPage")
    })
    public CmsPageResult edit(String id,CmsPage cmsPage);


    /**
    * @Description:  删除页面
    * @Param: [id]
    * @Return: com.xuecheng.framework.model.response.ResponseResult
    * @Date: 2020/2/2
    */
    @ApiOperation("删除页面")
    @ApiImplicitParams({
            @ApiImplicitParam(name="id",value = "id",required=true,paramType="path",dataType="String"),
    })
    public ResponseResult delete(String id);
}
