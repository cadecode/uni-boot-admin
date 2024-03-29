package com.github.cadecode.uniboot.framework.svc.convert;

import com.github.cadecode.uniboot.framework.svc.bean.po.SysMenu;
import com.github.cadecode.uniboot.framework.svc.bean.vo.SysMenuVo.SysMenuAddReqVo;
import com.github.cadecode.uniboot.framework.svc.bean.vo.SysMenuVo.SysMenuQueryResVo;
import com.github.cadecode.uniboot.framework.svc.bean.vo.SysMenuVo.SysMenuTreeResVo;
import com.github.cadecode.uniboot.framework.svc.bean.vo.SysMenuVo.SysMenuUpdateReqVo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 系统菜单BEAN转换
 *
 * @author Cade Li
 * @date 2022/5/27
 */
@Mapper
public interface SysMenuConvert {

    SysMenuConvert INSTANCE = Mappers.getMapper(SysMenuConvert.class);


    @Mapping(target = "children", ignore = true)
    SysMenuTreeResVo poToTreeResVo(SysMenu sysMenu);

    @Mapping(target = "parentId", ignore = true)
    @Mapping(target = "hiddenFlag", ignore = true)
    @Mapping(target = "updateUser", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "leafFlag", ignore = true)
    @Mapping(target = "enableFlag", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    SysMenu voToPo(SysMenuUpdateReqVo reqVo);

    @Mapping(target = "updateUser", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    SysMenu voToPo(SysMenuAddReqVo reqVo);

    List<SysMenuQueryResVo> poToQueryResVo(List<SysMenu> list);
}
