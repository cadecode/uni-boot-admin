package com.github.cadecode.uniboot.framework.base.config;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.EscapeUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.servlet.ServletUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.cadecode.uniboot.common.core.util.JacksonUtil;
import com.github.cadecode.uniboot.framework.api.consts.HttpConst;
import com.github.cadecode.uniboot.framework.base.config.FeignConfig.ApacheHttpFeignConfig;
import com.github.cadecode.uniboot.framework.base.config.FeignConfig.DefaultFeignConfig;
import com.github.cadecode.uniboot.framework.base.feign.FeignClientDecorator;
import com.github.cadecode.uniboot.framework.base.feign.FeignErrorDecoder;
import com.github.cadecode.uniboot.framework.base.security.model.SysUserDetails;
import com.github.cadecode.uniboot.framework.base.util.RequestUtil;
import com.github.cadecode.uniboot.framework.base.util.SecurityUtil;
import feign.Client;
import feign.Client.Default;
import feign.Logger.Level;
import feign.RequestInterceptor;
import feign.codec.ErrorDecoder;
import feign.httpclient.ApacheHttpClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.HttpClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.client.loadbalancer.LoadBalancedRetryFactory;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.cloud.client.loadbalancer.LoadBalancerProperties;
import org.springframework.cloud.loadbalancer.support.LoadBalancerClientFactory;
import org.springframework.cloud.openfeign.loadbalancer.FeignBlockingLoadBalancerClient;
import org.springframework.cloud.openfeign.loadbalancer.RetryableFeignBlockingLoadBalancerClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import javax.servlet.http.HttpServletRequest;

/**
 * OpenFeign 配置类
 *
 * @author Cade Li
 * @since 2023/7/29
 */
@Slf4j
@Configuration
@Import({ApacheHttpFeignConfig.class, DefaultFeignConfig.class})
public class FeignConfig {

    /**
     * feign 拦截器
     */
    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            // 设置内部请求来源标识
            requestTemplate.header(HttpConst.HEAD_SOURCE, HttpConst.HEAD_SOURCE_VALUE);
            HttpServletRequest servletRequest = RequestUtil.getRequest();
            if (ObjUtil.isNull(servletRequest)) {
                return;
            }
            // 配置客户端 IP
            requestTemplate.header(HttpConst.HEAD_FORWARDED_FOR, ServletUtil.getClientIP(servletRequest));
            // 传递 trace id
            String traceId = ServletUtil.getHeader(servletRequest, HttpConst.HEAD_TRACE_ID, CharsetUtil.CHARSET_UTF_8);
            if (ObjUtil.isNotEmpty(traceId)) {
                requestTemplate.header(HttpConst.HEAD_TRACE_ID, traceId);
            }
            // 传递用户 token
            String token = SecurityUtil.getTokenFromRequest(servletRequest);
            if (StrUtil.isNotEmpty(token)) {
                requestTemplate.header(HttpConst.HEAD_TOKEN, token);
            }
            // 传递用户信息
            String userDetailsJson = ServletUtil.getHeader(servletRequest, HttpConst.HEAD_USER_DETAILS, CharsetUtil.CHARSET_UTF_8);
            // 不存在则生成
            if (StrUtil.isEmpty(userDetailsJson)) {
                SysUserDetails userDetailsDto = SecurityUtil.getUserDetails(null);
                userDetailsJson = JacksonUtil.toJson(userDetailsDto);
            }
            requestTemplate.header(HttpConst.HEAD_USER_DETAILS, EscapeUtil.escape(userDetailsJson));
        };
    }

    /**
     * feign 消息 decoder
     */
    @Bean
    public ErrorDecoder errorDecoder(ObjectMapper objectMapper) {
        return new FeignErrorDecoder(objectMapper);
    }

    /**
     * feign 默认日志级别
     */
    @Bean
    public Level feignLogLevel() {
        return Level.BASIC;
    }

    /**
     * 覆盖 feign 配置, {@link org.springframework.cloud.openfeign.loadbalancer.FeignLoadBalancerAutoConfiguration}
     * 参照 DefaultFeignLoadBalancerConfiguration
     * 集成 FeignClientDecorator 装饰类
     */
    @ConditionalOnProperty(value = "feign.httpclient.enabled", havingValue = "false")
    static class DefaultFeignConfig {
        @Bean
        @ConditionalOnProperty(value = "spring.cloud.loadbalancer.retry.enabled", havingValue = "false")
        public Client feignClient(LoadBalancerClient loadBalancerClient, LoadBalancerProperties properties,
                                  LoadBalancerClientFactory loadBalancerClientFactory) {
            log.debug("Feign client use default");
            FeignClientDecorator decorator = new FeignClientDecorator(new Default(null, null));
            return new FeignBlockingLoadBalancerClient(decorator, loadBalancerClient, properties, loadBalancerClientFactory);
        }

        @Bean
        @ConditionalOnProperty(value = "spring.cloud.loadbalancer.retry.enabled", havingValue = "true", matchIfMissing = true)
        public Client feignRetryClient(LoadBalancerClient loadBalancerClient, LoadBalancedRetryFactory loadBalancedRetryFactory,
                                       LoadBalancerProperties properties, LoadBalancerClientFactory loadBalancerClientFactory) {
            log.debug("Feign client use default, is retryable");
            FeignClientDecorator decorator = new FeignClientDecorator(new Default(null, null));
            return new RetryableFeignBlockingLoadBalancerClient(decorator, loadBalancerClient, loadBalancedRetryFactory, properties, loadBalancerClientFactory);
        }
    }

    /**
     * 覆盖 feign 配置, {@link org.springframework.cloud.openfeign.loadbalancer.FeignLoadBalancerAutoConfiguration}
     * 参照 HttpClientFeignLoadBalancerConfiguration
     * 集成 FeignClientDecorator 装饰类，使用 ApacheHttpClient
     */
    @ConditionalOnProperty(value = "feign.httpclient.enabled", havingValue = "true", matchIfMissing = true)
    static class ApacheHttpFeignConfig {
        @Bean
        @ConditionalOnProperty(value = "spring.cloud.loadbalancer.retry.enabled", havingValue = "false")
        public Client apacheFeignClient(LoadBalancerClient loadBalancerClient, HttpClient httpClient,
                                        LoadBalancerProperties properties, LoadBalancerClientFactory loadBalancerClientFactory) {
            log.debug("Feign client use apache httpclient");
            FeignClientDecorator decorator = new FeignClientDecorator(new ApacheHttpClient(httpClient));
            return new FeignBlockingLoadBalancerClient(decorator, loadBalancerClient, properties, loadBalancerClientFactory);
        }

        @Bean
        @ConditionalOnProperty(value = "spring.cloud.loadbalancer.retry.enabled", havingValue = "true", matchIfMissing = true)
        public Client apacheFeignRetryClient(LoadBalancerClient loadBalancerClient, HttpClient httpClient,
                                             LoadBalancedRetryFactory loadBalancedRetryFactory, LoadBalancerProperties properties,
                                             LoadBalancerClientFactory loadBalancerClientFactory) {
            log.debug("Feign client use apache httpclient, is retryable");
            FeignClientDecorator decorator = new FeignClientDecorator(new ApacheHttpClient(httpClient));
            return new RetryableFeignBlockingLoadBalancerClient(decorator, loadBalancerClient, loadBalancedRetryFactory, properties, loadBalancerClientFactory);
        }
    }
}
