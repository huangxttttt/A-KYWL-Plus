package org.dromara.system.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.dromara.common.mybatis.core.mapper.BaseMapperPlus;
import org.dromara.system.domain.SysCmppSpConfig;
import org.dromara.system.domain.vo.SysCmppSpConfigVo;

/**
 * CMPP SP config mapper.
 */
public interface CmppSpConfigMapper extends BaseMapperPlus<SysCmppSpConfig, SysCmppSpConfigVo> {

    @Select("select * from sys_cmpp_sp where sp_id = #{spId} and status = '0'")
    SysCmppSpConfig selectBySpId(@Param("spId") String spId);
}
