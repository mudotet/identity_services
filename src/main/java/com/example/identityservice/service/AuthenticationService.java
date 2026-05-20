package com.example.identityservice.service;

import com.example.identityservice.dto.request.AuthenticationRequest;
import com.example.identityservice.dto.request.IntrospectRequest;
import com.example.identityservice.dto.request.LogoutRequest;
import com.example.identityservice.dto.request.RefreshTokenRequest;
import com.example.identityservice.dto.response.AuthenticationResponse;
import com.example.identityservice.dto.response.IntrospectResponse;
import com.example.identityservice.entity.InvalidatedToken;
import com.example.identityservice.entity.User;
import com.example.identityservice.exception.AppException;
import com.example.identityservice.exception.ErrorCode;
import com.example.identityservice.mapper.UserMapper;
import com.example.identityservice.repository.InvalidatedTokenRepository;
import com.example.identityservice.repository.UserRepository;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.StringJoiner;
import java.util.UUID;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationService {
  UserRepository userRepository;
  InvalidatedTokenRepository invalidatedTokenRepository;
  UserMapper userMapper;

  @NonFinal
  @Value("${jwt.signerKey}")
  protected String SIGNER_KEY;

  @NonFinal
  @Value("${jwt.valid-duration}")
  protected long VALID_DURATION;

  @NonFinal
  @Value("${jwt.refreshable-duration}")
  protected long REFRESHABLE_DURATION;

  PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);

  public AuthenticationResponse authenticateUser(AuthenticationRequest authenticationRequest) {
    if (!userRepository.existsByUserName(authenticationRequest.getUserName()))
      throw new AppException(ErrorCode.USER_NOT_FOUND);

    User user = userRepository.findByUserName(authenticationRequest.getUserName());
    boolean authenticated =
        passwordEncoder.matches(authenticationRequest.getPassWord(), user.getPassWord());
    if (!authenticated) throw new AppException(ErrorCode.UNAUTHENTICATED);
    String token = generateToken(user);
    return AuthenticationResponse.builder().authenticated(true).token(token).build();
  }

  private String generateToken(User user) {
    JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);
    JWTClaimsSet claimsSet =
        new JWTClaimsSet.Builder()
            .subject(user.getUserName())
            .issuer("Identity Service")
            .issueTime(new Date())
            .jwtID(UUID.randomUUID().toString())
            .expirationTime(
                new Date(Instant.now().plus(VALID_DURATION, ChronoUnit.SECONDS).toEpochMilli()))
            .claim("scope", buildScope(user))
            .build();
    Payload payload = new Payload(claimsSet.toJSONObject());
    JWSObject jwsObject = new JWSObject(header, payload);
    try {
      jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));
      return jwsObject.serialize();
    } catch (JOSEException e) {
      log.error("Can not generate token");
      throw new RuntimeException(e);
    }
  }

  public IntrospectResponse introspectUser(IntrospectRequest introspectRequest)
      throws JOSEException, ParseException {
    var token = introspectRequest.getToken();
    boolean isTokenExpired = true;
    try {
      verifyToken(token, false);
    } catch (AppException e) {
      isTokenExpired = false;
    }
    return IntrospectResponse.builder().valid(isTokenExpired).build();
  }

  private String buildScope(User user) {
    StringJoiner joiner = new StringJoiner(" ");
    if (!CollectionUtils.isEmpty(user.getRoles())) {
      user.getRoles()
          .forEach(
              role -> {
                joiner.add("ROLE_" + role.getRoleName());
                if (!CollectionUtils.isEmpty(role.getPermissions()))
                  role.getPermissions()
                      .forEach(permission -> joiner.add(permission.getPermissionName()));
              });
    }
    return joiner.toString();
  }

  public void logout(LogoutRequest logoutRequest) throws ParseException, JOSEException {
    try {
      var signToken = verifyToken(logoutRequest.getToken(), true);
      String jit = signToken.getJWTClaimsSet().getJWTID();
      Date expiration = signToken.getJWTClaimsSet().getExpirationTime();
      InvalidatedToken invalidatedToken =
          InvalidatedToken.builder().id(jit).expiryTime(expiration).build();
      invalidatedTokenRepository.save(invalidatedToken);
    } catch (AppException e) {
      log.info("Token is already expired");
    }
  }

  private SignedJWT verifyToken(String token, boolean isRefresh)
      throws ParseException, JOSEException {
    JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());
    SignedJWT signedJWT = SignedJWT.parse(token);
    Date expiration =
        (isRefresh)
            ? new Date(
                signedJWT
                    .getJWTClaimsSet()
                    .getIssueTime()
                    .toInstant()
                    .plus(REFRESHABLE_DURATION, ChronoUnit.SECONDS)
                    .toEpochMilli())
            : signedJWT.getJWTClaimsSet().getExpirationTime();
    var verified = signedJWT.verify(verifier);
    if (!(verified && expiration.after(new Date()))) {
      throw new AppException(ErrorCode.UNAUTHENTICATED);
    }
    if (invalidatedTokenRepository.existsById(signedJWT.getJWTClaimsSet().getJWTID())) {
      throw new AppException(ErrorCode.UNAUTHENTICATED);
    }
    return signedJWT;
  }

  public AuthenticationResponse refreshToken(RefreshTokenRequest request)
      throws ParseException, JOSEException {
    var signJWT = verifyToken(request.getToken(), true);
    var jit = signJWT.getJWTClaimsSet().getJWTID();
    var expiration = signJWT.getJWTClaimsSet().getExpirationTime();
    if (invalidatedTokenRepository.existsById(jit)) {
      throw new AppException(ErrorCode.UNAUTHENTICATED);
    }
    InvalidatedToken invalidatedToken =
        InvalidatedToken.builder().expiryTime(expiration).id(jit).build();
    invalidatedTokenRepository.save(invalidatedToken);

    var userName = signJWT.getJWTClaimsSet().getSubject();
    var user = userRepository.findByUserName(userName);
    return AuthenticationResponse.builder().authenticated(true).token(generateToken(user)).build();
  }
}
