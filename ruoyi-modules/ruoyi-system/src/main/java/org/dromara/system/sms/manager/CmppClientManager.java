package org.dromara.system.sms.manager;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.cmpp.client.CmppClient;
import org.dromara.common.cmpp.client.CmppClientHandler;
import org.dromara.common.cmpp.config.CmppProperties;
import org.dromara.system.domain.SysCmppSpConfig;
import org.dromara.system.mapper.CmppSpConfigMapper;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * CMPP client manager.
 */
@RequiredArgsConstructor
@Slf4j
@Service
public class CmppClientManager {

    private final Map<String, CmppClientHolder> clients = new ConcurrentHashMap<>();

    private final CmppSpConfigMapper cmppSpConfigMapper;
    private final CmppClientHandler cmppClientHandler;


    public CmppClient getClient(String spId) {
        if (spId == null || spId.trim().isEmpty()) {
            return null;
        }
        CmppClientHolder holder = clients.compute(spId, (key, existing) -> {
            if (existing != null && existing.client != null && existing.client.isConnected()) {
                return existing;
            }
            SysCmppSpConfig config = cmppSpConfigMapper.selectBySpId(spId);
            if (config == null) {
                log.warn("CMPP SP config not found or disabled for spId={}", spId);
                return null;
            }
            if (existing != null && existing.client != null) {
                safeClose(existing.client, spId);
            }
            CmppClient client = new CmppClient(toProperties(config), cmppClientHandler);
            client.start();
            return new CmppClientHolder(spId, config, client);
        });
        return holder == null ? null : holder.client;
    }

    public Collection<CmppClient> listClients() {
        return clients.values().stream()
            .map(holder -> holder.client)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }

    public void removeClient(String spId) {
        CmppClientHolder holder = clients.remove(spId);
        if (holder != null && holder.client != null) {
            safeClose(holder.client, spId);
        }
    }

    private void safeClose(CmppClient client, String spId) {
        try {
            client.close();
        } catch (Exception e) {
            log.warn("Failed to close CMPP client spId={}", spId, e);
        }
    }

    private CmppProperties toProperties(SysCmppSpConfig config) {
        CmppProperties props = new CmppProperties();
        props.setHost(config.getHost());
        props.setPort(config.getPort() == null ? 0 : config.getPort());
        props.setSpId(config.getSpId());
        props.setSharedSecret(config.getSharedSecret());
        props.setVersion(config.getVersion() == null ? 0 : config.getVersion());
        props.setSrcId(config.getSrcId());
        props.setServiceId(config.getServiceId());
        props.setFeeType(config.getFeeType());
        props.setFeeCode(config.getFeeCode());
        return props;
    }

    static class CmppClientHolder {
        private final String spId;
        private final SysCmppSpConfig config;
        private final CmppClient client;

        CmppClientHolder(String spId, SysCmppSpConfig config, CmppClient client) {
            this.spId = spId;
            this.config = config;
            this.client = client;
        }
    }
}
