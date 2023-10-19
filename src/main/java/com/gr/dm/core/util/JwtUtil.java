package com.gr.dm.core.util;

import java.text.ParseException;
import java.util.Date;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.gr.dm.core.dto.UserDto;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.KeyLengthException;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

@Component
public class JwtUtil {

	@Value("${dm.jwt.signingKey}")
	private String jwtSigningKey;

	@Value("${dm.jwt.tokenExpiry}")
	private Long tokenExpiry;

	@Value("${dm.jwt.tokenIssuer}")
	private String tokenIssuer;

	@Value("${dm.jwt.tokenVersion}")
	private String tokenVersion;

	public String generateToken(UserDto userDto) throws KeyLengthException, JOSEException {

		byte[] signingKey = jwtSigningKey.getBytes();

		JWSSigner signer = new MACSigner(new SecretKeySpec(signingKey, "HS256"));

		long currentMillis = System.currentTimeMillis();

		long expirationTime = currentMillis + tokenExpiry;

		JWTClaimsSet claimsSet = new JWTClaimsSet.Builder().subject(userDto.getFirstName())
				.issueTime(new Date(currentMillis)).issuer(tokenIssuer).expirationTime(new Date(expirationTime))
				.claim("version", tokenVersion).claim("login", userDto).build();

		SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimsSet);
		signedJWT.sign(signer);

		String jwt = signedJWT.serialize();
		return jwt;
	}

	public SignedJWT validateAndParseToken(String jwt) throws Exception {

		SignedJWT signedJWT = null;

		try {
			byte[] signingKey = new byte[32];
			signingKey = jwtSigningKey.getBytes();
			SecretKey key = new SecretKeySpec(signingKey, "HS256");
			signedJWT = SignedJWT.parse(jwt);
			signedJWT.verify(new MACVerifier(key));

			Date now = new Date(System.currentTimeMillis());

			if (signedJWT.getJWTClaimsSet().getExpirationTime().before(now)) {
				throw new Exception("Token Expired.");
			}

		} catch (ParseException | JOSEException ex) {
			throw new Exception();
		}

		return signedJWT;
	}

	public JWTClaimsSet getClaimSetFromToken(SignedJWT signedJWT) throws ParseException {
		long currentMillis = System.currentTimeMillis();

		Date oldIssueTime = signedJWT.getJWTClaimsSet().getIssueTime();
		Date oldExpTime = signedJWT.getJWTClaimsSet().getExpirationTime();

		long expDiff = oldExpTime.getTime() - oldIssueTime.getTime();

		JWTClaimsSet claimsSet;

		long expirationTime = currentMillis + expDiff;
		claimsSet = new JWTClaimsSet.Builder().subject(signedJWT.getJWTClaimsSet().getSubject())
				.issueTime(new Date(currentMillis)).issuer(tokenIssuer).expirationTime(new Date(expirationTime))
				.claim("version", signedJWT.getJWTClaimsSet().getClaim("version"))
				.claim("login", signedJWT.getJWTClaimsSet().getClaim("login")).build();
		return claimsSet;
	}

	public String generateTokenFromClaimSet(JWTClaimsSet jWTClaimsSet) throws JOSEException {
		byte[] signingKey = jwtSigningKey.getBytes();
		JWSSigner signer = new MACSigner(new SecretKeySpec(signingKey, "HS256"));
		SignedJWT signedJWTNew = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), jWTClaimsSet);
		signedJWTNew.sign(signer);
		String newJWT = signedJWTNew.serialize();
		return newJWT;
	}
}
