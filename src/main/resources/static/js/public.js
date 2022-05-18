

// 创建一个axios实例
const $axios = axios.create({
    // 设置超时时间
    timeout: 30000,
})

// 请求拦截器
$axios.interceptors.request.use(config => {
        if (config.method === 'get') { // 如果是get请求的话，则拼接上时间戳，防止浏览器缓存
            if (config.url.indexOf('?') === -1) {
                config.url = config.url + "?t=" + new Date().getTime();
            } else {
                config.url = config.url + "&t=" + new Date().getTime();
            }
        } else if (config.method === 'post') { // 如果是post请求的话，则根据请求头自动转换请求格式
            console.log('Content-Type = ' + config.headers['Content-Type']);
            if (config.headers['Content-Type'] === 'application/x-www-form-urlencoded; charset=UTF-8') {
                config.data = Qs.stringify(config.data);
            } else {
                config.data = JSON.stringify(config.data);
            }
        }

        return config
    }, error => {
        return Promise.reject(error)
    }
)

// 响应拦截器
$axios.interceptors.response.use(
    response => {
        // 获取请求状态码
        const code = response.data.code;
        if (code === 401) {
            console.log("axios.interceptors.response -- code = " + code);
            if (confirm("会话已失效，是否重新登录")) {
                // 跳转到登陆页面
                window.location.href = Util.ctx + "login";
            }
            return Promise.reject(response);
        } else {
            return response;
        }
    },
    error => {
        return Promise.reject(error)
    }
)


