package com.tinatiel.obschatbot.data.command.entity.sequencer;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SequencerRepository extends JpaRepository<SequencerEntity, UUID> {

}
