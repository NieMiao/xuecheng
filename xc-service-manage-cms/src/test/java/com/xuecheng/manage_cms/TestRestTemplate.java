package com.xuecheng.manage_cms;

import com.mongodb.client.gridfs.GridFSBucket;
import com.xuecheng.manage_cms.service.PageService;
import org.bson.types.ObjectId;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Map;

/**
 * @Description: 测试RestTemplate
 * @Author: Niem
 * @Date: 2020/2/6-16:42
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class TestRestTemplate {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private PageService pageService;

    @Autowired
    private GridFsTemplate gridFsTemplate;

    @Autowired
    private GridFSBucket gridFSBucket;

    @Test
    public void testRestTemplate(){
        ResponseEntity<Map> forEntity = restTemplate.getForEntity("http://localhost:31001/cms/config/getModel/5a791725dd573c3574ee333f", Map.class);
        System.out.println(forEntity);
    }


    @Test
    public void testGetHtml(){
        String html = pageService.getHtml("5e40017eab3b8a1ff8e70b6f");
        System.out.println(html);
    }

    @Test
    public void testSaveFiles(){
        File file = new File("D:/index_banner.ftl");
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        ObjectId objectId = gridFsTemplate.store(fileInputStream, "index_banner.ftl");
        System.out.println(objectId);
    }

    @Test
    public void testDeleteFile(){
        gridFsTemplate.delete(Query.query(Criteria.where("_id").is("5e400cb9ab3b8a14b8ceab4b")));
    }

}
