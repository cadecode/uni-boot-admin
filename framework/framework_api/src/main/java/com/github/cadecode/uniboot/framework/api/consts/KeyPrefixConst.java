package com.github.cadecode.uniboot.framework.api.consts;

/**
 * Redis key 命名前缀
 *
 * @author Cade Li
 * @date 2022/5/29
 */
public interface KeyPrefixConst {

    /**
     * 登录用户信息
     */
    String LOGIN_USER = "frame:loginUser";

    // 缓存

    /**
     * Api Roles 映射关系
     */
    String API_ROLES = "frame:cache:apiRoles";

    /**
     * 字典
     */
    String DICT = "frame:cache:dict";
}
