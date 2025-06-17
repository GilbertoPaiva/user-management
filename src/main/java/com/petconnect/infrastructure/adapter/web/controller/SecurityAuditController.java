package com.petconnect.infrastructure.adapter.web.controller;

import com.petconnect.domain.security.entity.SecurityAuditLog;
import com.petconnect.domain.security.port.SecurityAuditLogRepositoryPort;
import com.petconnect.infrastructure.adapter.web.shared.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Controller para gerenciamento de auditoria de segurança
 */
@RestController
@RequestMapping("/api/admin/security")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@PreAuthorize("hasRole('ADMIN')")
public class SecurityAuditController {
    
    private final SecurityAuditLogRepositoryPort securityAuditLogRepository;
    
    /**
     * Lista logs de auditoria de segurança com paginação
     */
    @GetMapping("/audit-logs")
    public ResponseEntity<ApiResponse<Page<SecurityAuditLogResponse>>> getAuditLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String eventType,
            @RequestParam(required = false) String userIdentifier,
            @RequestParam(required = false) String ipAddress) {
        
        List<SecurityAuditLog> logs;
        
        if (eventType != null) {
            logs = securityAuditLogRepository.findByEventType(eventType);
        } else if (userIdentifier != null) {
            logs = securityAuditLogRepository.findByUserIdentifier(userIdentifier);
        } else if (ipAddress != null) {
            logs = securityAuditLogRepository.findByIpAddress(ipAddress);
        } else {
            logs = securityAuditLogRepository.findAll();
        }
        
        // Converter para response DTOs
        List<SecurityAuditLogResponse> responses = logs.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        
        // Aplicar paginação manual
        Pageable pageable = PageRequest.of(page, size);
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), responses.size());
        
        Page<SecurityAuditLogResponse> pageResponse = new PageImpl<>(
                responses.subList(start, end), pageable, responses.size());
        
        return ResponseEntity.ok(ApiResponse.success("Logs de auditoria recuperados", pageResponse));
    }
    
    /**
     * Obtém estatísticas de segurança
     */
    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getSecurityStats() {
        LocalDateTime last24Hours = LocalDateTime.now().minusHours(24);
        LocalDateTime lastWeek = LocalDateTime.now().minusWeeks(1);
        
        List<SecurityAuditLog> recentLogs = securityAuditLogRepository.findByEventTimestampBetween(
                last24Hours, LocalDateTime.now());
        
        List<SecurityAuditLog> weeklyLogs = securityAuditLogRepository.findByEventTimestampBetween(
                lastWeek, LocalDateTime.now());
        
        // Contar eventos por tipo
        Map<String, Long> eventTypeCount = recentLogs.stream()
                .collect(Collectors.groupingBy(
                        SecurityAuditLog::getEventType, Collectors.counting()));
        
        // Contar tentativas de login falhas
        long failedLogins24h = recentLogs.stream()
                .filter(log -> "LOGIN_FAILURE".equals(log.getEventType()))
                .count();
        
        // Contar violações de segurança
        long securityViolations24h = recentLogs.stream()
                .filter(log -> "SECURITY_VIOLATION".equals(log.getEventType()) || 
                              "UNAUTHORIZED_ACCESS".equals(log.getEventType()))
                .count();
        
        // IPs mais ativos
        Map<String, Long> topIps = recentLogs.stream()
                .filter(log -> log.getIpAddress() != null)
                .collect(Collectors.groupingBy(
                        SecurityAuditLog::getIpAddress, Collectors.counting()));
        
        Map<String, Object> stats = Map.of(
                "totalEvents24h", recentLogs.size(),
                "totalEventsWeek", weeklyLogs.size(),
                "failedLogins24h", failedLogins24h,
                "securityViolations24h", securityViolations24h,
                "eventTypeCount", eventTypeCount,
                "topIpAddresses", topIps.entrySet().stream()
                        .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                        .limit(10)
                        .collect(Collectors.toMap(
                                Map.Entry::getKey,
                                Map.Entry::getValue,
                                (e1, e2) -> e1,
                                java.util.LinkedHashMap::new))
        );
        
        return ResponseEntity.ok(ApiResponse.success("Estatísticas de segurança", stats));
    }
    
    /**
     * Busca violações de segurança recentes
     */
    @GetMapping("/violations")
    public ResponseEntity<ApiResponse<List<SecurityAuditLogResponse>>> getSecurityViolations(
            @RequestParam(defaultValue = "24") int hours) {
        
        LocalDateTime since = LocalDateTime.now().minusHours(hours);
        List<SecurityAuditLog> violations = securityAuditLogRepository.findSecurityViolations(since);
        
        List<SecurityAuditLogResponse> responses = violations.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(ApiResponse.success("Violações de segurança", responses));
    }
    
    /**
     * Busca logs por usuário específico
     */
    @GetMapping("/user/{userIdentifier}")
    public ResponseEntity<ApiResponse<List<SecurityAuditLogResponse>>> getUserAuditLogs(
            @PathVariable String userIdentifier,
            @RequestParam(defaultValue = "7") int days) {
        
        LocalDateTime since = LocalDateTime.now().minusDays(days);
        List<SecurityAuditLog> userLogs = securityAuditLogRepository
                .findByUserIdentifierAndEventTimestampBetween(userIdentifier, since, LocalDateTime.now());
        
        List<SecurityAuditLogResponse> responses = userLogs.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(ApiResponse.success("Logs do usuário", responses));
    }
    
    /**
     * Mapeia SecurityAuditLog para Response DTO
     */
    private SecurityAuditLogResponse mapToResponse(SecurityAuditLog log) {
        return SecurityAuditLogResponse.builder()
                .id(log.getId())
                .eventType(log.getEventType())
                .eventDescription(log.getEventDescription())
                .userIdentifier(log.getUserIdentifier())
                .ipAddress(log.getIpAddress())
                .userAgent(log.getUserAgent())
                .success(log.getSuccess())
                .eventTimestamp(log.getEventTimestamp())
                .additionalData(log.getAdditionalData())
                .build();
    }
    
    /**
     * DTO para resposta de SecurityAuditLog
     */
    @lombok.Data
    @lombok.Builder
    public static class SecurityAuditLogResponse {
        private java.util.UUID id;
        private String eventType;
        private String eventDescription;
        private String userIdentifier;
        private String ipAddress;
        private String userAgent;
        private Boolean success;
        private LocalDateTime eventTimestamp;
        private String additionalData;
    }
}
