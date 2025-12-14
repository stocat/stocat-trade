package com.stocat.tradeapi.position.controller;

import com.stocat.common.response.ApiResponse;
import com.stocat.tradeapi.position.controller.dto.NewPositionRequest;
import com.stocat.tradeapi.position.controller.dto.PositionResponse;
import com.stocat.tradeapi.position.service.PositionService;
import com.stocat.tradeapi.position.service.dto.PositionDto;
import com.stocat.tradeapi.position.service.dto.command.GetPositionCommand;
import com.stocat.tradeapi.position.service.dto.command.NewPositionCommand;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Position", description = "포지션 조회 API")
@RestController()
@RequestMapping("/positions")
public class PositionController {

    private final PositionService positionService;

    public PositionController(PositionService positionService) {
        this.positionService = positionService;
    }

    @Operation(summary = "단일 포지션 조회", description = "유저가 보유한 특정 포지션을 조회합니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "포지션 조회 성공")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "유저가 소유한 포지션을 찾을 수 없습니다")
    @GetMapping("/{positionId}")
    public ResponseEntity<ApiResponse<PositionResponse>> getPosition(
            @Positive @PathVariable Long positionId,
            @Positive @RequestHeader("X-USER-ID") Long userId) {
        GetPositionCommand command = GetPositionCommand.from(positionId, userId);
        PositionDto position = positionService.getPositionById(command);

        PositionResponse response = PositionResponse.from(position);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "유저의 포지션 목록 조회", description = "유저가 소유한 전체 포지션 목록을 조회합니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "포지션 조회 성공")
    @GetMapping()
    public ResponseEntity<ApiResponse<List<PositionResponse>>> getUserPositions(
            @Positive @RequestHeader("X-USER-ID") Long userId) {
        List<PositionDto> userPositions = positionService.getUserPositions(userId);

        List<PositionResponse> response = userPositions.stream()
                .map(PositionResponse::from)
                .toList();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "유저의 포지션 생성", description = "포지션 정보를 받아 기존 Open 상태인 포지션에 반영하거나 새로 생성")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "포지션 생성/반영 성공")
    @GetMapping()
    public ResponseEntity<ApiResponse<Void>> newUserPosition(
            @Valid @RequestBody NewPositionRequest request) {
        NewPositionCommand command = NewPositionCommand.from(request);
        positionService.createNewUserPosition(command);

        return ResponseEntity.ok(ApiResponse.success());
    }
}
