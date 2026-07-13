package ms_mensajeria.mensajeriaService;

import ms_mensajeria.mensajeriaService.dto.MensajeDTO;
import ms_mensajeria.mensajeriaService.exception.AccesoDenegadoException;
import ms_mensajeria.mensajeriaService.model.Mensaje;
import ms_mensajeria.mensajeriaService.repository.MensajeRepository;
import ms_mensajeria.mensajeriaService.service.impl.MensajeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MensajeServiceImplTest {

    @Mock private MensajeRepository mensajeRepository;

    private MensajeServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new MensajeServiceImpl(mensajeRepository);
    }

    // ── enviarMensaje validaciones ────────────────────────────────────────────

    @Test
    void enviar_sinReceptor_lanzaExcepcion() {
        MensajeDTO dto = new MensajeDTO();
        dto.setIdUsuarioReceptor(null);
        dto.setCuerpoMensaje("Hola");

        assertThatThrownBy(() -> service.enviarMensaje(1L, dto))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("receptor");
    }

    @Test
    void enviar_cuerpoVacio_lanzaExcepcion() {
        MensajeDTO dto = new MensajeDTO();
        dto.setIdUsuarioReceptor(2L);
        dto.setCuerpoMensaje("   ");

        assertThatThrownBy(() -> service.enviarMensaje(1L, dto))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("cuerpo");
    }

    @Test
    void enviar_valido_guardaMensajeConEstadoNoLeido() {
        MensajeDTO dto = new MensajeDTO();
        dto.setIdUsuarioReceptor(2L);
        dto.setCuerpoMensaje("¿Cómo estás?");
        dto.setAsuntoMensaje("Saludo");

        when(mensajeRepository.save(any())).thenAnswer(inv -> {
            Mensaje m = inv.getArgument(0);
            m.setIdMensaje(1L);
            return m;
        });

        Mensaje resultado = service.enviarMensaje(1L, dto);

        assertThat(resultado.getEstadoMensaje()).isEqualTo(Mensaje.ESTADO_NO_LEIDO);
        assertThat(resultado.getIdUsuarioEmisor()).isEqualTo(1L);
        assertThat(resultado.getIdUsuarioReceptor()).isEqualTo(2L);
        assertThat(resultado.getFechaEnvioMensaje()).isNotNull();
        verify(mensajeRepository).save(any(Mensaje.class));
    }

    // ── listarConversacion ────────────────────────────────────────────────────

    @Test
    void conversacion_usuarioNoPertenece_lanzaAccesoDenegado() {
        assertThatThrownBy(() -> service.listarConversacion(3L, 1L, 2L))
                .isInstanceOf(AccesoDenegadoException.class)
                .hasMessageContaining("no formas parte");
    }

    @Test
    void conversacion_usuarioEsParticipante_retornaMensajes() {
        List<Mensaje> mensajes = List.of(mensaje(1L, 2L), mensaje(2L, 1L));
        when(mensajeRepository.findConversacion(1L, 2L)).thenReturn(mensajes);

        List<Mensaje> resultado = service.listarConversacion(1L, 1L, 2L);
        assertThat(resultado).hasSize(2);
    }

    // ── listarBandejaEntrada ──────────────────────────────────────────────────

    @Test
    void bandeja_usuarioDiferente_lanzaAccesoDenegado() {
        assertThatThrownBy(() -> service.listarBandejaEntrada(1L, 2L))
                .isInstanceOf(AccesoDenegadoException.class);
    }

    @Test
    void bandeja_usuarioCorrecto_retornaMensajes() {
        when(mensajeRepository.findByIdUsuarioReceptorOrderByFechaEnvioMensajeDesc(1L))
                .thenReturn(List.of(mensaje(2L, 1L)));

        List<Mensaje> resultado = service.listarBandejaEntrada(1L, 1L);
        assertThat(resultado).hasSize(1);
    }

    // ── listarEnviados ────────────────────────────────────────────────────────

    @Test
    void enviados_usuarioDiferente_lanzaAccesoDenegado() {
        assertThatThrownBy(() -> service.listarEnviados(1L, 2L))
                .isInstanceOf(AccesoDenegadoException.class);
    }

    // ── marcarLeido ───────────────────────────────────────────────────────────

    @Test
    void marcarLeido_noEsReceptor_lanzaAccesoDenegado() {
        Mensaje m = mensaje(2L, 3L); // emisor=2, receptor=3
        when(mensajeRepository.findById(1L)).thenReturn(Optional.of(m));

        assertThatThrownBy(() -> service.marcarLeido(1L, 1L)) // usuario 1 no es receptor
                .isInstanceOf(AccesoDenegadoException.class)
                .hasMessageContaining("destinatario");
    }

    @Test
    void marcarLeido_esReceptor_setEstadoLeido() {
        Mensaje m = mensaje(2L, 1L); // emisor=2, receptor=1
        when(mensajeRepository.findById(1L)).thenReturn(Optional.of(m));
        when(mensajeRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Mensaje resultado = service.marcarLeido(1L, 1L);
        assertThat(resultado.getEstadoMensaje()).isEqualTo(Mensaje.ESTADO_LEIDO);
    }

    @Test
    void marcarLeido_noExiste_lanzaExcepcion() {
        when(mensajeRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.marcarLeido(1L, 99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("99");
    }

    // ── contarNoLeidos ────────────────────────────────────────────────────────

    @Test
    void contarNoLeidos_usuarioDiferente_lanzaAccesoDenegado() {
        assertThatThrownBy(() -> service.contarNoLeidos(1L, 2L))
                .isInstanceOf(AccesoDenegadoException.class);
    }

    @Test
    void contarNoLeidos_usuarioCorrecto_retornaConteo() {
        when(mensajeRepository.countByIdUsuarioReceptorAndEstadoMensaje(1L, Mensaje.ESTADO_NO_LEIDO))
                .thenReturn(5L);
        assertThat(service.contarNoLeidos(1L, 1L)).isEqualTo(5L);
    }

    // ── helper ────────────────────────────────────────────────────────────────

    private Mensaje mensaje(Long emisor, Long receptor) {
        Mensaje m = new Mensaje();
        m.setIdUsuarioEmisor(emisor);
        m.setIdUsuarioReceptor(receptor);
        m.setEstadoMensaje(Mensaje.ESTADO_NO_LEIDO);
        m.setFechaEnvioMensaje(LocalDateTime.now());
        m.setCuerpoMensaje("texto");
        return m;
    }
}
