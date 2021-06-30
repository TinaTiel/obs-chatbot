package com.tinatiel.obschatbot.data.localuser;

import com.tinatiel.obschatbot.data.localuser.entity.LocalGroupRepository;
import com.tinatiel.obschatbot.data.localuser.entity.LocalUserRepository;
import com.tinatiel.obschatbot.data.localuser.mapper.LocalGroupMapper;
import com.tinatiel.obschatbot.data.localuser.mapper.LocalUserMapper;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for the local user package.
 */
@EntityScan("com.tinatiel.obschatbot.data.localuser.entity")
@Configuration
public class LocalUserConfig {

  @Autowired
  LocalUserRepository localUserRepository;

  @Autowired
  LocalGroupRepository localGroupRepository;

  @Bean
  LocalUserMapper localUserMapper() {
    return Mappers.getMapper(LocalUserMapper.class);
  }

  @Bean
  LocalGroupMapper localGroupMapper() {
    return Mappers.getMapper(LocalGroupMapper.class);
  }

  @Bean
  LocalUserService localUserService() {
    return new LocalUserServiceImpl(localUserRepository, localUserMapper());
  }

  @Bean
  LocalGroupService localGroupService() {
    return new LocalGroupServiceImpl(localGroupRepository, localGroupMapper());
  }

}
