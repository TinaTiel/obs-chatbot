package com.tinatiel.obschatbot.web.command;

import com.tinatiel.obschatbot.data.command.CommandEntityService;
import com.tinatiel.obschatbot.data.command.model.CommandDto;
import com.tinatiel.obschatbot.security.owner.OwnerService;
import com.tinatiel.obschatbot.web.WebConfig;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
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
  public ResponseEntity<CommandDto> get(@PathVariable("id") UUID id) {
    return commandEntityService.findById(id)
      .map(ResponseEntity::ok)
      .orElseGet(() -> ResponseEntity.notFound().build());
  }

  @GetMapping()
  public List<CommandDto> get() {
    return commandEntityService.findByOwner(ownerService.getOwner().getId());
  }

  @PostMapping
  public ResponseEntity<Void> create(@RequestBody CommandDto request) {
    // Ignore any ids, and set the owner
    request.setId(null);
    request.setOwner(ownerService.getOwner().getId());
    request.getActions().forEach(it -> it.setId(null));

    // Create
    CommandDto result = commandEntityService.save(request);

    // Return created
    return ResponseEntity.created(URI.create("/command/" + result.getId())).build();

  }

  @PutMapping("/{id}")
  public ResponseEntity<Void> update(@RequestBody CommandDto request) {
    commandEntityService.save(request);
    return ResponseEntity.ok().build();
  }

}
