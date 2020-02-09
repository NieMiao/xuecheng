package com.xuecheng.api.cms;

import com.xuecheng.framework.domain.cms.CmsConfig;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * @Description: 页面配置管理接口
 * @Author: Niem
 * @Date: 2020/2/6-15:46
 */
@Api(value = "cms页面配置管理接口",description = "cms配置管理接口，提供数据模型的管理，查询接口")
public interface CmsConfigControllerApi {

    @ApiOperation("根据id查询cms配置信息")
    public CmsConfig getModel(String id);
}
