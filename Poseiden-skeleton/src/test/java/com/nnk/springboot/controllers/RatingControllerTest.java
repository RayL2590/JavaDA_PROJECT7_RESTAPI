package com.nnk.springboot.controllers;

import com.nnk.springboot.domain.Rating;
import com.nnk.springboot.services.IRatingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@WebMvcTest(controllers = RatingController.class)
@DisplayName("RatingController - Tests unitaires")
@WithMockUser(username = "admin", roles = "ADMIN")
class RatingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IRatingService ratingService;

    private Rating rating1;
    private Rating rating2;
    private List<Rating> ratings;

    @BeforeEach
    void setUp() {
        rating1 = new Rating("Aaa", "AAA", "AAA");
        rating1.setId(1);
        rating1.setOrderNumber(1);

        rating2 = new Rating("Baa3", "BBB-", "BBB-");
        rating2.setId(2);
        rating2.setOrderNumber(12);

        ratings = Arrays.asList(rating1, rating2);
    }

    @Test
    @DisplayName("GET /rating/list - Doit afficher la liste")
    void list_ShouldReturnListView() throws Exception {
        
        when(ratingService.findAll()).thenReturn(ratings);

        
        mockMvc.perform(get("/rating/list"))
                .andExpect(status().isOk())
                .andExpect(view().name("rating/list"))
                .andExpect(model().attributeExists("ratings"))
                .andExpect(model().attribute("ratings", org.hamcrest.Matchers.hasSize(2)));

        verify(ratingService).findAll();
    }

    @Test
    @DisplayName("GET /rating/list - Avec liste vide - Doit afficher page vide")
    void list_WithEmptyList_ShouldReturnEmptyView() throws Exception {
        
        when(ratingService.findAll()).thenReturn(List.of());

        
        mockMvc.perform(get("/rating/list"))
                .andExpect(status().isOk())
                .andExpect(view().name("rating/list"))
                .andExpect(model().attribute("ratings", org.hamcrest.Matchers.hasSize(0)));
    }

    @Test
    @DisplayName("GET /rating/add - Doit afficher le formulaire d'ajout")
    void addForm_ShouldReturnAddView() throws Exception {
        
        mockMvc.perform(get("/rating/add"))
                .andExpect(status().isOk())
                .andExpect(view().name("rating/add"))
                .andExpect(model().attributeExists("rating"))
                .andExpect(model().attribute("rating", org.hamcrest.Matchers.isA(Rating.class)));
    }

    @Test
    @DisplayName("POST /rating/validate - Avec données valides - Doit créer")
    void validate_WithValidData_ShouldCreate() throws Exception {
        
        when(ratingService.create(any(Rating.class))).thenReturn(rating1);

        
        mockMvc.perform(post("/rating/validate")
                        .with(csrf())
                        .param("moodysRating", "Aaa")
                        .param("sandPRating", "AAA")
                        .param("fitchRating", "AAA")
                        .param("orderNumber", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/rating/list"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(ratingService).create(any(Rating.class));
    }

    @Test
    @DisplayName("POST /rating/validate - Avec erreurs de validation - Doit retourner formulaire")
    void validate_WithValidationErrors_ShouldReturnForm() throws Exception {
        
        mockMvc.perform(post("/rating/validate")
                        .with(csrf())
                        .param("moodysRating", "Aaa")
                        .param("sandPRating", "AAA")
                        .param("fitchRating", "AAA")
                        .param("orderNumber", "-1"))
                .andExpect(status().isOk())
                .andExpect(view().name("rating/add"))
                .andExpect(model().hasErrors());

        verify(ratingService, never()).create(any());
    }

    @Test
    @DisplayName("POST /rating/validate - Sans aucune notation - Doit échouer validation")
    void validate_WithNoRatings_ShouldFailValidation() throws Exception {
        mockMvc.perform(post("/rating/validate")
                        .with(csrf())
                        .param("moodysRating", "")
                        .param("sandPRating", "")
                        .param("fitchRating", "")
                        .param("orderNumber", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("rating/add"))
                .andExpect(model().hasErrors());

        verify(ratingService, never()).create(any());
    }

    @Test
    @DisplayName("POST /rating/validate - Format Moody's invalide - Doit échouer validation")
    void validate_WithInvalidMoodysFormat_ShouldFailValidation() throws Exception {
        
        mockMvc.perform(post("/rating/validate")
                        .with(csrf())
                        .param("moodysRating", "AAA")
                        .param("sandPRating", "")
                        .param("fitchRating", "")
                        .param("orderNumber", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("rating/add"))
                .andExpect(model().hasErrors());

        verify(ratingService, never()).create(any());
    }

    @Test
    @DisplayName("POST /rating/validate - Avec exception métier - Doit afficher erreur")
    void validate_WithBusinessException_ShouldShowError() throws Exception {
        
        when(ratingService.create(any(Rating.class)))
                .thenThrow(new IllegalArgumentException("Order number already exists"));

        
        mockMvc.perform(post("/rating/validate")
                        .with(csrf())
                        .param("moodysRating", "Aaa")
                        .param("sandPRating", "AAA")
                        .param("fitchRating", "AAA")
                        .param("orderNumber", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/rating/add"))
                .andExpect(flash().attributeExists("errorMessage"));
    }

    @Test
    @DisplayName("GET /rating/update/{id} - Avec ID valide - Doit afficher formulaire")
    void showUpdateForm_WithValidId_ShouldReturnUpdateView() throws Exception {
        
        when(ratingService.findById(1)).thenReturn(Optional.of(rating1));

        
        mockMvc.perform(get("/rating/update/1")
                        .with(csrf()))
                        .andExpect(status().isOk())
                        .andExpect(view().name("rating/update"))
                        .andExpect(model().attributeExists("rating"))
                        .andExpect(model().attribute("rating", org.hamcrest.Matchers.hasProperty("id", org.hamcrest.Matchers.is(1))));

        verify(ratingService).findById(1);
    }

    @Test
    @DisplayName("GET /rating/update/{id} - Avec ID inexistant - Doit rediriger avec erreur")
    void showUpdateForm_WithNonExistentId_ShouldRedirectWithError() throws Exception {
        
        when(ratingService.findById(999)).thenReturn(Optional.empty());

        
        mockMvc.perform(get("/rating/update/999"))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("errorMessage"));

        verify(ratingService).findById(999);
    }


    @Test
    @DisplayName("POST /rating/update/{id} - Avec données valides - Doit mettre à jour")
    void update_WithValidData_ShouldUpdate() throws Exception {
        
        when(ratingService.update(eq(1), any(Rating.class))).thenReturn(rating1);

        
        mockMvc.perform(post("/rating/update/1")
                        .with(csrf())
                        .param("moodysRating", "Aa1")
                        .param("sandPRating", "AA+")
                        .param("fitchRating", "AA+")
                        .param("orderNumber", "3"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/rating/list"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(ratingService).update(eq(1), any(Rating.class));
    }

    @Test
    @DisplayName("POST /rating/update/{id} - Avec erreurs validation - Doit retourner formulaire")
    void update_WithValidationErrors_ShouldReturnForm() throws Exception {
        
        mockMvc.perform(post("/rating/update/1")
                        .with(csrf())
                        .param("moodysRating", "Aaa")
                        .param("sandPRating", "AAA")
                        .param("fitchRating", "AAA")
                        .param("orderNumber", "0"))
                .andExpect(status().isOk())
                .andExpect(view().name("rating/update"))
                .andExpect(model().hasErrors());

        verify(ratingService, never()).update(any(), any());
    }

    @Test
    @DisplayName("POST /rating/update/{id} - Avec ID inexistant - Doit afficher erreur")
    void update_WithNonExistentId_ShouldShowError() throws Exception {
        
        when(ratingService.update(eq(999), any(Rating.class)))
                .thenThrow(new IllegalArgumentException("Rating not found"));

        
        mockMvc.perform(post("/rating/update/999")
                        .with(csrf())
                        .param("moodysRating", "Aaa")
                        .param("sandPRating", "AAA")
                        .param("fitchRating", "AAA")
                        .param("orderNumber", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/rating/update/999"))
                .andExpect(flash().attributeExists("errorMessage"));
    }

    @Test
    @DisplayName("POST /rating/delete/{id} - Avec ID valide - Doit supprimer")
    void delete_WithValidId_ShouldDelete() throws Exception {
        
        doNothing().when(ratingService).deleteById(1);

        
        mockMvc.perform(post("/rating/delete/1")
                        .with(csrf()))
                        .andExpect(status().is3xxRedirection())
                        .andExpect(redirectedUrl("/rating/list"))
                        .andExpect(flash().attributeExists("successMessage"));

        verify(ratingService).deleteById(1);
    }

    @Test
    @DisplayName("POST /rating/delete/{id} - Avec ID inexistant - Doit afficher erreur")
    void delete_WithNonExistentId_ShouldShowError() throws Exception {
        
        doThrow(new IllegalArgumentException("Rating not found"))
                .when(ratingService).deleteById(999);

        
        mockMvc.perform(post("/rating/delete/999")
                        .with(csrf()))
                        .andExpect(status().is3xxRedirection())
                        .andExpect(redirectedUrl("/rating/list"))
                        .andExpect(flash().attributeExists("errorMessage"));

        verify(ratingService).deleteById(999);
    }

    @Test
    @DisplayName("Flash attributes - Doivent être présents après redirection")
    void flashAttributes_ShouldBePresentAfterRedirect() throws Exception {
        
        when(ratingService.create(any(Rating.class))).thenReturn(rating1);

        
        mockMvc.perform(post("/rating/validate")
                        .with(csrf())
                        .param("moodysRating", "Aaa")
                        .param("sandPRating", "AAA")
                        .param("fitchRating", "AAA")
                        .param("orderNumber", "1"))
                .andExpect(flash().attribute("successMessage", "Rating added successfully"));
    }
}