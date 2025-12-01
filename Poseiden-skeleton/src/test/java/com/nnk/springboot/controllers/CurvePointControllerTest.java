package com.nnk.springboot.controllers;

import com.nnk.springboot.domain.CurvePoint;
import com.nnk.springboot.services.ICurvePointService;
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

@WebMvcTest(CurvePointController.class)
@DisplayName("CurvePointController Tests")
@org.springframework.security.test.context.support.WithMockUser
class CurvePointControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ICurvePointService curvePointService;

    private CurvePoint validCurvePoint;
    private List<CurvePoint> curvePoints;

    @BeforeEach
    void setUp() {
        validCurvePoint = new CurvePoint();
        validCurvePoint.setCurveId(1);
        validCurvePoint.setTerm(new java.math.BigDecimal("1.0"));
        validCurvePoint.setValue(new java.math.BigDecimal("2.5"));
        validCurvePoint.setId(1);

        CurvePoint curvePoint2 = new CurvePoint();
        curvePoint2.setCurveId(2);
        curvePoint2.setTerm(new java.math.BigDecimal("2.0"));
        curvePoint2.setValue(new java.math.BigDecimal("3.0"));
        curvePoint2.setId(2);

        curvePoints = Arrays.asList(validCurvePoint, curvePoint2);
    }

    @Test
    @DisplayName("GET /curvePoint/list should display all CurvePoints")
    void shouldDisplayAllCurvePoints() throws Exception {
        // Arrange
        when(curvePointService.findAll()).thenReturn(curvePoints);

        // Act & Assert
        mockMvc.perform(get("/curvePoint/list"))
                .andExpect(status().isOk())
                .andExpect(view().name("curvePoint/list"))
                .andExpect(model().attributeExists("curvePoints"))
                .andExpect(model().attribute("curvePoints", hasSize(2)));

        verify(curvePointService).findAll();
    }

    @Test
    @DisplayName("GET /curvePoint/list should display empty list when no CurvePoints")
    void shouldDisplayEmptyListWhenNoCurvePoints() throws Exception {
        // Arrange
        when(curvePointService.findAll()).thenReturn(Arrays.asList());

        // Act & Assert
        mockMvc.perform(get("/curvePoint/list"))
                .andExpect(status().isOk())
                .andExpect(view().name("curvePoint/list"))
                .andExpect(model().attribute("curvePoints", hasSize(0)));
    }

    // ADD Tests
    @Test
    @DisplayName("GET /curvePoint/add should display add form")
    void shouldDisplayAddForm() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/curvePoint/add"))
                .andExpect(status().isOk())
                .andExpect(view().name("curvePoint/add"))
                .andExpect(model().attributeExists("curvePoint"));
    }

    @Test
    @DisplayName("POST /curvePoint/validate should create CurvePoint and redirect")
    void shouldCreateCurvePointAndRedirect() throws Exception {
        // Arrange
        when(curvePointService.create(any(CurvePoint.class))).thenReturn(validCurvePoint);

        // Act & Assert
        mockMvc.perform(post("/curvePoint/validate")
                .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf())
                .param("curveId", "1")
                .param("term", "1.0")
                .param("value", "2.5"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/curvePoint/list"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(curvePointService).create(any(CurvePoint.class));
    }

    @Test
    @DisplayName("POST /curvePoint/validate should return to form with validation errors")
    void shouldReturnToFormWithValidationErrors() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/curvePoint/validate")
                .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf())
                .param("curveId", "") // Empty curveId
                .param("term", "")    // Empty term
                .param("value", "2.5"))
                .andExpect(status().isOk())
                .andExpect(view().name("curvePoint/add"))
                .andExpect(model().attributeHasErrors("curvePoint"));

        verify(curvePointService, never()).create(any(CurvePoint.class));
    }

    @Test
    @DisplayName("POST /curvePoint/validate should handle negative curveId")
    void shouldHandleNegativeCurveId() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/curvePoint/validate")
                .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf())
                .param("curveId", "-1")
                .param("term", "1.0")
                .param("value", "2.5"))
                .andExpect(status().isOk())
                .andExpect(view().name("curvePoint/add"))
                .andExpect(model().attributeHasErrors("curvePoint"));
    }

    @Test
    @DisplayName("POST /curvePoint/validate should handle negative term")
    void shouldHandleNegativeTerm() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/curvePoint/validate")
                .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf())
                .param("curveId", "1")
                .param("term", "-1.0")
                .param("value", "2.5"))
                .andExpect(status().isOk())
                .andExpect(view().name("curvePoint/add"))
                .andExpect(model().attributeHasErrors("curvePoint"));
    }

    @Test
    @DisplayName("POST /curvePoint/validate should handle service exception")
    void shouldHandleServiceExceptionOnCreate() throws Exception {
        // Arrange
        when(curvePointService.create(any(CurvePoint.class)))
                .thenThrow(new IllegalArgumentException("Test error"));

        // Act & Assert
        mockMvc.perform(post("/curvePoint/validate")
                .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf())
                .param("curveId", "1")
                .param("term", "1.0")
                .param("value", "2.5"))
                .andExpect(status().isOk())
                .andExpect(view().name("curvePoint/add"))
                .andExpect(model().attributeExists("errorMessage"));
    }

    @Test
    @DisplayName("GET /curvePoint/update/{id} should display update form")
    void shouldDisplayUpdateForm() throws Exception {
        // Arrange
        when(curvePointService.findById(1)).thenReturn(Optional.of(validCurvePoint));

        // Act & Assert
        mockMvc.perform(get("/curvePoint/update/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("curvePoint/update"))
                .andExpect(model().attributeExists("curvePoint"))
                .andExpect(model().attribute("curvePoint", hasProperty("curveId", is(1))));

        verify(curvePointService).findById(1);
    }

    @Test
    @DisplayName("GET /curvePoint/update/{id} should redirect when CurvePoint not found")
    void shouldRedirectWhenCurvePointNotFoundForUpdate() throws Exception {
        // Arrange
        when(curvePointService.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/curvePoint/update/999"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/curvePoint/list"))
                .andExpect(flash().attributeExists("errorMessage"));
    }

    @Test
    @DisplayName("POST /curvePoint/update/{id} should update CurvePoint and redirect")
    void shouldUpdateCurvePointAndRedirect() throws Exception {
        // Arrange
        when(curvePointService.update(eq(1), any(CurvePoint.class))).thenReturn(validCurvePoint);

        // Act & Assert
        mockMvc.perform(post("/curvePoint/update/1")
                .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf())
                .param("curveId", "2")
                .param("term", "2.0")
                .param("value", "3.5"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/curvePoint/list"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(curvePointService).update(eq(1), any(CurvePoint.class));
    }

    @Test
    @DisplayName("POST /curvePoint/update/{id} should return to form with validation errors")
    void shouldReturnToUpdateFormWithValidationErrors() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/curvePoint/update/1")
                .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf())
                .param("curveId", "") // Empty curveId
                .param("term", "")    // Empty term
                .param("value", "2.5"))
                .andExpect(status().isOk())
                .andExpect(view().name("curvePoint/update"))
                .andExpect(model().attributeHasErrors("curvePoint"));

        verify(curvePointService, never()).update(any(), any());
    }

    @Test
    @DisplayName("POST /curvePoint/update/{id} should handle service exception")
    void shouldHandleServiceExceptionOnUpdate() throws Exception {
        // Arrange
        when(curvePointService.update(eq(999), any(CurvePoint.class)))
                .thenThrow(new IllegalArgumentException("CurvePoint not found"));

        // Act & Assert
        mockMvc.perform(post("/curvePoint/update/999")
                .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf())
                .param("curveId", "1")
                .param("term", "1.0")
                .param("value", "2.5"))
                .andExpect(status().isOk())
                .andExpect(view().name("curvePoint/update"))
                .andExpect(model().attributeExists("errorMessage"));
    }

    @Test
    @DisplayName("POST /curvePoint/delete/{id} should delete CurvePoint and redirect")
    void shouldDeleteCurvePointAndRedirect() throws Exception {
        // Arrange
        doNothing().when(curvePointService).deleteById(1);

        // Act & Assert
        mockMvc.perform(post("/curvePoint/delete/1")
                .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/curvePoint/list"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(curvePointService).deleteById(1);
    }

    @Test
    @DisplayName("POST /curvePoint/delete/{id} should handle non-existent CurvePoint")
    void shouldHandleDeleteNonExistentCurvePoint() throws Exception {
        // Arrange
        doThrow(new IllegalArgumentException("CurvePoint not found"))
                .when(curvePointService).deleteById(999);

        // Act & Assert
        mockMvc.perform(post("/curvePoint/delete/999")
                .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/curvePoint/list"))
                .andExpect(flash().attributeExists("errorMessage"));
    }

    @Test
    @DisplayName("Should use flash attributes for success messages")
    void shouldUseFlashAttributesForSuccess() throws Exception {
        // Arrange
        when(curvePointService.create(any(CurvePoint.class))).thenReturn(validCurvePoint);

        // Act & Assert
        mockMvc.perform(post("/curvePoint/validate")
                .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf())
                .param("curveId", "1")
                .param("term", "1.0")
                .param("value", "2.5"))
                .andExpect(flash().attribute("successMessage", notNullValue()));
    }

    @Test
    @DisplayName("Should use flash attributes for error messages")
    void shouldUseFlashAttributesForErrors() throws Exception {
        // Arrange
        when(curvePointService.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/curvePoint/update/999"))
                .andExpect(flash().attribute("errorMessage", notNullValue()));
    }

    @Test
    @DisplayName("Should handle zero term")
    void shouldHandleZeroTerm() throws Exception {
        // Arrange
        when(curvePointService.create(any(CurvePoint.class))).thenReturn(validCurvePoint);

        // Act & Assert
        mockMvc.perform(post("/curvePoint/validate")
                .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf())
                .param("curveId", "1")
                .param("term", "0.0")
                .param("value", "2.5"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @DisplayName("Should handle financial precision (4 decimals)")
    void shouldHandleFinancialPrecision() throws Exception {
        // Arrange
        when(curvePointService.create(any(CurvePoint.class))).thenReturn(validCurvePoint);

        // Act & Assert
        mockMvc.perform(post("/curvePoint/validate")
                .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf())
                .param("curveId", "1")
                .param("term", "0.2500")
                .param("value", "2.7500"))
                .andExpect(status().is3xxRedirection());
    }
}