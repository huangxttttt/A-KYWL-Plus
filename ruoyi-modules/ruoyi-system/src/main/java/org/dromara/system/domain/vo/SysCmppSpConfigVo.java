package org.dromara.system.domain.vo;

import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.dromara.system.domain.SysCmppSpConfig;

/**
 * CMPP SP config vo.
 */
@Data
@AutoMapper(target = SysCmppSpConfig.class)
public class SysCmppSpConfigVo {

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
