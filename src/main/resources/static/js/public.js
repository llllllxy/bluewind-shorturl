

/**
 * 设置AJAX的全局默认选项，
 * 当AJAX请求会话过期时，跳转到登陆页面
 */
$.ajaxSetup({
    complete: function(XMLHttpRequest, textStatus){
        if (XMLHttpRequest.status == 302) {
            layer.alert('会话已过期，请重新登录', function(index){
                layer.close(index);
                window.location.href = Util.ctx + "admin/login";
            });
        }
    }
} );


/**
 * 配置layer.open的宽度和高度
 */
const layerwidth = 800;
const layerheight = ($(window).height() - 50);


/**
 * Ajax请求封装
 */
const AjaxUtil = {
    ctx: /*[[@{/}]]*/'',

    /**
     * GET 请求
     */
    get: function (options) {
        if (!options.url) {
            alert('请求错误，url不可为空!');
            return false;
        }
        options.type = 'GET';
        options.timeout = options.timeout || 5000; // 设置本地的请求超时时间（以毫秒计）
        options.async = options.async || true; // 布尔值，表示请求是否异步处理。默认是 true
        options.cache = options.cache || false; // 布尔值，表示浏览器是否缓存被请求页面，默认是true
        options.dataType = options.dataType || 'json';
        $.ajax({
            url: options.url,
            type: options.type,
            timeout: options.timeout,
            async: options.async,
            cache: options.cache,
            dataType: options.dataType,
            success: function (data, textStatus, jqXHR) {
                // 成功回调
                options.success(data);
            },
            error: function (XMLHttpRequest, textStatus, errorThrown) {
                // 错误回调
                options.error("错误提示： " + XMLHttpRequest.status + " " + XMLHttpRequest.statusText);
            }
        });
    },


    /**
     * POST 请求
     */
    post: function (options) {
        if (!options.url) {
            alert('请求错误，url不可为空!');
            return false;
        }
        options.type = 'POST';
        options.timeout = options.timeout || 5000;
        options.async = options.async || true;
        options.cache = options.cache || false;
        options.dataType = options.dataType || 'json';
        options.contentType = options.contentType || 'application/x-www-form-urlencoded';
        options.data = options.data || '';
        $.ajax({
            url: options.url,
            type: options.type,
            timeout: options.timeout,
            async: options.async,
            cache: options.cache,
            dataType: options.dataType,
            data: options.data,
            contentType: options.contentType, // 发送数据到服务器时所使用的内容类型。默认是："application/x-www-form-urlencoded"
            success: function (data, textStatus, jqXHR) {
                // 成功回调
                options.success(data);
            },
            error: function (XMLHttpRequest, textStatus, errorThrown) {
                // 错误回调
                options.error("错误提示： " + XMLHttpRequest.status + " " + XMLHttpRequest.statusText);
            }
        });
    },


    /**
     * upload 文件上传 请求
     */
    upload: function (options) {
        if (!options.url) {
            alert('请求错误，url不可为空!');
            return false;
        }
        options.type = 'POST';
        options.timeout = options.timeout || 5000;
        options.async = options.async || true;
        options.cache = options.cache || false;
        options.dataType = options.dataType || 'json';
        options.contentType = options.contentType || false;
        options.processData = options.processData || false;
        options.data = options.data || new FormData();
        $.ajax({
            url: options.url,
            type: options.type,
            timeout: options.timeout,
            async: options.async,
            cache: options.cache,
            dataType: options.dataType,
            processData: options.processData,
            data: options.data,
            contentType: options.contentType,
            success: function (data, textStatus, jqXHR) {
                // 成功回调
                options.success(data);
            },
            error: function (XMLHttpRequest, textStatus, errorThrown) {
                // 错误回调
                options.error("错误提示： " + XMLHttpRequest.status + " " + XMLHttpRequest.statusText);
            }
        });
    }
}