package ms_mensajeria.mensajeriaService.service.impl;

import ms_mensajeria.mensajeriaService.dto.MensajeDTO;
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
    public Mensaje enviarMensaje(MensajeDTO dto) {
        if (dto.getIdUsuarioEmisor() == null || dto.getIdUsuarioReceptor() == null) {
            throw new RuntimeException("El emisor y el receptor del mensaje son obligatorios.");
        }
        if (dto.getCuerpoMensaje() == null || dto.getCuerpoMensaje().isBlank()) {
            throw new RuntimeException("El cuerpo del mensaje no puede estar vacío.");
        }

        Mensaje mensaje = new Mensaje();
        mensaje.setAsuntoMensaje(dto.getAsuntoMensaje());
        mensaje.setCuerpoMensaje(dto.getCuerpoMensaje());
        mensaje.setIdUsuarioEmisor(dto.getIdUsuarioEmisor());
        mensaje.setIdUsuarioReceptor(dto.getIdUsuarioReceptor());
        mensaje.setFechaEnvioMensaje(LocalDateTime.now());
        mensaje.setEstadoMensaje(Mensaje.ESTADO_NO_LEIDO);
        return mensajeRepository.save(mensaje);
    }

    @Override
    public List<Mensaje> listarConversacion(Long idUsuario1, Long idUsuario2) {
        return mensajeRepository.findConversacion(idUsuario1, idUsuario2);
    }

    @Override
    public List<Mensaje> listarBandejaEntrada(Long idUsuarioReceptor) {
        return mensajeRepository.findByIdUsuarioReceptorOrderByFechaEnvioMensajeDesc(idUsuarioReceptor);
    }

    @Override
    public List<Mensaje> listarEnviados(Long idUsuarioEmisor) {
        return mensajeRepository.findByIdUsuarioEmisorOrderByFechaEnvioMensajeDesc(idUsuarioEmisor);
    }

    @Override
    public Mensaje marcarLeido(Long idMensaje) {
        Mensaje mensaje = mensajeRepository.findById(idMensaje)
                .orElseThrow(() -> new RuntimeException("Mensaje no encontrado con id: " + idMensaje));
        mensaje.setEstadoMensaje(Mensaje.ESTADO_LEIDO);
        return mensajeRepository.save(mensaje);
    }

    @Override
    public long contarNoLeidos(Long idUsuarioReceptor) {
        return mensajeRepository.countByIdUsuarioReceptorAndEstadoMensaje(idUsuarioReceptor, Mensaje.ESTADO_NO_LEIDO);
    }
}
