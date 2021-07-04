package com.tinatiel.obschatbot.web.command;

import com.tinatiel.obschatbot.data.command.CommandEntityService;
import com.tinatiel.obschatbot.data.command.model.CommandDto;
import com.tinatiel.obschatbot.security.owner.OwnerService;
import com.tinatiel.obschatbot.web.WebConfig;
import java.util.Optional;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping(path = WebConfig.BASE_PATH + "/command")
@RestController
public class CommandController {
  private final CommandEntityService commandEntityService;
  private final OwnerService ownerService;

  public CommandController(
    CommandEntityService commandEntityService,
    OwnerService ownerService) {
    this.commandEntityService = commandEntityService;
    this.ownerService = ownerService;
  }

  @GetMapping("/{id}")
  public ResponseEntity<CommandDto> get(@PathVariable("id") UUID id) {
    return commandEntityService.findById(id)
      .map(ResponseEntity::ok)
      .orElseGet(() -> ResponseEntity.notFound().build());
  }

}
