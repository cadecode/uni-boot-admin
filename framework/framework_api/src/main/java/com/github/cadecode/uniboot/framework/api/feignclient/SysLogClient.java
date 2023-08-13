package com.github.cadecode.uniboot.framework.api.feignclient;

import com.github.cadecode.uniboot.framework.api.bean.dto.SysLogDto.SysLogSaveReqDto;
import com.github.cadecode.uniboot.framework.api.consts.SvcNameConst;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * SysLog feign client
 *
 * @author Cade Li
 * @since 2023/7/28
 */
@FeignClient(contextId = "SysLogClient", name = SvcNameConst.FRAMEWORK)
public interface SysLogClient {

    @PostMapping("system/log/save")
    boolean save(@RequestBody @NotEmpty List<SysLogSaveReqDto> dtoList);

}
