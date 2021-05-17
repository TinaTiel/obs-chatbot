package com.tinatiel.obschatbot.data.command.model.action;

import com.tinatiel.obschatbot.data.command.entity.action.ActionEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ActionRepository extends JpaRepository<ActionEntity, UUID> {

}
