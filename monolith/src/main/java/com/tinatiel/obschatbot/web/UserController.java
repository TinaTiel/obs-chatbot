package com.tinatiel.obschatbot.web;

import com.tinatiel.obschatbot.data.command.CommandEntityService;
import com.tinatiel.obschatbot.data.command.model.CommandDto;
import com.tinatiel.obschatbot.data.localuser.LocalUserService;
import com.tinatiel.obschatbot.data.localuser.model.LocalUserDto;
import com.tinatiel.obschatbot.security.owner.OwnerService;
import java.util.List;
import java.util.UUID;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping(path = WebConfig.BASE_PATH + "/user")
@RestController
public class UserController {

  private final LocalUserService localUserService;
  private final OwnerService ownerService;

  @GetMapping("/{id}")
  public ResponseEntity<LocalUserDto> findById(@PathVariable("id") UUID id) {
    return localUserService.findById(id)
      .map(ResponseEntity::ok)
      .orElseGet(() -> ResponseEntity.notFound().build());
  }

  @GetMapping()
  public List<LocalUserDto> findAll() {
    return localUserService.findByOwner(ownerService.getOwner().getId());
  }

  @PutMapping("/{id}")
  public ResponseEntity<Void> save(@PathVariable("id") UUID id, @Valid @RequestBody LocalUserDto request) {
    request.setId(id);
    request.setOwner(ownerService.getOwner().getId());
    localUserService.save(request);
    return ResponseEntity.ok().build();
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable("id") UUID id) {
    localUserService.delete(id);
    return ResponseEntity.ok().build();
  }

}
