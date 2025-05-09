package kr.co.pharosinfo.emailverify.mail;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class EmailVerificationService {

    private final StringRedisTemplate redisTemplate;
    private static final String PREFIX_VERIFY = "email:verify:";
    private static final String PREFIX_STATUS = "email:status:";
    private static final String STATUS_VERIFIED = "verified";

    public void storeVerificationToken(String sessionId, String token){
        String key = PREFIX_VERIFY +sessionId;
        redisTemplate.opsForValue().set(key, token, Duration.ofMinutes(5));
    }

    public boolean verifyToken(String sessionId, String token){
        String key = PREFIX_VERIFY +sessionId;
        String storedToken = redisTemplate.opsForValue().get(key);
        if(storedToken != null && storedToken.equals(token)){
            redisTemplate.delete(key);
            redisTemplate.opsForValue().set(PREFIX_STATUS +sessionId, STATUS_VERIFIED);
            return true;
        }
        return false;
    }

    public boolean isVerified(String sessionId){
        String key = PREFIX_STATUS +sessionId;
        String status = redisTemplate.opsForValue().get(key);
        return STATUS_VERIFIED.equals(status);
    }

    public void clearVerification(String sessionId) {
        redisTemplate.delete(PREFIX_STATUS + sessionId);
    }

}
