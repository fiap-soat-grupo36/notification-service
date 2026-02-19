package br.com.fiap.oficina.notification.service.impl;

import br.com.fiap.oficina.notification.exception.EmailNaoEnviadoException;
import br.com.fiap.oficina.notification.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final ITemplateEngine templateEngine;

    @Value("${app.notification.email.sender}")
    private String sender;

    @Async("taskExecutor")
    public void enviarEmail(String para, String assunto, String template, Map<String, Object> variaveis) {
        String correlationId = resolveCorrelationId(variaveis);
        log.info("Iniciando envio de email. correlationId={}, to={}, subject=\"{}\", template={}",
                correlationId, para, assunto, template);

        try {
            Context context = new Context();
            context.setVariables(variaveis);

            String bodyHtml = templateEngine.process(template, context);
            log.debug("Template renderizado. correlationId={}, htmlLength={}, variableKeys={}",
                    correlationId,
                    bodyHtml == null ? 0 : bodyHtml.length(),
                    variaveis == null ? "[]" : variaveis.keySet());

            MimeMessage mensagem = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mensagem, "UTF-8");

            helper.setFrom(sender);
            helper.setTo(para);
            helper.setSubject(assunto);
            helper.setText(bodyHtml, true);

            mailSender.send(mensagem);
            log.info("Email enviado com sucesso. correlationId={}, to={}, subject=\"{}\", messageId={}",
                    correlationId, para, assunto, mensagem.getMessageID());
        } catch (MailException | MessagingException e) {
            log.error("Falha no envio de email. correlationId={}, to={}, subject=\"{}\", template={}, reason={}",
                    correlationId, para, assunto, template, e.getMessage(), e);
            throw new EmailNaoEnviadoException("Erro ao enviar o e-mail", e);
        } catch (Exception e) {
            log.error("Erro inesperado no envio de email. correlationId={}, to={}, subject=\"{}\", template={}",
                    correlationId, para, assunto, template, e);
            throw new EmailNaoEnviadoException("Erro inesperado ao enviar o e-mail", e);
        }
    }

    private String resolveCorrelationId(Map<String, Object> variaveis) {
        if (variaveis == null || variaveis.isEmpty()) {
            return "n/a";
        }

        Object ordemServicoId = variaveis.get("ordemServicoId");
        if (ordemServicoId != null) {
            return "os-" + ordemServicoId;
        }

        Object customerId = variaveis.get("customerId");
        if (customerId != null) {
            return "customer-" + customerId;
        }

        return "n/a";
    }
}
