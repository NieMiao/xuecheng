package com.xuecheng.manage_cms.dao;

import com.xuecheng.framework.domain.cms.CmsConfig;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @Description: cms页面配置管理
 * @Author: Niem
 * @Date: 2020/2/6-15:52
 */
public interface CmsConfigRepository extends MongoRepository<CmsConfig,String> {

}
