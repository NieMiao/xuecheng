package com.xuecheng.manage_cms.dao;

import com.xuecheng.framework.domain.cms.CmsTemplate;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @Description: 页面模板
 * @Author: Niem
 * @Date: 2020/2/9-11:45
 */
public interface CmsTemplateRepository extends MongoRepository<CmsTemplate,String> {
}
