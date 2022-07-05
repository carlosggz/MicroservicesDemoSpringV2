package org.example.moviesapi.infrastructure.events;

import org.example.moviesapi.domain.events.LikeMovieEvent;
import org.example.moviesapi.utils.MoviesObjectMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.Message;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EventsPublisherImplTest {

    @Mock
    StreamBridge streamBridge;

    @InjectMocks
    EventsPublisherImpl eventsPublisher;

    @Captor
    ArgumentCaptor<Message<LikeMovieEvent>> captor;

    @Test
    void publishEventSendsMessage() {
        //given
        var givenMovie = MoviesObjectMother.getRandomMovie();
        var givenEvent = new LikeMovieEvent(givenMovie);

        //when
        eventsPublisher.publish(givenEvent);

        //then
        verify(streamBridge).send(eq(EventsPublisherImpl.BINDING), captor.capture());
        var message = captor.getValue();
        assertNotNull(message);
        assertEquals(givenEvent, message.getPayload());
        assertTrue(message.getHeaders().containsKey(EventsPublisherImpl.ROUTING_KEY));
        assertEquals(givenEvent.getEventName(), message.getHeaders().get(EventsPublisherImpl.ROUTING_KEY));
    }

}
