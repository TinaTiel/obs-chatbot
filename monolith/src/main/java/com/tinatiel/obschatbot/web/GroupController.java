package com.tinatiel.obschatbot.web;

import com.tinatiel.obschatbot.data.localuser.LocalGroupService;
import com.tinatiel.obschatbot.data.localuser.model.LocalGroupDto;
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

@RequestMapping(path = WebConfig.BASE_PATH + "/group")
@RestController
@RequiredArgsConstructor
public class GroupController {

  private final LocalGroupService localGroupService;
  private final OwnerService ownerService;

  @GetMapping("/{id}")
  public ResponseEntity<LocalGroupDto> findById(@PathVariable("id") UUID id) {
    return localGroupService.findById(id)
      .map(ResponseEntity::ok)
      .orElseGet(() -> ResponseEntity.notFound().build());
  }

  @GetMapping()
  public List<LocalGroupDto> findAll() {
    return localGroupService.findByOwner(ownerService.getOwner().getId());
  }

  @PutMapping("/{id}")
  public ResponseEntity<Void> save(@PathVariable("id") UUID id, @Valid @RequestBody LocalGroupDto request) {
    request.setId(id);
    request.setOwner(ownerService.getOwner().getId());
    localGroupService.save(request);
    return ResponseEntity.ok().build();
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable("id") UUID id) {
    localGroupService.delete(id);
    return ResponseEntity.ok().build();
  }

}
