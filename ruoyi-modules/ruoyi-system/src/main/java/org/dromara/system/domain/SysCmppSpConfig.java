package org.dromara.system.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.tenant.core.TenantEntity;

/**
 * CMPP SP config table sys_cmpp_sp.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_cmpp_sp")
public class SysCmppSpConfig extends TenantEntity {

    @TableId(value = "id")
    private Long id;

    private String spId;
    private String host;
    private Integer port;
    private String sharedSecret;
    private Integer version;
    private String srcId;
    private String serviceId;
    private String feeType;
    private String feeCode;
    private String status;
}
