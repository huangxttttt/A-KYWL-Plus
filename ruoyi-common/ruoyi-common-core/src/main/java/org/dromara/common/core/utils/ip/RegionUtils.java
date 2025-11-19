package org.dromara.common.core.utils.ip;

import cn.hutool.core.io.resource.NoResourceException;
import cn.hutool.core.io.resource.ResourceUtil;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.core.utils.StringUtils;
import org.lionsoul.ip2region.xdb.LongByteArray;
import org.lionsoul.ip2region.xdb.Searcher;
import org.lionsoul.ip2region.xdb.Version;

/**
 * 根据ip地址定位工具类，离线方式
 * 参考地址：<a href="https://gitee.com/lionsoul/ip2region/tree/master/binding/java">集成 ip2region 实现离线IP地址定位库</a>
 *
 * @author lishuyan
 */
@Slf4j
public class RegionUtils {

    // IP地址库文件名称
    public static final String IPV4_XDB_FILENAME = "ip2region_v4.xdb";
    public static final String IPV6_XDB_FILENAME = "ip2region_v6.xdb";

    private static final Searcher SEARCHER_IPV4;
    private static final Searcher SEARCHER_IPV6;

    private static final Version version_ipv4 = Version.IPv4;
    private static final Version version_ipv6 = Version.IPv6;

    static {
        try {
            byte[] ipv4Bytes = ResourceUtil.readBytes(IPV4_XDB_FILENAME);
            byte[] ipv6Bytes = ResourceUtil.readBytes(IPV6_XDB_FILENAME);
            // 2. 转成 LongByteArray
            LongByteArray longByteArray_v4 = new LongByteArray(ipv4Bytes);
            LongByteArray longByteArray_v6 = new LongByteArray(ipv6Bytes);
            // 1、将 ip2region 数据库文件 xdb 从 ClassPath 加载到内存。
            // 2、基于加载到内存的 xdb 数据创建一个 Searcher 查询对象。
            SEARCHER_IPV4 = Searcher.newWithBuffer(version_ipv4, longByteArray_v4);
            SEARCHER_IPV6 = Searcher.newWithBuffer(version_ipv6, longByteArray_v6);
            log.info("RegionUtils初始化成功，加载IP地址库数据成功！");
        } catch (NoResourceException e) {
            throw new ServiceException("RegionUtils初始化失败，原因：IP地址库数据不存在！");
        } catch (Exception e) {
            throw new ServiceException("RegionUtils初始化失败，原因：" + e.getMessage());
        }
    }

    /**
     * 根据IP地址离线获取城市
     */
    public static String getCityInfo(String ip) {
        try {
            // 3、执行查询
            String region = SEARCHER_IPV4.search(StringUtils.trim(ip));
            return region.replace("0|", "").replace("|0", "");
        } catch (Exception e) {
            log.error("IP地址离线获取城市异常 {}", ip);
            return "未知";
        }
    }


    public static String getCityInfoByIPV6(String ip) {
        try {
            // 3、执行查询
            String region = SEARCHER_IPV6.search(StringUtils.trim(ip));
            return region.replace("0|", "").replace("|0", "");
        } catch (Exception e) {
            log.error("IP地址离线获取城市异常 {}", ip);
            return "未知";
        }
    }

}
