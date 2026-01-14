package org.dromara.common.cmpp.config;

import org.dromara.common.cmpp.client.CmppClient;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(CmppProperties.class) // 绑定配置文件
public class CmppAutoConfiguration {

    @Bean
    public CmppClient cmppClient(CmppProperties cmppProperties) {
        CmppClient cmppClient = new CmppClient(cmppProperties);
        cmppClient.start();  // 启动连接
        return cmppClient;
    }

}
