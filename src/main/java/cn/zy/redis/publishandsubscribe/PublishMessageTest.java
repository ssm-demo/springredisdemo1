package cn.zy.redis.publishandsubscribe;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.TimeUnit;

public class PublishMessageTest
{
    public static void main(String[] args)
        throws InterruptedException
    {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("spring-redis.xml");
        RedisTemplate redisTemplate = applicationContext.getBean(RedisTemplate.class);
        TimeUnit.SECONDS.sleep(10);
        System.out.println("first 10s");
        redisTemplate.convertAndSend("zy","hello wrold");
        TimeUnit.SECONDS.sleep(10);
        System.out.println("second 10s");
        redisTemplate.convertAndSend("zy","hello wrold 2");
    }
}
