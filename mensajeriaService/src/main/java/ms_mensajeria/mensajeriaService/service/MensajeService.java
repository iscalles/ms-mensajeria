package ms_mensajeria.mensajeriaService.service;

import ms_mensajeria.mensajeriaService.dto.MensajeDTO;
import ms_mensajeria.mensajeriaService.model.Mensaje;

import java.util.List;

public interface MensajeService {

    Mensaje enviarMensaje(Long idUsuarioActual, MensajeDTO dto);

    List<Mensaje> listarConversacion(Long idUsuarioActual, Long idUsuario1, Long idUsuario2);

    List<Mensaje> listarBandejaEntrada(Long idUsuarioActual, Long idUsuarioReceptor);

    List<Mensaje> listarEnviados(Long idUsuarioActual, Long idUsuarioEmisor);

    Mensaje marcarLeido(Long idUsuarioActual, Long idMensaje);

    long contarNoLeidos(Long idUsuarioActual, Long idUsuarioReceptor);
}
