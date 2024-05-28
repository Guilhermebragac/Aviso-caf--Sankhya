package acaoagendada;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import org.cuckoo.core.ScheduledAction;
import org.cuckoo.core.ScheduledActionContext;
import com.sankhya.util.TimeUtils;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.core.JapeSession.SessionHandle;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;
import br.com.sankhya.modelcore.auth.AuthenticationInfo;
import br.com.sankhya.modelcore.util.SPBeanUtils;
import br.com.sankhya.ws.ServiceContext;

public class Fazercafe implements ScheduledAction {

    @Override
    public void onTime(ScheduledActionContext ctx) {
        BigDecimal codigoUsuario = getCodigoUsuarioAtual();
        notificausu(ctx, codigoUsuario);
    }

    private void notificausu(ScheduledActionContext ctx, BigDecimal codigoUsuario) {
        ServiceContext sc = new ServiceContext(null);
        sc.setAutentication(AuthenticationInfo.getCurrent());
        sc.makeCurrent();
        try {
            SPBeanUtils.setupContext(sc);
        } catch (Exception e) {
            e.printStackTrace();
            ctx.info("Error: ao notificar\n" + e.getMessage());
        }

        String exibir = "<div style='background-color: white; border: 2px solid #ffa500; border-radius: 10px; "
                + "padding: 20px; margin: 50px auto; max-width: 400px; box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);'>"
                + "<h1 style='color: #ffa500; font-size: 18px;'>Tá na hora de fazer o café!!!</h1>" + "</div>";

        String image = "<div style='display: flex; justify-content: center; align-items: center;'> "
                + "<img src='https://mir-s3-cdn-cf.behance.net/project_modules/hd/6d99c2133796483.61c5e24c9e4ad.gif'"
                + " width='200px' height='200px'> </div> <br>";

        String exibirmensagem = (exibir+"<div style='text-align: center;'><p>"+image+ "</p></div>");

        JdbcWrapper jdbc = null;
        SessionHandle hnd = null;
        JapeWrapper avisoDAO = JapeFactory.dao("AvisoSistema");

        try {
            avisoDAO.create()
            .set("NUAVISO", null)
            .set("CODUSUREMETENTE", BigDecimal.valueOf(0))
            .set("CODUSU", BigDecimal.valueOf(0))
            .set("TITULO", "Já fez o café ?")
            .set("DESCRICAO", exibirmensagem)
            .set("DHCRIACAO", TimeUtils.getNow())
            .set("IDENTIFICADOR", "PERSONALIZADO")
            .set("IMPORTANCIA", BigDecimal.valueOf(0))
            .set("SOLUCAO", "Fazer café")
            .set("TIPO", "P")
            .save();
        } catch (Exception e) {
            e.printStackTrace();
            RuntimeException re = new RuntimeException(e);
            throw re;
        } finally {
            JdbcWrapper.closeSession(jdbc);
            JapeSession.close(hnd);
        }
    }

    public BigDecimal getCodigoUsuarioAtual() {
        LocalDateTime agora = LocalDateTime.now();
        DayOfWeek diaSemana = agora.getDayOfWeek();
        int hora = agora.getHour();

        switch (diaSemana) {
            // SEGUNDA
            case MONDAY:
                return hora < 12 ? BigDecimal.valueOf(173) : BigDecimal.valueOf(995);
            // TERÇA
            case TUESDAY:
                return hora < 12 ? BigDecimal.valueOf(994) : BigDecimal.valueOf(979);
            // QUARTA
            case WEDNESDAY:
                return hora < 12 ? BigDecimal.valueOf(64) : BigDecimal.valueOf(198);
            // QUINTA
            case THURSDAY:
                return hora < 12 ? BigDecimal.valueOf(995) : BigDecimal.valueOf(994);
            // SEXTA
            case FRIDAY:
                return hora < 12 ? BigDecimal.valueOf(979) : BigDecimal.valueOf(64);
            default:
                return BigDecimal.valueOf(0); // Retorno inválido
        }
    }
}



