package cn.zy.redis.publishandsubscribe;

import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;

public class RedisMessageListenerDemo implements MessageListener
{
    private RedisTemplate redisTemplate;

    public RedisTemplate getRedisTemplate()
    {
        return redisTemplate;
    }

    public void setRedisTemplate(
        RedisTemplate redisTemplate)
    {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void onMessage(Message message, byte[] pattern)
    {
        System.err.println("on message");
        System.err.println("message:" + getRedisTemplate().getValueSerializer().deserialize(message.getBody()));
        System.err.println("channel:" + getRedisTemplate().getValueSerializer().deserialize(message.getChannel()));
        System.err.println("pattern:" + pattern);
        System.err.println("deal over message");
    }
}
