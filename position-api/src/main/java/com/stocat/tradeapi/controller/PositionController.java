package com.stocat.tradeapi.controller;

import com.stocat.common.response.ApiResponse;
import com.stocat.tradeapi.controller.dto.PositionResponse;
import com.stocat.tradeapi.service.PositionService;
import com.stocat.tradeapi.service.dto.PositionDto;
import com.stocat.tradeapi.service.dto.command.GetPositionCommand;
import com.stocat.tradeapi.service.dto.command.GetUserPositionCommand;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController()
@RequestMapping("/positions")
public class PositionController {

    private final PositionService positionService;

    public PositionController(PositionService positionService) {
        this.positionService = positionService;
    }

    @GetMapping("/{positionId}")
    public ResponseEntity<ApiResponse<PositionResponse>> getPosition(@PathVariable Long positionId, @RequestParam Long userId) {
        GetPositionCommand command = GetPositionCommand.from(positionId, userId);
        PositionDto position = positionService.getPositionById(command);

        PositionResponse response = PositionResponse.from(position);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping()
    public ResponseEntity<ApiResponse<List<PositionResponse>>> getUserPositions(@RequestParam Long userId) {
        GetUserPositionCommand command = GetUserPositionCommand.from(userId);
        List<PositionDto> userPositions = positionService.getUserPositions(command);
        
        List<PositionResponse> response = userPositions.stream()
                .map(PositionResponse::from)
                .toList();
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
