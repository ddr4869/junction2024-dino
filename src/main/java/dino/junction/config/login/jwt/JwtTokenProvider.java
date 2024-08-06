package dino.junction.config.login.jwt;

import dino.junction.common.dto.token.TokenDto;
import dino.junction.config.security.AuthMember;
import dino.junction.domain.user.repository.UserRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.Date;

// 토큰을 생성하고 검증하는 클래스입니다.
// 해당 컴포넌트는 필터클래스에서 사전 검증을 거칩니다.
@Component
@Slf4j(topic = "JwtTokenProvider")
@RequiredArgsConstructor
public class JwtTokenProvider {
    @Value("${jwt.secret}")
    private String secretKey;
    @Value("${jwt.access-token-time}")
    private long accessTokenTime;
    @Value("${jwt.refresh-token-time}")
    private long refreshTokenTime;
    private final StringRedisTemplate redisTemplate;
    private final UserRepository userRepository;

    // 객체 초기화, secretKey를 Base64로 인코딩한다.
    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    // JWT 토큰 생성
    public String createToken(Long memberId, JwtTokenType tokenType) throws Exception {


        // 토큰 생성
        //Claims claims = Jwts.claims().setSubject(member.getId().toString()); // JWT payload 에 저장되는 정보단위, 보통 여기서 user를 식별하는 값을 넣는다.
        Claims claims = Jwts.claims().setSubject("Mock"); // JWT payload 에 저장되는 정보단위, 보통 여기서 user를 식별하는 값을 넣는다.
        claims.put("type", tokenType.getTokenType()); // 토큰 타입 (access token, refresh token)
        Date now = new Date();
        Date expiration;

        if (tokenType == JwtTokenType.REFRESH_TOKEN) { // refresh token
            expiration = new Date(now.getTime() + refreshTokenTime);
        } else if (tokenType == JwtTokenType.ACCESS_TOKEN) { // access token
            expiration = new Date(now.getTime() + accessTokenTime);
        } else {
            throw new Exception("Invalid Token Type");
        }

        return Jwts.builder()
                .setClaims(claims) // 정보 저장
                .setIssuedAt(now) // 토큰 발행 시간 정보
                .setExpiration(expiration) // set Expire Time
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes()), SignatureAlgorithm.HS256) // signature 에 들어갈 secret값 세팅
                .compact();
    }

    // JWT 토큰에서 인증 정보 조회
    public Authentication getAuthentication(Jws<Claims> claimsJws) throws Exception {
        String memberId = claimsJws.getBody().getSubject();
//        Member member = memberRepository.findById(Long.parseLong(memberId))
//                .orElseThrow(MemberException.MemberNotFoundException::new);

        // AuthMember 객체 생성
        AuthMember authMember = AuthMember.builder()
//                .memberId(member.getId())
//                .phoneNumber(member.getPhoneNumber())
//                .kakaoId(member.getKakaoId())
//                .role(member.getRole().getName())
                .build();

        return new UsernamePasswordAuthenticationToken(authMember, "", authMember.getAuthorities());
    }

//    // Request의 Header에서 token 값을 가져옵니다. "Authorization" : Bearer [TOKEN 값]'
    public String resolveToken(HttpServletRequest request) {
        String value = request.getHeader("Authorization");
        if (value != null && value.startsWith("Bearer ")) {
            return value.substring(7);
        } else {
            return null;
        }
    }

//    // 토큰의 유효성 + 만료일자 확인
    public Jws<Claims> validateAndParseToken(String jwt) {
        JwtParser jwtParser = Jwts.parserBuilder().setSigningKey(secretKey.getBytes()).build();
        return jwtParser.parseClaimsJws(jwt);
    }

    public boolean isRefreshToken(Jws<Claims> jws) {
        return jws.getBody().get("type").equals(JwtTokenType.REFRESH_TOKEN.getTokenType());
    }

    public boolean isAccessToken(Jws<Claims> jws) {
        return jws.getBody().get("type").equals(JwtTokenType.ACCESS_TOKEN.getTokenType());
    }

    public TokenDto refresh(Long memberId, String token) throws Exception {
//        if (Boolean.FALSE.equals(redisTemplate.hasKey(token))) {
//            throw new MemberException.InvalidTokenException();
//        }
        redisTemplate.delete(token);
        String accessToken = createToken(memberId, JwtTokenType.ACCESS_TOKEN);
        String refreshToken = createToken(memberId, JwtTokenType.REFRESH_TOKEN);
        redisTemplate.opsForValue().set(refreshToken, memberId.toString());

        return TokenDto.of(accessToken, refreshToken);
    }
}
