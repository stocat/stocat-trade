package com.stocat.tradeapi.position.service;

import com.stocat.common.domain.position.PositionEntity;
import com.stocat.common.exception.ApiException;
import com.stocat.tradeapi.position.service.dto.command.PositionUpsertCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PositionServiceTest {

    @Mock
    private PositionQueryService positionQueryService;

    private PositionService positionService;

    @BeforeEach
    void setUp() {
        positionService = new PositionService(positionQueryService);
    }

    @Test
    void 포지션이_없으면_신규_생성한다() {
        PositionUpsertCommand command = new PositionUpsertCommand(1L, 10L, new BigDecimal("5.00"), new BigDecimal("100.00"));
        when(positionQueryService.getUserPosition(command.assetId(), command.userId()))
                .thenReturn(Optional.empty());

        positionService.updateUserPosition(command);

        ArgumentCaptor<PositionEntity> captor = ArgumentCaptor.forClass(PositionEntity.class);
        verify(positionQueryService).saveUserPosition(captor.capture());

        PositionEntity saved = captor.getValue();
        assertEquals(command.userId(), saved.getUserId());
        assertEquals(command.assetId(), saved.getAssetId());
        assertEquals(0, saved.getQuantity().compareTo(command.quantity()));
        assertEquals(0, saved.getAvgEntryPrice().compareTo(command.avgEntryPrice()));
    }

    @Test
    void 기존_포지션이면_수량을_증가시킨다() {
        PositionEntity existing = PositionEntity.create(1L, 10L, new BigDecimal("2.00"), new BigDecimal("50.00"));
        when(positionQueryService.getUserPosition(10L, 1L)).thenReturn(Optional.of(existing));

        PositionUpsertCommand addCommand = new PositionUpsertCommand(1L, 10L, new BigDecimal("3.00"), new BigDecimal("100.00"));

        positionService.updateUserPosition(addCommand);

        verify(positionQueryService).saveUserPosition(existing);
        assertEquals(0, existing.getQuantity().compareTo(new BigDecimal("5.00")));
        assertEquals(0, existing.getAvgEntryPrice().compareTo(new BigDecimal("80.00")));
    }

    @Test
    void 수량이_0이면_예외가_발생한다() {
        PositionEntity existing = PositionEntity.create(2L, 11L, new BigDecimal("2.00"), new BigDecimal("70.00"));
        when(positionQueryService.getUserPosition(11L, 2L)).thenReturn(Optional.of(existing));

        PositionUpsertCommand invalidCommand = new PositionUpsertCommand(2L, 11L, BigDecimal.ZERO, new BigDecimal("70.00"));

        assertThrows(ApiException.class, () -> positionService.updateUserPosition(invalidCommand));
        verify(positionQueryService).getUserPosition(11L, 2L);
        verifyNoMoreInteractions(positionQueryService);
    }
}
