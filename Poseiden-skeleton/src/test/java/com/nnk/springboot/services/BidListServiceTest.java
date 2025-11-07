package com.nnk.springboot.services;

import com.nnk.springboot.domain.BidList;
import com.nnk.springboot.repositories.BidListRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.EmptyResultDataAccessException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("BidListService Tests")
class BidListServiceTest {

    @Mock
    private BidListRepository bidListRepository;

    @InjectMocks
    private BidListService bidListService;

    private BidList validBidList;

    @BeforeEach
    void setUp() {
        validBidList = new BidList("TestAccount", "TestType", 100.0);
        validBidList.setBidListId(1);
    }

    // CREATE Tests
    @Test
    @DisplayName("Should create BidList successfully")
    void shouldCreateBidListSuccessfully() {
        
        when(bidListRepository.save(any(BidList.class))).thenReturn(validBidList);

        
        BidList created = bidListService.create(validBidList);

        
        assertThat(created).isNotNull();
        assertThat(created.getAccount()).isEqualTo("TestAccount");
        assertThat(created.getCreationDate()).isNotNull();
        verify(bidListRepository).save(any(BidList.class));
    }

    @Test
    @DisplayName("Should force null ID when creating BidList")
    void shouldForceNullIdWhenCreating() {
        
        validBidList.setBidListId(999); // ID existant
        when(bidListRepository.save(any(BidList.class))).thenReturn(validBidList);

        
        bidListService.create(validBidList);

        
        verify(bidListRepository).save(argThat(bidList -> bidList.getBidListId() == null));
    }

    @Test
    @DisplayName("Should throw exception when creating null BidList")
    void shouldThrowExceptionWhenCreatingNull() {
        
        assertThatThrownBy(() -> bidListService.create(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("cannot be null");
    }

    // UPDATE Tests
    @Test
    @DisplayName("Should update BidList successfully")
    void shouldUpdateBidListSuccessfully() {
        
        when(bidListRepository.existsById(1)).thenReturn(true);
        when(bidListRepository.save(any(BidList.class))).thenReturn(validBidList);

        
        BidList updated = bidListService.update(1, validBidList);

        
        assertThat(updated).isNotNull();
        assertThat(updated.getRevisionDate()).isNotNull();
        verify(bidListRepository).existsById(1);
        verify(bidListRepository).save(any(BidList.class));
    }

    @Test
    @DisplayName("Should force ID when updating BidList")
    void shouldForceIdWhenUpdating() {
        
        validBidList.setBidListId(999); // ID différent
        when(bidListRepository.existsById(1)).thenReturn(true);
        when(bidListRepository.save(any(BidList.class))).thenReturn(validBidList);

        
        bidListService.update(1, validBidList);

        
        verify(bidListRepository).save(argThat(bidList -> bidList.getBidListId().equals(1)));
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent BidList")
    void shouldThrowExceptionWhenUpdatingNonExistent() {
        
        when(bidListRepository.existsById(999)).thenReturn(false);

        
        assertThatThrownBy(() -> bidListService.update(999, validBidList))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("not found");
    }

    @Test
    @DisplayName("Should throw exception when updating with invalid ID")
    void shouldThrowExceptionWhenUpdatingWithInvalidId() {
        
        assertThatThrownBy(() -> bidListService.update(null, validBidList))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid ID");

        assertThatThrownBy(() -> bidListService.update(0, validBidList))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid ID");

        assertThatThrownBy(() -> bidListService.update(-1, validBidList))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid ID");
    }

    // READ Tests
    @Test
    @DisplayName("Should find all BidLists")
    void shouldFindAllBidLists() {
        
        List<BidList> bidLists = Arrays.asList(validBidList, new BidList("Account2", "Type2", 200.0));
        when(bidListRepository.findAll()).thenReturn(bidLists);

        
        List<BidList> result = bidListService.findAll();

        
        assertThat(result).hasSize(2);
        verify(bidListRepository).findAll();
    }

    @Test
    @DisplayName("Should find BidList by ID")
    void shouldFindBidListById() {
        
        when(bidListRepository.findById(1)).thenReturn(Optional.of(validBidList));

        
        Optional<BidList> result = bidListService.findById(1);

        
        assertThat(result).isPresent();
        assertThat(result.get().getAccount()).isEqualTo("TestAccount");
        verify(bidListRepository).findById(1);
    }

    @Test
    @DisplayName("Should return empty when finding by invalid ID")
    void shouldReturnEmptyWhenFindingByInvalidId() {
        
        Optional<BidList> result1 = bidListService.findById(null);
        Optional<BidList> result2 = bidListService.findById(0);
        Optional<BidList> result3 = bidListService.findById(-1);

        
        assertThat(result1).isEmpty();
        assertThat(result2).isEmpty();
        assertThat(result3).isEmpty();
        verify(bidListRepository, never()).findById(any());
    }

    @Test
    @DisplayName("Should return empty when BidList not found")
    void shouldReturnEmptyWhenBidListNotFound() {
        
        when(bidListRepository.findById(999)).thenReturn(Optional.empty());

        
        Optional<BidList> result = bidListService.findById(999);

        
        assertThat(result).isEmpty();
    }

    // DELETE Tests
    @Test
    @DisplayName("Should delete BidList successfully")
    void shouldDeleteBidListSuccessfully() {
        
        doNothing().when(bidListRepository).deleteById(1);

        
        bidListService.deleteById(1);

        
        verify(bidListRepository).deleteById(1);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent BidList")
    void shouldThrowExceptionWhenDeletingNonExistent() {
        
        doThrow(new EmptyResultDataAccessException(1)).when(bidListRepository).deleteById(999);

        
        assertThatThrownBy(() -> bidListService.deleteById(999))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("not found");
    }

    @Test
    @DisplayName("Should throw exception when deleting with invalid ID")
    void shouldThrowExceptionWhenDeletingWithInvalidId() {
        
        assertThatThrownBy(() -> bidListService.deleteById(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid ID");

        assertThatThrownBy(() -> bidListService.deleteById(0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid ID");

        assertThatThrownBy(() -> bidListService.deleteById(-1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid ID");
    }
}