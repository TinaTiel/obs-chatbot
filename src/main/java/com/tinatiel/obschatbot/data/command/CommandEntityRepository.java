package com.tinatiel.obschatbot.data.command;

import com.tinatiel.obschatbot.data.command.entity.CommandEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommandEntityRepository extends JpaRepository<CommandEntity, UUID> {

}
