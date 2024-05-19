package com.matjarna.service.jwt;

import java.util.Date;
import java.util.Set;

import io.jsonwebtoken.Claims;

public interface IJwtService {

	String generateToken(String email, long id, Set<String> roles);

	Date extractExpiration(String token);

	String extractEmail(String token);

	Set<String> extractRoles(String token);

	boolean isTokenValid(String token);

	long extractId(String token);

	Claims extractClaims(String token);
}