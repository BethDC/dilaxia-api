package it.avbo.dilaxia.api.servlets.auth;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import it.avbo.dilaxia.api.database.UserSource;
import it.avbo.dilaxia.api.entities.User;
import it.avbo.dilaxia.api.models.auth.LoginModel;
import it.avbo.dilaxia.api.services.Utils;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.core.Response;
import org.wildfly.security.password.PasswordFactory;
import org.wildfly.security.password.interfaces.SaltedSimpleDigestPassword;
import org.wildfly.security.password.spec.SaltedHashPasswordSpec;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Optional;

@WebServlet("/auth/login")
public class LoginServlet extends HttpServlet {
    private static final Gson gson = new Gson();
    private static final PasswordFactory passwordFactory;


    static {
        try {
            passwordFactory = PasswordFactory.getInstance(SaltedSimpleDigestPassword.ALGORITHM_SALT_PASSWORD_DIGEST_SHA_256);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if(req.isRequestedSessionIdValid()) {
            resp.setStatus(Response.Status.OK.getStatusCode());
            return;
        }

        Optional<String> data = Utils.stringFromReader(req.getReader());
        if(data.isEmpty()) {
            resp.sendError(
                    HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE,
                    "Il corpo della richiesta è vuoto"
            );
            return;
        }
        LoginModel loginModel;
        try {
             loginModel = gson.fromJson(data.get(), LoginModel.class);
        } catch (JsonSyntaxException e) {
            resp.sendError(
                    HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE,
                    "Il formato dei dati inviati non corrisponde alla documentazione"
            );
            return;
        }

        Optional<User> result = UserSource.getUserByIdentifier(loginModel.getIdentifier());
        if(result.isEmpty()) {
            resp.sendError(
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "Impossibile trovare l'utente");
            return;
        }
        User user = result.get();
        SaltedHashPasswordSpec saltedHashSpec = new SaltedHashPasswordSpec(user.passwordHash, user.salt);

        try {
            SaltedSimpleDigestPassword restored = (SaltedSimpleDigestPassword) passwordFactory.generatePassword(saltedHashSpec);
            if(passwordFactory.verify(restored, loginModel.getPassword().toCharArray())) {
                req.getSession().setAttribute("role", user.role);
                req.getSession().setMaxInactiveInterval(300);
                resp.setStatus(HttpServletResponse.SC_OK);
            }
        } catch (InvalidKeyException | InvalidKeySpecException ignored) {
            resp.sendError(
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "Impossibile creare la sessione utente"
            );
        }

    }
}
