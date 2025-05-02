package store.mtvs.academyconnect.global.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Slf4j
public class RequestDebugFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    HttpServletResponse res,
                                    FilterChain chain)
            throws ServletException, IOException {

        /* 절대 request.setAttribute() 호출하지 않음 */
        log.info("[REQ] {} {} | ip={} | ua={}",
                req.getMethod(),
                req.getRequestURI() + Optional.ofNullable(req.getQueryString()).map(q -> "?" + q).orElse(""),
                req.getRemoteAddr(),
                Optional.ofNullable(req.getHeader("User-Agent")).orElse(""));

        chain.doFilter(req, res);
    }
}
