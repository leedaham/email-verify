package kr.co.pharosinfo.emailverify;

import jakarta.servlet.http.HttpSession;
import kr.co.pharosinfo.emailverify.mail.EmailService;
import kr.co.pharosinfo.emailverify.mail.EmailVerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
@RequestMapping("/email-confirm")
public class EmailConfirmVerifyController {

    private final EmailVerificationService emailVerificationService;
    private final EmailService emailService;


    @GetMapping
    public String emailPage() {
        return "email-confirm-verify";
    }

    @PostMapping("/send")
    @ResponseBody
    public Map<String, String> sendEmail(@RequestBody Map<String, String> body, HttpSession session) {
        String email = body.get("email");
        String sessionId = session.getId();
        String token = UUID.randomUUID().toString();

        // Redis 저장 및 이메일 전송
        emailVerificationService.storeVerificationToken(sessionId, token);
        emailService.sendEmail(email, "이메일 인증",
                "아래 링크를 눌러 인증하세요:\n" +
                        "http://localhost:8080/email-confirm/confirm?key=" + URLEncoder.encode(sessionId + ":" + token, StandardCharsets.UTF_8)
        );

        return Map.of("sessionId", sessionId);
    }

    @GetMapping("/confirm")
    public String confirmEmail(@RequestParam("key") String key, Model model) {
        String[] parts = key.split(":");
        if (parts.length != 2) return "잘못된 요청입니다.";

        String sessionId = parts[0];
        String token = parts[1];

        boolean verified = emailVerificationService.verifyToken(sessionId, token);

        model.addAttribute("verified", verified ? "이메일 인증 완료!" : "인증 실패 또는 만료됨");
        return "confirm-result";
    }

    @GetMapping("/verify")
    @ResponseBody
    public String verifySession(@RequestParam("sessionId") String sessionId) {
        boolean verified = emailVerificationService.isVerified(sessionId);

        if (verified) {
            // 검증 후 Redis에서 키 제거
            emailVerificationService.clearVerification(sessionId);
            return "✅ 인증 완료! 세션 정보 제거함";
        } else {
            return "❌ 아직 인증되지 않았어요";
        }
    }
}
