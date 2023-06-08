package top.cadecode.uniboot.framework.security.filter;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import top.cadecode.uniboot.framework.bean.dto.SysUserDto.SysUserDetailsDto;
import top.cadecode.uniboot.framework.enums.AuthErrorEnum;
import top.cadecode.uniboot.framework.enums.AuthModelEnum;
import top.cadecode.uniboot.framework.security.TokenAuthFilter;
import top.cadecode.uniboot.framework.security.TokenAuthHolder;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * JWT 模式校验过滤器
 *
 * @author Cade Li
 * @date 2022/5/28
 */
@RequiredArgsConstructor
@Component
@ConditionalOnProperty(name = "uni-boot.security.auth-model", havingValue = "jwt")
public class JwtTokenAuthFilter extends TokenAuthFilter {

    private final TokenAuthHolder tokenAuthHolder;

    @Override
    public AuthModelEnum getAuthModel() {
        return AuthModelEnum.JWT;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String requestURI = request.getRequestURI();
        String jwtToken = tokenAuthHolder.getTokenFromRequest(request);
        // token 不存在
        if (StrUtil.isEmpty(jwtToken)) {
            filterChain.doFilter(request, response);
            return;
        }
        // token 不合法
        if (!tokenAuthHolder.verifyToken(jwtToken)) {
            writeResponse(response, AuthErrorEnum.TOKEN_ERROR, requestURI);
            return;
        }
        // token 已过期
        if (tokenAuthHolder.isExpired(jwtToken)) {
            writeResponse(response, AuthErrorEnum.TOKEN_EXPIRED, requestURI);
            return;
        }
        // 获取 jwt 内容
        JSONObject payload = tokenAuthHolder.getPayload(jwtToken);
        SysUserDetailsDto sysUserDetailsDto = payload.toBean(SysUserDetailsDto.class);
        // 设置 AuthenticationToken
        setAuthentication(request, sysUserDetailsDto);
        // 判断是否需要刷新 token
        // 获取过期时间，单位秒
        long expiresAt = Long.parseLong(String.valueOf(payload.get("exp")));
        // 过期时间的一半，秒转为毫秒
        long halfExpiration = tokenAuthHolder.getExpiration() / 2;
        // 如果当时时间距离过期时间不到配置的 expiration 一半，就下发新的 token
        if (expiresAt - System.currentTimeMillis() / 1000 < halfExpiration) {
            // 生成 jwt token
            String newJwtToken = tokenAuthHolder.generateToken(sysUserDetailsDto.getId(), sysUserDetailsDto.getUsername(), sysUserDetailsDto.getRoles());
            // token 放在请求头
            response.addHeader(tokenAuthHolder.getHeader(), newJwtToken);
        }
        filterChain.doFilter(request, response);
    }
}
