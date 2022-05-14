###用户
#namespace("user")
	#include("user/index.sql")
#end

###访问日志
#namespace("access_log")
	#include("access_log/index.sql")
#end

###短链存储
#namespace("url_map")
	#include("url_map/index.sql")
#end