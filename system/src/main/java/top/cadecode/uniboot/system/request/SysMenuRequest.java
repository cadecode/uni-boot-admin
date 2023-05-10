package top.cadecode.uniboot.system.request;

import lombok.Data;
import lombok.EqualsAndHashCode;
import top.cadecode.uniboot.common.response.PageParams;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 菜单请求体
 *
 * @author Cade Li
 * @since 2023/5/5
 */
public class SysMenuRequest {

    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class SysMenuRolesRequest extends PageParams {
        private String routeName;
        private String menuName;
        private List<Long> roleIdList;
        private Boolean enableFlag;
    }

    @Data
    public static class SysMenuUpdateEnableRequest{
        @NotNull
        private Long id;
        @NotNull
        private Boolean enableFlag;
    }

    @Data
    public static class SysMenuUpdateRequest{
        @NotNull
        private Long id;
        private Long parentId;
        @NotEmpty
        private String routeName;
        @NotEmpty
        private String routePath;
        @NotEmpty
        private String componentPath;
        @NotEmpty
        private String menuName;
        @NotNull
        private Boolean leafFlag;
        private String icon;
        @NotNull
        private Integer orderNum;
    }

    @Data
    public static class SysMenuAddRequest{
        private Long parentId;
        @NotEmpty
        private String routeName;
        @NotEmpty
        private String routePath;
        @NotEmpty
        private String componentPath;
        @NotEmpty
        private String menuName;
        @NotNull
        private Boolean leafFlag;
        private String icon;
        @NotNull
        private Integer orderNum;
        @NotNull
        private Boolean enableFlag;
    }
}