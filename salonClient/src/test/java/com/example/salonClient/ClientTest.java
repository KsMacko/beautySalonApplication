package com.example.salonClient;

import static org.mockito.Mockito.*;

import com.example.salonclient.Model.ServerAccess;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.util.function.Consumer;

public class ClientTest {

    @Test
    void testSendRequest() throws Exception {
        HttpURLConnection mockConnection = Mockito.mock(HttpURLConnection.class);
        when(mockConnection.getResponseCode()).thenReturn(HttpURLConnection.HTTP_OK);
        InputStream mockInputStream = new ByteArrayInputStream("{\"message\": \"Success\"}".getBytes());
        when(mockConnection.getInputStream()).thenReturn(mockInputStream);

        Consumer<String> onSuccess = mock(Consumer.class);
        Consumer<Throwable> onError = mock(Consumer.class);
        URLStreamHandler streamHandler = new URLStreamHandler() {
            @Override
            protected URLConnection openConnection(URL url) {
                return mockConnection;
            }
        };
        ServerAccess.sendRequest("/test", "POST", "{\"key\": \"value\"}",
                onSuccess::accept,
                error -> { onError.accept(error);});
        Thread.sleep(8000);
        verify(onSuccess).accept(any());
        verify(onError, never()).accept(any());
    }
}
