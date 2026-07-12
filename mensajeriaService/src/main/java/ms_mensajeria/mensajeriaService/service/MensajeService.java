package ms_mensajeria.mensajeriaService.service;

import ms_mensajeria.mensajeriaService.dto.MensajeDTO;
import ms_mensajeria.mensajeriaService.model.Mensaje;

import java.util.List;

public interface MensajeService {

    Mensaje enviarMensaje(MensajeDTO dto);

    List<Mensaje> listarConversacion(Long idUsuario1, Long idUsuario2);

    List<Mensaje> listarBandejaEntrada(Long idUsuarioReceptor);

    List<Mensaje> listarEnviados(Long idUsuarioEmisor);

    Mensaje marcarLeido(Long idMensaje);

    long contarNoLeidos(Long idUsuarioReceptor);
}
