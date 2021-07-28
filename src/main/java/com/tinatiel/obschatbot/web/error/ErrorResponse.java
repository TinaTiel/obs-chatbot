package com.tinatiel.obschatbot.web.error;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ErrorResponse {
  @Default
  private Timestamp timestamp = Timestamp.from(Instant.now());
  private String path;
  private Integer status;
  private String error;
  private String message;
  private List<FieldMessage> fields;

  @AllArgsConstructor
  @NoArgsConstructor
  @Data
  @Builder
  static class FieldMessage {
    private String field;
    private String message;
  }
}
