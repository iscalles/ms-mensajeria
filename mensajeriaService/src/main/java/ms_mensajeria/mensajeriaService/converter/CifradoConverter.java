package ms_mensajeria.mensajeriaService.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Base64;

@Converter
@Component
public class CifradoConverter implements AttributeConverter<String, String> {

    private static final String ALGORITMO = "AES/GCM/NoPadding";
    private static final int TAMANO_TAG_BITS = 128;
    private static final int TAMANO_IV_BYTES = 12;

    private final SecretKeySpec claveSecreta;
    private final SecureRandom generadorAleatorio = new SecureRandom();

    public CifradoConverter(@Value("${mensajeria.encryption.key}") String claveBase64) {
        byte[] claveBytes = Base64.getDecoder().decode(claveBase64);
        this.claveSecreta = new SecretKeySpec(claveBytes, "AES");
    }

    @Override
    public String convertToDatabaseColumn(String textoPlano) {
        if (textoPlano == null) {
            return null;
        }
        try {
            byte[] iv = new byte[TAMANO_IV_BYTES];
            generadorAleatorio.nextBytes(iv);

            Cipher cipher = Cipher.getInstance(ALGORITMO);
            cipher.init(Cipher.ENCRYPT_MODE, claveSecreta, new GCMParameterSpec(TAMANO_TAG_BITS, iv));
            byte[] textoCifrado = cipher.doFinal(textoPlano.getBytes(StandardCharsets.UTF_8));

            ByteBuffer buffer = ByteBuffer.allocate(iv.length + textoCifrado.length);
            buffer.put(iv);
            buffer.put(textoCifrado);
            return Base64.getEncoder().encodeToString(buffer.array());
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Error al cifrar el mensaje", e);
        }
    }

    @Override
    public String convertToEntityAttribute(String textoCifradoBase64) {
        if (textoCifradoBase64 == null) {
            return null;
        }
        try {
            byte[] datos = Base64.getDecoder().decode(textoCifradoBase64);
            ByteBuffer buffer = ByteBuffer.wrap(datos);
            byte[] iv = new byte[TAMANO_IV_BYTES];
            buffer.get(iv);
            byte[] textoCifrado = new byte[buffer.remaining()];
            buffer.get(textoCifrado);

            Cipher cipher = Cipher.getInstance(ALGORITMO);
            cipher.init(Cipher.DECRYPT_MODE, claveSecreta, new GCMParameterSpec(TAMANO_TAG_BITS, iv));
            byte[] textoPlano = cipher.doFinal(textoCifrado);
            return new String(textoPlano, StandardCharsets.UTF_8);
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Error al descifrar el mensaje", e);
        }
    }
}
