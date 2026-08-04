package board.hotarticle.service;

import board.common.event.Event;
import board.common.event.EventType;
import board.hotarticle.service.eventhandler.EventHandler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.Stream;

import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class HotArticleServiceTest {
    @InjectMocks
    HotArticleService hotArticleService;
    @Mock
    List<EventHandler> eventHandlers;
    @Mock
    HotArticleScoreUpdater hotArticleScoreUpdater;

    @Test
    void handleEventIfEventHandlerNotFoundTest() {
        Event event = mock(Event.class);
        EventHandler eventHandler = mock(EventHandler.class);

        given(eventHandlers.stream()).willReturn(Stream.of(eventHandler));
        given(eventHandler.supports(event)).willReturn(false);

        hotArticleService.handleEvent(event);

        verify(eventHandler, never()).handle(event);
        verify(hotArticleScoreUpdater, never()).update(event, eventHandler);
    }

    @Test
    void handleEventIfArticleCreatedEventTest() {

        // given
        Event event = mock(Event.class);
        EventHandler eventHandler = mock(EventHandler.class);

        given(eventHandlers.stream()).willReturn(Stream.of(eventHandler));
        given(eventHandler.supports(event)).willReturn(true);
        given(event.getType()).willReturn(EventType.ARTICLE_CREATED);

        // when
        hotArticleService.handleEvent(event);

        // then
        verify(eventHandler).handle(event);
        verify(hotArticleScoreUpdater, never()).update(event, eventHandler);
    }

    @Test
    void handleEventIfArticleDeletedEventTest() {
        Event event = mock(Event.class);
        EventHandler eventHandler = mock(EventHandler.class);

        given(eventHandlers.stream()).willReturn(Stream.of(eventHandler));
        given(eventHandler.supports(event)).willReturn(true);
        given(event.getType()).willReturn(EventType.ARTICLE_DELETED);

        hotArticleService.handleEvent(event);

        verify(eventHandler).handle(event);
        verify(hotArticleScoreUpdater, never()).update(event, eventHandler);
    }

    @Test
    void handleEventIfScoreUpdatableEventTest() {
        Event event = mock(Event.class);
        EventHandler eventHandler = mock(EventHandler.class);

        given(eventHandlers.stream()).willReturn(Stream.of(eventHandler));
        given(eventHandler.supports(event)).willReturn(true);
        given(event.getType()).willReturn(mock(EventType.class));

        hotArticleService.handleEvent(event);

        verify(eventHandler, never()).handle(event);
        verify(hotArticleScoreUpdater).update(event, eventHandler);
    }
}
