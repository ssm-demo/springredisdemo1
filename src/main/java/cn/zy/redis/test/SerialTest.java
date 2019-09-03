package cn.zy.redis.test;

import cn.zy.redis.pojo.Role;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class SerialTest
{
    public static void main(String[] args)
    {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("spring-redis.xml");
        final RedisTemplate redisTemplate = applicationContext.getBean(RedisTemplate.class);
        final Role role = new Role();
        role.setId("id");
        role.setName("name");
        role.setSth("sth");
        redisTemplate.opsForValue().set("role_1",role);
        System.out.println("role has set into redis");
        Role role1 = (Role)redisTemplate.opsForValue().get("role_1");
        System.out.println(role1);


        System.out.println("======test String=======");
        String string = "Str-test";
        redisTemplate.opsForValue().set(
            "str_1", string
        );

        System.out.println("str:" + redisTemplate.opsForValue().get("str_1"));


        // 上面的set和get可能不是同一个redis连接，可以使用Session111Callback或者RedisCallBack来实现同一个连接操作
        // 其中RedisCallBack偏底层，使用友好性较低

        SessionCallback callback = new SessionCallback<Role>()
        {
            public Role execute(RedisOperations operations)
                throws DataAccessException
            {
                operations.boundValueOps("role_2").set(role);
                return (Role)operations.boundValueOps("role_2").get();
            }
        };

        Role saveRole = (Role)redisTemplate.execute(callback);
        System.out.println(saveRole);

        testHashData(redisTemplate);
        testListData(redisTemplate);
        testSetData(redisTemplate);
        testZSetData(redisTemplate);
        testHyperLogLog(redisTemplate);

        // 事务测试
        testTransaction(redisTemplate);

        // 流水线测试
        testPipeLine(redisTemplate);

        // 超时键值测试
        testExpireTime(redisTemplate);
    }

    // 存储hash结构
    private static void testHashData(RedisTemplate redisTemplate)
    {
        System.out.println("=====test hash start ======");
        HashMap<String, String> hashMap = new HashMap();
        hashMap.put("key1", "test1");
        hashMap.put("key2", "test2");
        redisTemplate.opsForHash().putAll("hash1", hashMap);
        System.out.println("hash1-entries:" + redisTemplate.opsForHash().entries("hash1"));
        System.out.println("hash1-values:" + redisTemplate.opsForHash().values("hash1"));

        redisTemplate.boundHashOps("hash2").putAll(hashMap);
        System.out.println("hash2-entries:" + redisTemplate.opsForHash().entries("hash2"));
        System.out.println("hash2-values:" + redisTemplate.opsForHash().values("hash2"));
        System.out.println("=====test hash end ======");
    }

    // 存储链表结构
    private static void testListData(RedisTemplate redisTemplate)
    {
        System.out.println("=====test list start ======");
        List<String> list = new ArrayList();
        list.add("Test1");
        list.add("Test2");
        list.add("test3");
        redisTemplate.opsForList().leftPushAll("list1",list);
        System.out.println("size:" + redisTemplate.opsForList().size("list1"));
        System.out.println(redisTemplate.opsForList().range("list1",0,2));
        System.out.println("=====test list end ======");
    }

    // 存储集合结构
    private static void testSetData(RedisTemplate redisTemplate)
    {
        //需要将值序列设置为StringRedisSerializer
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        System.out.println("=====test Set start ======");
        Set<String> strings = new HashSet<String>();
        redisTemplate.opsForSet().add("s1","hello","world","china");
        System.out.println("s1-size: " + redisTemplate.opsForSet().size("s1"));
        System.out.println("s1-member:" + redisTemplate.opsForSet().members("s1"));
        redisTemplate.boundSetOps("s2").add("what", "others");
        System.out.println("s2-size: " + redisTemplate.opsForSet().size("s2"));
        System.out.println("s2-member:" + redisTemplate.boundSetOps("s2").members());
        System.out.println("s1-s2 diff" + redisTemplate.opsForSet().difference("s1","s2"));
        System.out.println("s1-s2 union" + redisTemplate.opsForSet().union("s1","s2"));
        System.out.println("=====test Set end ======");

    }
    // 存储有序集合结构
    private static void testZSetData(RedisTemplate redisTemplate)
    {
        System.out.println("=====test zSet start ======");
        Set<ZSetOperations.TypedTuple> set1 = new HashSet<ZSetOperations.TypedTuple>();
        Set<ZSetOperations.TypedTuple> set2 = new HashSet<ZSetOperations.TypedTuple>();
        Random random = new Random();
        for(int i = 0 ; i < 8; i++)
        {
            set1.add(new DefaultTypedTuple("s1-value" + i, random.nextDouble()));
            set2.add(new DefaultTypedTuple("s2-value" + i, random.nextDouble()));
        }
        redisTemplate.boundZSetOps("zs1").add(set1);
        redisTemplate.boundZSetOps("zs2").add(set2);

        System.out.println("zs1-range" + redisTemplate.opsForZSet().range("zs1",0,10));
        System.out.println("zs2-reverseRange" + redisTemplate.opsForZSet().reverseRange("zs1",0,10));
        System.out.println("=====test zSet end ======");
    }

    // 基数
    private static void  testHyperLogLog(RedisTemplate redisTemplate)
    {
        System.out.println("=====test HyperLogLog start ======");
        redisTemplate.opsForHyperLogLog().add("hll1","test","a","a","b","bb","c","bc");
        System.out.println("size:" + redisTemplate.opsForHyperLogLog().size("hll1"));
        System.out.println("=====test HyperLogLog end ======");
    }

    // 事务测试
    private static void testTransaction(RedisTemplate redisTemplate)
    {
        System.out.println("======test transaction start ======");
        SessionCallback callback =  new SessionCallback()
        {
            @Override
            public Object execute(RedisOperations ops)
                throws DataAccessException
            {
                ops.multi();
                ops.boundValueOps("trans1").set("trans1-test");
                ops.boundValueOps("trans2").set("10");
                System.out.println("exec-before-trans1:" + redisTemplate.opsForValue().get("trans1"));
                System.out.println("exec-before-trans2:" + redisTemplate.opsForValue().get("trans2"));
                ops.exec();

                return null;
            }
        };

        redisTemplate.execute(callback);
        System.out.println("trans1:" + redisTemplate.opsForValue().get("trans1"));
        System.out.println("trans2:" + redisTemplate.opsForValue().get("trans2"));
        System.out.println("======test transaction start=====");
    }

    // 流水线测试
    public static void testPipeLine(RedisTemplate redisTemplate)
    {
        System.out.println("======test PipeLine start ======");
        long start = System.currentTimeMillis();
        SessionCallback callback =  new SessionCallback()
        {
            @Override
            public Object execute(RedisOperations ops)
                throws DataAccessException
            {
                for(int i = 0; i < 100000; i++)
                {
                    ops.boundValueOps("piple-key-" + i).set("piple-value-" + i);
                }

                return null;
            }
        };

        redisTemplate.executePipelined(callback);
        long end = System.currentTimeMillis();
        System.out.println("time to execute 100000 times:" + (end -start));
        System.out.println("======test PipeLine end ======");

    }

    /**
     * 设置键值超时
     * @param redisTemplate
     */
    public static void testExpireTime(RedisTemplate redisTemplate)
    {
        System.out.println("======test ExpireTime begin======");

        redisTemplate.opsForValue().set("expire-key1","test");
        System.out.println("before set expire time, expireTime:" + redisTemplate.getExpire("expire-key1") + ",value:" + redisTemplate.opsForValue().get("expire-key1"));
        redisTemplate.expire("expire-key1",10, TimeUnit.SECONDS);
        System.out.println("after set 10s expire time, expireTime:" + redisTemplate.getExpire("expire-key1")+ ",value:" + redisTemplate.opsForValue().get("expire-key1"));
        try
        {
            TimeUnit.SECONDS.sleep(10);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();;
        }

        System.out.println("after sleep 10s expire time, expireTime:" + redisTemplate.getExpire("expire-key1")+ ",value:" + redisTemplate.opsForValue().get("expire-key1"));
        System.out.println("======test ExpireTime end=======");
    }

}
