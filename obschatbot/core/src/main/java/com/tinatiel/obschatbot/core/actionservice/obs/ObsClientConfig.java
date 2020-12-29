package com.tinatiel.obschatbot.core.actionservice.obs;

import net.twasi.obsremotejava.OBSRemoteController;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

@Configuration
public class ObsClientConfig {

    @Lazy
    @Bean
    public ObsClient obsClient() {
        OBSRemoteController controller = new OBSRemoteController("ws://localhost:4444", false, null , false);
        if(controller.isFailed()) {
            // handle failed connection
            System.out.println("FSDFSFSFSFSFFS");
        }
        return new ObsClientImpl(controller);
    }

}
