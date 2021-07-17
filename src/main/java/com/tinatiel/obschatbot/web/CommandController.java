package com.tinatiel.obschatbot.web;

import com.tinatiel.obschatbot.data.command.CommandEntityService;
import com.tinatiel.obschatbot.data.command.model.CommandDto;
import com.tinatiel.obschatbot.security.owner.OwnerService;
import com.tinatiel.obschatbot.web.WebConfig;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
  public ResponseEntity<CommandDto> findById(@PathVariable("id") UUID id) {
    return commandEntityService.findById(id)
      .map(ResponseEntity::ok)
      .orElseGet(() -> ResponseEntity.notFound().build());
  }

  @GetMapping()
  public List<CommandDto> findAll() {
    return commandEntityService.findByOwner(ownerService.getOwner().getId());
  }

  @PutMapping("/{id}")
  public ResponseEntity<Void> save(@PathVariable("id") UUID id, @RequestBody CommandDto request) {
    request.setId(id);
    request.setOwner(ownerService.getOwner().getId());
    commandEntityService.save(request);
    return ResponseEntity.ok().build();
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable("id") UUID id) {
    commandEntityService.delete(id);
    return ResponseEntity.ok().build();
  }

}
