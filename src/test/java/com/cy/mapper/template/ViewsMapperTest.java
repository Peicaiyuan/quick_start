package com.cy.mapper.template;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

/**
 * @author cyPei
 * @date 2023-11-20 17:43
 */
@SpringBootTest(properties = "spring.profiles.active=dev")
public class ViewsMapperTest {
    Logger logger = LoggerFactory.getLogger(ViewsMapperTest.class);

    @Resource
    private ViewsMapper viewsMapper;

    @Test
    public void countTest() {
        Integer count = viewsMapper.count();
        logger.info("{}", count);
    }
}
