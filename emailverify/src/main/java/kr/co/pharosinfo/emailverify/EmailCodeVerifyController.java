package kr.co.pharosinfo.emailverify;

import jakarta.servlet.http.HttpSession;
import kr.co.pharosinfo.emailverify.mail.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/email-code")
public class EmailCodeVerifyController {

    private final EmailService emailService;

    // 1️⃣ 최초 진입 페이지
    @GetMapping
    public String getEmailPage() {
        return "email-code-verify"; // 타임리프 페이지 이름
    }

    // 2️⃣ 인증 코드 전송
    @PostMapping("/send")
    @ResponseBody
    public ResponseEntity<?> sendCode(@RequestParam("email") String email, HttpSession session) {
        String code = String.valueOf((int)(Math.random() * 900000) + 100000);
        session.setAttribute("emailCode", code);
        session.setAttribute("codeGeneratedAt", System.currentTimeMillis());

        emailService.sendEmail(email, "이메일 인증 코드", "인증 코드: " + code);

        return ResponseEntity.ok("인증 코드 전송 완료");
    }

    // 3️⃣ 인증 코드 검증
    @PostMapping("/verify")
    @ResponseBody
    public ResponseEntity<?> verifyCode(@RequestParam("code") String code, HttpSession session) {
        String sessionCode = (String) session.getAttribute("emailCode");
        Long createdAt = (Long) session.getAttribute("codeGeneratedAt");

        if (sessionCode == null || createdAt == null) {
            return ResponseEntity.badRequest().body("인증 세션이 만료되었습니다.");
        }

        long elapsed = (System.currentTimeMillis() - createdAt) / 1000;

        if (elapsed > 300) {
            session.removeAttribute("emailCode");
            session.removeAttribute("codeGeneratedAt");
            return ResponseEntity.status(410).body("인증 시간이 초과되었습니다.");
        }

        if (sessionCode.equals(code)) {
            session.removeAttribute("emailCode");
            session.removeAttribute("codeGeneratedAt");
            return ResponseEntity.ok("인증 성공");
        } else {
            return ResponseEntity.badRequest().body("인증 코드가 일치하지 않습니다.");
        }
    }


}
