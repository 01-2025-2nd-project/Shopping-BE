package com.github.farmplus.service.redis;

import com.github.farmplus.web.dto.product.response.ProductMain;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisUtil {
    private final RedisTemplate<String, ProductMain> redisTemplate;


    public List<ProductMain> getCachedProductList(String redisKey) {
        List<ProductMain> cachedList = redisTemplate.opsForList().range(redisKey, 0, -1);
        return cachedList != null && !cachedList.isEmpty() ? cachedList : null;
    }

    public void cacheProductList(String redisKey, List<ProductMain> productList, long expireTime, TimeUnit timeUnit) {
        redisTemplate.delete(redisKey);
        redisTemplate.opsForList().rightPushAll(redisKey, productList.toArray(new ProductMain[0]));
        redisTemplate.expire(redisKey, expireTime, timeUnit);
    }

    public String generateRedisKey(String categoryName, String sort) {
        if (categoryName.equalsIgnoreCase("all")) {
            return "products:ranking:" + sort.toLowerCase();
        }
        return "products:ranking:category:" + categoryName.toLowerCase() + ":" + sort.toLowerCase();
    }
}