package com.jobs;

import com.constant.RedisConstant;
import com.utils.QiniuUtils;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Set;

public class ClearImgJob {

    /**
     * 定时清理垃圾图片的方法
     */
    @Autowired
    private JedisPool jedisPool;

    public void clearImg(){
        Jedis jedis = jedisPool.getResource();
        //根据Redis中保存的两个set集合进行差值计算，获得垃圾图片名称集合
        Set<String> imgNames = jedis.sdiff(RedisConstant.SETMEAL_PIC_RESOURCES, RedisConstant.SETMEAL_PIC_DB_RESOURCES);
        //删除图片
        for (String imgName : imgNames) {
            //从缓存中删除
            jedis.srem(RedisConstant.SETMEAL_PIC_RESOURCES,imgName);
            //从七牛云中删除
            QiniuUtils.deleteFileFromQiniu(imgName);
            System.out.println("自定义任务执行，清理垃圾图片："+imgName);
        }
    }
}
