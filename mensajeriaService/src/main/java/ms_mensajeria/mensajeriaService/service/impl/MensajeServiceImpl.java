package ms_mensajeria.mensajeriaService.service.impl;

import ms_mensajeria.mensajeriaService.dto.MensajeDTO;
import ms_mensajeria.mensajeriaService.exception.AccesoDenegadoException;
import ms_mensajeria.mensajeriaService.model.Mensaje;
import ms_mensajeria.mensajeriaService.repository.MensajeRepository;
import ms_mensajeria.mensajeriaService.service.MensajeService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MensajeServiceImpl implements MensajeService {

    private final MensajeRepository mensajeRepository;

    public MensajeServiceImpl(MensajeRepository mensajeRepository) {
        this.mensajeRepository = mensajeRepository;
    }

    @Override
    public Mensaje enviarMensaje(Long idUsuarioActual, MensajeDTO dto) {
        if (dto.getIdUsuarioReceptor() == null) {
            throw new RuntimeException("El receptor del mensaje es obligatorio.");
        }
        if (dto.getCuerpoMensaje() == null || dto.getCuerpoMensaje().isBlank()) {
            throw new RuntimeException("El cuerpo del mensaje no puede estar vacío.");
        }

        Mensaje mensaje = new Mensaje();
        mensaje.setAsuntoMensaje(dto.getAsuntoMensaje());
        mensaje.setCuerpoMensaje(dto.getCuerpoMensaje());
        mensaje.setIdUsuarioEmisor(idUsuarioActual);
        mensaje.setIdUsuarioReceptor(dto.getIdUsuarioReceptor());
        mensaje.setFechaEnvioMensaje(LocalDateTime.now());
        mensaje.setEstadoMensaje(Mensaje.ESTADO_NO_LEIDO);
        return mensajeRepository.save(mensaje);
    }

    @Override
    public List<Mensaje> listarConversacion(Long idUsuarioActual, Long idUsuario1, Long idUsuario2) {
        if (!idUsuarioActual.equals(idUsuario1) && !idUsuarioActual.equals(idUsuario2)) {
            throw new AccesoDenegadoException("No puedes ver una conversación de la que no formas parte.");
        }
        return mensajeRepository.findConversacion(idUsuario1, idUsuario2);
    }

    @Override
    public List<Mensaje> listarBandejaEntrada(Long idUsuarioActual, Long idUsuarioReceptor) {
        if (!idUsuarioActual.equals(idUsuarioReceptor)) {
            throw new AccesoDenegadoException("No puedes ver la bandeja de entrada de otro usuario.");
        }
        return mensajeRepository.findByIdUsuarioReceptorOrderByFechaEnvioMensajeDesc(idUsuarioReceptor);
    }

    @Override
    public List<Mensaje> listarEnviados(Long idUsuarioActual, Long idUsuarioEmisor) {
        if (!idUsuarioActual.equals(idUsuarioEmisor)) {
            throw new AccesoDenegadoException("No puedes ver los mensajes enviados de otro usuario.");
        }
        return mensajeRepository.findByIdUsuarioEmisorOrderByFechaEnvioMensajeDesc(idUsuarioEmisor);
    }

    @Override
    public Mensaje marcarLeido(Long idUsuarioActual, Long idMensaje) {
        Mensaje mensaje = mensajeRepository.findById(idMensaje)
                .orElseThrow(() -> new RuntimeException("Mensaje no encontrado con id: " + idMensaje));
        if (!mensaje.getIdUsuarioReceptor().equals(idUsuarioActual)) {
            throw new AccesoDenegadoException("Solo el destinatario puede marcar el mensaje como leído.");
        }
        mensaje.setEstadoMensaje(Mensaje.ESTADO_LEIDO);
        return mensajeRepository.save(mensaje);
    }

    @Override
    public long contarNoLeidos(Long idUsuarioActual, Long idUsuarioReceptor) {
        if (!idUsuarioActual.equals(idUsuarioReceptor)) {
            throw new AccesoDenegadoException("No puedes ver el contador de otro usuario.");
        }
        return mensajeRepository.countByIdUsuarioReceptorAndEstadoMensaje(idUsuarioReceptor, Mensaje.ESTADO_NO_LEIDO);
    }
}
