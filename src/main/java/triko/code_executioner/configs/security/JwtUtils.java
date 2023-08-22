package triko.code_executioner.configs.security;

import java.security.Key;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import triko.code_executioner.models.DUser;

@Component
public class JwtUtils {
	@Value("${auth.jwt.secret}")
	private String secret;
	
	private final long EXP_TIME = 7 * 24 * 60 * 60 * 1000;

	private Key key;
	
	@PostConstruct
	public void init() {
		this.key = Keys.hmacShaKeyFor(secret.getBytes());
	}
	
	public Claims getAllClaimsFromToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }

    public String getUsernameFromToken(String token) {
        return getAllClaimsFromToken(token).getSubject();
    }

    public Date getExpirationDateFromToken(String token) {
        return getAllClaimsFromToken(token).getExpiration();
    }
    
    @SuppressWarnings("unchecked")
    public ArrayList<String> getRolesFromToken(String token) {
    	return getAllClaimsFromToken(token).get("roles", ArrayList.class);
    }

    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }
	
	public String generateToken(DUser user) {
		Date now = new Date();
		Date expiration = new Date(now.getTime() + EXP_TIME);
		Map<String, Object> claims = new HashMap<String, Object>();
		claims.put("id", user.id());
		claims.put("name", user.name());
		claims.put("roles", user.roles());
		
		
		return Jwts.builder()
				.setSubject(user.username())
				.addClaims(claims)
				.setIssuedAt(now)
				.setExpiration(expiration)
				.signWith(key)
				.compact();
	}
	
	public Boolean validateToken(String token) {
        return !isTokenExpired(token);
    }
}
