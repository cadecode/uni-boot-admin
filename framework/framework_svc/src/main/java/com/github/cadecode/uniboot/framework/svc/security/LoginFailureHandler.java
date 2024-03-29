package com.github.cadecode.uniboot.framework.svc.security;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.extra.servlet.ServletUtil;
import cn.hutool.http.ContentType;
import com.github.cadecode.uniboot.common.core.exception.ApiException;
import com.github.cadecode.uniboot.common.core.util.JacksonUtil;
import com.github.cadecode.uniboot.common.core.web.response.ApiResult;
import com.github.cadecode.uniboot.framework.api.enums.AuthErrorEnum;
import com.github.cadecode.uniboot.framework.svc.config.FrameSecurityConfig;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 登录失败处理器
 *
 * @author Cade Li
 * @date 2021/12/11
 */
@Component
public class LoginFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) {
        ApiResult<Object> result = ApiResult.error(AuthErrorEnum.TOKEN_CREATE_ERROR).path(FrameSecurityConfig.LOGIN_URL);
        response.setStatus(AuthErrorEnum.TOKEN_CREATE_ERROR.getStatus());
        Throwable cause = exception.getCause() == null ? exception : exception.getCause();
        if (cause instanceof ApiException) {
            result.moreInfo(((ApiException) cause).getMoreInfo());
        } else if (cause instanceof BadCredentialsException) {
            result.moreInfo("密码错误");
        }
        ServletUtil.write(response, JacksonUtil.toJson(result), ContentType.JSON.toString(CharsetUtil.CHARSET_UTF_8));
    }
}
