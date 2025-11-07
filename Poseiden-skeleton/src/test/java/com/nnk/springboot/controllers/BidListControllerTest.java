package com.nnk.springboot.controllers;

import com.nnk.springboot.domain.BidList;
import com.nnk.springboot.services.IBidListService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = BidListController.class)
@DisplayName("BidListController Tests")
@WithMockUser
class BidListControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IBidListService bidListService;

    private BidList validBidList;
    private List<BidList> bidLists;

    @BeforeEach
    void setUp() {
        validBidList = new BidList("TestAccount", "TestType", 100.0);
        validBidList.setBidListId(1);

        BidList bidList2 = new BidList("Account2", "Type2", 200.0);
        bidList2.setBidListId(2);

        bidLists = Arrays.asList(validBidList, bidList2);
    }

    @Test
    @DisplayName("GET /bidList/list should display all BidLists")
    void shouldDisplayAllBidLists() throws Exception {
        
        when(bidListService.findAll()).thenReturn(bidLists);

        
        mockMvc.perform(get("/bidList/list"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("bidLists"))
                .andExpect(model().attribute("bidLists", hasSize(2)))
                .andExpect(model().attribute("bidLists", hasItem(
                        hasProperty("account", is("TestAccount")))))
                .andExpect(model().attribute("bidLists", hasItem(
                        hasProperty("account", is("Account2")))))
                .andExpect(view().name("bidList/list"));

        verify(bidListService).findAll();
    }

    @Test
    @DisplayName("GET /bidList/list should display empty list when no BidLists")
    void shouldDisplayEmptyListWhenNoBidLists() throws Exception {
        
        when(bidListService.findAll()).thenReturn(Arrays.asList());

        
        mockMvc.perform(get("/bidList/list"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("bidLists", hasSize(0)))
                .andExpect(view().name("bidList/list"));
    }

    @Test
    @DisplayName("GET /bidList/add should display add form")
    void shouldDisplayAddForm() throws Exception {
        
        mockMvc.perform(get("/bidList/add"))
                .andExpect(status().isOk())
                .andExpect(view().name("bidList/add"))
                .andExpect(model().attributeExists("bidList"));
    }

    @Test
    @DisplayName("POST /bidList/validate should create BidList and redirect")
    void shouldCreateBidListAndRedirect() throws Exception {
        
        when(bidListService.create(any(BidList.class))).thenReturn(validBidList);

        
        mockMvc.perform(post("/bidList/validate")
                .with(csrf())
                .param("account", "TestAccount")
                .param("type", "TestType")
                .param("bidQuantity", "100.0"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/bidList/list"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(bidListService).create(any(BidList.class));
    }

    @Test
    @DisplayName("POST /bidList/validate should return to form with validation errors")
    void shouldReturnToFormWithValidationErrors() throws Exception {
        mockMvc.perform(post("/bidList/validate")
                .with(csrf())
                .param("account", "") // Empty account
                .param("type", "") // Empty type
                .param("bidQuantity", "100.0"))
                .andExpect(status().isOk())
                .andExpect(view().name("bidList/add"))
                .andExpect(model().attributeHasErrors("bidList"));

        verify(bidListService, never()).create(any(BidList.class));
    }

    @Test
    @DisplayName("POST /bidList/validate should handle negative bidQuantity")
    void shouldHandleNegativeBidQuantity() throws Exception {
        mockMvc.perform(post("/bidList/validate")
                .with(csrf())
                .param("account", "TestAccount")
                .param("type", "TestType")
                .param("bidQuantity", "-100.0"))
                .andExpect(status().isOk())
                .andExpect(view().name("bidList/add"))
                .andExpect(model().attributeHasErrors("bidList"));
    }

    @Test
    @DisplayName("POST /bidList/validate should handle service exception")
    void shouldHandleServiceExceptionOnCreate() throws Exception {
        
        when(bidListService.create(any(BidList.class)))
                .thenThrow(new IllegalArgumentException("Test error"));

        
        mockMvc.perform(post("/bidList/validate")
                .with(csrf())
                .param("account", "TestAccount")
                .param("type", "TestType")
                .param("bidQuantity", "100.0"))
                .andExpect(status().isOk())
                .andExpect(view().name("bidList/add"))
                .andExpect(model().attributeExists("errorMessage"));
    }

    @Test
    @DisplayName("GET /bidList/update/{id} should display update form")
    void shouldDisplayUpdateForm() throws Exception {
        
        when(bidListService.findById(1)).thenReturn(Optional.of(validBidList));

        
        mockMvc.perform(get("/bidList/update/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("bidList/update"))
                .andExpect(model().attributeExists("bidList"))
                .andExpect(model().attribute("bidList", hasProperty("account", is("TestAccount"))));

        verify(bidListService).findById(1);
    }

    @Test
    @DisplayName("GET /bidList/update/{id} should redirect when BidList not found")
    void shouldRedirectWhenBidListNotFoundForUpdate() throws Exception {
        
        when(bidListService.findById(999)).thenReturn(Optional.empty());

        
        mockMvc.perform(get("/bidList/update/999"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/bidList/list"))
                .andExpect(flash().attributeExists("errorMessage"));
    }

    @Test
    @DisplayName("POST /bidList/update/{id} should update BidList and redirect")
    void shouldUpdateBidListAndRedirect() throws Exception {
        
        when(bidListService.update(eq(1), any(BidList.class))).thenReturn(validBidList);

        
        mockMvc.perform(post("/bidList/update/1")
                .with(csrf())
                .param("account", "UpdatedAccount")
                .param("type", "UpdatedType")
                .param("bidQuantity", "150.0"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/bidList/list"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(bidListService).update(eq(1), any(BidList.class));
    }

    @Test
    @DisplayName("POST /bidList/update/{id} should return to form with validation errors")
    void shouldReturnToUpdateFormWithValidationErrors() throws Exception {
        mockMvc.perform(post("/bidList/update/1")
                .with(csrf())
                .param("account", "")
                .param("type", "")
                .param("bidQuantity", "150.0"))
                .andExpect(status().isOk())
                .andExpect(view().name("bidList/update"))
                .andExpect(model().attributeHasErrors("bidList"));

        verify(bidListService, never()).update(any(), any());
    }

    @Test
    @DisplayName("POST /bidList/update/{id} should handle service exception")
    void shouldHandleServiceExceptionOnUpdate() throws Exception {
        
        when(bidListService.update(eq(999), any(BidList.class)))
                .thenThrow(new IllegalArgumentException("BidList not found"));

        
        mockMvc.perform(post("/bidList/update/999")
                .with(csrf())
                .param("account", "TestAccount")
                .param("type", "TestType")
                .param("bidQuantity", "100.0"))
                .andExpect(status().isOk())
                .andExpect(view().name("bidList/update"))
                .andExpect(model().attributeExists("errorMessage"));
    }

    // DELETE Tests
    @Test
    @DisplayName("POST /bidList/delete/{id} should delete BidList and redirect")
    void shouldDeleteBidListAndRedirect() throws Exception {
        
        doNothing().when(bidListService).deleteById(1);

        
        mockMvc.perform(post("/bidList/delete/1")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/bidList/list"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(bidListService).deleteById(1);
    }

    @Test
    @DisplayName("POST /bidList/delete/{id} should handle non-existent BidList")
    void shouldHandleDeleteNonExistentBidList() throws Exception {
        
        doThrow(new IllegalArgumentException("BidList not found"))
                .when(bidListService).deleteById(999);

        
        mockMvc.perform(post("/bidList/delete/999")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/bidList/list"))
                .andExpect(flash().attributeExists("errorMessage"));
    }

    @Test
    @DisplayName("Should use flash attributes for success messages")
    void shouldUseFlashAttributesForSuccess() throws Exception {
        
        when(bidListService.create(any(BidList.class))).thenReturn(validBidList);

        
        mockMvc.perform(post("/bidList/validate")
                .with(csrf())
                .param("account", "TestAccount")
                .param("type", "TestType")
                .param("bidQuantity", "100.0"))
                .andExpect(flash().attribute("successMessage", notNullValue()));
    }

    @Test
    @DisplayName("Should use flash attributes for error messages")
    void shouldUseFlashAttributesForErrors() throws Exception {
        
        when(bidListService.findById(999)).thenReturn(Optional.empty());

        
        mockMvc.perform(get("/bidList/update/999"))
                .andExpect(flash().attribute("errorMessage", notNullValue()));
    }

    @Test
    @DisplayName("Should handle very long account name")
    void shouldHandleVeryLongAccountName() throws Exception {
        String longAccount = "a".repeat(100);
        
        mockMvc.perform(post("/bidList/validate")
                .with(csrf())
                .param("account", longAccount)
                .param("type", "TestType")
                .param("bidQuantity", "100.0"))
                .andExpect(status().isOk())
                .andExpect(view().name("bidList/add"))
                .andExpect(model().attributeHasErrors("bidList"));
    }

    @Test
    @DisplayName("Should handle zero bidQuantity")
    void shouldHandleZeroBidQuantity() throws Exception {
        when(bidListService.create(any(BidList.class))).thenReturn(validBidList);

        
        mockMvc.perform(post("/bidList/validate")
                .with(csrf())
                .param("account", "TestAccount")
                .param("type", "TestType")
                .param("bidQuantity", "0.0"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @DisplayName("Should handle null optional fields")
    void shouldHandleNullOptionalFields() throws Exception {
        
        when(bidListService.create(any(BidList.class))).thenReturn(validBidList);

        
        mockMvc.perform(post("/bidList/validate")
                .with(csrf())
                .param("account", "TestAccount")
                .param("type", "TestType")
                .param("bidQuantity", "100.0"))
                .andExpect(status().is3xxRedirection());
    }
}