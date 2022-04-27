# bluewind-shorturl
# 在线短链接生成器

<a href='https://gitee.com/leisureLXY/bluewind-shorturl/stargazers'><img src='https://gitee.com/leisureLXY/bluewind-shorturl/badge/star.svg?theme=dark' alt='star'></img></a>
<a href='https://gitee.com/leisureLXY/bluewind-shorturl/members'><img src='https://gitee.com/leisureLXY/bluewind-shorturl/badge/fork.svg?theme=dark' alt='fork'></img></a>

## 已实现功能
1、将长链接转换成短链接，访问短链接时， 302重定向至原始长链接

2、支持设置短链有效期

3、支持记录访问次数

## 待实现功能
1、使用布隆过滤器优化短链冲突问题，提高超大数据量下的性能表现

## 界面展示
![首页](src/main/resources/static/images/readme/example_main.png)
![404页](src/main/resources/static/images/readme/example_404.png)
![失效页](src/main/resources/static/images/readme/example_expire.png)

## 技术选型
| 依赖        | 说明                  |
| ----------- | ---------------------|
| SpringBoot | 基础框架             |
| Thymeleaf   | 模板引擎             |
| JdbcTemplate| 持久层框架           |
| Redis       | 缓存                |
| guava       | Hash算法、布隆过滤器  |

## 实现逻辑
1、使用 MurmurHash 算法将原始长链接 hash 为 32 位散列值，将散列值转为 BASE62编码 ，即为短链接。

2、用户访问短链接时，在 Redis 中查找是否存在缓存，存在则刷新缓存时间；
   缓存中不存在，则去数据库查找，查找成功则添加到 Redis 缓存，302 重定向至原始长链接，并自增短链接访问量;
   数据库中若也不存在，则跳转到404页面。

3、具体实现参考文章：[掘金-高性能短链设计](https://juejin.cn/post/6844904090602848270) 

## 部分技术介绍
1、MurmurHash：MurmurHash 是一种非加密型哈希函数，适用于一般的哈希检索操作。
    与其它流行的MD5等哈希函数相比，对于规律性较强的 key，MurmurHash 的随机分布特征表现更良好。
    非加密意味着着相比 MD5，SHA 这些函数它的性能肯定更高（实际上性能是 MD5 等加密算法的十倍以上）。
    MurmurHash 有 32 bit、64 bit、128 bit 的实现，32 bit 已经足够表示近 43 亿个短链接。
    使用 Java 的话，在 Google 的 [guava](https://github.com/google/guava) 
    或 [hutool](https://github.com/dromara/hutool) 中有相应实现，这里使用 guava。
    
2、base62：MurmurHash 生成的哈希值最长有 10 位十进制数，为了进一步缩短短链接长度，可以将哈希值转为 base62 编码，这样最长就只有 6 个字符了。
    
3、301 和 302 重定向的区别
   - 301，代表 **永久重定向**，也就是说第一次请求拿到长链接后，下次浏览器再去请求短链的话，不会向短网址服务器请求了，而是直接从浏览器的缓存里拿，这样在 server 层面就无法获取到短网址的点击数了，如果这个链接刚好是某个活动的链接，也就无法分析此活动的效果。所以我们一般不采用 301。
   - 302，代表 **临时重定向**，也就是说每次去请求短链都会去请求短网址服务器（除非响应中用 Cache-Control 或 Expired 暗示浏览器缓存）,这样就便于 server 统计点击数，所以虽然用 302 会给 server 增加一点压力，但在数据异常重要的今天，这点代码是值得的，所以推荐使用 302！
  
   