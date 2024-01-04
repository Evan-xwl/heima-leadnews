package com.heima.schedule.test;

import com.heima.common.redis.CacheService;
import com.heima.schedule.ScheduleApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.Set;

/**
 * @author ruoling
 * @date 2024/1/4 14:53:12
 * @description
 */
@SpringBootTest(classes = ScheduleApplication.class)
@RunWith(SpringRunner.class)
public class RedisTest {
    @Resource
    CacheService cacheService;

    @Test
    public void testList() {
        Long lLeftPush = cacheService.lLeftPush("name", "zs");
        System.out.println(lLeftPush);
    }

    @Test
    public void testZset() {
//        cacheService.zAdd("group", "ls", 60);
//        cacheService.zAdd("group", "ww", 90);
//        cacheService.zAdd("group", "zs", 50);
        Set<String> group = cacheService.zRangeByScore("group", 50, 90);
        System.out.println(group);
    }
}
