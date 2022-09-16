package com.bluewind.shorturl.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * @author liuxingyu01
 * @date 2022-09-16 13:04
 * @description Yaml读取公共类，
 *              配置示例  bluewind:
 *                         name: ${environment_variable:bluewind-shorturl}
 *              使用示例  YmlReaderUtils.getInstance("application").get("bluewind.name")
 *              输出结果： bluewind-shorturl，如果配置了环境变量的话，则输出的是environment_variable对应的值
 **/
@Deprecated
public class YmlReaderUtils {
    private static final Logger logger = LoggerFactory.getLogger(YmlReaderUtils.class);
    /**
     * 当打开多个资源文件时，缓存资源文件
     */
    private static HashMap<String, YmlReaderUtils> configMap = new HashMap<String, YmlReaderUtils>();
    /**
     * 缓存properties
     */
    private static HashMap<String, String> propMap = new HashMap<>();

    /**
     * 默认加载顺序,从里往外找application.yml,以最外层的为准
     *
     * @see org.springframework.boot.context.config.ConfigFileApplicationListener
     */
    private static final String DEFAULT_SEARCH_LOCATIONS = "classpath:/,classpath:/config/,file:./,file:./config/*/,file:./config/";

    /**
     * 默认资源文件名称
     */
    private static final String NAME = "application";

    /**
     * 默认资源文件扩展名
     */
    private static final String SUFFIX = "yml";

    /**
     * 匹配${AAA_BBB}或者${AAA_BBB:xxx}形式字符串
     */
    private static final Pattern SYSTEM_VARIABLE_PATTERN = Pattern.compile("\\$\\{(.*?)\\}");

    private Properties properties = null;

    private YmlReaderUtils() {
        new YmlReaderUtils(NAME);
    }

    /**
     * 私有构造方法，创建单例
     */
    private YmlReaderUtils(String name) {
        YamlPropertiesFactoryBean yamlPropertiesFactoryBean = new YamlPropertiesFactoryBean();
        String[] searchLocations = DEFAULT_SEARCH_LOCATIONS.split(",");
        DefaultResourceLoader resourceLoader = new DefaultResourceLoader();
        // 默认使用SpringBoot默认目录下的application.yml
        for (String location : searchLocations) {
            String myYaml = location + name + "." + SUFFIX;
            Resource resource = resourceLoader.getResource(myYaml);
            if (resource.exists()) {
                if (!StringUtils.hasText(StringUtils.getFilenameExtension(resource.getFilename()))) {
                    if (logger.isTraceEnabled()) {
                        logger.trace("Skipped empty config extension '" + resource + "' (" + myYaml + ")");
                    }
                } else {
                    yamlPropertiesFactoryBean.setResources(resource);
                    if (logger.isDebugEnabled()) {
                        logger.debug("Loaded config file '{}' ({})", resource, myYaml);
                    }
                }
            }
        }
        properties = yamlPropertiesFactoryBean.getObject();
    }

    public static synchronized YmlReaderUtils getInstance() {
        return getInstance(NAME);
    }

    public static synchronized YmlReaderUtils getInstance(String name) {
        YmlReaderUtils conf = configMap.get(NAME);
        if (null == conf) {
            conf = new YmlReaderUtils(NAME);
            configMap.put(NAME, conf);
        }
        return conf;
    }

    private String getProperty(String name) {
        if (properties == null) {
            return null;
        }
        String value = properties.getProperty(name);
        if (StringUtils.isEmpty(value)) {
            return null;
        }
        value = convertSystemVariable(value);
        return value;
    }


    /**
     * 根据key读取value
     *
     * @param key 键
     * @return property配置值
     */
    public String get(String key) {
        // 如果propMap中有，则直接从propMap中取就行(相当于缓存了)
        if (propMap.containsKey(key)) {
            return propMap.get(key);
        } else { // 否则，从properties中拿，拿到后，缓存到propMap中
            String property = getProperty(key);
            propMap.put(key, property);
            return property;
        }
    }


    /**
     * 匹配${AAA_BBB}或者${AAA_BBB:xxx}形式字符串,用到环境变量中找AAA_BBB,将字符串转化为对应的值,默认为xxx
     *
     * @param variableStr 原始字符串
     * @return 转换后的字符串
     */
    public static String convertSystemVariable(String variableStr) {
        //解析含有${}格式的propertyValue值,将其以环境变量替代
        Matcher matcher = SYSTEM_VARIABLE_PATTERN.matcher(variableStr);
        Map<String, String> temp = new HashMap<>();
        while (matcher.find()) {
            int start = matcher.start(), end = matcher.end();
            String matchStr = variableStr.substring(start, end);
            String systemVariableKey = "", defaultValue = "";
            if (matchStr.contains(":")) {
                systemVariableKey = matchStr.split(":")[0].replace("${", "");
                defaultValue = matchStr.replace("${" + systemVariableKey + ":", "").replace("}", "");
            } else {
                systemVariableKey = matchStr.substring(2, matchStr.length() - 1);
            }
            // 读JAVA环境变量
            String systemProperties = System.getProperty(systemVariableKey);
            // 读系统环境变量
            String systemVariableValue = System.getenv().get(systemVariableKey);

            if (null != systemProperties) {
                temp.put(matchStr, systemProperties);
            } else if (null != systemVariableValue) {
                temp.put(matchStr, systemVariableValue);
            } else {
                temp.put(matchStr, defaultValue);
            }
        }
        for (String key : temp.keySet()) {
            variableStr = variableStr.replace(key, temp.get(key));
        }
        return variableStr;
    }

}
