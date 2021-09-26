package com.tinatiel.obschatbot.web;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import com.tinatiel.obschatbot.data.error.DataPersistenceException;
import com.tinatiel.obschatbot.security.WebSecurityConfig;
import com.tinatiel.obschatbot.web.ErrorHandlingIT.DummyController;
import com.tinatiel.obschatbot.web.ErrorHandlingIT.DummyService;
import com.tinatiel.obschatbot.web.error.GlobalControllerErrorAdvice;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@ContextConfiguration(classes = {
  DummyController.class, DummyService.class, WebSecurityConfig.class, GlobalControllerErrorAdvice.class})
@WebMvcTest(DummyController.class)
public class ErrorHandlingIT {

  @Autowired
  MockMvc mockMvc;

  @MockBean
  DummyService dummyService;

  @MockBean
  ClientRegistrationRepository ignoreme;

  @Test
  void handleIllegalArguments() throws Exception {

    when(dummyService.someMethod()).thenThrow(new IllegalArgumentException("foo"));

    mockMvc.perform(get("/test-error"))
    .andDo(print())
    .andExpect(status().is(400))
    .andExpect(jsonPath("$.error").value(IllegalArgumentException.class.getSimpleName()))
    .andExpect(jsonPath("$.message").value("foo"));

  }

  @Test
  void handlePersistenceError() throws Exception {

    when(dummyService.someMethod()).thenThrow(new DataPersistenceException("bar"));

    mockMvc.perform(get("/test-error"))
      .andDo(print())
      .andExpect(status().is(400))
      .andExpect(jsonPath("$.error").value(DataPersistenceException.class.getSimpleName()))
      .andExpect(jsonPath("$.message").value("bar"));

  }

  @Test
  void handleValidationError() throws Exception {

    when(dummyService.someMethod()).thenThrow(new DataPersistenceException("bar"));

    mockMvc.perform(put("/test-error")
      .content(
        "{\n"
        + "\t\"someField\": \"    \"\n"
        + "}"
      )
      .contentType(MediaType.APPLICATION_JSON))
      .andDo(print())
      .andExpect(status().is(400))
      .andExpect(jsonPath("$.error").value(MethodArgumentNotValidException.class.getSimpleName()))
      .andExpect(jsonPath("$.message").value("invalid fields"))
      .andExpect(jsonPath("$.fields[0].field").value("otherField"))
      .andExpect(jsonPath("$.fields[0].message").value("other message"))
      .andExpect(jsonPath("$.fields[1].field").value("someField"))
      .andExpect(jsonPath("$.fields[1].message").value("some message"));

  }

  @Test
  void handleInvalidJson() throws Exception {
    //

    mockMvc.perform(put("/test-error")
      .content(
        "{\n"
          + "\t\"someField\": \"    \"\n" // <-- forgot the comma
          + "\t\"otherField\": \"    \"\n"
          + "}"
      )
      .contentType(MediaType.APPLICATION_JSON))
      .andDo(print())
      .andExpect(status().is(400))
      .andExpect(jsonPath("$.error").value(HttpMessageNotReadableException.class.getSimpleName()))
      .andExpect(jsonPath("$.message").exists());

  }

  @RestController
  public static class DummyController {

    private final DummyService dummyService;

    public DummyController(DummyService dummyService) {
      this.dummyService = dummyService;
    }

    @GetMapping("/test-error")
    public String test() {
      return dummyService.someMethod();
    }

    @PutMapping("/test-error")
    public String test(@Valid @RequestBody ValidatedObject validatedObject) {
      return "doesntmatter";
    }
  }

  @Component
  public static class DummyService {
    String someMethod() { return "foo"; }
  }

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  public static class ValidatedObject {
    @NotBlank(message = "some message")
    String someField;

    @NotNull(message = "other message")
    String otherField;
  }

}
