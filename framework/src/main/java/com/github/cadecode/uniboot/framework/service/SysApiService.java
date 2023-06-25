package com.github.cadecode.uniboot.framework.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.cadecode.uniboot.framework.bean.po.SysApi;
import com.github.cadecode.uniboot.framework.bean.vo.SysApiVo.SysApiRolesVo;
import com.github.cadecode.uniboot.framework.request.SysApiRequest.SysApiRolesRequest;
import com.github.pagehelper.PageInfo;

import java.util.List;

/**
 * 系统接口服务
 *
 * @author Cade Li
 * @date 2022/5/27
 */
public interface SysApiService extends IService<SysApi> {

    List<SysApiRolesVo> listRolesVo();

    List<SysApiRolesVo> listRolesVoByApiIds(List<Long> userIds);

    List<SysApiRolesVo> listRolesVo(SysApiRolesRequest request);

    PageInfo<SysApiRolesVo> pageRolesVo(SysApiRolesRequest request);
}