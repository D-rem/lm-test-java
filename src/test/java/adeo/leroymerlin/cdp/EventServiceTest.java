package adeo.leroymerlin.cdp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class EventServiceTest {


    @BeforeEach
    public void setUp() {
        Mockito.reset(eventRepository);
        Mockito.clearInvocations(eventRepository);
    }

    @InjectMocks
    private EventService eventService;

    @Mock
    private EventRepository eventRepository;


    @Test
    void whenNoEvent_getFilteredEvents_returnEmptyList() {
        Mockito.when(eventRepository.findAll()).thenReturn(Collections.emptyList());
        List<Event> filteredEvents = eventService.getFilteredEvents("");
        assertAll(() -> {
            assertEquals(0, filteredEvents.size());
            Mockito.verify(eventRepository, Mockito.times(1)).findAll();
        });
    }


    @Test
    void whenNoQueryMatch_getFilteredEvents_returnEmptyList() {
        List<Event> filteredEvents = eventService.getFilteredEvents("3");
        assertAll(() -> {
            assertEquals(0, filteredEvents.size());
            Mockito.verify(eventRepository, Mockito.times(1)).findAll();
        });
    }

    @Test
    void whenQueryMatch_getFilteredEvents_returnListOfEvents() {

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
        event.setBands((Set.of(band_1)));

        Mockito.when(eventRepository.findAll()).thenReturn(Collections.singletonList(event));

        List<Event> filteredEvents = eventService.getFilteredEvents("2");

        assertAll(() -> {
            assertEquals(1, filteredEvents.size());
            assertEquals("event 1 [1]", filteredEvents.getFirst().getTitle());
            assertEquals("band 1 [2]", filteredEvents.getFirst().getBands().stream().findFirst().orElse(new Band()).getName());
            Mockito.verify(eventRepository, Mockito.times(1)).findAll();
        });
    }

    @Test
    void whenEvent_save_shouldSaveOnce() {
        Event event = new Event();
        event.setId(1L);
        event.setTitle("event 1");

        eventService.save(event);
        assertAll(() -> Mockito.verify(eventRepository, Mockito.times(1)).save(event));
    }



}
