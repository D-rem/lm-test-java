package adeo.leroymerlin.cdp;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EventService {

    private final EventRepository eventRepository;

    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public List<Event> getEvents() {
        return eventRepository.findAll();
    }

    public void delete(Long id) {
        eventRepository.deleteById(id);
    }

    /**
     *
     * @param query
     * @return
     */
    public List<Event> getFilteredEvents(String query) {
        List<Event> events = eventRepository.findAll();
        return events
                .parallelStream()
                .filter(event -> event.getBands().stream().anyMatch(band -> band.getMembers()
                        .stream().anyMatch(member -> member.getName().toUpperCase().contains(query.toUpperCase()))))
                .map(event -> {
                    event.setTitle(String.format("%s [%s]", event.getTitle(), event.getBands().size()));
                    event.setBands(event.getBands().stream().map(band -> {
                        band.setName(String.format("%s [%s]", band.getName(), band.getMembers().size()));
                        return band;
                    }).collect(Collectors.toSet()));
                    return event;
                }).collect(Collectors.toList());
    }

    /**
     * Save an event
     *
     * @param event the event to save
     */
    public void save(Event event) {
        eventRepository.save(event);
    }
}
