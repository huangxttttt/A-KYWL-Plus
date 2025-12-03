package org.dromara.common.core.constant;

/**
 * 缓存的key 常量
 *
 * @author Lion Li
 */
public interface CacheConstants {

    /**
     * 在线用户 redis key
     */
    String ONLINE_TOKEN_KEY = "online_tokens:";

    /**
     * 参数管理 cache key
     */
    String SYS_CONFIG_KEY = "sys_config:";

    /**
     * 字典管理 cache key
     */
    String SYS_DICT_KEY = "sys_dict:";

    /**
     * 登录账户密码错误次数 redis key
     */
    String PWD_ERR_CNT_KEY = "pwd_err_cnt:";

    /**
     * 存储城市黑名单
     */
    String SYSTEM_RULE_CITY = "system:rule:city";
    /**
     * 存储IP黑名单
     */
    String SYSTEM_RULE_IP = "system:rule:ip";


    /**
     * 存储短信Url
     */
    String SYSTEM_SHORTURL_CODE = "system:shorturl:code:";


}
