package adeo.leroymerlin.cdp;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class EventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EventService eventService;

    @Test
    void whenQueryMatch_findEvents_returnEvents() throws Exception {

        Member member_1 = new Member();
        member_1.setId(1L);
        member_1.setName("member 1");

        Member member_2 = new Member();
        member_2.setId(2L);
        member_2.setName("member 2");

        Band band_1 = new Band();
        band_1.setName("band 1");
        band_1.setId(1L);
        band_1.setMembers(Set.of(member_1, member_2));

        Event event = new Event();
        event.setId(1L);
        event.setTitle("event 1");
        event.setImgUrl("img 1");
        event.setBands((Set.of(band_1)));
        event.setNbStars(4);
        event.setComment("comment 1");

        when(eventService.getFilteredEvents("event 1")).thenReturn(List.of(event));
        mockMvc.perform(get("/api/events/search/{query}", "event 1"))
                .andExpect(jsonPath("$.*").value(hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].title").value("event 1"))
                .andExpect(jsonPath("$[0].imgUrl").value("img 1"))
                .andExpect(jsonPath("$[0].bands").value(hasSize(1)))
                .andExpect(jsonPath("$[0].bands[0].name").value("band 1"))
                .andExpect(jsonPath("$[0].bands[0].members").value(hasSize(2)))
                .andExpect(jsonPath("$[0].bands[0].members[*].name").value(contains("member 1", "member 2")))
                .andExpect(jsonPath("$[0].nbStars").value(4))
                .andExpect(jsonPath("$[0].comment").value("comment 1"))
                .andExpect(status().isOk());
    }


    @Test
    void whenNoQueryMatch_findEvents_returnEvents() throws Exception {
        when(eventService.getFilteredEvents("unmatchedQuery")).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/events/search/{query}", "unmatchedQuery"))
                .andExpect(jsonPath("$.*").value(hasSize(0)))
                .andExpect(status().isOk());
    }

}
