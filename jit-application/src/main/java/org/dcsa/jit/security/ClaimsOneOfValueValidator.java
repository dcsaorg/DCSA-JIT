package org.dcsa.jit.security;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/** Validates that the JWT token contains the intended audience in its claims. */
@Slf4j
@RequiredArgsConstructor(staticName = "of")
public class ClaimsOneOfValueValidator implements OAuth2TokenValidator<Jwt> {

  private final String claimName;
  private final Set<String> oneOf;
  private final ClaimShape claimShape;

  @Getter(lazy = true, value = AccessLevel.PROTECTED)
  private final Set<String> oneOfScope =
      oneOf.stream().map(s -> "clients/" + s).collect(Collectors.toUnmodifiableSet());

  private OAuth2TokenValidatorResult missingClaim() {
    return OAuth2TokenValidatorResult.failure(
        new OAuth2Error("invalid_token", "The required claim " + claimName + " is missing", null));
  }

  private OAuth2TokenValidatorResult notMatchingExpectedClaimValue() {
    return OAuth2TokenValidatorResult.failure(
        new OAuth2Error(
            "invalid_token",
            "The required claim " + claimName + " did not match one of the valid values",
            null));
  }

  private OAuth2TokenValidatorResult wrongClaimShape() {
    return OAuth2TokenValidatorResult.failure(
        new OAuth2Error(
            "invalid_token",
            "The required claim "
                + claimName
                + " had the wrong shape/format (e.g. list vs. string)",
            null));
  }

  public OAuth2TokenValidatorResult validate(Jwt jwt) {
    if (claimName.equals("") || oneOf.isEmpty()) {
      // No requirements, then we accept
      return OAuth2TokenValidatorResult.success();
    }
    if (!jwt.containsClaim(claimName) && !jwt.containsClaim("scope")) {
      return missingClaim();
    }
    List<String> claimValues;
    String scope;
    try {
      scope = jwt.getClaimAsString("scope");
      switch (claimShape) {
        case STRING:
          claimValues = List.of(jwt.getClaimAsString(claimName));
          break;
        case LIST_OF_STRINGS:
          claimValues = jwt.getClaimAsStringList(claimName);
          break;
        default:
          return wrongClaimShape();
      }
    } catch (IllegalArgumentException e) {
      // The jwt.getClaimAsX methods will throw IllegalArgumentException if the claim does not
      // match the "X" type implied.
      return wrongClaimShape();
    }
    if (claimValues != null) {
      for (String value : claimValues) {
        if (oneOf.contains(value)) {
          return OAuth2TokenValidatorResult.success();
        }
      }
    }
    // TODO: This should be configurable separately
    if (scope != null
        && scopesList.apply(scope).stream().anyMatch(sc -> getOneOfScope().contains(sc))) {
      return OAuth2TokenValidatorResult.success();
    }
    return notMatchingExpectedClaimValue();
  }

  private final Function<String, Set<String>> scopesList =
      scope -> {
        if (scope.contains(" ")) {
          return Arrays.stream(scope.split(" ")).collect(Collectors.toSet());
        } else {
          return Set.of(scope);
        }
      };
}
