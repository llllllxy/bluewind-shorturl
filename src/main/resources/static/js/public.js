/**
 * 设置AJAX的全局默认选项，
 * 当AJAX请求会话过期时，跳转到登陆页面
 */
$.ajaxSetup({
    complete: function(XMLHttpRequest, textStatus) {
        // alert(JSON.stringify(XMLHttpRequest))
        if (XMLHttpRequest.responseJSON.code === 401) {
            if (confirm("会话已过期，请重新登录")) {
                window.location.href = AjaxUtil.ctx + "tenant/login";
            }
        }
    }
} );


/**
 * 驼峰转换下划线
 * @param name
 * @returns {string}
 */
function toLine(name) {
    return name.replace(/([A-Z])/g, "_$1").toLowerCase();
}


/**
 * 检验是否是个正确的网址
 * @param str_url
 * @returns {boolean}
 */
function isURL(str_url) {
    var strRegex =
        '^((https|http|ftp|rtsp|mms)?://)' +
        "?(([0-9a-z_!~*'().&=+$%-]+: )?[0-9a-z_!~*'().&=+$%-]+@)?" + // ftp的user@
        '(([0-9]{1,3}.){3}[0-9]{1,3}' + // IP形式的URL- 199.194.52.184
        '|' + // 允许IP和DOMAIN（域名）
        "([0-9a-z_!~*'()-]+.)*" + // 域名- www.
        '([0-9a-z][0-9a-z-]{0,61})?[0-9a-z].' + // 二级域名
        '[a-z]{2,6})' + // first level domain- .com or .museum
        '(:[0-9]{1,4})?' + // 端口- :80
        '((/?)|' + // a slash isn't required if there is no file name
        "(/[0-9a-z_!~*'().;?:@&=+$,%#-]+)+/?)$";
    var re = new RegExp(strRegex);
    if (re.test(str_url)) {
        return true;
    }
    return false;
};


/**
 * bootstrapTable封装，减少公共配置项
 * @param options
 */
function initTable(options) {
    // 初始化表格,动态从服务器加载数据
    $("#" + options.domId).bootstrapTable('destroy');
    $("#" + options.domId).bootstrapTable({
        // 自定义样式配置
        classes: 'table table-bordered table-hover table-striped',
        // 使用POST请求到服务器获取数据
        method: options.method || "POST",
        contentType: options.contentType || "application/x-www-form-urlencoded;charset=UTF-8",
        // 获取数据的后端地址
        url: options.url,
        // 每一行的唯一标识，一般为主键列
        uniqueId: options.uniqueId || "id",
        // 工具按钮用哪个容器
        toolbar: "#" + options.toolbar,
        // 表格显示条纹
        striped: options.striped || true,
        //数据类型
        dataType: options.dataType || "json",
        // 是否显示刷新按钮
        showRefresh: options.showRefresh || true,
        // 必须设置刷新事件
        silent: options.silent || true,
        // 是否显示列筛选按钮
        showColumns: options.showColumns || true,
        // 设置为 false 禁用 AJAX 数据缓存， 默认为true
        cache: options.cache || false,
        // 是否启用点击选中行，设置true 将在点击行时，自动选择rediobox 和 checkbox
        clickToSelect: options.clickToSelect || false,
        // 显示导出插件
        showExport: options.showExport || false,
        // 导出图标类型
        Icons: options.Icons || 'glyphicon-export',
        // basic:当前页的数据,all:全部的数据,selected:选中的数据
        exportDataType: options.exportDataType || "basic",
        exportTypes: ['excel'], // 导出文件类型
        // 文件名称设置
        exportOptions: {
            ignoreColumn: [7, 7], // 忽略某一列的索引
            fileName: options.fileName || "myexcel",
            tableName: options.tableName || "myexcel"
        },
        // 是否启用排序
        sortable: options.sortable || true,
        // 排序方式
        sortOrder: options.sortOrder || "asc",
        // 默认排序列
        sortName: options.sortName || "id",
        // 是否显示搜索框
        search: options.search || false,
        // 启动分页
        pagination: options.pagination || true,
        // 启用分页条无限循环的功能
        paginationLoop: options.paginationLoop || false,
        // 每页显示的记录数
        pageSize: options.pageSize || 10,
        // 当前第几页
        pageNumber: options.pageNumber || 1,
        // 记录数可选列表
        pageList: options.pageList || [10, 25, 50, 100],
        // 显示跳转到
        paginationShowPageGo: options.paginationShowPageGo || true,
        paginationPreText: options.paginationPreText || "上一页",
        paginationNextText: options.paginationNextText || "下一页",
        // 是否启用详细信息视图
        detailView: false,
        // 在行下面展示其他数据列表
        detailFormatter: options.detailFormatter,
        // 设置在哪里进行分页，可选值为"client" 或者 "server"
        sidePagination: "server",
        // 设置为""可以获取pageNumber，pageSize，searchText，sortName，sortOrder
        // 设置为limit可以获取limit, offset, search, sort, order
        queryParamsType: options.queryParamsType || "",
        queryParams: options.queryParams,
        // json数据解析，后端固定格式
        responseHandler: function(res) {
            return {
                "rows": res.data.rows,
                "total": res.data.total
            };
        },
        //数据列
        columns: options.columns
    });
}


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


const LayerUtil = {
    /**
     * 打开新增、修改页面、明细页面
     */
    openPage(options) {
        if (!options.content) {
            alert('请求错误，content不能为空');
            return false;
        }
        options.type = options.type || 2
        options.title = options.title || '新页面'
        options.maxmin = options.maxmin || true // 开启最大化最小化按钮
        options.shadeClose = options.shadeClose || false
        options.shade = options.shade || 0.5
        options.area = options.area || ['45%', '100%']
        options.offset = options.offset || 'r'
        options.anim = options.anim || 2
        layer.open({
            type: options.type,
            title: options.title,
            shadeClose: options.shadeClose,
            maxmin: options.maxmin,
            shade: options.shade,
            area: options.area,
            content: options.content,
            offset: options.offset,
            anim: options.anim,
            end: function (index, layero) {
                options.end(index, layero);
            }
        });
    }
}