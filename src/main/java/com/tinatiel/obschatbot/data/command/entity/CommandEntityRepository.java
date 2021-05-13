package com.tinatiel.obschatbot.data.command.entity;

import com.tinatiel.obschatbot.data.command.entity.CommandEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommandEntityRepository extends JpaRepository<CommandEntity, UUID> {

}
