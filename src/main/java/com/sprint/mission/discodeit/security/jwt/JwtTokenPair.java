package com.sprint.mission.discodeit.security.jwt;

public record JwtTokenPair(
    String accessToken,
    String refreshToken
) {

}
