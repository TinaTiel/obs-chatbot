package com.tinatiel.obschatbot.core.actionservice.obs;

import net.twasi.obsremotejava.OBSRemoteController;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ObsClientConfig {

    @Bean
    public ObsClient obsClient() {
        OBSRemoteController controller = new OBSRemoteController("ws://localhost:4444", false, "" , false);
        return new ObsClientImpl(controller);
    }

}
