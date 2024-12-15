package com.example.demo;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.time.LocalDate;
import java.util.Collections;

import com.example.demo.Services.BookingService;
import com.example.demo.Services.MasterService;
import com.example.demo.Services.SalonServiceService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class ClientControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SalonServiceService salonService;

    @MockBean
    private MasterService masterService;

    @MockBean
    private BookingService bookingService;

    @Test
    public void testGetServices() throws Exception {
        when(salonService.getAllServices()).thenReturn(Collections.emptyList());

        this.mockMvc.perform(get("/client/service"))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    public void testGetMasterByService() throws Exception {
        Long serviceId = 1L;
        when(masterService.getMastersByService(serviceId)).thenReturn(Collections.emptyList());

        this.mockMvc.perform(get("/client/master")
                        .param("serviceId", serviceId.toString()))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    public void testGetAllMasters() throws Exception {
        when(masterService.getAllMasters()).thenReturn(Collections.emptyList());

        this.mockMvc.perform(get("/client/master/all"))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.length()").value(0));
    }
}
