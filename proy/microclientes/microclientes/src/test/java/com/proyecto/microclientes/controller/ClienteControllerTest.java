package com.proyecto.microclientes.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.proyecto.microclientes.dto.ClienteDTO;
import com.proyecto.microclientes.dto.PersonaDTO;
import com.proyecto.microclientes.entity.Cliente;
import com.proyecto.microclientes.entity.Persona;
import com.proyecto.microclientes.service.ClienteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
class ClienteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private ClienteService clienteService;

    @Mock
    private ModelMapper mapper;

    @InjectMocks
    private ClienteController controller;

    private Cliente cliente;
    private ClienteDTO clienteDTO;
    private Persona persona;
    private PersonaDTO personaDTO;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        objectMapper = new ObjectMapper();

        persona = new Persona();
        persona.setNombre("Test");

        cliente = new Cliente();
        cliente.setClienteid("123");
        cliente.setPersona(persona);

        personaDTO = new PersonaDTO();
        personaDTO.setNombre("Test");

        clienteDTO = new ClienteDTO();
        clienteDTO.setClienteid("123");
        clienteDTO.setPersona(personaDTO);
    }

    // Pruebas unitarias
    @Test
    void listar_DebeRetornarListaDeClientesDTO() {
        when(clienteService.listar()).thenReturn(Arrays.asList(cliente));
        when(mapper.map(cliente, ClienteDTO.class)).thenReturn(clienteDTO);
        when(mapper.map(persona, PersonaDTO.class)).thenReturn(personaDTO);

        List<ClienteDTO> resultado = controller.listar();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(clienteService).listar();
    }

    @Test
    void buscar_DebeRetornarClienteDTO() {
        when(clienteService.buscarPorId("123")).thenReturn(cliente);
        when(mapper.map(cliente, ClienteDTO.class)).thenReturn(clienteDTO);
        when(mapper.map(persona, PersonaDTO.class)).thenReturn(personaDTO);

        ClienteDTO resultado = controller.buscar("123");

        assertNotNull(resultado);
        assertEquals("123", resultado.getClienteid());
        verify(clienteService).buscarPorId("123");
    }

    @Test
    void crear_DebeRetornarNuevoClienteDTO() {
        when(mapper.map(clienteDTO, Cliente.class)).thenReturn(cliente);
        when(mapper.map(personaDTO, Persona.class)).thenReturn(persona);
        when(clienteService.guardar(cliente)).thenReturn(cliente);
        when(mapper.map(cliente, ClienteDTO.class)).thenReturn(clienteDTO);
        when(mapper.map(persona, PersonaDTO.class)).thenReturn(personaDTO);

        ClienteDTO resultado = controller.crear(clienteDTO);

        assertNotNull(resultado);
        verify(clienteService).guardar(cliente);
    }

    @Test
    void actualizar_DebeRetornarClienteActualizado() {
        when(mapper.map(clienteDTO, Cliente.class)).thenReturn(cliente);
        when(mapper.map(personaDTO, Persona.class)).thenReturn(persona);
        when(clienteService.guardar(cliente)).thenReturn(cliente);
        when(mapper.map(cliente, ClienteDTO.class)).thenReturn(clienteDTO);
        when(mapper.map(persona, PersonaDTO.class)).thenReturn(personaDTO);

        ClienteDTO resultado = controller.actualizar("123", clienteDTO);

        assertNotNull(resultado);
        assertEquals("123", resultado.getClienteid());
        verify(clienteService).guardar(cliente);
    }

    @Test
    void eliminar_DebeEliminarCliente() {
        doNothing().when(clienteService).eliminar("123");
        controller.eliminar("123");
        verify(clienteService).eliminar("123");
    }

    // Pruebas de integración
    @Test
    void listar_DebeRetornar200() throws Exception {
        when(clienteService.listar()).thenReturn(Arrays.asList(cliente));
        when(mapper.map(cliente, ClienteDTO.class)).thenReturn(clienteDTO);

        mockMvc.perform(get("/clientes")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].clienteid", is("123")));
    }

    @Test
    void buscar_DebeRetornar200() throws Exception {
        when(clienteService.buscarPorId("123")).thenReturn(cliente);
        when(mapper.map(cliente, ClienteDTO.class)).thenReturn(clienteDTO);

        mockMvc.perform(get("/clientes/123")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.clienteid", is("123")));
    }

    @Test
    void eliminar_DebeRetornar200() throws Exception {
        doNothing().when(clienteService).eliminar("123");

        mockMvc.perform(delete("/clientes/123")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}