package org.dromara.common.cmpp.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "cmpp")
public class CmppProperties {
    private String host; // ISMG 主机
    private int port;    // ISMG 端口
    private String spId; // SP_Id
    private String sharedSecret; // 共享密钥
    private int version; // 协议版本
    /**
     * 源号码
     * SP的服务代码或前缀为服务代码的长号码, 网关将该号码完整的填到SMPP协议Submit_SM消息相应的source_addr字段，该号码最终在用户手机上显示为短消息的主叫号码
     */
    private String srcId; // 来源号码（可选）
    private String serviceId;//业务编码
    /**
     * 资费类别
     * 01：对“计费用户号码”免费
     * 02：对“计费用户号码”按条计信息费
     * 03：对“计费用户号码”按包月收取信息费
     * 04：对“计费用户号码”的信息费封顶
     * 05：对“计费用户号码”的收费是由SP实现
     */
    private String feeType;
    /**
     * 资费代码（以分为单位）
     */
    private String feeCode;
}
