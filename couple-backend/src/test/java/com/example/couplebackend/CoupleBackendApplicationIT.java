package com.example.couplebackend;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.couplebackend.service.CoupleService;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import tools.jackson.databind.ObjectMapper;

@SpringBootTest
class CoupleBackendApplicationIT {
    @Autowired
    private CoupleService coupleService;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void contextLoads() {
    }

    @Test
    void bootstrapContainsBackendDrivenMiniProgramData() {
        Map<String, Object> bootstrap = coupleService.bootstrap(1L);

        assertThat((List<?>) bootstrap.get("serviceItems")).isNotEmpty();
        assertThat((List<?>) bootstrap.get("moodOptions")).isNotEmpty();
        assertThat((List<?>) bootstrap.get("quickSignals")).isNotEmpty();
        assertThat((List<?>) bootstrap.get("orderStatusFilters")).isNotEmpty();
        assertThat((List<?>) bootstrap.get("todoTabs")).isNotEmpty();
        assertThat((List<?>) bootstrap.get("mineMenuGroups")).isNotEmpty();
        assertThat((List<?>) bootstrap.get("templates")).isNotEmpty();

        @SuppressWarnings("unchecked")
        Map<String, Object> firstService = (Map<String, Object>) ((List<?>) bootstrap.get("serviceItems")).get(0);
        assertThat(firstService).containsKeys("visualText", "priceText", "orderButtonText");
    }

    @Test
    void longIdsSerializeAsStringsForMiniProgram() throws Exception {
        String json = objectMapper.writeValueAsString(Map.of("id", 316805743915012096L));

        assertThat(json).contains("\"id\":\"316805743915012096\"");
    }
}
