package com.nnk.springboot.controllers;

import com.nnk.springboot.domain.RuleName;
import com.nnk.springboot.services.IRuleNameService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = RuleNameController.class, excludeAutoConfiguration = {
        org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class
})
@DisplayName("RuleNameController Tests")
class RuleNameControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IRuleNameService ruleNameService;

    private RuleName validRuleName;
    private List<RuleName> ruleNames;

    @BeforeEach
    void setUp() {
        validRuleName = new RuleName("TestRule", "TestDescription");
        validRuleName.setId(1);

        RuleName ruleName2 = new RuleName("Rule2", "Description2");
        ruleName2.setId(2);

        ruleNames = Arrays.asList(validRuleName, ruleName2);
    }

    @Test
    @DisplayName("GET /ruleName/list should display all RuleNames")
    void shouldDisplayAllRuleNames() throws Exception {
        // Arrange
        when(ruleNameService.findAll()).thenReturn(ruleNames);

        // Act & Assert
        mockMvc.perform(get("/ruleName/list"))
                .andExpect(status().isOk())
                .andExpect(view().name("ruleName/list"))
                .andExpect(model().attributeExists("ruleNames"))
                .andExpect(model().attribute("ruleNames", hasSize(2)));

        verify(ruleNameService).findAll();
    }

    @Test
    @DisplayName("GET /ruleName/list should display empty list when no RuleNames")
    void shouldDisplayEmptyListWhenNoRuleNames() throws Exception {
        // Arrange
        when(ruleNameService.findAll()).thenReturn(Arrays.asList());

        // Act & Assert
        mockMvc.perform(get("/ruleName/list"))
                .andExpect(status().isOk())
                .andExpect(view().name("ruleName/list"))
                .andExpect(model().attribute("ruleNames", hasSize(0)));
    }

    @Test
    @DisplayName("GET /ruleName/add should display add form")
    void shouldDisplayAddForm() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/ruleName/add"))
                .andExpect(status().isOk())
                .andExpect(view().name("ruleName/add"))
                .andExpect(model().attributeExists("ruleName"));
    }

    @Test
    @DisplayName("POST /ruleName/validate should create RuleName and redirect")
    void shouldCreateRuleNameAndRedirect() throws Exception {
        // Arrange
        when(ruleNameService.create(any(RuleName.class))).thenReturn(validRuleName);

        // Act & Assert
        mockMvc.perform(post("/ruleName/validate")
                .param("name", "TestRule")
                .param("description", "TestDescription"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/ruleName/list"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(ruleNameService).create(any(RuleName.class));
    }

    @Test
    @DisplayName("POST /ruleName/validate should return to form with validation errors")
    void shouldReturnToFormWithValidationErrors() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/ruleName/validate")
                .param("name", "") // Empty name
                .param("description", "")) // Empty description
                .andExpect(status().isOk())
                .andExpect(view().name("ruleName/add"))
                .andExpect(model().attributeHasErrors("ruleName"));

        verify(ruleNameService, never()).create(any(RuleName.class));
    }

    @Test
    @DisplayName("POST /ruleName/validate should handle service exception")
    void shouldHandleServiceExceptionOnCreate() throws Exception {
        // Arrange
        when(ruleNameService.create(any(RuleName.class)))
                .thenThrow(new IllegalArgumentException("Test error"));

        // Act & Assert
        mockMvc.perform(post("/ruleName/validate")
                .param("name", "TestRule")
                .param("description", "TestDescription"))
                .andExpect(status().isOk())
                .andExpect(view().name("ruleName/add"))
                .andExpect(model().attributeExists("errorMessage"));
    }

    @Test
    @DisplayName("GET /ruleName/update/{id} should display update form")
    void shouldDisplayUpdateForm() throws Exception {
        // Arrange
        when(ruleNameService.findById(1)).thenReturn(Optional.of(validRuleName));

        // Act & Assert
        mockMvc.perform(get("/ruleName/update/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("ruleName/update"))
                .andExpect(model().attributeExists("ruleName"))
                .andExpect(model().attribute("ruleName", hasProperty("name", is("TestRule"))));

        verify(ruleNameService).findById(1);
    }

    @Test
    @DisplayName("GET /ruleName/update/{id} should redirect when RuleName not found")
    void shouldRedirectWhenRuleNameNotFoundForUpdate() throws Exception {
        // Arrange
        when(ruleNameService.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/ruleName/update/999"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/ruleName/list"))
                .andExpect(flash().attributeExists("errorMessage"));
    }

    @Test
    @DisplayName("POST /ruleName/update/{id} should update RuleName and redirect")
    void shouldUpdateRuleNameAndRedirect() throws Exception {
        // Arrange
        when(ruleNameService.update(eq(1), any(RuleName.class))).thenReturn(validRuleName);

        // Act & Assert
        mockMvc.perform(post("/ruleName/update/1")
                .param("name", "UpdatedRule")
                .param("description", "UpdatedDescription"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/ruleName/list"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(ruleNameService).update(eq(1), any(RuleName.class));
    }

    @Test
    @DisplayName("POST /ruleName/update/{id} should return to form with validation errors")
    void shouldReturnToUpdateFormWithValidationErrors() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/ruleName/update/1")
                .param("name", "") // Empty name
                .param("description", "")) // Empty description
                .andExpect(status().isOk())
                .andExpect(view().name("ruleName/update"))
                .andExpect(model().attributeHasErrors("ruleName"));

        verify(ruleNameService, never()).update(any(), any());
    }

    @Test
    @DisplayName("POST /ruleName/update/{id} should handle service exception")
    void shouldHandleServiceExceptionOnUpdate() throws Exception {
        // Arrange
        when(ruleNameService.update(eq(999), any(RuleName.class)))
                .thenThrow(new IllegalArgumentException("RuleName not found"));

        // Act & Assert
        mockMvc.perform(post("/ruleName/update/999")
                .param("name", "TestRule")
                .param("description", "TestDescription"))
                .andExpect(status().isOk())
                .andExpect(view().name("ruleName/update"))
                .andExpect(model().attributeExists("errorMessage"));
    }

    @Test
    @DisplayName("POST /ruleName/delete/{id} should delete RuleName and redirect")
    void shouldDeleteRuleNameAndRedirect() throws Exception {
        // Arrange
        doNothing().when(ruleNameService).deleteById(1);

        // Act & Assert
        mockMvc.perform(post("/ruleName/delete/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/ruleName/list"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(ruleNameService).deleteById(1);
    }

    @Test
    @DisplayName("POST /ruleName/delete/{id} should handle non-existent RuleName")
    void shouldHandleDeleteNonExistentRuleName() throws Exception {
        // Arrange
        doThrow(new IllegalArgumentException("RuleName not found"))
                .when(ruleNameService).deleteById(999);

        // Act & Assert
        mockMvc.perform(post("/ruleName/delete/999"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/ruleName/list"))
                .andExpect(flash().attributeExists("errorMessage"));
    }

    @Test
    @DisplayName("Should use flash attributes for success messages")
    void shouldUseFlashAttributesForSuccess() throws Exception {
        // Arrange
        when(ruleNameService.create(any(RuleName.class))).thenReturn(validRuleName);

        // Act & Assert
        mockMvc.perform(post("/ruleName/validate")
                .param("name", "TestRule")
                .param("description", "TestDescription"))
                .andExpect(flash().attribute("successMessage", notNullValue()));
    }

    @Test
    @DisplayName("Should use flash attributes for error messages")
    void shouldUseFlashAttributesForErrors() throws Exception {
        // Arrange
        when(ruleNameService.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/ruleName/update/999"))
                .andExpect(flash().attribute("errorMessage", notNullValue()));
    }

    @Test
    @DisplayName("Should handle very long name")
    void shouldHandleVeryLongName() throws Exception {
        // Arrange
        String longName = "a".repeat(126); // > 125 characters

        // Act & Assert
        mockMvc.perform(post("/ruleName/validate")
                .param("name", longName)
                .param("description", "TestDescription"))
                .andExpect(status().isOk())
                .andExpect(view().name("ruleName/add"))
                .andExpect(model().attributeHasErrors("ruleName"));
    }

    @Test
    @DisplayName("Should handle very long template")
    void shouldHandleVeryLongTemplate() throws Exception {
        // Arrange
        String longTemplate = "t".repeat(513);

        // Act & Assert
        mockMvc.perform(post("/ruleName/validate")
                .param("name", "TestRule")
                .param("description", "TestDescription")
                .param("template", longTemplate))
                .andExpect(status().isOk())
                .andExpect(view().name("ruleName/add"))
                .andExpect(model().attributeHasErrors("ruleName"));
    }

    @Test
    @DisplayName("Should handle null optional fields")
    void shouldHandleNullOptionalFields() throws Exception {
        // Arrange
        when(ruleNameService.create(any(RuleName.class))).thenReturn(validRuleName);

        // Act & Assert
        mockMvc.perform(post("/ruleName/validate")
                .param("name", "TestRule")
                .param("description", "TestDescription"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @DisplayName("Should accept all optional fields when provided")
    void shouldAcceptAllOptionalFields() throws Exception {
        // Arrange
        when(ruleNameService.create(any(RuleName.class))).thenReturn(validRuleName);

        // Act & Assert
        mockMvc.perform(post("/ruleName/validate")
                .param("name", "TestRule")
                .param("description", "TestDescription")
                .param("json", "{\"key\":\"value\"}")
                .param("template", "<template>test</template>")
                .param("sqlStr", "SELECT * FROM table")
                .param("sqlPart", "WHERE id = 1"))
                .andExpect(status().is3xxRedirection());
    }
}